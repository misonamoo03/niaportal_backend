package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.Board;
import com.misonamoo.niaportal.domain.BoardContent;

import java.util.List;
import java.util.Map;

public interface BoardService {

    Map<String, Object> getBoardList(Board board);

    Board getBoard(Long boardNo);

    Long insertBoard(Board board);

    void deleteBoard(Long getBoardSeq);

    void updateBoardContent(BoardContent boardContent);

    BoardContent getBoardContent(BoardContent boardContent);

    void deleteBoardContent(BoardContent boardContent);
}
