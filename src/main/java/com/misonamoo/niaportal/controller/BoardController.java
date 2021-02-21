package com.misonamoo.niaportal.controller;

import com.misonamoo.niaportal.domain.Board;
import com.misonamoo.niaportal.domain.BoardContent;
import com.misonamoo.niaportal.service.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

        if (board.getBoardNo() == 0 ) {
            // 필수 변수값 없음
            ret.put("status", 100);
            ret.put("message", "필수 변수값 없음");
            return returnMap(ret);
        }

        Map<String, Object> result = boardService.getBoardList(board);
        data.put("currentPage",board.getCurrentPage());
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
            }
            boardService.updateBoardContenViewCnt(boardContent.getBoardContentNo());
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
     * 게시물 상세 조회
     * @param boardContent
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/detailBoardGroup")
    public Map<String, Object> getBoardGroup(BoardContent boardContent, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        boolean isMyOriginalContent = false;
        ret.put("status", 200);

        if (isNull(boardContent.getBoardContentNo())) {
            // 필수 변수값 없음
            ret.put("status", 100);
            ret.put("message", "필수 변수값 없음");
            return returnMap(ret);
        }
        BoardContent info = boardService.getBoardContent(boardContent);//글정보 조회
        if(info != null && !isNull(info.getOrgBoardContentNo())){ // 다글인경우 원글을 조회한다.
            if(info.getOrgBoardContentNo()!=null && Integer.parseInt(info.getOrgBoardContentNo())>0) {
                boardContent.setBoardContentNo(info.getOrgBoardContentNo());
                BoardContent orgInfo = boardService.getBoardContent(boardContent);
                if (orgInfo != null) {
                    info = orgInfo;
                } else {
                    ret.put("status", 105);
                    return returnMap(ret);
                }
            }
        }
        if(info != null) {
            if(isLoginNow(request)) { //로그인
                if (!isSuperUser(request)) { // 일반유저

                    //자신의 글이 아니면서 비밀글인 경우
                    if (info.getUserNo() != Long.parseLong(getCookieValue(request, "userNo")) && info.getSecYn().equals("Y")) {//본인글

                        ret.put("status", 104);
                        ret.put("message", "접근권한 없음");
                        return returnMap(ret);

                    }else{
                        isMyOriginalContent = true;
                    }
                }
            }


            List<BoardContent> replyList = boardService.getReplyList(boardContent);
            if(!isMyOriginalContent &&  !isSuperUser(request)){ //본인이 작성한 글이 아닐 경우 && 슈퍼유저가 아닐경우

                int size = replyList.size();
                for(int i = 0; i < size; i++) {
                    BoardContent content = replyList.get(i);
                    if(content.getSecYn().equals("Y")){ //보안글일경우
                        replyList.remove(content);
                        size--;
                        i--;
                    }
                }

            }
            boardService.updateBoardContenViewCnt(boardContent.getBoardContentNo());//조회수 업데이트
            info.setReplyList(replyList);
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
    public Map<String,Object> insertBoard(@RequestBody Board board, HttpServletRequest request) throws Exception {

        Cookie[] cookies = request.getCookies();
        log.info(String.valueOf(cookies));
        Map<String, Object> ret = new HashMap<String,Object>();
        Map<String, Object> data = new HashMap<String, Object>();
        long insertKey=0;

        log.info("==================boardNo==============" + board.getBoardNo());
        ret.put("status", 200);
        ret.put("message", "다운로드 요청 정상 처리");

        if(isLoginNow(request)) {
            // boardNo가 없을 경우 처리
            Board newBoard = boardService.getBoard(board.getBoardNo());
            board.setUserNo(Long.parseLong(getCookieValue(request,"userNo")));
            log.info("=============newBoard===============" + newBoard.toString());
            if (newBoard.getBoardTypeCode().equals("CD006001")) {
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
            if (newBoard.getBoardTypeCode().equals("CD006002")) {
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
    public Map<String,Object> updateBoard(@RequestBody BoardContent boardContent, HttpServletRequest request) throws Exception {
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
     * 게시물 수정
     * @param boardContentNo
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/updateViewCnt")
    public Map<String,Object> updateViewCnt(@RequestParam(value="boardContentNo", required = true) String boardContentNo, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap<String,Object>();
        ret.put("status", 200);

        if (isNull(boardContentNo)) {

            ret.put("status", 100);
            ret.put("message", "필수 변수값 없음");
            return returnMap(ret);
        }

        //update 처리
        boardService.updateBoardContenViewCnt(boardContentNo);

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
