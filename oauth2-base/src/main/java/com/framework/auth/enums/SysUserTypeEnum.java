/**
 * 
 */
package com.framework.auth.enums;

/**
 * 账号类型:1普通用户;2:微信小程序用户
 * 
 * @author xingyl
 * @date 2019年9月27日
 */
public enum SysUserTypeEnum {
	/**
	 * 普通用户
	 */
	COMMON("普通用户", 1), 
	/**
	 * 访客用户
	 */
	SMALL_PROGRAM("小程序用户", 2);

	private String name;
	private Integer code;

	/**
	 * @param name
	 * @param code
	 */
	private SysUserTypeEnum(String name, Integer code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

}
