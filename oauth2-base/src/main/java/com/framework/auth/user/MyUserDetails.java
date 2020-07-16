package com.framework.auth.user;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter;


/**
 * @author aLiang
 * @date 2020年4月2日
 */
@SuppressWarnings("serial")
@Getter
@Setter
public class MyUserDetails extends User {

	private SysUser sysUser;

	public MyUserDetails(SysUser user,List<SimpleGrantedAuthority> authorities) {
		super(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
		this.sysUser = user;
	}

}