package com.wasu.game.domain;

import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 回复消息
 * @author -琴兽-
 *
 */
public class Response implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 响应Id
	 */
	private String responseId;
	
	/**
	 * 模块号
	 */
	private short module;
	
	/**
	 * 命令号
	 */
	private short cmd;
	
	/**
	 * 结果码
	 */
	private int stateCode = ResultCode.SUCCESS;

	/**
	 * 数据
	 */
//	private byte[] data;
	private Object data;
	
	public Response() {
	}
	
	public Response(Request message) {
		this.module = message.getModule();
		this.cmd = message.getCmd();
		this.responseId=message.getRequestId();
		if(StringUtils.isEmpty(this.responseId))
			this.responseId=new Date().getTime()+"";
	}	
	
	public Response(Result message) {
		this.module = message.getModule();
		this.cmd = message.getCmd();
		this.responseId=message.getResponseId();
		if(StringUtils.isEmpty(this.responseId))
			this.responseId=new Date().getTime()+"";
	}
	
	public Response(short module, short cmd, Object data){
		this.module = module;
		this.cmd = cmd;
		this.data = data;
	}

	public String getResponseId() {
		return responseId;
	}

	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}

	public int getStateCode() {
		return stateCode;
	}

	public void setStateCode(int stateCode) {
		this.stateCode = stateCode;
	}

//	public byte[] getData() {
//		return data;
//	}
//
//	public void setData(byte[] data) {
//		this.data = data;
//	}


	public Object getData() {
		return data;
	}

	public void setData(Object data) {
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

	@Override
	public String toString() {
		return "Response{" +
				"responseId='" + responseId + '\'' +
				", cmd=" + cmd +
				", data=" + data +
				", stateCode=" + stateCode +
				", module=" + module +
				'}';
	}
}
