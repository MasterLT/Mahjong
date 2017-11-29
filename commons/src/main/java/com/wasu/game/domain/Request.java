package com.wasu.game.domain;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.UUID;

/**
 * 消息对象
 * @author -琴兽-
 *
 */
public class Request implements Serializable {

	private static final long serialVersionUID = 3L;
	
	/**
	 * 请求id
	 */
	private String requestId;
	
	/**
	 * 模块号
	 */
	private short module;
	
	/**
	 * 命令号
	 */
	private short cmd;
	
	/**
	 * 数据
	 */
	private JSONObject data;
	
	public Request(){}
	
	public Request(String request){
		JSONObject obj=JSONObject.parseObject(request);
		this.setModule(obj.getShort("module"));
		this.setCmd(obj.getShort("cmd"));
		this.requestId=obj.getString("requestId")==null?"":obj.getString("requestId");
		this.setData((JSONObject)obj.get("data"));
	}
	
	public static Request valueOf(short module, short cmd, JSONObject data){
		Request request = new Request();
		request.setModule(module);
		request.setCmd(cmd);
		request.setData(data);
		request.setRequestId(UUID.randomUUID().toString());
		return request;
	}

	public static Request valueOf(short module, short cmd, JSONObject data, Request r){
		Request request = new Request();
		request.setModule(module);
		request.setCmd(cmd);
		request.setData(data);
		request.setRequestId(r.getRequestId());
		return request;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Object getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public short getModule() {
		return module;
	}

	public void setModule(short module) {
		this.module = module;
	}

	public short getCmd() {
		return cmd;
	}

	public void setCmd(short cmd) {
		this.cmd = cmd;
	}
}
