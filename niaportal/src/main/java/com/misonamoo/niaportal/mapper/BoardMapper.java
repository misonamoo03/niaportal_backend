package com.misonamoo.niaportal.mapper;

import com.misonamoo.niaportal.domain.Board;
import com.misonamoo.niaportal.domain.BoardParameter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 게시판 Mapper
 * @author Yohan
 */
@Repository
@Mapper
public interface BoardMapper {

    List<Board> getBoardList();

    Board getBoard(Long boardSeq);

    Long saveBoard(BoardParameter param);

    Long updateBoard(BoardParameter param);

    void deleteBoard(Long boardNo);



}
