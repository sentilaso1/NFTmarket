package ltd.nft.mall.controller.admin;
import cn.hutool.captcha.ShearCaptcha;
import ltd.nft.mall.common.ServiceResultEnum;
import ltd.nft.mall.entity.AdminUser;
import ltd.nft.mall.service.AdminUserService;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminUserService adminUserService;

    @GetMapping({"/login"})
    public String login() {
        return "admin/login";
    }


    @GetMapping({"", "/", "/index", "/index.html"})
    public String index(HttpServletRequest request) {
        request.setAttribute("path", "index");
        return "admin/index";
    }

    @PostMapping(value = "/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode,
                        HttpSession session) {
        if (!StringUtils.hasText(verifyCode)) {
            session.setAttribute("errorMsg", "Verification code cannot be empty");
            return "admin/login";
        }
        if (!StringUtils.hasText(userName) || !StringUtils.hasText(password)) {
            session.setAttribute("errorMsg", "Username or password cannot be empty");
            return "admin/login";
        }
        ShearCaptcha shearCaptcha = (ShearCaptcha) session.getAttribute("verifyCode");
        if (shearCaptcha == null || !shearCaptcha.verify(verifyCode)) {
            session.setAttribute("errorMsg", "Incorrect verification code");
            return "admin/login";
        }
        AdminUser adminUser = adminUserService.login(userName, password);
        if (adminUser != null) {
            session.setAttribute("loginUser", adminUser.getNickName());
            session.setAttribute("loginUserId", adminUser.getAdminUserId());
            // Set session expiration time to 7200 seconds (i.e., two hours)
            // session.setMaxInactiveInterval(60 * 60 * 2);
            return "redirect:/admin/index";
        } else {
            session.setAttribute("errorMsg", "Login failed");
            return "admin/login";
        }

    }

    @GetMapping("/profile")
    public String profile(HttpServletRequest request) {
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        AdminUser adminUser = adminUserService.getUserDetailById(loginUserId);
        if (adminUser == null) {
            return "admin/login";
        }
        request.setAttribute("path", "profile");
        request.setAttribute("loginUserName", adminUser.getLoginUserName());
        request.setAttribute("nickName", adminUser.getNickName());
        return "admin/profile";
    }

    @PostMapping("/profile/password")
    @ResponseBody
    public String passwordUpdate(HttpServletRequest request, @RequestParam("originalPassword") String originalPassword,
            @RequestParam("newPassword") String newPassword) {
        if (!StringUtils.hasText(originalPassword) || !StringUtils.hasText(newPassword)) {
            return "Parameters cannot be empty";
        }
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updatePassword(loginUserId, originalPassword, newPassword)) {
            // After successful update, clear the data in the session, and the frontend
            // controls the redirection to the login page
            request.getSession().removeAttribute("loginUserId");
            request.getSession().removeAttribute("loginUser");
            request.getSession().removeAttribute("errorMsg");
            return ServiceResultEnum.SUCCESS.getResult();
        } else {
            return "Update failed";
        }
    }

    @PostMapping("/profile/name")
    @ResponseBody
    public String nameUpdate(HttpServletRequest request, @RequestParam("loginUserName") String loginUserName,
            @RequestParam("nickName") String nickName) {
        if (!StringUtils.hasText(loginUserName) || !StringUtils.hasText(nickName)) {
            return "Parameters cannot be empty";
        }
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updateName(loginUserId, loginUserName, nickName)) {
            return ServiceResultEnum.SUCCESS.getResult();
        } else {
            return "Update failed";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("loginUserId");
        request.getSession().removeAttribute("loginUser");
        request.getSession().removeAttribute("errorMsg");
        return "admin/login";
    }
}
