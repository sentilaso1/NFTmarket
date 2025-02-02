package ltd.nft.mall.controller.mall;

import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import ltd.nft.mall.common.Constants;
import ltd.nft.mall.common.marketException;
import ltd.nft.mall.common.ServiceResultEnum;
import ltd.nft.mall.controller.vo.ShoppingCartItemVO;
import ltd.nft.mall.controller.vo.MallUserVO;
import ltd.nft.mall.entity.ShoppingCartItem;
import ltd.nft.mall.service.ShoppingCartService;
import ltd.nft.mall.util.Result;
import ltd.nft.mall.util.ResultGenerator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Resource
    private ShoppingCartService newShoppingCartService;

    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request,
            HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<ShoppingCartItemVO> myShoppingCartItems = newShoppingCartService
                .getMyShoppingCartItems(user.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)) {
            // Total number of shopping items
            itemsTotal = myShoppingCartItems.stream().mapToInt(ShoppingCartItemVO::getGoodsCount).sum();
            if (itemsTotal < 1) {
                // Shopping items cannot be empty
                marketException.fail("Shopping items cannot be empty");
            }
            // Total price
            for (ShoppingCartItemVO newShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += newShoppingCartItemVO.getGoodsCount()
                        * newShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                // Shopping item price is abnormal
                marketException.fail("Abnormal shopping item price");
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }

    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveNewShoppingCartItem(@RequestBody ShoppingCartItem newShoppingCartItem,
            HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        newShoppingCartItem.setUserId(user.getUserId());
        String saveResult = newShoppingCartService.saveNewCartItem(newShoppingCartItem);
        // Addition successful
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        // Addition failed
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateNewShoppingCartItem(@RequestBody ShoppingCartItem newShoppingCartItem,
            HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        newShoppingCartItem.setUserId(user.getUserId());
        String updateResult = newShoppingCartService.updateNewCartItem(newShoppingCartItem);
        // Modification successful
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        // Modification failed
        return ResultGenerator.genFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{newShoppingCartItemId}")
    @ResponseBody
    public Result updateNewShoppingCartItem(
            @PathVariable("newShoppingCartItemId") Long newShoppingCartItemId,
            HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = newShoppingCartService.deleteById(newShoppingCartItemId, user.getUserId());
        // Deletion successful
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        // Deletion failed
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request,
            HttpSession httpSession) {
        int priceTotal = 0;
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<ShoppingCartItemVO> myShoppingCartItems = newShoppingCartService
                .getMyShoppingCartItems(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            // No data, do not redirect to the checkout page
            return "/shop-cart";
        } else {
            // Total price
            for (ShoppingCartItemVO newShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += newShoppingCartItemVO.getGoodsCount()
                        * newShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                marketException.fail("Abnormal shopping item price");
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/order-settle";
    }

}
