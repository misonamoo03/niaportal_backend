package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.Board;
import com.misonamoo.niaportal.domain.BoardContent;
import com.misonamoo.niaportal.mapper.BoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BoardServiceImpl implements BoardService {

    @Autowired
    BoardMapper boardMapper;

    public Map<String, Object> getBoardList(Board board) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = boardMapper.getBoardList(board);
        int totalCnt = boardMapper.getBoardTotalCnt(board);
        result.put("totalCnt", totalCnt);
        result.put("list",list);

        return result;
    }

    public Board getBoard(Long boardNo) {
        return boardMapper.getBoard(boardNo);
    }

    public Long insertBoard(Board board) {
        // 조회하여 리턴된 정보
            boardMapper.insertBoard(board);
        return board.getBoardContentNo();
    }

    public void updateBoardContent(BoardContent boardContent) {
        boardMapper.updateBoardContent(boardContent);
    }

    public BoardContent getBoardContent(BoardContent boardContent) {
            return boardMapper.getBoardContent(boardContent);

    }

    /**
     * @param boardContent
     */
    public void deleteBoardContent(BoardContent boardContent) {
        if(boardContent.getBoardContentNo().equals(boardContent.getContentGroup())){ // 원글
            boardMapper.deleteContentGroup(boardContent.getContentGroup());
        }else{
            boardMapper.deleteBoardContent(boardContent.getBoardContentNo());
        }
    }

    @Override
    public List<Map<String, Object>> getSportsList(Map<String, String> sportsMap) {


        return boardMapper.getSportsList(sportsMap);
    }

    @Override
    public List<BoardContent> getReplyList(BoardContent boardContent) {
        Map<String, Object> result = new HashMap<>();
        return boardMapper.getReplyList(boardContent);

    }
}
