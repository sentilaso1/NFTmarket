package ltd.nft.mall.controller.common;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import ltd.nft.mall.common.Constants;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class CommonController {

    @GetMapping("/common/kaptcha")
    public void defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws Exception {
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/png");

        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(150, 30, 4, 2);

        // Store the captcha in the session
        httpServletRequest.getSession().setAttribute("verifyCode", shearCaptcha);

        // Output the image stream
        shearCaptcha.write(httpServletResponse.getOutputStream());
    }

    @GetMapping("/common/mall/kaptcha")
    public void mallKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws Exception {
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/png");

        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(110, 40, 4, 2);

        // Store the captcha in the session
        httpServletRequest.getSession().setAttribute(Constants.MALL_VERIFY_CODE_KEY, shearCaptcha);

        // Output the image stream
        shearCaptcha.write(httpServletResponse.getOutputStream());
    }

}
