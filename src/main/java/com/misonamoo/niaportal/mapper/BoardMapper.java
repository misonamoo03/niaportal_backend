package com.misonamoo.niaportal.mapper;

import com.misonamoo.niaportal.domain.Board;
import com.misonamoo.niaportal.domain.BoardContent;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 게시판 Mapper
 *
 * @author Yohan
 */
@Repository
@Mapper
public interface BoardMapper {

    List<Map<String, Object>> getBoardList(Board board);

    Board getBoard(Board board);

    Long insertBoard(Board board);

    void updateBoardContent(BoardContent boardContent);

    int getBoardTotalCnt(Board board);

    BoardContent getBoardContent(BoardContent boardContent);

    void deleteContentGroup(String contentGroup);

    void deleteBoardContent(String boardContentNo);

    List<Map<String, Object>> getSportsList(Map<String, String> sportsMap);

    List<BoardContent> getReplyList(BoardContent boardContent);

    void updateBoardContenViewCnt(String boardContentNo);
}
