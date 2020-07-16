package com.framework.auth.service;

import com.framework.auth.user.SysUser;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author xingyl
 * @since 2019-11-22
 */
public interface ISysUserService {

	/**
	 * 根据用户名查询用户信息，匹配用户名和手机号
	 * 
	 * @param username
	 * @return
	 */
	SysUser listByUsername(String username);

	/**
	 * 更新最后登录时间
	 * 
	 * @param userId
	 */
	void updateLastLoginTime(Long userId);

	/**
	 * 保存用户
	 * 
	 * @param user
	 * @return
	 */
	boolean save(SysUser user);

	/**
	 * 根据openid查询用户
	 * 
	 * @param openid
	 * @return
	 */
	SysUser getByOpenid(String openid);

}
