package com.nemustech.common.model;

import java.io.Serializable;

import com.nemustech.common.util.StringUtil;

/**
 * 헤더
 * 
 * @author skoh
 */
public class Header implements Serializable {
	/**
	 * 성공 여부
	 */
	protected Boolean success_yn = false;

	/**
	 * 에러 코드
	 */
	protected String error_code;

	/**
	 * 에러 메시지
	 */
	protected String error_message;

	public Header() {
	}

	public Header(Boolean success_yn, String error_code, String error_message) {
		setSuccess_yn(success_yn);
		setError_code(error_code);
		setError_message(error_message);
	}

	public Boolean getSuccess_yn() {
		return success_yn;
	}

	public void setSuccess_yn(Boolean success_yn) {
		this.success_yn = success_yn;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	public String getError_message() {
		return error_message;
	}

	public void setError_message(String error_message) {
		this.error_message = error_message;
	}

	@Override
	public String toString() {
		return StringUtil.toString(this);
	}
}