package ltd.nft.mall.service;

import java.util.List;

import ltd.nft.mall.controller.vo.IndexCarouselVO;
import ltd.nft.mall.entity.Carousel;
import ltd.nft.mall.util.PageQueryUtil;
import ltd.nft.mall.util.PageResult;

public interface CarouselService {
    /**
     * Backend pagination
     *
     * @param pageUtil
     * @return
     */

    PageResult getCarouselPage(PageQueryUtil pageUtil);

    String saveCarousel(Carousel carousel);

    String updateCarousel(Carousel carousel);

    Carousel getCarouselById(Integer id);

    Boolean deleteBatch(Integer[] ids);

    /**
     * Return a fixed number of carousel objects (called on the homepage)
     *
     * @param number
     * @return
     */

    List<IndexCarouselVO> getCarouselsForIndex(int number);
}
