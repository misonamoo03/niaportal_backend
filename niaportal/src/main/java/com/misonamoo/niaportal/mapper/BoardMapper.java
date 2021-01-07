package com.misonamoo.niaportal.mapper;

import com.misonamoo.niaportal.vo.BoardVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface BoardMapper {

    /**
     * 게시물 전체 조회
     * @param
     * @return java.util.List
     * @exception Exception
     */
    List<BoardVO> getBoardList();

    /**
     * 게시물 상세 조회
     * @param vo - 게시물 정보가 담긴 BoardVO
     * @return Object
     * @exception
     */
    BoardVO getBoard(BoardVO vo);

    /**
     * 게시물 추가
     * @param vo - 게시물 정보가 담긴 BoardVO
     * @return
     */
    int insertBoard(BoardVO vo);

    /**
     * 게시물 삭제
     * @param no - 게시물 번호
     * @return
     */
    int deleteBoard(Long no);

    BoardVO getBoardTest(BoardVO vo);
}
