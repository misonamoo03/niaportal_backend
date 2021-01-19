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

/**
 * 게시판 서비스
 * @author Yohan
 */
@Service
@Transactional
public class BoardServiceImpl implements BoardService {

    @Autowired
    BoardMapper boardMapper;

    /**
     * 목록 리턴.
     * @return
     */
    public Map<String, Object> getBoardList(Board board) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = boardMapper.getBoardList(board);
        int totalCnt = boardMapper.getBoardTotalCnt(board);
        result.put("totalCnt", totalCnt);
        result.put("list",list);

        return result;
    }
    /**
     * 상세 정보 리턴.
     * @return
     */
    public Board getBoard(Long boardNo) {
        return boardMapper.getBoard(boardNo);
    }


    /**
     * 등록/수정 처리.
     */
    public Long insertBoard(Board board) {
        // 조회하여 리턴된 정보
            boardMapper.insertBoard(board);
        return board.getBoardNo();
    }

    /**
     * 삭제 처리.
     */
    public void deleteBoard(Long boardSeq) {
        boardMapper.deleteBoard(boardSeq);
    }

    @Override
    public void updateBoardContent(BoardContent boardContent) {
        boardMapper.updateBoardContent(boardContent);
    }

    @Override
    public BoardContent getBoardContent(BoardContent boardContent) {
            return boardMapper.getBoardContent(boardContent);

    }

    @Override
    public void deleteBoardContent(BoardContent boardContent) {
        if(boardContent.getBoardContentNo().equals(boardContent.getContentGroup())){ // 원글
            boardMapper.deleteContentGroup(boardContent.getContentGroup());
        }else{
            boardMapper.deleteBoardContent(boardContent.getBoardContentNo());
        }
    }
}
