package com.misonamoo.niaportal.controller;

import com.misonamoo.niaportal.vo.BoardVO;
import com.misonamoo.niaportal.service.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board")
public class BoardController {

    private static final Logger log = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    BoardService boardService;

    // 게시물 전체 조회
    @GetMapping("/list")
    public List<BoardVO> getBoardList() throws Exception {
        return boardService.getBoardList();
    }

    // 게시물 상세 조회
    @GetMapping("/detail")
    public BoardVO getBoard(BoardVO boardVO) throws Exception {
        BoardVO result = boardService.getBoard(boardVO);
        log.info("========sampleVO========" + result);
        return boardVO;
    }

    // 게시물 추가
    @PostMapping("/insert")
    public int insertBoard(BoardVO boardVO) {
        int cnt = boardService.insertBoard(boardVO);
        return cnt;
    }
    // 게시물 변경

    // 게시물 삭제
    @PostMapping("/delete")
    public int deleteBoard(@RequestParam(value = "no") Long boardNo) {
        int cnt = boardService.deleteBoard(boardNo);
        return cnt;
    }
}
