package ltd.nft.mall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import ltd.nft.mall.common.CategoryLevelEnum;
import ltd.nft.mall.common.marketException;
import ltd.nft.mall.common.ServiceResultEnum;
import ltd.nft.mall.controller.vo.SearchGoodsVO;
import ltd.nft.mall.dao.GoodsCategoryMapper;
import ltd.nft.mall.dao.GoodsMapper;
import ltd.nft.mall.entity.GoodsCategory;
import ltd.nft.mall.entity.Goods;
import ltd.nft.mall.service.GoodsService;
import ltd.nft.mall.util.BeanUtil;
import ltd.nft.mall.util.marketUtils;
import ltd.nft.mall.util.PageQueryUtil;
import ltd.nft.mall.util.PageResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public PageResult getNewGoodsPage(PageQueryUtil pageUtil) {
        List<Goods> goodsList = goodsMapper.findNewGoodsList(pageUtil);
        int total = goodsMapper.getTotalNewGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveNewGoods(Goods goods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // If the category does not exist or is not a third-level category, then this
        // parameter field is invalid
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != CategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        if (goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId()) != null) {
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        goods.setGoodsName(marketUtils.cleanString(goods.getGoodsName()));
        goods.setGoodsIntro(marketUtils.cleanString(goods.getGoodsIntro()));
        goods.setTag(marketUtils.cleanString(goods.getTag()));
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveNewGoods(List<Goods> newGoodsList) {
        if (!CollectionUtils.isEmpty(newGoodsList)) {
            goodsMapper.batchInsert(newGoodsList);
        }
    }

    @Override
    public String updateNewGoods(Goods goods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // If the category does not exist or is not a third-level category, then this
        // parameter field is invalid
        if (goodsCategory == null
                || goodsCategory.getCategoryLevel().intValue() != CategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        Goods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        Goods temp2 = goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId());
        if (temp2 != null && !temp2.getGoodsId().equals(goods.getGoodsId())) {
            // The name and category ID are the same, but the IDs are different, so
            // modification cannot continue
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        goods.setGoodsName(marketUtils.cleanString(goods.getGoodsName()));
        goods.setGoodsIntro(marketUtils.cleanString(goods.getGoodsIntro()));
        goods.setTag(marketUtils.cleanString(goods.getTag()));
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Goods getNewGoodsById(Long id) {
        Goods newGoods = goodsMapper.selectByPrimaryKey(id);
        if (newGoods == null) {
            marketException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        return newGoods;
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchNewGoods(PageQueryUtil pageUtil) {
        List<Goods> goodsList = goodsMapper.findNewGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalNewGoodsBySearch(pageUtil);
        List<SearchGoodsVO> newSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            newSearchGoodsVOS = BeanUtil.copyList(goodsList, SearchGoodsVO.class);
            for (SearchGoodsVO newSearchGoodsVO : newSearchGoodsVOS) {
                String goodsName = newSearchGoodsVO.getGoodsName();
                String goodsIntro = newSearchGoodsVO.getGoodsIntro();
                // Prevent text overflow due to string being too long
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    newSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    newSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(newSearchGoodsVOS, total, pageUtil.getLimit(),
                pageUtil.getPage());
        return pageResult;
    }

}
