package ltd.newbee.mall.service;

import java.util.List;

import ltd.newbee.mall.controller.vo.NewBeeMallIndexCarouselVO;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

public interface NewBeeMallCarouselService {
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

    List<NewBeeMallIndexCarouselVO> getCarouselsForIndex(int number);
}
