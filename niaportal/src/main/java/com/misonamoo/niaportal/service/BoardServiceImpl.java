package com.misonamoo.niaportal.service;

import com.misonamoo.niaportal.vo.BoardVO;
import com.misonamoo.niaportal.mapper.BoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    BoardMapper boardMapper;

    @Override
    public List<BoardVO> getBoardList() {
        return boardMapper.getBoardList();
    }

    @Override
    public BoardVO getBoard(BoardVO vo) {
        return boardMapper.getBoard(vo);
    }

    @Override
    public int insertBoard(BoardVO vo) {
        return boardMapper.insertBoard(vo);
    }

    @Override
    public int deleteBoard(Long no) {
        return boardMapper.deleteBoard(no);
    }

}
