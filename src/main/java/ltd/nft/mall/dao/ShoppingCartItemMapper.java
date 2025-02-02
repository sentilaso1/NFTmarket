package ltd.nft.mall.dao;

import org.apache.ibatis.annotations.Param;

import ltd.nft.mall.entity.ShoppingCartItem;

import java.util.List;

public interface ShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(ShoppingCartItem record);

    int insertSelective(ShoppingCartItem record);

    ShoppingCartItem selectByPrimaryKey(Long cartItemId);

    ShoppingCartItem selectByUserIdAndGoodsId(@Param("newUserId") Long newUserId, @Param("goodsId") Long goodsId);

    List<ShoppingCartItem> selectByUserId(@Param("newUserId") Long newUserId, @Param("number") int number);

    int selectCountByUserId(Long newUserId);

    int updateByPrimaryKeySelective(ShoppingCartItem record);

    int updateByPrimaryKey(ShoppingCartItem record);

    int deleteBatch(List<Long> ids);
}