package com.framework.auth.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.framework.auth.service.ISysUserService;
import com.framework.auth.user.MyUserDetails;
import com.framework.auth.user.SysUser;

/**
 * @author aLiang
 * @date 2020年4月2日
 */
@Service
public class CommonUserDetailsService implements UserDetailsService {

	@Autowired
	private ISysUserService sysUserService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SysUser user = sysUserService.listByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("用户不存在");
		} else {
			List<SimpleGrantedAuthority> authorities = new ArrayList<>();
			return new MyUserDetails(user, authorities);
		}
	}
}
