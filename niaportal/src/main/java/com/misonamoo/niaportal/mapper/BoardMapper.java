package com.misonamoo.niaportal.mapper;

import com.misonamoo.niaportal.domain.Board;
import com.misonamoo.niaportal.domain.BoardContent;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 게시판 Mapper
 * @author Yohan
 */
@Repository
@Mapper
public interface BoardMapper {

    List<Map<String, Object>> getBoardList(Board board);

    Board getBoard(Long boardNo);

    Long insertBoard(Board board);

    Long updateBoard(Board board);

    void deleteBoard(Long boardNo);


    void updateBoardContent(BoardContent boardContent);

    int getBoardTotalCnt(Board board);

    BoardContent getBoardContent(BoardContent boardContent);

    void deleteContentGroup(String contentGroup);

    void deleteBoardContent(String boardContentNo);
}
