package com.wasu.game.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 结果对象
 * @author -琴兽-
 *
 * @param <T>
 */
public class Result<T> implements Serializable{
	
	private static final long serialVersionUID = 2L;
	
	/**
	 * 结果码
	 */
	private int resultCode;
	
	private List<Long> users=new ArrayList<Long>();

	/**
	 * 响应id
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
	 * 结果内容
	 */
	private T content;
	
	public Result() {}
	
	public Result(Request res) {
		this.cmd=res.getCmd();
		this.module=res.getModule();
		this.responseId=res.getRequestId();
	}

	public static <T> Result<T> SUCCESS(T content,Request res,List<Long> users){
		Result<T> result = new Result<T>(res);
		result.resultCode = ResultCode.SUCCESS;
		result.responseId=res.getRequestId();
		result.content = content;
		result.users=users;
		return result;
	}

	public static <T> Result<T> SUCCESS(T content,Request res,Long userId){
		List<Long> users = new ArrayList<Long>();
		users.add(userId);
		Result<T> result = new Result<T>(res);
		result.resultCode = ResultCode.SUCCESS;
		result.responseId=res.getRequestId();
		result.content = content;
		result.users=users;
		return result;
	}

	public static <T> Result<T> SUCCESS(T content){
		Result<T> result = new Result<T>();
		result.resultCode = ResultCode.SUCCESS;
		result.content = content;
		return result;
	}
	
	public static <T> Result<T> SUCCESS(){
		Result<T> result = new Result<T>();
		result.resultCode = ResultCode.SUCCESS;
		return result;
	}
	
	public static <T> Result<T> ERROR(int resultCode){
		Result<T> result = new Result<T>();
		result.resultCode = resultCode;
		return result;
	}
	
	public static <T> Result<T> ERROR(int resultCode,Request res,Long id){
		Result<T> result = new Result<T>(res);
		result.resultCode = resultCode;
		result.responseId=res.getRequestId();
		result.getUsers().add(id);
		return result;
	}
	
	public static <T> Result<T> valueOf(int resultCode, T content){
		Result<T> result = new Result<T>();
		result.resultCode = resultCode;
		result.content = content;
		return result;
	}

	public String getResponseId() {
		return responseId;
	}

	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}
	
	public boolean isSuccess(){
		return this.resultCode == ResultCode.SUCCESS;
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

	public List<Long> getUsers() {
		return users;
	}

	public void setUsers(List<Long> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "Result [resultCode=" + resultCode + ", Users=" + users + ", module=" + module + ", cmd=" + cmd
				+ ", content=" + content + "]";
	}
	
}
