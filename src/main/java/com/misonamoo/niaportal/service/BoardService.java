package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.Board;
import com.misonamoo.niaportal.domain.BoardContent;

import java.util.List;
import java.util.Map;

public interface BoardService {

    Map<String, Object> getBoardList(Board board);

    Board getBoard(Board board);

    Long insertBoard(Board board);

    void updateBoardContent(BoardContent boardContent);

    BoardContent getBoardContent(BoardContent boardContent);

    void deleteBoardContent(BoardContent boardContent);

    List<Map<String, Object>> getSportsList(Map<String, String> sportsMap);

    List<BoardContent> getReplyList(BoardContent boardContent);

    void updateBoardContenViewCnt(String boardContentNo);
}
