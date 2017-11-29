package com.wasu.game.login.service;

import com.wasu.game.domain.entity.User;
import com.wasu.game.login.dao.UserDao;
import com.wasu.game.rong.RongCloud;
import com.wasu.game.rong.models.TokenReslut;
import com.wasu.game.rong.util.GsonUtil;
import com.wasu.game.rong.util.HostType;
import com.wasu.game.rong.util.HttpUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

/**
 * Created by kale on 2016/12/13.
 */
@Component
public class RongYunServiceImpl implements RongYunservice{
    private static Logger logger = Logger.getLogger(RongYunServiceImpl.class);
    @Autowired
    private UserDao userDao;

    private static final String UTF8 = "UTF-8";
    private static String appKey = "z3v5yqkbzu8a0";//替换成您的appkey
    private static String appSecret = "atdw2GASBCG";//替换成匹配上面key的secret

    /**
     * 本地调用测试
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {


        Reader reader = null ;
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);


        System.out.println("************************User********************");
        // 获取 Token 方法
        TokenReslut userGetTokenResult = getTokenByWeb("7758258", "kale", "http://www.rongcloud.cn/images/logo.png");
        System.out.println("getToken:  " + userGetTokenResult.toString());

    }

    /**
     * 获取 Token 方法
     *
     * @param  userId:用户 Id，最大长度 64 字节.是用户在 App 中的唯一标识码，必须保证在同一个 App 内不重复，重复的用户 Id 将被当作是同一用户。（必传）
     * @param  name:用户名称，最大长度 128 字节.用来在 Push 推送时显示用户的名称.用户名称，最大长度 128 字节.用来在 Push 推送时显示用户的名称。（必传）
     * @param  portraitUri:用户头像 URI，最大长度 1024 字节.用来在 Push 推送时显示用户的头像。（必传）
     *
     * @return TokenReslut
     **/
    public static TokenReslut getTokenByWeb(String userId, String name, String portraitUri) throws Exception {
        if (userId == null) {
            throw new IllegalArgumentException("Paramer 'userId' is required");
        }

        if (name == null) {
            throw new IllegalArgumentException("Paramer 'name' is required");
        }

        if (portraitUri == null) {
            throw new IllegalArgumentException("Paramer 'portraitUri' is required");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("&userId=").append(URLEncoder.encode(userId.toString(), UTF8));
        sb.append("&name=").append(URLEncoder.encode(name.toString(), UTF8));
        sb.append("&portraitUri=").append(URLEncoder.encode(portraitUri.toString(), UTF8));
        String body = sb.toString();
        if (body.indexOf("&") == 0) {
            body = body.substring(1, body.length());
        }

        HttpURLConnection conn = HttpUtil.CreatePostHttpConnection(HostType.API, appKey, appSecret, "/user/getToken.json", "application/x-www-form-urlencoded");
        HttpUtil.setBodyParameter(body, conn);

        return (TokenReslut) GsonUtil.fromJson(HttpUtil.returnResult(conn), TokenReslut.class);
    }

    //获取token，如果有token直接返回，没有token调用接口获取token，保存后返回
    @Override
    public String getToken(User user) throws Exception{
        String result=null;
        if(!StringUtils.isEmpty(user.getToken())){
            logger.info("已存在token，直接返回token为:"+user.getToken());
            return user.getToken();
        }else{
            TokenReslut userGetTokenResult = getTokenByWeb(Long.toString(user.getId()),user.getwId(),"http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
            logger.info("获取token，返回token为:"+user.getToken());
            result=userGetTokenResult.getToken();
            user.setToken(result);
            //更新User表
            userDao.updateUser(user);
        }
        return result;
    }
}
