package ltd.nft.mall.service;

import java.util.List;

import ltd.nft.mall.controller.vo.IndexConfigGoodsVO;
import ltd.nft.mall.entity.IndexConfig;
import ltd.nft.mall.util.PageQueryUtil;
import ltd.nft.mall.util.PageResult;

public interface IndexConfigService {
    /**
     * Backend pagination
     *
     * @param pageUtil
     * @return
     */

    PageResult getConfigsPage(PageQueryUtil pageUtil);

    String saveIndexConfig(IndexConfig indexConfig);

    String updateIndexConfig(IndexConfig indexConfig);

    IndexConfig getIndexConfigById(Long id);

    /**
     * Return a fixed number of homepage configured product objects (called on the
     * homepage)
     *
     * @param number
     * @return
     */
    List<IndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number);

    Boolean deleteBatch(Long[] ids);
}
