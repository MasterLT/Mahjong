package com.wasu.game.login.handler;


import com.alibaba.fastjson.JSONObject;

import com.wasu.game.domain.Result;
import com.wasu.game.domain.ResultCode;
import com.wasu.game.domain.Session;
import com.wasu.game.domain.response.ConfigResponse;
import com.wasu.game.domain.response.UserResponse;
import com.wasu.game.exception.ErrorCodeException;
import com.wasu.game.login.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 玩家模块
 *
 * @author -琴兽-
 */
@Component
public class PlayerHandlerImpl implements PlayerHandler {

    @Autowired
    private LoginService loginService;

    @Override
    public void leave(Long id) {
        try {
            loginService.leave(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result<UserResponse> weixinLogin(Session session, JSONObject data) {
        UserResponse result = null;
        try {
            //去出对象
            String wId = data.getString("wId");
            String face = data.getString("face");
            String name = data.getString("name");
            String gender = data.getString("gender");

            //参数判空
            if (StringUtils.isEmpty(wId)) {
                return Result.ERROR(ResultCode.PLAYERNAME_NULL);
            }

            //执行业务
            result = loginService.weixinLogin(session, wId, face, name, gender);
        } catch (ErrorCodeException e) {
            return Result.ERROR(e.getErrorCode());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.ERROR(ResultCode.UNKOWN_EXCEPTION);
        }
        return Result.SUCCESS(result);
    }

    @Override
    public void link(Session session, String wid) {
        loginService.link(session,wid);
    }

    @Override
    public Result<List<Map<String,String>>> config(Session session, JSONObject data) {
        List<Map<String,String>> result = null;
        try {
            //执行业务
            result = loginService.config();
        } catch (ErrorCodeException e) {
            return Result.ERROR(e.getErrorCode());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.ERROR(ResultCode.UNKOWN_EXCEPTION);
        }
        return Result.SUCCESS(result);
    }

    @Override
    public Result<Integer> authen(Session session, JSONObject data) {
        Integer result;
        try {
            Integer id = data.getInteger("id");
            String realName = data.getString("realName");
            String idNum = data.getString("idNum");
            //执行业务
            result = loginService.authen(id,realName,idNum);
        } catch (ErrorCodeException e) {
            return Result.ERROR(e.getErrorCode());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.ERROR(ResultCode.UNKOWN_EXCEPTION);
        }
        return Result.SUCCESS(result);
    }

}
