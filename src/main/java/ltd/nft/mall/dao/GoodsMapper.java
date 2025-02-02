package ltd.nft.mall.dao;

import org.apache.ibatis.annotations.Param;

import ltd.nft.mall.entity.Goods;
import ltd.nft.mall.entity.StockNumDTO;
import ltd.nft.mall.util.PageQueryUtil;

import java.util.List;

public interface GoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(Goods record);

    int insertSelective(Goods record);

    Goods selectByPrimaryKey(Long goodsId);

    Goods selectByCategoryIdAndName(@Param("goodsName") String goodsName, @Param("goodsCategoryId") Long goodsCategoryId);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKeyWithBLOBs(Goods record);

    int updateByPrimaryKey(Goods record);

    List<Goods> findNewGoodsList(PageQueryUtil pageUtil);

    int getTotalNewGoods(PageQueryUtil pageUtil);

    List<Goods> selectByPrimaryKeys(List<Long> goodsIds);

    List<Goods> findNewGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalNewGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("newGoodsList") List<Goods> newGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int recoverStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);

}