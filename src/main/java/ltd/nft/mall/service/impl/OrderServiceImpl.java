package ltd.nft.mall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import ltd.nft.mall.common.*;
import ltd.nft.mall.controller.vo.*;
import ltd.nft.mall.dao.GoodsMapper;
import ltd.nft.mall.dao.OrderItemMapper;
import ltd.nft.mall.dao.OrderMapper;
import ltd.nft.mall.dao.ShoppingCartItemMapper;
import ltd.nft.mall.entity.Goods;
import ltd.nft.mall.entity.Order;
import ltd.nft.mall.entity.OrderItem;
import ltd.nft.mall.entity.StockNumDTO;
import ltd.nft.mall.service.OrderService;
import ltd.nft.mall.util.BeanUtil;
import ltd.nft.mall.util.NumberUtil;
import ltd.nft.mall.util.PageQueryUtil;
import ltd.nft.mall.util.PageResult;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper newOrderMapper;
    @Autowired
    private OrderItemMapper newOrderItemMapper;
    @Autowired
    private ShoppingCartItemMapper newShoppingCartItemMapper;
    @Autowired
    private GoodsMapper newGoodsMapper;

    @Override
    public PageResult getNewOrdersPage(PageQueryUtil pageUtil) {
        List<Order> newOrders = newOrderMapper.findNewOrderList(pageUtil);
        int total = newOrderMapper.getTotalNewOrders(pageUtil);
        PageResult pageResult = new PageResult(newOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(Order newOrder) {
        Order temp = newOrderMapper.selectByPrimaryKey(newOrder.getOrderId());
        // If not null and orderStatus >= 0, some information can be modified before the
        // status is out of stock
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(newOrder.getTotalPrice());
            temp.setUserAddress(newOrder.getUserAddress());
            temp.setUpdateTime(new Date());
            if (newOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        // Query all orders, check the status, modify the status and update the time
        List<Order> orders = newOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (Order newOrder : orders) {
                if (newOrder.getIsDeleted() == 1) {
                    errorOrderNos += newOrder.getOrderNo() + " ";
                    continue;
                }
                if (newOrder.getOrderStatus() != 1) {
                    errorOrderNos += newOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                // Order status is normal; can perform the operation of completing allocation,
                // modify the order status, and update the time
                if (newOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                // Orders cannot be shipped at this time
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + " The order status is not successful payment and cannot be shipped";
                } else {
                    return "You have selected too many orders with status not successful payment and cannot complete the allocation operation";
                }
            }
        }
        // No data found, return error message
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        // Query all orders, check the status, modify the status and update the time
        List<Order> orders = newOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (Order newOrder : orders) {
                if (newOrder.getIsDeleted() == 1) {
                    errorOrderNos += newOrder.getOrderNo() + " ";
                    continue;
                }
                if (newOrder.getOrderStatus() != 1 && newOrder.getOrderStatus() != 2) {
                    errorOrderNos += newOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                // Order status is normal; can perform the operation of completing allocation,
                // modify the order status, and update the time
                if (newOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                // Orders cannot be shipped at this time
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos
                            + " The order status is not successful payment or completed allocation and cannot be shipped";
                } else {
                    return "You have selected too many orders with status not successful payment or completed allocation and cannot complete the allocation operation";
                }
            }
        }
        // No data found, return error message
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        // Query all orders, check the status, modify the status and update the time
        List<Order> orders = newOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (Order newOrder : orders) {
                // isDeleted=1 means the order is already closed
                if (newOrder.getIsDeleted() == 1) {
                    errorOrderNos += newOrder.getOrderNo() + " ";
                    continue;
                }
                // Orders that are closed or completed cannot be closed
                if (newOrder.getOrderStatus() == 4 || newOrder.getOrderStatus() < 0) {
                    errorOrderNos += newOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                // Order status is normal; can perform the close operation, modify the order
                // status and update the time & restore stock
                if (newOrderMapper.closeOrder(Arrays.asList(ids),
                        OrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0
                        && recoverStockNum(Arrays.asList(ids))) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                // Orders cannot be closed at this time
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + " Orders cannot be closed";
                } else {
                    return "You have selected too many orders that cannot be closed";
                }
            }
        }
        // No data found, return error message
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(MallUserVO user, List<ShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(ShoppingCartItemVO::getCartItemId)
                .collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(ShoppingCartItemVO::getGoodsId)
                .collect(Collectors.toList());
        List<Goods> newGoods = newGoodsMapper.selectByPrimaryKeys(goodsIds);
        // Check if it includes off-shelf goods
        List<Goods> goodsListNotSelling = newGoods.stream()
                .filter(newGoodsTemp -> newGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            // If goodsListNotSelling object is not empty, it indicates there are off-shelf
            // goods
            marketException
                    .fail(goodsListNotSelling.get(0).getGoodsName() + " is off-shelf, cannot create an order");
        }
        Map<Long, Goods> newGoodsMap = newGoods.stream().collect(
                Collectors.toMap(Goods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        // Check product inventory
        for (ShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            // If the associated product data does not exist in the shopping cart, return an
            // error reminder
            if (!newGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                marketException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            // If the quantity exceeds the stock, return an error reminder
            if (shoppingCartItemVO.getGoodsCount() > newGoodsMap.get(shoppingCartItemVO.getGoodsId())
                    .getStockNum()) {
                marketException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        // Delete shopping items
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds)
                && !CollectionUtils.isEmpty(newGoods)) {
            if (newShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = newGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    marketException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                // Generate order number
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                // Save order
                Order newOrder = new Order();
                newOrder.setOrderNo(orderNo);
                newOrder.setUserId(user.getUserId());
                newOrder.setUserAddress(user.getAddress());
                // Total price
                for (ShoppingCartItemVO newShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += newShoppingCartItemVO.getGoodsCount()
                            * newShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    marketException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                newOrder.setTotalPrice(priceTotal);
                // Order body field, used as description information for generating payment
                // orders, currently not connected to third-party payment interfaces, so this
                // field is temporarily set as an empty string
                String extraInfo = "";
                newOrder.setExtraInfo(extraInfo);
                // Generate order items and save order item records
                if (newOrderMapper.insertSelective(newOrder) > 0) {
                    // Generate snapshots of all order items and save them to the database
                    List<OrderItem> newOrderItems = new ArrayList<>();
                    for (ShoppingCartItemVO newShoppingCartItemVO : myShoppingCartItems) {
                        OrderItem newOrderItem = new OrderItem();
                        // Use BeanUtil to copy properties from newShoppingCartItemVO to
                        // newOrderItem
                        BeanUtil.copyProperties(newShoppingCartItemVO, newOrderItem);
                        // Using useGeneratedKeys in the insert() method of NewOrderMapper file,
                        // so orderId can be obtained
                        newOrderItem.setOrderId(newOrder.getOrderId());
                        newOrderItems.add(newOrderItem);
                    }
                    // Save to database
                    if (newOrderItemMapper.insertBatch(newOrderItems) > 0) {
                        // If all operations are successful, return the order number to allow the
                        // Controller method to jump to the order details
                        return orderNo;
                    }
                    marketException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                marketException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            marketException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        marketException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public OrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        Order newOrder = newOrderMapper.selectByOrderNo(orderNo);
        if (newOrder == null) {
            marketException.fail(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        // Verify if it is an order under the current userId, otherwise report an error
        if (!userId.equals(newOrder.getUserId())) {
            marketException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        List<OrderItem> orderItems = newOrderItemMapper.selectByOrderId(newOrder.getOrderId());
        // Get order item data
        if (CollectionUtils.isEmpty(orderItems)) {
            marketException.fail(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());
        }
        List<OrderItemVO> newOrderItemVOS = BeanUtil.copyList(orderItems, OrderItemVO.class);
        OrderDetailVO newOrderDetailVO = new OrderDetailVO();
        BeanUtil.copyProperties(newOrder, newOrderDetailVO);
        newOrderDetailVO.setOrderStatusString(OrderStatusEnum
                .getNewOrderStatusEnumByStatus(newOrderDetailVO.getOrderStatus()).getName());
        newOrderDetailVO
                .setPayTypeString(PayTypeEnum.getPayTypeEnumByType(newOrderDetailVO.getPayType()).getName());
        newOrderDetailVO.setNewOrderItemVOS(newOrderItemVOS);
        return newOrderDetailVO;
    }

    @Override
    public Order getNewOrderByOrderNo(String orderNo) {
        return newOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = newOrderMapper.getTotalNewOrders(pageUtil);
        List<Order> newOrders = newOrderMapper.findNewOrderList(pageUtil);
        List<OrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            // Data conversion: convert entity classes to VO
            orderListVOS = BeanUtil.copyList(newOrders, OrderListVO.class);
            // Set the order status display value in Chinese
            for (OrderListVO newOrderListVO : orderListVOS) {
                newOrderListVO.setOrderStatusString(OrderStatusEnum
                        .getNewOrderStatusEnumByStatus(newOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = newOrders.stream().map(Order::getOrderId)
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<OrderItem> orderItems = newOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<OrderItem>> itemByOrderIdMap = orderItems.stream()
                        .collect(groupingBy(OrderItem::getOrderId));
                for (OrderListVO newOrderListVO : orderListVOS) {
                    // Encapsulate the order item data for each order list object
                    if (itemByOrderIdMap.containsKey(newOrderListVO.getOrderId())) {
                        List<OrderItem> orderItemListTemp = itemByOrderIdMap
                                .get(newOrderListVO.getOrderId());
                        // Convert NewOrderItem object list to NewOrderItemVO object list
                        List<OrderItemVO> newOrderItemVOS = BeanUtil.copyList(orderItemListTemp,
                                OrderItemVO.class);
                        newOrderListVO.setNewOrderItemVOS(newOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String cancelOrder(String orderNo, Long userId) {
        Order newOrder = newOrderMapper.selectByOrderNo(orderNo);
        if (newOrder != null) {
            // Verify if it is an order under the current userId, otherwise report an error
            if (!userId.equals(newOrder.getUserId())) {
                marketException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
            }
            // Order status judgment
            if (newOrder.getOrderStatus().intValue() == OrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
                    || newOrder.getOrderStatus().intValue() == OrderStatusEnum.ORDER_CLOSED_BY_MALLUSER
                            .getOrderStatus()
                    || newOrder.getOrderStatus().intValue() == OrderStatusEnum.ORDER_CLOSED_BY_EXPIRED
                            .getOrderStatus()
                    || newOrder.getOrderStatus().intValue() == OrderStatusEnum.ORDER_CLOSED_BY_JUDGE
                            .getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            // Modify order status & restore inventory
            if (newOrderMapper.closeOrder(Collections.singletonList(newOrder.getOrderId()),
                    OrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0
                    && recoverStockNum(Collections.singletonList(newOrder.getOrderId()))) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        Order newOrder = newOrderMapper.selectByOrderNo(orderNo);
        if (newOrder != null) {
            // Verify if it is an order under the current userId, otherwise report an error
            if (!userId.equals(newOrder.getUserId())) {
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            // Order status judgment: Do not modify if not in the shipped status
            if (newOrder.getOrderStatus().intValue() != OrderStatusEnum.ORDER_EXPRESS
                    .getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            newOrder.setOrderStatus((byte) OrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            newOrder.setUpdateTime(new Date());
            if (newOrderMapper.updateByPrimaryKeySelective(newOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        Order newOrder = newOrderMapper.selectByOrderNo(orderNo);
        if (newOrder != null) {
            // Order status judgment: Do not modify if not in pending payment status
            if (newOrder.getOrderStatus().intValue() != OrderStatusEnum.ORDER_PRE_PAY
                    .getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            newOrder.setOrderStatus((byte) OrderStatusEnum.ORDER_PAID.getOrderStatus());
            newOrder.setPayType((byte) payType);
            newOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            newOrder.setPayTime(new Date());
            newOrder.setUpdateTime(new Date());
            if (newOrderMapper.updateByPrimaryKeySelective(newOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public List<OrderItemVO> getOrderItems(Long id) {
        Order newOrder = newOrderMapper.selectByPrimaryKey(id);
        if (newOrder != null) {
            List<OrderItem> orderItems = newOrderItemMapper
                    .selectByOrderId(newOrder.getOrderId());
            // Get order item data
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<OrderItemVO> newOrderItemVOS = BeanUtil.copyList(orderItems,
                        OrderItemVO.class);
                return newOrderItemVOS;
            }
        }
        return null;
    }

    /**
     * Recover stock
     * 
     * @param orderIds
     * @return
     */
    public Boolean recoverStockNum(List<Long> orderIds) {
        // Query corresponding order items
        List<OrderItem> newOrderItems = newOrderItemMapper.selectByOrderIds(orderIds);
        // Get corresponding product IDs and quantities, and assign them to StockNumDTO
        // objects
        List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(newOrderItems, StockNumDTO.class);
        // Execute the operation to recover stock
        int updateStockNumResult = newGoodsMapper.recoverStockNum(stockNumDTOS);
        if (updateStockNumResult < 1) {
            marketException.fail(ServiceResultEnum.CLOSE_ORDER_ERROR.getResult());
            return false;
        } else {
            return true;
        }
    }

}