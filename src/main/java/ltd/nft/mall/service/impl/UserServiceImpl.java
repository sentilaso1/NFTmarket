package ltd.nft.mall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ltd.nft.mall.common.Constants;
import ltd.nft.mall.common.ServiceResultEnum;
import ltd.nft.mall.controller.vo.MallUserVO;
import ltd.nft.mall.dao.MallUserMapper;
import ltd.nft.mall.entity.MallUser;
import ltd.nft.mall.service.UserService;
import ltd.nft.mall.util.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private MallUserMapper mallUserMapper;

    @Override
    public PageResult getNewUsersPage(PageQueryUtil pageUtil) {
        List<MallUser> mallUsers = mallUserMapper.findMallUserList(pageUtil);
        int total = mallUserMapper.getTotalMallUsers(pageUtil);
        PageResult pageResult = new PageResult(mallUsers, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String register(String loginName, String password) {
        if (mallUserMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        MallUser registerUser = new MallUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        if (mallUserMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String login(String loginName, String passwordMD5, HttpSession httpSession) {
        MallUser user = mallUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        if (user != null && httpSession != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }
            // Nickname is too long, which affects the display on the page
            if (user.getNickName() != null && user.getNickName().length() > 7) {
                String tempNickName = user.getNickName().substring(0, 7) + "..";
                user.setNickName(tempNickName);
            }
            MallUserVO newUserVO = new MallUserVO();
            BeanUtil.copyProperties(user, newUserVO);
            // Set the number of items in the shopping cart
            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, newUserVO);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public MallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession) {
        MallUserVO userTemp = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallUser userFromDB = mallUserMapper.selectByPrimaryKey(userTemp.getUserId());
        if (userFromDB != null) {
            if (StringUtils.hasText(mallUser.getNickName())) {
                userFromDB.setNickName(marketUtils.cleanString(mallUser.getNickName()));
            }
            if (StringUtils.hasText(mallUser.getAddress())) {
                userFromDB.setAddress(marketUtils.cleanString(mallUser.getAddress()));
            }
            if (StringUtils.hasText(mallUser.getIntroduceSign())) {
                userFromDB.setIntroduceSign(marketUtils.cleanString(mallUser.getIntroduceSign()));
            }
            if (mallUserMapper.updateByPrimaryKeySelective(userFromDB) > 0) {
                MallUserVO newUserVO = new MallUserVO();
                BeanUtil.copyProperties(userFromDB, newUserVO);
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, newUserVO);
                return newUserVO;
            }
        }
        return null;
    }

    @Override
    public Boolean lockUsers(Integer[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return mallUserMapper.lockUserBatch(ids, lockStatus) > 0;
    }
}
