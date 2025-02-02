package ltd.nft.mall.service;

import java.util.List;

import ltd.nft.mall.controller.vo.IndexCategoryVO;
import ltd.nft.mall.controller.vo.SearchPageCategoryVO;
import ltd.nft.mall.entity.GoodsCategory;
import ltd.nft.mall.util.PageQueryUtil;
import ltd.nft.mall.util.PageResult;

public interface CategoryService {
    /**
     * Backend pagination
     *
     * @param pageUtil
     * @return
     */
    PageResult getCategorisPage(PageQueryUtil pageUtil);

    String saveCategory(GoodsCategory goodsCategory);

    String updateGoodsCategory(GoodsCategory goodsCategory);

    GoodsCategory getGoodsCategoryById(Long id);

    Boolean deleteBatch(Integer[] ids);

    /**
     * Return category data (called on the homepage)
     *
     * @return
     */
    List<IndexCategoryVO> getCategoriesForIndex();

    /**
     * Return category data (called on the search page)
     *
     * @param categoryId
     * @return
     */
    SearchPageCategoryVO getCategoriesForSearch(Long categoryId);

    /**
     * Get category list based on parentId and level
     *
     * @param parentIds
     * @param categoryLevel
     * @return
     */
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel);

}
