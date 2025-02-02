package ltd.nft.mall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import ltd.nft.mall.common.Constants;
import ltd.nft.mall.common.CategoryLevelEnum;
import ltd.nft.mall.common.ServiceResultEnum;
import ltd.nft.mall.controller.vo.IndexCategoryVO;
import ltd.nft.mall.controller.vo.SearchPageCategoryVO;
import ltd.nft.mall.controller.vo.SecondLevelCategoryVO;
import ltd.nft.mall.controller.vo.ThirdLevelCategoryVO;
import ltd.nft.mall.dao.GoodsCategoryMapper;
import ltd.nft.mall.entity.GoodsCategory;
import ltd.nft.mall.service.CategoryService;
import ltd.nft.mall.util.BeanUtil;
import ltd.nft.mall.util.PageQueryUtil;
import ltd.nft.mall.util.PageResult;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public PageResult getCategorisPage(PageQueryUtil pageUtil) {
        List<GoodsCategory> goodsCategories = goodsCategoryMapper.findGoodsCategoryList(pageUtil);
        int total = goodsCategoryMapper.getTotalGoodsCategories(pageUtil);
        PageResult pageResult = new PageResult(goodsCategories, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveCategory(GoodsCategory goodsCategory) {
        GoodsCategory temp = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp != null) {
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        if (goodsCategoryMapper.insertSelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateGoodsCategory(GoodsCategory goodsCategory) {
        GoodsCategory temp = goodsCategoryMapper.selectByPrimaryKey(goodsCategory.getCategoryId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        GoodsCategory temp2 = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp2 != null && !temp2.getCategoryId().equals(goodsCategory.getCategoryId())) {
            // If the name is the same and the ID is different, do not continue to modify
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        goodsCategory.setUpdateTime(new Date());
        if (goodsCategoryMapper.updateByPrimaryKeySelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public GoodsCategory getGoodsCategoryById(Long id) {
        return goodsCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return false;
        }
        // Delete category data
        return goodsCategoryMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<IndexCategoryVO> getCategoriesForIndex() {
        List<IndexCategoryVO> newIndexCategoryVOS = new ArrayList<>();
        // Get a fixed number of first-level categories
        List<GoodsCategory> firstLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(
                Collections.singletonList(0L), CategoryLevelEnum.LEVEL_ONE.getLevel(),
                Constants.INDEX_CATEGORY_NUMBER);
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            List<Long> firstLevelCategoryIds = firstLevelCategories.stream().map(GoodsCategory::getCategoryId)
                    .collect(Collectors.toList());
            // Get second-level categories
            List<GoodsCategory> secondLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(
                    firstLevelCategoryIds, CategoryLevelEnum.LEVEL_TWO.getLevel(), 0);
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                List<Long> secondLevelCategoryIds = secondLevelCategories.stream().map(GoodsCategory::getCategoryId)
                        .collect(Collectors.toList());
                // Get third-level categories
                List<GoodsCategory> thirdLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(
                        secondLevelCategoryIds, CategoryLevelEnum.LEVEL_THREE.getLevel(), 0);
                if (!CollectionUtils.isEmpty(thirdLevelCategories)) {
                    // Group third-level categories by parentId
                    Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdLevelCategories.stream()
                            .collect(groupingBy(GoodsCategory::getParentId));
                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    // Process second-level categories
                    for (GoodsCategory secondLevelCategory : secondLevelCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtil.copyProperties(secondLevelCategory, secondLevelCategoryVO);
                        // If the second-level category has data, add it to the secondLevelCategoryVOS
                        // object
                        if (thirdLevelCategoryMap.containsKey(secondLevelCategory.getCategoryId())) {
                            // Retrieve the list of third-level categories from the group based on the id of
                            // the second-level category
                            List<GoodsCategory> tempGoodsCategories = thirdLevelCategoryMap
                                    .get(secondLevelCategory.getCategoryId());
                            secondLevelCategoryVO.setThirdLevelCategoryVOS(
                                    (BeanUtil.copyList(tempGoodsCategories, ThirdLevelCategoryVO.class)));
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }
                    // Process first-level categories
                    if (!CollectionUtils.isEmpty(secondLevelCategoryVOS)) {
                        // Group second-level categories by parentId
                        Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap = secondLevelCategoryVOS
                                .stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));
                        for (GoodsCategory firstCategory : firstLevelCategories) {
                            IndexCategoryVO newIndexCategoryVO = new IndexCategoryVO();
                            BeanUtil.copyProperties(firstCategory, newIndexCategoryVO);
                            // If the first-level category has data, add it to the
                            // newIndexCategoryVOS object
                            if (secondLevelCategoryVOMap.containsKey(firstCategory.getCategoryId())) {
                                // Retrieve the list of second-level categories from the group based on the id
                                // of the first-level category
                                List<SecondLevelCategoryVO> tempGoodsCategories = secondLevelCategoryVOMap
                                        .get(firstCategory.getCategoryId());
                                newIndexCategoryVO.setSecondLevelCategoryVOS(tempGoodsCategories);
                                newIndexCategoryVOS.add(newIndexCategoryVO);
                            }
                        }
                    }
                }
            }
            return newIndexCategoryVOS;
        } else {
            return null;
        }
    }

    @Override
    public SearchPageCategoryVO getCategoriesForSearch(Long categoryId) {
        SearchPageCategoryVO searchPageCategoryVO = new SearchPageCategoryVO();
        GoodsCategory thirdLevelGoodsCategory = goodsCategoryMapper.selectByPrimaryKey(categoryId);
        if (thirdLevelGoodsCategory != null
                && thirdLevelGoodsCategory.getCategoryLevel() == CategoryLevelEnum.LEVEL_THREE.getLevel()) {
            // Get the second-level category of the current third-level category
            GoodsCategory secondLevelGoodsCategory = goodsCategoryMapper
                    .selectByPrimaryKey(thirdLevelGoodsCategory.getParentId());
            if (secondLevelGoodsCategory != null && secondLevelGoodsCategory
                    .getCategoryLevel() == CategoryLevelEnum.LEVEL_TWO.getLevel()) {
                // Get the list of third-level categories under the current second-level
                // category
                List<GoodsCategory> thirdLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(
                        Collections.singletonList(secondLevelGoodsCategory.getCategoryId()),
                        CategoryLevelEnum.LEVEL_THREE.getLevel(), Constants.SEARCH_CATEGORY_NUMBER);
                searchPageCategoryVO.setCurrentCategoryName(thirdLevelGoodsCategory.getCategoryName());
                searchPageCategoryVO.setSecondLevelCategoryName(secondLevelGoodsCategory.getCategoryName());
                searchPageCategoryVO.setThirdLevelCategoryList(thirdLevelCategories);
                return searchPageCategoryVO;
            }
        }
        return null;
    }

    @Override
    public List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel) {
        return goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(parentIds, categoryLevel, 0); // 0 means query all
    }
}
