package com.wasu.game.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.GeneratedMessage;
import com.googlecode.protobuf.format.JsonFormat;
import com.wasu.game.activeMQ.JmsTypeEnum;
import com.wasu.game.activeMQ.MQProductor;
import com.wasu.game.domain.*;
import com.wasu.game.domain.entity.User;
import com.wasu.game.module.ModuleId;
import com.wasu.game.module.PlayerCmd;
import com.wasu.game.netty.scanner.Invoker;
import com.wasu.game.netty.scanner.InvokerHoler;
import com.wasu.game.service.UserSessionService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import static io.netty.handler.codec.http.HttpHeaderNames.HOST;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    private static Logger logger = Logger.getLogger(ServerHandler.class);

    private static final String WEBSOCKET_PATH = "/websocket";

    private static final String TOKEN = "token";

    private WebSocketServerHandshaker handshaker;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof ReadTimeoutException){
            logger.info("读超时自动断开连接"+ctx.channel().remoteAddress());
            ctx.close();
        }
        else
            ctx.fireExceptionCaught(cause);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 传统http数据包接入
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest httpRequest = (FullHttpRequest) msg;
            if (!httpRequest.uri().contains(WEBSOCKET_PATH))
                return;
            handleHttpRequest(ctx, httpRequest);
            //断线重连携带token
            if (httpRequest.uri().contains(TOKEN)) {
                String wid = httpRequest.uri().split(TOKEN + "=")[1];
                Invoker invoker = InvokerHoler.getInvoker(ModuleId.Login, PlayerCmd.LINK);
                invoker.invoke(new Session(ctx), wid);
                logger.info("在线数：" + UserSessionService.getOnlinePlayerCountOfCurrentServer() + "/"+ UserSessionService.getOnlinePlayerCountOfAllServer()+ " @用户连接wid：" + wid);
            }
        }
        // WebSocket数据包接入
        else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * 断线移除会话
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Session session = new Session(ctx);
        User user = (User) session.getAttachment();
        if (user == null)
            return;
        logger.info("-------断线处理--------wid:"+user.getwId());
        //删除缓存
        Session oldSession = UserSessionService.removeSession(user.getId());
        oldSession.removeAttachment();
        //发送断线通知
        Invoker invoker = InvokerHoler.getInvoker(ModuleId.Login, PlayerCmd.Leave);
        invoker.invoke(user.getId());
    }

    /**
     * websocket链接处理
     *
     * @param ctx
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 判断是否是关闭链路指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }

        // 判断是否是Ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        // 判断是否是文本消息
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(
                    String.format("%s frame types not supported.", frame.getClass().getName()));
        }

        // 判断是否是二进制消息
        if (frame instanceof BinaryWebSocketFrame) {

        }

        // 获取消息数据
        String request = ((TextWebSocketFrame) frame).text();

        // 处理心跳响应
        if (request.indexOf("heartBeat")==0) {
//            logger.info("heartBeat:"+ctx.channel().remoteAddress().toString()+"@"+request);
            ctx.writeAndFlush(new TextWebSocketFrame(request));
            return;
        }

        Request json = null;
        try {
            logger.info("获取消息:" + request);
            json = new Request(request);

            // 调用逻辑
            handlerMessage(new Session(ctx), json);
        } catch (Exception e) {
            e.printStackTrace();
            //参数异常返回
            Response response = new Response();
            response.setStateCode(ResultCode.AGRUMENT_ERROR);
            TextWebSocketFrame error = new TextWebSocketFrame(JSONObject.toJSONString(response));
            ctx.writeAndFlush(error);
        }
    }

    /**
     * http链接处理
     *
     * @param ctx
     * @param req
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // http解析失败，返回http异常
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }

        // 只允许GET方法
        if (req.method() != GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }

        // 构造握手响应返回
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req),
                null, true);

        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * 返回应答给客户端
     *
     * @param ctx
     * @param req
     * @param res
     */
    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpHeaderUtil.setContentLength(res, res.content().readableBytes());
        }

        ChannelFuture f = ctx.channel().writeAndFlush(res);
        // 非keep-alive，关闭连接
        if (!HttpHeaderUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HOST) + WEBSOCKET_PATH;
        return "ws://" + location;
    }

    /**
     * 消息处理
     *
     * @param session
     * @param request
     */
    private void handlerMessage(Session session, Request request) {

        Response response = new Response(request);

        logger.info("rquestId:" + request.getRequestId() + "module:" + request.getModule() + "   " + "cmd：" + request.getCmd());

        // 获取命令执行器
        Invoker invoker = InvokerHoler.getInvoker(request.getModule(), request.getCmd());
        if (request.getModule() == ModuleId.Login) {
            if (invoker != null) {
                try {
                    Result<?> result = null;
                    // 假如是玩家模块传入channel参数，否则传入playerId参数

                    result = (Result<?>) invoker.invoke(session, request.getData());

                    // 判断请求是否成功
                    if (result.getResultCode() == ResultCode.SUCCESS) {
                        // 回写数据
                        Object object = result.getContent();
                        if (object != null) {
                            if (object instanceof GeneratedMessage) {
                                GeneratedMessage content = (GeneratedMessage) object;
                                response.setData(JsonFormat.printToString(content));
                            } else {
                                response.setData(JSON.toJSONString(object));
                            }
                        }
                        System.out.println(JSONObject.toJSONString(response));
                        session.write(response);
                    } else {
                        // 返回错误码
                        response.setStateCode(result.getResultCode());
                        session.write(response);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // 系统未知异常
                    response.setStateCode(ResultCode.UNKOWN_EXCEPTION);
                    session.write(response);
                }
            } else {
                // 未找到执行者
                response.setStateCode(ResultCode.NO_INVOKER);
                session.write(response);
                return;
            }
        } else {
            // gameServer处理发送消息到topick
            logger.info("MQ处理：" + JSON.toJSONString(request));
            MQProductor mQProductor = MQProductor.build();
            mQProductor.send(request, JmsTypeEnum.REQUEST);
            return;
        }
    }

    /**
     * 从消息队列获取处理结果并广播
     *
     * @param result
     */
    public static void Radiate(Result result) {
        // 判断请求是否成功
        if (result.getResultCode() == ResultCode.SUCCESS) {
            Response response = new Response(result);
            // 回写数据
            Object object = result.getContent();
            if (object != null) {
                if (object instanceof GeneratedMessage) {
                    GeneratedMessage content = (GeneratedMessage) object;
                    response.setData(JsonFormat.printToString(content));
                } else {
                    response.setData(JSON.toJSONString(object));
                }
            }
//			logger.info(JSONObject.toJSONString(response));
            for (Object userId : result.getUsers()) {
                Session session = UserSessionService.getSession((Long) userId);
                if (session == null) {
                    logger.info("session为NULL！！返回失败:userId:" + userId + " CONTENT:" + JSON.toJSONString(response));
                    continue;
                }
                session.write(response);
                logger.info("返回:userId:" + userId + " CONTENT:" + JSON.toJSONString(response));
            }
        } else {
            Response response = new Response(result);
            response.setStateCode(result.getResultCode());
            Session session =  UserSessionService.getSession((Long) result.getUsers().get(0));
            session.write(response);
            logger.info("返回:userId:" + result.getUsers().get(0) + " ERROR:" + JSON.toJSONString(response));
        }
    }
}
