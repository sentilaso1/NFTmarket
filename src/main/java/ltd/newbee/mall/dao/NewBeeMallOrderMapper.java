package ltd.newbee.mall.dao;

import org.apache.ibatis.annotations.Param;

import ltd.newbee.mall.entity.NewBeeMallOrder;
import ltd.newbee.mall.util.PageQueryUtil;

import java.util.List;

public interface NewBeeMallOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(NewBeeMallOrder record);

    int insertSelective(NewBeeMallOrder record);

    NewBeeMallOrder selectByPrimaryKey(Long orderId);

    NewBeeMallOrder selectByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(NewBeeMallOrder record);

    int updateByPrimaryKey(NewBeeMallOrder record);

    List<NewBeeMallOrder> findNewBeeMallOrderList(PageQueryUtil pageUtil);

    int getTotalNewBeeMallOrders(PageQueryUtil pageUtil);

    List<NewBeeMallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    int checkDone(@Param("orderIds") List<Long> asList);
}