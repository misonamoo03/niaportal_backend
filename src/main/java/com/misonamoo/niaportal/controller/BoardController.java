package com.misonamoo.niaportal.controller;

import com.misonamoo.niaportal.domain.Board;
import com.misonamoo.niaportal.domain.BoardContent;
import com.misonamoo.niaportal.service.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.misonamoo.niaportal.common.CommonUtil.*;
import static com.misonamoo.niaportal.common.CommonUtil.isNull;

@RestController
@RequestMapping("/board")
public class BoardController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    BoardService boardService;

    /**
     * 게시물 전체 조회
     * @param board
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/list")
    public Map<String, Object> getBoardList(Board board, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        Map<String, Object> data = new LinkedHashMap<>();
        ret.put("status", 200);

        if (board.getBoardNo() == 0 && isNull(board.getSportsBoardCode())) {
            // 필수 변수값 없음
            ret.put("status", 100);
            ret.put("message", "필수 변수값 없음");
            return returnMap(ret);
        }

        Map<String, Object> result = boardService.getBoardList(board);
        data.put("currentPage",board.getCurrentpage());
        data.put("pagePerRow", board.getPagePerRow());
        data.put("totalCnt",result.get("totalCnt"));
        data.put("list",result.get("list"));
        ret.put("data",data);
        return returnMap(ret);
    }

    /**
     * 게시물 상세 조회
     * @param boardContent
     * @param request
     * @return
     * @throws Exception
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

                    //자신의 글이 아니면서 비밀글인 경우
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
     * 게시물 추가
     * @param board
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/insert")
    public Map<String,Object> insertBoard(Board board, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        long insertKey=0;

        log.info("==================boardNo==============" + board.getBoardNo());
        ret.put("status", 200);
        ret.put("message", "다운로드 요청 정상 처리");

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
                    // FAQ 글쓰기
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

    /**
     * 게시물 수정
     * @param boardContent
     * @param request
     * @return
     * @throws Exception
     */
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

    /**
     * 게시물 삭제
     * @param boardContent
     * @param request
     * @return
     * @throws Exception
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
