package ltd.nft.mall.dao;

import org.apache.ibatis.annotations.Param;

import ltd.nft.mall.entity.OrderItem;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Long orderItemId);

    /**
     * Get the order item list based on the order id
     *
     * @param orderId
     * @return
     */
    List<OrderItem> selectByOrderId(Long orderId);

    /**
     * Get the order item list based on the order ids
     *
     * @param orderIds
     * @return
     */
    List<OrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * Batch insert order item data
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<OrderItem> orderItems);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);
}