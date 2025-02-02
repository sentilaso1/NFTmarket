package ltd.nft.mall.service;

import java.util.List;

import ltd.nft.mall.controller.vo.ShoppingCartItemVO;
import ltd.nft.mall.entity.ShoppingCartItem;

public interface ShoppingCartService {

    /**
     * Save product to shopping cart
     *
     * @param newShoppingCartItem
     * @return
     */
    String saveNewCartItem(ShoppingCartItem newShoppingCartItem);

    /**
     * Modify attributes in the shopping cart
     *
     * @param newShoppingCartItem
     * @return
     */
    String updateNewCartItem(ShoppingCartItem newShoppingCartItem);

    /**
     * Get shopping item details
     *
     * @param newShoppingCartItemId
     * @return
     */
    ShoppingCartItem getNewCartItemById(Long newShoppingCartItemId);

    /**
     * Delete product from the shopping cart
     *
     *
     * @param shoppingCartItemId
     * @param userId
     * @return
     */
    Boolean deleteById(Long shoppingCartItemId, Long userId);

    /**
     * Get the list data in my shopping cart
     *
     * @param newUserId
     * @return
     */
    List<ShoppingCartItemVO> getMyShoppingCartItems(Long newUserId);

}
