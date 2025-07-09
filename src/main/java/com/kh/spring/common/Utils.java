package com.kh.spring.common;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletContext;
import org.springframework.web.multipart.MultipartFile;
public class Utils {
	// 파일 저장 함수
	// 파일을 저장하면서, 파일명을 수정하고 수정된 파일명을 반환
	public static String saveFile(MultipartFile upfile, ServletContext application, String boardCode) {
		// 첨부파일을 저장할 저장경로 획득 (하드코딩할수도 있고 properties에서 가져올수도 있고 방법은 다양)
		String webPath = "/resources/images/board/"+boardCode+"/"; // 어플리케이션이 가동되는 context상의 경로
		// getRealPath(경로)
		//  - 실제 서버의 파일 시스템 경로를 절대경로로 반환하는 메서드
		//  - ex) C:/springWorkspace/spring-project/... 현재 운용환경에 맞는 절대경로를 가져와야돼서 하드코딩 X
		String serverFolderPath = application.getRealPath(webPath);
		System.out.println(serverFolderPath);
		
		// 저장경로가 존재하지 않는다면 생성
		File dir = new File(serverFolderPath);
		if(!dir.exists()) {
			dir.mkdirs(); // 중첩 디렉토리 생성을 위해
		}
		
		// 랜덤한 (고유한) 파일명 생성
		String originName = upfile.getOriginalFilename(); // 파일의 원본명
		String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		int random = (int)(Math.random()*90000 + 10000); // 5자리 랜덤값
		String ext = originName.substring(originName.lastIndexOf(".")); // .에서부터 끝까지 (확장자명)
		String changeName = currentTime + random + ext;
		
		// 서버에 파일을 업로드
		try {
			upfile.transferTo(new File(serverFolderPath + changeName));
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 파일명 반환
		return webPath + changeName;
	}
	/*
	 * XSS(크로스 사이트 스크립트) 공격을 방지하기위한 메서드
	 *  - 스크립트 삽입 공격
	 *  - 사용자가<script>태그를 게시글에 작성하여 큻라이언트가 게시글 클릭시
	 *    script에 지정한 코드가 실행되도록 유도하는방식
	 *  - 위 내용을 그대로db 에저장 후 브라우저에 랜더링 라면 문제가 발생 할 수있으므로 태
	 *    그가 아닌 기본 문자열로 인식 할 수 있게끔 html내부entitly로 반환 처리함
	 * 
	 * 
	 */
	public static String XSSHandling(String content) {
        if(content != null) {
            content = content.replaceAll("&", "&amp;");
            content = content.replaceAll("<", "&lt;");
            content = content.replaceAll(">", "&gt;");
            content = content.replaceAll("\"", "&quot;");
        }
        return content;
    }
    // 개행문자 처리
    // textarea -> \n , p -> <br>
    public static String newLineHandling(String content) {
        return content.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
    }
    // 개행해제 처리
    public static String newLineClear(String content) {
        return content.replaceAll("<br>","\n");
    }
	
}




