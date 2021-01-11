package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.Board;
import com.misonamoo.niaportal.domain.BoardParameter;

import java.util.List;

public interface BoardService {

    List<Board> getBoardList();

    Board getBoard(Long getBoardSeq);

    Long saveBoard(BoardParameter param);

    void deleteBoard(Long getBoardSeq);
}
