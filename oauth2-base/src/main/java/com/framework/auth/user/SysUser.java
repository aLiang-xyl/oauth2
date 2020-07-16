package com.framework.auth.user;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author xingyl
 * @since 2019-11-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SysUser对象", description = "用户信息")
public class SysUser implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键id")
	private Long id;

	@ApiModelProperty(value = "账号")
	private String username;

	@ApiModelProperty(value = "密码")
	private String password;

	@ApiModelProperty(value = "姓名")
	private String name;

	@ApiModelProperty(value = "性别")
	private Integer sex;

	@ApiModelProperty(value = "地址")
	private String address;

	@ApiModelProperty(value = "手机号")
	private String mobile;

	@ApiModelProperty(value = "邮箱")
	private String email;

	@ApiModelProperty(value = "最后登陆时间")
	private String lastLoginTime;

	@ApiModelProperty(value = "false禁用;true启用")
	private Boolean enable;

	@ApiModelProperty(value = "账号类型:1普通用户;2:微信小程序用户")
	private Integer type;

	@ApiModelProperty(value = "微信openid,微信下程序用户唯一识别号")
	private String openid;
}
