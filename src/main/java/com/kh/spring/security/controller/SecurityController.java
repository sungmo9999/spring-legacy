package com.kh.spring.security.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.validator.MemberValidator;
import com.kh.spring.member.model.vo.Member;
import com.kh.spring.security.model.vo.MemberExt;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class SecurityController {
	
	private BCryptPasswordEncoder passwordEncoder;
	private MemberService mService;
	
	
	// 생성자방식 의존성 주입(생성자가 현재 클래스에 1개 라면 @Autowired 생략가능)
	public SecurityController(BCryptPasswordEncoder passwordEncoder, MemberService memberService) {
		this.passwordEncoder = passwordEncoder;
		this.mService = memberService;
	}
	
	// 에러페이지 포워딩용 url
	@RequestMapping("/security/accessDenied")
	public String accessDenied(Model model) {
		model.addAttribute("errorMsg","접근불가!!");
		return "common/errorPage";
	}
	
	//회원가입
	@GetMapping("/security/insert")
	public String enroll(@ModelAttribute Member member
			// @ModelAttribute
			// - 커맨드 객체 바인딩시 사용
			// - model영역에 커맨드객체 저장
			) {
		return "member/memberEnrollForm";
		
	}
	
	/*
	 * InitBinder
	 *  - 현재 컨트롤러에서 Binding작업을 수행 할때 실행되는 객체
	 *  - @MdelAttribute에 대한 바인딩 설정을 수행
	 *  
	 * 처리순서
	 * 1) 클라이언트의 요청 파라미터를 커멘드 객체 필드에 바인딩
	 * 2) 바인딩 과정에서 WebDataBinder가 필요한 경우 타입변환이나, 유효성 검사를 수행
	 * 3) 유효성 검사 결과를 BindingResult에 저장 
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(new MemberValidator());
		
		// 타입변환
		// 문자열 형태의 날자값을 Date로 변환
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMDD"); //생년월일
		dateFormat.setLenient(false);// yyMMDD형식이 아닌경우 에러를 발생
		
		binder.registerCustomEditor(Date.class, "birthday", new CustomDateEditor(dateFormat, true));
		// birthday=991215와 같은 형식으로 데이터가 들어오는 경우 수행되는 커스텀에디터 등록
		
	}
	
	@PostMapping("/security/insert")
	public String register(@Validated @ModelAttribute Member member, BindingResult bindingResult, RedirectAttributes ra) {
		//  RedirectAttributes
		// - 유효성 검사 결과를 저장하는 객체
		// -forward시 자동으로 jsp에게 전달되며 form태그 내부에 에러 내용을 바인딩 할 떄 사용
		// 유효성 검사
		// 
		if(bindingResult.hasErrors()) {
			return "member/memberEnrollForm";
		}
		
		// 융효성 검사 통과시 비밀번호정보는 암호화하여, 회원가입 진행
		String encryptedPassword = passwordEncoder.encode(member.getUserPwd());
		member.setUserPwd(encryptedPassword);
		
		mService.insertMember(member);
		
		// 회원가입 완료 후 로그인 페이지로 리다이렉트
		return "redirect:/member/login";
	}
	
	/*
	 * Authentication
	 *  - Principal : 인증에 사용된 사용자 객체
	 *  - Credentials : 인증에 필요한 비밀번호에 대한 정보를 가진 객체(내부적으로 인증작업시 사용)
	 *  - Authorities : 인증된 사용자가 가진 권한을 저장하는 객체
	 */
	@GetMapping("/security/myPage")
	public String myPage(Authentication auth2, Principal principal2, Model model) {
		// 인증된 사용자 정보 가져오기
		// 1. SecurityContextHolder이용
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		MemberExt principal = (MemberExt) auth.getPrincipal();
		model.addAttribute("loginUser", principal);
		
		// 2. ArgumentResolver 를 이용한 자동 바인딩
		log.info("auth = {}", auth);
		log.info("principal = {}", principal);
		log.info("auth2 = {}", auth2);
		log.info("principal2 = {}", principal2);
		
		return "member/myPage";
		
		
	}
	@PostMapping("/security/update")
	public String update(@Validated @ModelAttribute MemberExt loginUser, 
			BindingResult bindingResult,
			Authentication auth,
			RedirectAttributes ra) {
		if(bindingResult.hasErrors()) {
			
			return "redirect:/security/myPage";
		}
		// 비지니스로직
		//1. db의 member객체를 수정
		int result = mService.updateMember(loginUser);
		
		//2. 변경된 회원정보를 db에서 얻어온 후 새로운 인증정보 생성하여
		//   스레드 로컬에 저장
		//    - 변경된 회원정보(loginUser)
		//    - athoritis, credentials필요
		
		// 새로운 Authentication 객체 생성
		Authentication newAuth = new UsernamePasswordAuthenticationToken
				(loginUser, auth.getCredentials(), auth.getAuthorities());
		
		SecurityContextHolder.getContext().setAuthentication(newAuth);
		ra.addFlashAttribute("alertMsg","내 정보 수정 성공");
		
		return "redirect:/security/myPage";
	}
	
	
	

}
























