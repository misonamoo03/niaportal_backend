package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.vo.BoardVO;

import java.util.List;

public interface BoardService {
    List<BoardVO> getBoardList() throws Exception;
    BoardVO getBoard(BoardVO vo) throws Exception;
    int insertBoard(BoardVO vo);
    int deleteBoard(Long no);

    BoardVO getBoardTest(BoardVO vo);
}
