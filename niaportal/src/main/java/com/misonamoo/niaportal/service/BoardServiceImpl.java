package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.domain.Board;
import com.misonamoo.niaportal.domain.BoardParameter;
import com.misonamoo.niaportal.mapper.BoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<Board> getBoardList() {
        return boardMapper.getBoardList();
    }

    /**
     * 상세 정보 리턴.
     * @param boardSeq
     * @return
     */
    public Board getBoard(Long boardSeq) {
        return boardMapper.getBoard(boardSeq);
    }

    /**
     * 등록/수정 처리.
     * @param param
     */
    public Long saveBoard(BoardParameter param) {
        // 조회하여 리턴된 정보
        Board board = boardMapper.getBoard(param.getBoardSeq());
        if (board == null) {
            boardMapper.saveBoard(param);
        } else {
            boardMapper.updateBoard(param);
        }
        return param.getBoardSeq();
    }

    /**
     * 삭제 처리.
     * @param boardSeq
     */
    public void deleteBoard(Long boardSeq) {
        boardMapper.deleteBoard(boardSeq);
    }
}
