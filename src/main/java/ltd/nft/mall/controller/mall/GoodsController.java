package ltd.nft.mall.controller.mall;

import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import ltd.nft.mall.common.Constants;
import ltd.nft.mall.common.marketException;
import ltd.nft.mall.common.ServiceResultEnum;
import ltd.nft.mall.controller.vo.GoodsDetailVO;
import ltd.nft.mall.controller.vo.SearchPageCategoryVO;
import ltd.nft.mall.entity.Goods;
import ltd.nft.mall.service.CategoryService;
import ltd.nft.mall.service.GoodsService;
import ltd.nft.mall.util.BeanUtil;
import ltd.nft.mall.util.PageQueryUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class GoodsController {

    @Resource
    private GoodsService newGoodsService;
    @Resource
    private CategoryService newCategoryService;

    @GetMapping({ "/search", "/search.html" })
    public String searchPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        // Package category data
        if (params.containsKey("goodsCategoryId") && StringUtils.hasText(params.get("goodsCategoryId") + "")) {
            Long categoryId = Long.valueOf(params.get("goodsCategoryId") + "");
            SearchPageCategoryVO searchPageCategoryVO = newCategoryService.getCategoriesForSearch(categoryId);
            if (searchPageCategoryVO != null) {
                request.setAttribute("goodsCategoryId", categoryId);
                request.setAttribute("searchPageCategoryVO", searchPageCategoryVO);
            }
        }
        // Package parameters for frontend display
        if (params.containsKey("orderBy") && StringUtils.hasText(params.get("orderBy") + "")) {
            request.setAttribute("orderBy", params.get("orderBy") + "");
        }
        String keyword = "";
        // Filter the keyword by removing spaces
        if (params.containsKey("keyword") && StringUtils.hasText((params.get("keyword") + "").trim())) {
            keyword = params.get("keyword") + "";
        }
        request.setAttribute("keyword", keyword);
        params.put("keyword", keyword);
        // Search for products that are in the "on-sale" status
        params.put("goodsSellStatus", Constants.SELL_STATUS_UP);
        // Package product data
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("pageResult", newGoodsService.searchNewGoods(pageUtil));
        return "mall/search";
    }

    @GetMapping("/goods/detail/{goodsId}")
    public String detailPage(@PathVariable("goodsId") Long goodsId, HttpServletRequest request) {
        if (goodsId < 1) {
            marketException.fail("Parameter exception");
        }
        Goods goods = newGoodsService.getNewGoodsById(goodsId);
        if (Constants.SELL_STATUS_UP != goods.getGoodsSellStatus()) {
            marketException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        GoodsDetailVO goodsDetailVO = new GoodsDetailVO();
        BeanUtil.copyProperties(goods, goodsDetailVO);
        goodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        request.setAttribute("goodsDetail", goodsDetailVO);
        return "mall/detail";
    }

}
