package ltd.nft.mall.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import ltd.nft.mall.common.Constants;
import ltd.nft.mall.common.CategoryLevelEnum;
import ltd.nft.mall.common.marketException;
import ltd.nft.mall.common.ServiceResultEnum;
import ltd.nft.mall.entity.GoodsCategory;
import ltd.nft.mall.entity.Goods;
import ltd.nft.mall.service.CategoryService;
import ltd.nft.mall.service.GoodsService;
import ltd.nft.mall.util.PageQueryUtil;
import ltd.nft.mall.util.Result;
import ltd.nft.mall.util.ResultGenerator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class AdminGoodsController {

    @Resource
    private GoodsService newGoodsService;
    @Resource
    private CategoryService newCategoryService;

    @GetMapping("/goods")
    public String goodsPage(HttpServletRequest request) {
        request.setAttribute("path", "goods");
        return "admin/goods";
    }

    @GetMapping("/goods/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        // Query all first-level categories
        List<GoodsCategory> firstLevelCategories = newCategoryService.selectByLevelAndParentIdsAndNumber(
                Collections.singletonList(0L), CategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            // Query all second-level categories for the first item in the first-level
            // categories list
            List<GoodsCategory> secondLevelCategories = newCategoryService.selectByLevelAndParentIdsAndNumber(
                    Collections.singletonList(firstLevelCategories.get(0).getCategoryId()),
                    CategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                // Query all third-level categories for the first item in the second-level
                // categories list
                List<GoodsCategory> thirdLevelCategories = newCategoryService.selectByLevelAndParentIdsAndNumber(
                        Collections.singletonList(secondLevelCategories.get(0).getCategoryId()),
                        CategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                request.setAttribute("path", "goods-edit");
                return "admin/goods_edit";
            }
        }
        marketException.fail("Incomplete category data");
        return null;
    }

    @GetMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request, @PathVariable("goodsId") Long goodsId) {
        request.setAttribute("path", "edit");
        Goods newGoods = newGoodsService.getNewGoodsById(goodsId);

        if (newGoods.getGoodsCategoryId() > 0) {
            if (newGoods.getGoodsCategoryId() != null || newGoods.getGoodsCategoryId() > 0) {
                // If there is a category field, query related category data to return to the
                // front-end for three-level linkage display
                GoodsCategory currentGoodsCategory = newCategoryService
                        .getGoodsCategoryById(newGoods.getGoodsCategoryId());
                // The category ID stored in the product table is the ID of the third-level
                // category, if it's not a third-level category, it's erroneous data
                if (currentGoodsCategory != null && currentGoodsCategory
                        .getCategoryLevel() == CategoryLevelEnum.LEVEL_THREE.getLevel()) {
                    // Query all first-level categories
                    List<GoodsCategory> firstLevelCategories = newCategoryService
                            .selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L),
                                    CategoryLevelEnum.LEVEL_ONE.getLevel());
                    // Query all third-level categories under the parentId of the current category
                    List<GoodsCategory> thirdLevelCategories = newCategoryService
                            .selectByLevelAndParentIdsAndNumber(
                                    Collections.singletonList(currentGoodsCategory.getParentId()),
                                    CategoryLevelEnum.LEVEL_THREE.getLevel());
                    // Query the parent second-level category of the current third-level category
                    GoodsCategory secondCategory = newCategoryService
                            .getGoodsCategoryById(currentGoodsCategory.getParentId());
                    if (secondCategory != null) {
                        // Query all second-level categories under the parentId of the current
                        // second-level category
                        List<GoodsCategory> secondLevelCategories = newCategoryService
                                .selectByLevelAndParentIdsAndNumber(
                                        Collections.singletonList(secondCategory.getParentId()),
                                        CategoryLevelEnum.LEVEL_TWO.getLevel());
                        // Query the parent first-level category of the current second-level category
                        GoodsCategory firstCategory = newCategoryService
                                .getGoodsCategoryById(secondCategory.getParentId());
                        if (firstCategory != null) {
                            // After obtaining all category data, put it in the request object for the
                            // front-end to read
                            request.setAttribute("firstLevelCategories", firstLevelCategories);
                            request.setAttribute("secondLevelCategories", secondLevelCategories);
                            request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                            request.setAttribute("firstLevelCategoryId", firstCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId", secondCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId", currentGoodsCategory.getCategoryId());
                        }
                    }
                }
            }
        }

        if (newGoods.getGoodsCategoryId() == 0) {
            // Query all first-level categories
            List<GoodsCategory> firstLevelCategories = newCategoryService.selectByLevelAndParentIdsAndNumber(
                    Collections.singletonList(0L), CategoryLevelEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                // Query all second-level categories under the first first-level category
                List<GoodsCategory> secondLevelCategories = newCategoryService
                        .selectByLevelAndParentIdsAndNumber(
                                Collections.singletonList(firstLevelCategories.get(0).getCategoryId()),
                                CategoryLevelEnum.LEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    // Query all third-level categories under the first second-level category
                    List<GoodsCategory> thirdLevelCategories = newCategoryService
                            .selectByLevelAndParentIdsAndNumber(
                                    Collections.singletonList(secondLevelCategories.get(0).getCategoryId()),
                                    CategoryLevelEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }

        request.setAttribute("goods", newGoods);
        request.setAttribute("path", "goods-edit");
        return "admin/goods_edit";
    }

    /**
     * List of goods
     */
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (ObjectUtils.isEmpty(params.get("page")) || ObjectUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("Invalid parameters!");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(newGoodsService.getNewGoodsPage(pageUtil));
    }

    /**
     * Add a new product
     */
    @RequestMapping(value = "/goods/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody Goods newGoods) {
        // Check if all necessary fields are provided
        if (!StringUtils.hasText(newGoods.getGoodsName())
                || !StringUtils.hasText(newGoods.getGoodsIntro())
                || !StringUtils.hasText(newGoods.getTag())
                || Objects.isNull(newGoods.getOriginalPrice())
                || Objects.isNull(newGoods.getGoodsCategoryId())
                || Objects.isNull(newGoods.getSellingPrice())
                || Objects.isNull(newGoods.getStockNum())
                || Objects.isNull(newGoods.getGoodsSellStatus())
                || !StringUtils.hasText(newGoods.getGoodsCoverImg())
                || !StringUtils.hasText(newGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("Invalid parameters!");
        }
        // Save the new product
        String result = newGoodsService.saveNewGoods(newGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * Update a product
     */
    @RequestMapping(value = "/goods/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody Goods newGoods) {
        // Check if all necessary fields are provided
        if (Objects.isNull(newGoods.getGoodsId())
                || !StringUtils.hasText(newGoods.getGoodsName())
                || !StringUtils.hasText(newGoods.getGoodsIntro())
                || !StringUtils.hasText(newGoods.getTag())
                || Objects.isNull(newGoods.getOriginalPrice())
                || Objects.isNull(newGoods.getSellingPrice())
                || Objects.isNull(newGoods.getGoodsCategoryId())
                || Objects.isNull(newGoods.getStockNum())
                || Objects.isNull(newGoods.getGoodsSellStatus())
                || !StringUtils.hasText(newGoods.getGoodsCoverImg())
                || !StringUtils.hasText(newGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("Invalid parameters!");
        }
        // Update the product details
        String result = newGoodsService.updateNewGoods(newGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * Get product details
     */
    @GetMapping("/goods/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        // Fetch product details by ID
        Goods goods = newGoodsService.getNewGoodsById(id);
        // Return product details
        return ResultGenerator.genSuccessResult(goods);
    }

    /**
     * Batch update sales status
     */
    @RequestMapping(value = "/goods/status/{sellStatus}", method = RequestMethod.PUT)
    @ResponseBody
    public Result updateSellStatus(@RequestBody Long[] ids, @PathVariable("sellStatus") int sellStatus) {
        // Check if the list of IDs is empty
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("Invalid parameters!");
        }

        // Validate if the provided status is either 'up' or 'down'
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("Invalid status!");
        }

        // Attempt to batch update the sales status
        if (newGoodsService.batchUpdateSellStatus(ids, sellStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("Update failed");
        }
    }

}