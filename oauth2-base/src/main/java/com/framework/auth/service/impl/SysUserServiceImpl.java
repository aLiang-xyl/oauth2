package com.framework.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.framework.auth.service.ISysUserService;
import com.framework.auth.user.SysUser;

/**
 * <p>
 * 用户信息服务类
 * </p>
 *
 * @author xingyl
 * @since 2019-11-22
 */
@Service
public class SysUserServiceImpl implements ISysUserService {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public SysUser listByUsername(String username) {
//		QueryWrapper<SysUser> queryWrapper = queryWrapper();
		//只有普通用户且未被禁用的用户才能登陆
//		queryWrapper.lambda().eq(SysUser::getEnable, true).eq(SysUser::getType, SysUserTypeEnum.COMMON.getCode())
//				.and(wrapper -> wrapper.eq(SysUser::getUsername, username).or().eq(SysUser::getMobile, username));

//		return super.getOne(queryWrapper);
		return new SysUser().setUsername("admin").setPassword(passwordEncoder.encode("123456"));
	}

	@Override
	public void updateLastLoginTime(Long userId) {
		// 更新登陆时间
//		UpdateWrapper<SysUser> updateWrapper = updateWrapper();
//		updateWrapper.lambda().eq(SysUser::getId, userId).set(SysUser::getLastLoginTime,
//				DateTimeUtils.format(LocalDateTime.now()));

//		super.update(updateWrapper);
	}
	
	@Override
	public boolean save(SysUser user) {
		return true;
	}
	
	@Override
	public SysUser getByOpenid(String openid) {
		// 根据openid查询用户
		return null;
	}
}
