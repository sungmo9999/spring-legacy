package com.kh.spring.common.model.vo;

import lombok.Data;

@Data
public class PageInfo {
	private int listCount; //게시글 겟수
	private int currentPage;// 요청한 페이지
	private int pageLimit;// 페이지갯수
	private int boardLimit;// 페이지당 보여줄 게시글 갯수
	
	private int maxPage;
	private int startPage;
	private int endPage;
}
