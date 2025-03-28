package com.example.flim.service;

import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.flim.config.CustomUserDetails;
import com.example.flim.dto.UserDTO;
import com.example.flim.mapper.UserMapper;

@Service
public class AuthService implements UserDetailsService {
   
	
	
	 
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	@Lazy //순환의존성 방지
	private PasswordEncoder passwordEncoder;
	
	
	
	
	//회원 가입
	public void registerUser(UserDTO userDTO) {
		 // 비밀번호를 BCrypt로 암호화
	    String hashedPassword =  passwordEncoder.encode(userDTO.getPassword());  // 비밀번호 암호화
	    // 암호화된 비밀번호를 UserDTO에 설정
	    userDTO.setPassword(hashedPassword);
		userMapper.insertUser(userDTO);
	}
	
	// 이메일로 (닉네임,비번 가져오기)
	public String getNickname(String email) {
	 String nickname = userMapper.getNickname(email);
		return nickname;
	}
	public String getPhonenumber(String email) {
	 String phoneNumber = userMapper.getPhonenumber(email);
		return phoneNumber;
	}
	
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	    UserDTO userDTO = userMapper.findByEmail(email); // 이메일로 조회
	    if (userDTO == null) {
	        throw new UsernameNotFoundException("이메일에 해당되는 유저 찾지못함: " + email);
	    }

	    return new CustomUserDetails(userDTO.getEmail(), userDTO.getPassword(), userDTO.getNickname(),userDTO.getPhoneNumber(),
	        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
	}
	 
	  // 이메일과 비밀번호로 인증
    public boolean authenticateUser(String email, String password) {
        // 이메일로 사용자 조회
        UserDTO user = userMapper.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return true;
        }
        return false;
    }

    // 비번찾기
	public String searchPassword(UserDTO userDTO) {
		//사용자 확인(이메일,전화번호)
	    UserDTO isUser = userMapper.findUserByEmailPhone(userDTO);
		if (isUser != null) {
			 //임시 비밀번호 생성, 리턴 
			 String tempPassword =	UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
			 //임시 비밀번호 암호화
			 String hashedTempPassword = passwordEncoder.encode(tempPassword);  
			 //임시 비밀번호 db저장
			 userMapper.updateTempPassword(isUser.getEmail(),isUser.getPhoneNumber(),hashedTempPassword);
			 return tempPassword;
		}
		return null;
	}

	public UserDTO searchProfile(String email) {
		return userMapper.findByEmail(email);
	}

	public UserDTO updateProfile(String email, String nickname, String password) {
		String hasedupdatePass = passwordEncoder.encode(password);
		userMapper.updateProfile(email,nickname,hasedupdatePass);
		return userMapper.findByEmail(email);
	}


	
}