package com.kh.spring.security.model.service;

import org.springframework.security.core.userdetails.UserDetailsService;

/*
 * UserDetailsService
 *  - 스프링 시큐리티에서 인증 처리시 사용하는 핵심 인터페이스로 사용자 정보를 조회 하는 매서드인
 *    lodeUserByUsername()를 정의 하고있다
 * loadUserByUsername()
 *  - 사용자의 아이디를 기반으로 USerDetails객체를 반환하는 메서드
 *  - 반환된UserDetails는 비밀번호 및 권한 검증시 사용된다
 *  - 일반적으로 DB를 이용하여 사용자 정보를 조회하는 비지니스 로직을 작성한다   
 */

public interface SecurityService extends UserDetailsService {

}
