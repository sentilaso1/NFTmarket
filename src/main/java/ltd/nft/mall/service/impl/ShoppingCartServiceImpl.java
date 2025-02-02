package ltd.nft.mall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import ltd.nft.mall.common.Constants;
import ltd.nft.mall.common.ServiceResultEnum;
import ltd.nft.mall.controller.vo.ShoppingCartItemVO;
import ltd.nft.mall.dao.GoodsMapper;
import ltd.nft.mall.dao.ShoppingCartItemMapper;
import ltd.nft.mall.entity.Goods;
import ltd.nft.mall.entity.ShoppingCartItem;
import ltd.nft.mall.service.ShoppingCartService;
import ltd.nft.mall.util.BeanUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartItemMapper newShoppingCartItemMapper;

    @Autowired
    private GoodsMapper newGoodsMapper;

    @Override
    public String saveNewCartItem(ShoppingCartItem newShoppingCartItem) {
        ShoppingCartItem temp = newShoppingCartItemMapper.selectByUserIdAndGoodsId(
                newShoppingCartItem.getUserId(), newShoppingCartItem.getGoodsId());
        if (temp != null) {
            // If it already exists, modify this record
            temp.setGoodsCount(newShoppingCartItem.getGoodsCount());
            return updateNewCartItem(temp);
        }
        Goods newGoods = newGoodsMapper
                .selectByPrimaryKey(newShoppingCartItem.getGoodsId());
        // If the product is empty
        if (newGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = newShoppingCartItemMapper.selectCountByUserId(newShoppingCartItem.getUserId())
                + 1;
        // Exceeds the maximum number of single items
        if (newShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        // Exceeds the maximum total number of items
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        // Save the record
        if (newShoppingCartItemMapper.insertSelective(newShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateNewCartItem(ShoppingCartItem newShoppingCartItem) {
        ShoppingCartItem newShoppingCartItemUpdate = newShoppingCartItemMapper
                .selectByPrimaryKey(newShoppingCartItem.getCartItemId());
        if (newShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        // Exceeds the maximum number of single items
        if (newShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        // The userId of the currently logged-in account is different from the userId in
        // the cartItem to be modified, return an error
        if (!newShoppingCartItemUpdate.getUserId().equals(newShoppingCartItem.getUserId())) {
            return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
        }
        // If the values are the same, no data operation is performed
        if (newShoppingCartItem.getGoodsCount().equals(newShoppingCartItemUpdate.getGoodsCount())) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        newShoppingCartItemUpdate.setGoodsCount(newShoppingCartItem.getGoodsCount());
        newShoppingCartItemUpdate.setUpdateTime(new Date());
        // Modify the record
        if (newShoppingCartItemMapper.updateByPrimaryKeySelective(newShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public ShoppingCartItem getNewCartItemById(Long newShoppingCartItemId) {
        return newShoppingCartItemMapper.selectByPrimaryKey(newShoppingCartItemId);
    }

    @Override
    public Boolean deleteById(Long shoppingCartItemId, Long userId) {
        ShoppingCartItem newShoppingCartItem = newShoppingCartItemMapper.selectByPrimaryKey(shoppingCartItemId);
        if (newShoppingCartItem == null) {
            return false;
        }
        // Cannot be deleted if userId is different
        if (!userId.equals(newShoppingCartItem.getUserId())) {
            return false;
        }
        return newShoppingCartItemMapper.deleteByPrimaryKey(shoppingCartItemId) > 0;
    }

    @Override
    public List<ShoppingCartItemVO> getMyShoppingCartItems(Long newUserId) {
        List<ShoppingCartItemVO> newShoppingCartItemVOS = new ArrayList<>();
        List<ShoppingCartItem> newShoppingCartItems = newShoppingCartItemMapper
                .selectByUserId(newUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (!CollectionUtils.isEmpty(newShoppingCartItems)) {
            // Query product information and perform data conversion
            List<Long> newGoodsIds = newShoppingCartItems.stream()
                    .map(ShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<Goods> newGoods = newGoodsMapper.selectByPrimaryKeys(newGoodsIds);
            Map<Long, Goods> newGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(newGoods)) {
                newGoodsMap = newGoods.stream().collect(Collectors.toMap(Goods::getGoodsId,
                        Function.identity(), (entity1, entity2) -> entity1));
            }
            for (ShoppingCartItem newShoppingCartItem : newShoppingCartItems) {
                ShoppingCartItemVO newShoppingCartItemVO = new ShoppingCartItemVO();
                BeanUtil.copyProperties(newShoppingCartItem, newShoppingCartItemVO);
                if (newGoodsMap.containsKey(newShoppingCartItem.getGoodsId())) {
                    Goods newGoodsTemp = newGoodsMap
                            .get(newShoppingCartItem.getGoodsId());
                    newShoppingCartItemVO.setGoodsCoverImg(newGoodsTemp.getGoodsCoverImg());
                    String goodsName = newGoodsTemp.getGoodsName();
                    // Prevent text overflow due to string being too long
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    newShoppingCartItemVO.setGoodsName(goodsName);
                    newShoppingCartItemVO.setSellingPrice(newGoodsTemp.getSellingPrice());
                    newShoppingCartItemVOS.add(newShoppingCartItemVO);
                }
            }
        }
        return newShoppingCartItemVOS;
    }

}
