package com.misonamoo.niaportal.controller;

import com.misonamoo.niaportal.domain.Board;
import com.misonamoo.niaportal.domain.BoardParameter;
import com.misonamoo.niaportal.service.BoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 게시판 컨트롤러
 * @author Yohan
 */
@RestController
@RequestMapping("/board")
@Api(tags = "게시판 API")
public class BoardController {

    private static final Logger log = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    BoardService boardService;

    /**
     * 목록 리턴.
     * @return
     */
    @ApiOperation(value = "목록 조회", notes = "게시판 목록을 조회할 수 있습니다.")
    @GetMapping("/list")
    public List<Board> getBoardList() throws Exception {
        List<Board> list = boardService.getBoardList();
        return list;
    }

    /**
     * 상세 정보 리턴.
     * @param boardSeq
     * @return
     */
    @ApiOperation(value = "상세 조회", notes = "게시물 번호에 해당하는 상세 정보를 조회할 수 있습니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "boardSeq", value = "게시물 번호", example = "1")
    })
    @GetMapping("/{boardSeq}")
    public Board getBoard(@PathVariable Long boardSeq) throws Exception {
        return boardService.getBoard(boardSeq);
    }

    /**
     * 등록/수정 처리.
     * @param param
     */
    @ApiOperation(value = "등록/수정 처리", notes = "신규 게시물 저장 및 기존 게시물 업데이트가 가능합니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "boardSeq", value = "게시물 번호", example = "1"),
            @ApiImplicitParam(name = "boardTitle", value = "제목", example = "테스트 제목"),
            @ApiImplicitParam(name = "boardContent", value = "내용", example = "테스트 내용")
    })
    @PutMapping("/save")
    public Long saveBoard(BoardParameter param) {
       boardService.saveBoard(param);
       return param.getBoardSeq();
    }

    /**
     * 삭제 처리.
     * @param boardSeq
     */
    @ApiOperation(value = "삭제 처리", notes = "게시물 번호에 해당하는 정보를 삭제합니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "boardSeq", value = "게시물 번호", example = "1")
    })
    @DeleteMapping("/{boardSeq}")
    public boolean deleteBoard(@PathVariable Long boardSeq) {
        Board board = boardService.getBoard(boardSeq);
        if (board == null) {
            return false;
        }
        boardService.deleteBoard(boardSeq);
        return true;
    }
}
