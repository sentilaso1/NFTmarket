package ltd.nft.mall.service;

import java.util.List;

import ltd.nft.mall.controller.vo.OrderDetailVO;
import ltd.nft.mall.controller.vo.OrderItemVO;
import ltd.nft.mall.controller.vo.ShoppingCartItemVO;
import ltd.nft.mall.controller.vo.MallUserVO;
import ltd.nft.mall.entity.Order;
import ltd.nft.mall.util.PageQueryUtil;
import ltd.nft.mall.util.PageResult;

public interface OrderService {
    /**
     * Backend pagination
     *
     * @param pageUtil
     * @return
     */
    PageResult getNewOrdersPage(PageQueryUtil pageUtil);

    /**
     * Update order information
     *
     * @param newOrder
     * @return
     */
    String updateOrderInfo(Order newOrder);

    /**
     * Allocate goods
     *
     * @param ids
     * @return
     */
    String checkDone(Long[] ids);

    /**
     * Ship goods
     *
     * @param ids
     * @return
     */
    String checkOut(Long[] ids);

    /**
     * Close orders
     *
     * @param ids
     * @return
     */
    String closeOrder(Long[] ids);

    /**
     * Save order
     *
     * @param user
     * @param myShoppingCartItems
     * @return
     */
    String saveOrder(MallUserVO user, List<ShoppingCartItemVO> myShoppingCartItems);

    /**
     * Get order details
     *
     * @param orderNo
     * @param userId
     * @return
     */
    OrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * Get order details
     *
     * @param orderNo
     * @return
     */
    Order getNewOrderByOrderNo(String orderNo);

    /**
     * My orders list
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyOrders(PageQueryUtil pageUtil);

    /**
     * Manually cancel order
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String cancelOrder(String orderNo, Long userId);

    /**
     * Confirm receipt
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String finishOrder(String orderNo, Long userId);

    String paySuccess(String orderNo, int payType);

    List<OrderItemVO> getOrderItems(Long id);
}
