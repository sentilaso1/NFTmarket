package ltd.nft.mall.controller.mall;

import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import ltd.nft.mall.common.Constants;
import ltd.nft.mall.common.IndexConfigTypeEnum;
import ltd.nft.mall.common.marketException;
import ltd.nft.mall.controller.vo.IndexCarouselVO;
import ltd.nft.mall.controller.vo.IndexCategoryVO;
import ltd.nft.mall.controller.vo.IndexConfigGoodsVO;
import ltd.nft.mall.service.CarouselService;
import ltd.nft.mall.service.CategoryService;
import ltd.nft.mall.service.IndexConfigService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Resource
    private CarouselService newCarouselService;

    @Resource
    private IndexConfigService newIndexConfigService;

    @Resource
    private CategoryService newCategoryService;

    @GetMapping({ "/index", "/", "/index.html" })
    public String indexPage(HttpServletRequest request) {
        List<IndexCategoryVO> categories = newCategoryService.getCategoriesForIndex();
        if (CollectionUtils.isEmpty(categories)) {
            marketException.fail("Category data is incomplete");
        }
        List<IndexCarouselVO> carousels = newCarouselService
                .getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<IndexConfigGoodsVO> hotGoodses = newIndexConfigService.getConfigGoodsesForIndex(
                IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<IndexConfigGoodsVO> newGoodses = newIndexConfigService.getConfigGoodsesForIndex(
                IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<IndexConfigGoodsVO> recommendGoodses = newIndexConfigService.getConfigGoodsesForIndex(
                IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        request.setAttribute("categories", categories); // "Category data"
        request.setAttribute("carousels", carousels); // "Carousel images"
        request.setAttribute("hotGoodses", hotGoodses); // "Hot selling products"
        request.setAttribute("newGoodses", newGoodses); // "New products"
        request.setAttribute("recommendGoodses", recommendGoodses); // "Recommended products"
        return "mall/index";
    }

}
