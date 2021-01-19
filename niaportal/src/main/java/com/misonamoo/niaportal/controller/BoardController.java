package com.misonamoo.niaportal.controller;

import com.misonamoo.niaportal.domain.BasePaging;
import com.misonamoo.niaportal.domain.Board;
import com.misonamoo.niaportal.domain.BoardContent;
import com.misonamoo.niaportal.service.BoardService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.misonamoo.niaportal.common.CommonUtil.*;
import static com.misonamoo.niaportal.common.CommonUtil.isNull;

/**
 * 게시판 컨트롤러
 * @author Yohan
 */
@RestController
@RequestMapping("/board")
public class BoardController extends BaseController{

    private static final Logger log = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    BoardService boardService;

    /**
     * 목록 리턴.
     * @return
     */
    @GetMapping("/list")
    public Map<String, Object> getBoardList(Board board, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        ret.put("status", 200);

        if (board.getBoardNo() == 0 && isNull(board.getSportsBoardCode())) {
            // 필수 변수값 없음
            ret.put("status", 100);
            ret.put("message", "필수 변수값 없음");
            return returnMap(ret);
        }

        Map<String, Object> result = boardService.getBoardList(board);
        data.put("list",result.get("list"));
        data.put("totalCnt",result.get("totalCnt"));
        data.put("currentPage",board.getCurrentpage());
        data.put("pagePerRow", board.getPagePerRow());
        ret.put("data",data);
        return returnMap(ret);
    }

    /**
     * 상세 정보 리턴.
     * @param boardSeq
     * @return
     */
    @GetMapping("/detail")
    public Map<String, Object> getBoard(BoardContent boardContent, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        ret.put("status", 200);

        if (isNull(boardContent.getBoardContentNo())) {
            // 필수 변수값 없음
            ret.put("status", 100);
            ret.put("message", "필수 변수값 없음");
            return returnMap(ret);
        }
        BoardContent info = boardService.getBoardContent(boardContent);
        if(info != null) {
            if(isLoginNow(request)) { //로그인
                if (!isSuperUser(request)) { // 일반유저

                    //자신의 글 or 비밀글이 아닌글
                    if (info.getUserNo() != Long.parseLong(getCookieValue(request, "userNo")) && info.getSecYn().equals("Y")) {//본인글

                        ret.put("status", 104);
                        ret.put("message", "접근권한 없음");
                        return returnMap(ret);

                    }
                }
            }else{
                if(info.getSecYn().equals("Y")){
                    ret.put("status", 104);
                    ret.put("message", "접근권한 없음");
                    return returnMap(ret);
                }
            }
            data.put("info", info);
            ret.put("data",data);

        }else{
            ret.put("status", 105);
            ret.put("message", "요청정보 없음");
            return returnMap(ret);
        }

        return returnMap(ret);
    }

    /**
     * 등록/수정 처리.
     * @param board
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    @PostMapping("/insert")
    public Map<String,Object> insertBoard(Board board, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        long insertKey=0;

        log.info("==================boardNo==============" + board.getBoardNo());
        ret.put("status", 200);
        ret.put("message", "다운로드 요청 정상 처리");
        // 1. 로그인 체크 여부
        // 2. 답글인지 원글인지 ( 답글인 경우 관리자만 사용 )
        // 3. FAQ 등록은 관리자만 가능 Q&A는 당연히 사용자도 등록 가능
        // 4. BOARD_NO, TITLE, CONTENT는 필수
        // 5. 게시판 구분 코드로 분기 FAQ인지 QNA인지 -> BOARDNO로 전체 정보로 조회할 수 있는 쿼리를 하나 만들어서 활용
        if(isLoginNow(request)) {
            Board newBoard = boardService.getBoard(board.getBoardNo());
            board.setUserNo(Long.parseLong(getCookieValue(request,"userNo")));
            log.info("=============newBoard===============" + newBoard.toString());
            if (newBoard.getBoardTypeCode().equals("CD006001")) {
                //문의 사항
                // 원글 번호가 들어왔을 경우는 답글임
                if (!isNull(board.getOrgBoardContentNo())) {
                    //답글
                    if (isSuperUser(request)) {
                        // 문의 답변 글쓰기
                        // insertKey 제대로 안 받아와 짐
                        insertKey = boardService.insertBoard(board);

                    } else {
                        ret.put("status", 104);
                        ret.put("message", "접근권한 없음");
                        return returnMap(ret);
                    }
                } else {
                    //원글
                    insertKey = boardService.insertBoard(board);
                }
            }
            if (newBoard.getBoardTypeCode().equals("CD006002")) {
                // FAQ 게시판
                // 관리자가 아니면 return; 관리자면 글 쓰기
                if (isSuperUser(request)) {
                    // faq 글쓰기
                    insertKey= boardService.insertBoard(board);
                } else {
                    ret.put("status", 104);
                    ret.put("message", "접근권한 없음");
                    return returnMap(ret);
                }
            }
        } else {
            ret.put("status", 104);
            ret.put("message", "접근권한 없음");
            return returnMap(ret);
        }
        data.put("boardContentNo",insertKey);
        ret.put("data",data);
       return returnMap(ret);
    }

    @PostMapping("/update")
    public Map<String,Object> updateBoard(BoardContent boardContent, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        ret.put("status", 200);

        if (isNull(boardContent.getBoardContentNo())) {

            ret.put("status", 100);
            ret.put("message", "필수 변수값 없음");
            return returnMap(ret);
        }

        if (isLoginNow(request)) {
            if(!isSuperUser(request)){ // 일반유저

                //boardContent.setUserNo(Long.parseLong(getCookieValue(request, "userNo")));
                BoardContent info = boardService.getBoardContent(boardContent);
                //log.info(result.toString());
                if(info.getUserNo() != Long.parseLong(getCookieValue(request, "userNo"))){
                    ret.put("status", 104);
                    ret.put("message", "접근권한 없음");
                    return returnMap(ret);
                }
            }

            //update 처리
            boardService.updateBoardContent(boardContent);

        }
        return returnMap(ret);
    }
    // 수정 / 삭제
    // 1. 본인이 작성한 글
    // 2. super는 본인이 작성한 글이 아니여도 삭제 가능
    // 삭제의 경우 원글, 답글일 경우
    // 답글의 경우 답글이 삭제되면 REPLY_YN = 'N'
    // 원글일 경우 CASCADE로 답글까지 삭제됨. = CONTENT_GROUP


    /**
     * 삭제 처리.
     * @param boardSeq
     */

    @GetMapping("/delete")
    public Map<String,Object> deleteBoard(BoardContent boardContent,HttpServletRequest request) throws Exception  {
        Map<String, Object> ret = new HashMap<String,Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        ret.put("status", 200);

        if (isNull(boardContent.getBoardContentNo())) {

            ret.put("status", 100);
            ret.put("message", "필수 변수값 없음");
            return returnMap(ret);
        }

        if (isLoginNow(request)) {
            //boardContent.setUserNo(Long.parseLong(getCookieValue(request, "userNo")));
            BoardContent info = boardService.getBoardContent(boardContent);
            if(info != null) {
                if (!isSuperUser(request)) { // 일반유저

                    //log.info(result.toString());
                    if (info.getUserNo() != Long.parseLong(getCookieValue(request, "userNo"))) {
                        ret.put("status", 104);
                        ret.put("message", "접근권한 없음");
                        return returnMap(ret);
                    }
                }

                //삭제 처리
                boardService.deleteBoardContent(info);
            }else{
                ret.put("status", 105);
                ret.put("message", "요청정보 없음");
                return returnMap(ret);
            }

        }
        return returnMap(ret);
    }
}
