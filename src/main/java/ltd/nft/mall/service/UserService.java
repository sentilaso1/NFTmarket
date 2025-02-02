package ltd.nft.mall.service;

import javax.servlet.http.HttpSession;

import ltd.nft.mall.controller.vo.MallUserVO;
import ltd.nft.mall.entity.MallUser;
import ltd.nft.mall.util.PageQueryUtil;
import ltd.nft.mall.util.PageResult;

public interface UserService {
    /**
     * Backend pagination
     *
     * @param pageUtil
     * @return
     */
    PageResult getNewUsersPage(PageQueryUtil pageUtil);

    /**
     * User registration
     *
     * @param loginName
     * @param password
     * @return
     */
    String register(String loginName, String password);

    /**
     * Login
     *
     * @param loginName
     * @param passwordMD5
     * @param httpSession
     * @return
     */
    String login(String loginName, String passwordMD5, HttpSession httpSession);

    /**
     * Modify user information and return the latest user information
     *
     * @param mallUser
     * @return
     */
    MallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession);

    /**
     * Disable and re-enable users (0 - not locked, 1 - locked)
     *
     * @param ids
     * @param lockStatus
     * @return
     */
    Boolean lockUsers(Integer[] ids, int lockStatus);
}
