package ltd.nft.mall.dao;

import org.apache.ibatis.annotations.Param;

import ltd.nft.mall.entity.Order;
import ltd.nft.mall.util.PageQueryUtil;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Long orderId);

    Order selectByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    List<Order> findNewOrderList(PageQueryUtil pageUtil);

    int getTotalNewOrders(PageQueryUtil pageUtil);

    List<Order> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    int checkDone(@Param("orderIds") List<Long> asList);
}