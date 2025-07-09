package com.kh.spring.board.model.service;

import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.board.model.dao.BoardDao;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.board.model.vo.BoardImg;
import com.kh.spring.board.model.vo.BoardType;
import com.kh.spring.common.Utils;
import com.kh.spring.common.model.vo.PageInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

	private final BoardDao boardDao;
	
	@Override
	public Map<String, String> getBoardTypeMap() {
		return boardDao.getBoardTypeMap();
		
	}

	@Override
	public int selectListCount(Map<String, Object> paramMap) {
		return boardDao.selectListCount(paramMap);
	}

	@Override
	public List<Board> selectList(PageInfo pi, Map<String, Object> paramMap) {
		return boardDao.selectList(pi, paramMap);
	}
	
	@Override
	@Transactional(rollbackFor = {Exception.class})
	/* 
	 * @Transactional
	 *  - 선언적 트랜잭션 관리용 어노테이션
	 *  - 예외가 발생하면 무조건 rollback처리 한다.
	 *  - rollbackFor를 지정하지 않으면 RuntimeException에러가 발생한 경우만 
	 *    rollback한다.
	 * */
	public int insertBoard(Board b, List<BoardImg> imgList)  {
		/*
		 * 0. 게시글 데이터 전처리(개행 문자 처리 및 xss공격 핸들링)
		 * 1. 게시글 테이블에 데이터를 먼저 추가
		 * 2. 첨부파일 테이블에 데이터 추가
		 * 3. 이미지 및 테이블 등록 실패시 롤백(에러반환)
		 */
		
		// 데이터 전처리
		// - 게시글 내용 : xss핸들링 및 개행문자 처리
		// - 게시글 제목 : xss핸들링
		b.setBoardContent(Utils.XSSHandling(b.getBoardContent()));
		b.setBoardContent(Utils.newLineHandling(b.getBoardContent()));
		b.setBoardTitle(Utils.XSSHandling(b.getBoardTitle()));
		
		// 게시글 저장
		//  - mybatis의 selectkey기능을 이용하여 boardNo값을 b객체에 바인딩
		int result = boardDao.insertBoard(b);
		
		if(result == 0) {
			throw new RuntimeException("게시글 등록 실패");
		}
		
		// 첨부파일등록
		// - 전달받은 imgList가 비어있지 않은 경우 진행
		// - 게시글번호를 추가로 refBno필드에 바인딩
		if(!imgList.isEmpty()) {
			for(BoardImg bi : imgList) {
				bi.setRefBno(b.getBoardNo());
			}
			// 다중 인서트문 실행
			int imgResult = boardDao.insertBoardImgList(imgList);
			
			
			if(imgResult != imgList.size()) {
				throw new RuntimeException("첨부파일 등록 실패");
			}
		}

		return result;
	}

	@Override
	public BoardExt selectBoard(int boardNo) {
		return boardDao.selectBoard(boardNo);
	}

	@Override
	public int increaseCount(int boardNo) {
		return boardDao.increaseCount(boardNo);
	}

	@Override
	public List<BoardImg> selectBoardImgList(int boardNo) {

		return null;
	}

	@Override
	public int updateBoard(Board board, String deleteList, MultipartFile upfile, List<MultipartFile> upfiles)
			throws Exception {

		return 0;
	}

	@Override
	public List<String> selectFileList() {

		return null;
	}

	@Override
	public List<BoardType> selectBoardTypeList() {

		return null;
	}


}
