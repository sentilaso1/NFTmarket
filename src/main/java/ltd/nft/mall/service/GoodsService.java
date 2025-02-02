package ltd.nft.mall.service;

import java.util.List;

import ltd.nft.mall.entity.Goods;
import ltd.nft.mall.util.PageQueryUtil;
import ltd.nft.mall.util.PageResult;

public interface GoodsService {
    /**
     * Backend pagination
     *
     * @param pageUtil
     * @return
     */
    PageResult getNewGoodsPage(PageQueryUtil pageUtil);

    /**
     * Add product
     *
     * @param goods
     * @return
     */
    String saveNewGoods(Goods goods);

    /**
     * Batch add product data
     *
     * @param newGoodsList
     * @return
     */
    void batchSaveNewGoods(List<Goods> newGoodsList);

    /**
     * Modify product information
     *
     * @param goods
     * @return
     */
    String updateNewGoods(Goods goods);

    /**
     * Get product details
     *
     * @param id
     * @return
     */
    Goods getNewGoodsById(Long id);

    /**
     * Batch modify sales status (put on/off shelves)
     *
     * @param ids
     * @return
     */
    Boolean batchUpdateSellStatus(Long[] ids, int sellStatus);

    /**
     * Product search
     *
     * @param pageUtil
     * @return
     */
    PageResult searchNewGoods(PageQueryUtil pageUtil);

}
