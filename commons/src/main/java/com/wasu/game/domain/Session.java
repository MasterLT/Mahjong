package com.wasu.game.domain;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public class Session {
	/**
     * id
     */
    private Long roomId;

    /**
     * 连接上下文
     */
    private ChannelHandlerContext ctx;
    
//    private Object attachment;
    /**
	 * 绑定对象key
	 */
	public static AttributeKey<Object> ATTACHMENT_KEY  = AttributeKey.valueOf("ATTACHMENT_KEY");

    private volatile boolean isClosed = false;

    /**
     * 是否是被踢下线
     */
    private boolean isKick = false;

    public Session(Long roomId, ChannelHandlerContext ctx) {
        this.roomId = roomId;
        this.ctx = ctx;
    }

    public Session(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId=roomId;
    }
    public Channel getChannel() {
        return ctx.channel();
    }

    /**
     * 往连接写入数据
     *
     * @param res
     */
    public void write(Response res) {
        TextWebSocketFrame frame = new TextWebSocketFrame(JSONObject.toJSONString(res));
        ctx.writeAndFlush(frame);
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (!isClosed) {
            isClosed = true;
            ctx.close();
        }
    }

    /**
     * 获取ip
     */
    public String getIp() {
        InetSocketAddress ads=(InetSocketAddress) getChannel().remoteAddress();
        return ads.getAddress().getHostAddress();
    }

    public boolean isClosed() {
        return isClosed;
    }

    public boolean isKick() {
        return isKick;
    }

    public void setKick(boolean isKick) {
        this.isKick = isKick;
    }
    
    public void setAttachment(Object attachment){
//    	this.attachment=attachment;
    	this.ctx.channel().attr(ATTACHMENT_KEY).set(attachment);
    }
    
    public Object getAttachment(){
    	return this.ctx.channel().attr(ATTACHMENT_KEY).get();
    }
    
    public void removeAttachment(){
    	this.ctx.channel().attr(ATTACHMENT_KEY).remove();
    }
}
