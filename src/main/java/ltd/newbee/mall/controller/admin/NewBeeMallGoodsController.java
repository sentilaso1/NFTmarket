package ltd.newbee.mall.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.NewBeeMallCategoryLevelEnum;
import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.service.NewBeeMallCategoryService;
import ltd.newbee.mall.service.NewBeeMallGoodsService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class NewBeeMallGoodsController {

    @Resource
    private NewBeeMallGoodsService newBeeMallGoodsService;
    @Resource
    private NewBeeMallCategoryService newBeeMallCategoryService;

    @GetMapping("/goods")
    public String goodsPage(HttpServletRequest request) {
        request.setAttribute("path", "newbee_mall_goods");
        return "admin/newbee_mall_goods";
    }

    @GetMapping("/goods/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        // Query all first-level categories
        List<GoodsCategory> firstLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(
                Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            // Query all second-level categories for the first item in the first-level
            // categories list
            List<GoodsCategory> secondLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(
                    Collections.singletonList(firstLevelCategories.get(0).getCategoryId()),
                    NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                // Query all third-level categories for the first item in the second-level
                // categories list
                List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(
                        Collections.singletonList(secondLevelCategories.get(0).getCategoryId()),
                        NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                request.setAttribute("path", "goods-edit");
                return "admin/newbee_mall_goods_edit";
            }
        }
        NewBeeMallException.fail("Incomplete category data");
        return null;
    }

    @GetMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request, @PathVariable("goodsId") Long goodsId) {
        request.setAttribute("path", "edit");
        NewBeeMallGoods newBeeMallGoods = newBeeMallGoodsService.getNewBeeMallGoodsById(goodsId);

        if (newBeeMallGoods.getGoodsCategoryId() > 0) {
            if (newBeeMallGoods.getGoodsCategoryId() != null || newBeeMallGoods.getGoodsCategoryId() > 0) {
                // If there is a category field, query related category data to return to the
                // front-end for three-level linkage display
                GoodsCategory currentGoodsCategory = newBeeMallCategoryService
                        .getGoodsCategoryById(newBeeMallGoods.getGoodsCategoryId());
                // The category ID stored in the product table is the ID of the third-level
                // category, if it's not a third-level category, it's erroneous data
                if (currentGoodsCategory != null && currentGoodsCategory
                        .getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
                    // Query all first-level categories
                    List<GoodsCategory> firstLevelCategories = newBeeMallCategoryService
                            .selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L),
                                    NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel());
                    // Query all third-level categories under the parentId of the current category
                    List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService
                            .selectByLevelAndParentIdsAndNumber(
                                    Collections.singletonList(currentGoodsCategory.getParentId()),
                                    NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    // Query the parent second-level category of the current third-level category
                    GoodsCategory secondCategory = newBeeMallCategoryService
                            .getGoodsCategoryById(currentGoodsCategory.getParentId());
                    if (secondCategory != null) {
                        // Query all second-level categories under the parentId of the current
                        // second-level category
                        List<GoodsCategory> secondLevelCategories = newBeeMallCategoryService
                                .selectByLevelAndParentIdsAndNumber(
                                        Collections.singletonList(secondCategory.getParentId()),
                                        NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                        // Query the parent first-level category of the current second-level category
                        GoodsCategory firstCategory = newBeeMallCategoryService
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

        if (newBeeMallGoods.getGoodsCategoryId() == 0) {
            // Query all first-level categories
            List<GoodsCategory> firstLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(
                    Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                // Query all second-level categories under the first first-level category
                List<GoodsCategory> secondLevelCategories = newBeeMallCategoryService
                        .selectByLevelAndParentIdsAndNumber(
                                Collections.singletonList(firstLevelCategories.get(0).getCategoryId()),
                                NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    // Query all third-level categories under the first second-level category
                    List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService
                            .selectByLevelAndParentIdsAndNumber(
                                    Collections.singletonList(secondLevelCategories.get(0).getCategoryId()),
                                    NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }

        request.setAttribute("goods", newBeeMallGoods);
        request.setAttribute("path", "goods-edit");
        return "admin/newbee_mall_goods_edit";
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
        return ResultGenerator.genSuccessResult(newBeeMallGoodsService.getNewBeeMallGoodsPage(pageUtil));
    }

    /**
     * Add a new product
     */
    @RequestMapping(value = "/goods/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody NewBeeMallGoods newBeeMallGoods) {
        // Check if all necessary fields are provided
        if (!StringUtils.hasText(newBeeMallGoods.getGoodsName())
                || !StringUtils.hasText(newBeeMallGoods.getGoodsIntro())
                || !StringUtils.hasText(newBeeMallGoods.getTag())
                || Objects.isNull(newBeeMallGoods.getOriginalPrice())
                || Objects.isNull(newBeeMallGoods.getGoodsCategoryId())
                || Objects.isNull(newBeeMallGoods.getSellingPrice())
                || Objects.isNull(newBeeMallGoods.getStockNum())
                || Objects.isNull(newBeeMallGoods.getGoodsSellStatus())
                || !StringUtils.hasText(newBeeMallGoods.getGoodsCoverImg())
                || !StringUtils.hasText(newBeeMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("Invalid parameters!");
        }
        // Save the new product
        String result = newBeeMallGoodsService.saveNewBeeMallGoods(newBeeMallGoods);
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
    public Result update(@RequestBody NewBeeMallGoods newBeeMallGoods) {
        // Check if all necessary fields are provided
        if (Objects.isNull(newBeeMallGoods.getGoodsId())
                || !StringUtils.hasText(newBeeMallGoods.getGoodsName())
                || !StringUtils.hasText(newBeeMallGoods.getGoodsIntro())
                || !StringUtils.hasText(newBeeMallGoods.getTag())
                || Objects.isNull(newBeeMallGoods.getOriginalPrice())
                || Objects.isNull(newBeeMallGoods.getSellingPrice())
                || Objects.isNull(newBeeMallGoods.getGoodsCategoryId())
                || Objects.isNull(newBeeMallGoods.getStockNum())
                || Objects.isNull(newBeeMallGoods.getGoodsSellStatus())
                || !StringUtils.hasText(newBeeMallGoods.getGoodsCoverImg())
                || !StringUtils.hasText(newBeeMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("Invalid parameters!");
        }
        // Update the product details
        String result = newBeeMallGoodsService.updateNewBeeMallGoods(newBeeMallGoods);
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
        NewBeeMallGoods goods = newBeeMallGoodsService.getNewBeeMallGoodsById(id);
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
        if (newBeeMallGoodsService.batchUpdateSellStatus(ids, sellStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("Update failed");
        }
    }

}