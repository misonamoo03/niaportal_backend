package com.misonamoo.niaportal.common;

import com.misonamoo.niaportal.domain.User;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class CommonUtil {
    /**
     * 텍스트가 널이거나 비어있는지 확인
     * @param val 확인할 텍스트
     * @return
     */
    public static boolean isNull(String val) {
        if (val == null || val.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 쿠키 정보를 가져와서 원하는 값 반환
     * @param request
     * @param key 값을 원하는 쿠키의 이름
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getCookieValue(HttpServletRequest request, String key) throws UnsupportedEncodingException {
        Cookie[] cookies = request.getCookies(); //쿠키 정보를 받아온다.
        String returnValue = null;
        if(cookies != null) {
            for(Cookie c : cookies){
                if (key.equals(c.getName())) {
                    returnValue = URLDecoder.decode(c.getValue(), "UTF-8");
                }
            }
        }
        return returnValue;
    }

    /**
     * 쿠키 정보를 모두 삭제
     * @param request
     * @param response
     */
    public static void setLogout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) { // 쿠키가 한개라도 있으면 실행
            for (Cookie c : cookies) {
                c.setMaxAge(0); // 유효시간을 0으로 설정
                c.setPath("/");
                response.addCookie(c); // 응답 헤더에 추가
            }
        }
    }

    /**
     * 텍스트 암호화 처리
     * @param word : 암호화할 텍스트
     * @param salt : 암호화 키
     * @return
     */
    public static String setEncryptPass(String word, String salt) { //비밀번호를 암호화해서 저장
        System.out.println(salt);
        String encWord;
        encWord = SHA256Util.getEncrypt(word, salt);

        return encWord;
    }

}
