package ltd.newbee.mall.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.dao.NewBeeMallShoppingCartItemMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class NewBeeMallCartNumberInterceptor implements HandlerInterceptor {

    @Autowired
    private NewBeeMallShoppingCartItemMapper newBeeMallShoppingCartItemMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        // The quantity in the shopping cart will change, but the data in the session is
        // not modified in these interfaces, so handle it uniformly here
        if (null != request.getSession()
                && null != request.getSession().getAttribute(Constants.MALL_USER_SESSION_KEY)) {
            // If currently logged in, query the database and set the quantity in the
            // shopping cart
            NewBeeMallUserVO newBeeMallUserVO = (NewBeeMallUserVO) request.getSession()
                    .getAttribute(Constants.MALL_USER_SESSION_KEY);
            // Set the quantity in the shopping cart
            newBeeMallUserVO.setShopCartItemCount(
                    newBeeMallShoppingCartItemMapper.selectCountByUserId(newBeeMallUserVO.getUserId()));
            request.getSession().setAttribute(Constants.MALL_USER_SESSION_KEY, newBeeMallUserVO);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
