package com.misonamoo.niaportal.controller;

import com.misonamoo.niaportal.common.SHA256Util;
import com.misonamoo.niaportal.domain.PwSec;
import com.misonamoo.niaportal.domain.User;
import com.misonamoo.niaportal.service.PwSecService;
import com.misonamoo.niaportal.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/User")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    PwSecService pwSecService;

    //회원가입
    @PostMapping(value = "/register")
    public Map<String, String> register(@RequestBody User user) throws Exception {
        Map<String, String> ret = new HashMap();
        ret.put("code", "200");
        int emailCnt = userService.dupEmail(user);
        if (emailCnt == 0) {
            // 회원가입처리
            userService.register(user);
        } else {
            ret.put("code", "102");
            ret.put("message", "중복된 ID");
        }
        return ret;
    }

    //회원삭제
    @PostMapping(value = "/delete")
    public Map<String, String> delete(@ModelAttribute User user) throws Exception {
        Map<String, String> ret = new HashMap();
        ret.put("code", "200");
        //쿠키정보를 체크해서 회원인지를 확인한다. user.email과 쿠키에 있는 이메일이 같은지 확인하다.
        int emailCnt = userService.dupEmail(user);
        if (emailCnt > 0) {
            //회원탈퇴처리
            userService.delete(user);
            //회원탈퇴가 제대로 처리 되면 쿠키를 삭제한다.
        } else {
            ret.put("code", "101");
            ret.put("message", "회원정보 없음");
        }
        return ret;
    }

    //회원탈퇴
    @PostMapping(value = "/withdraw")
    public Map<String, String> withdraw(@ModelAttribute User user) throws Exception {
        Map<String, String> ret = new HashMap();
        ret.put("code", "200");
        //쿠키정보를 체크해서 회원인지를 확인한다. user.email과 쿠키에 있는 이메일이 같은지 확인하다.
        int emailCnt = userService.dupEmail(user);
        if (emailCnt > 0) {
            //회원탈퇴처리
            userService.withdraw(user);
            //회원탈퇴가 제대로 처리 되면 쿠키를 삭제한다.
        } else {
            ret.put("code", "101");
            ret.put("message", "회원정보 없음");
        }
        return ret;
    }

    //회원 정보 수정
    @PostMapping(value = "/edit")
    public Map<String, String> edit(@RequestBody User user) throws Exception {
        Map<String, String> ret = new HashMap();
        ret.put("code", "200");
        ret.put("message", "조회 정상");
        int emailCnt = userService.checkEmailPass(user);
        if (emailCnt > 0) {
            //회원정보 수정처리
            userService.edit(user);
            //회원수정이 제대로 처리 되면 쿠키를 삭제한다.
        } else {
            ret.put("code", "101");
            ret.put("message", "회원정보 없음");
        }
        return ret;
    }

    @Value("${key}")
    private String salt;    // 비밀번호 암호화 키

    // 로그인
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map login(@ModelAttribute User params, HttpServletResponse response) throws Exception {
        Map<String, Object> rst = new HashMap<String, Object>();
        User vo = new User();
        String password = vo.getPassword();
        password = SHA256Util.getEncrypt(password, salt);
        vo.setEmail(params.getEmail());
        vo.setPassword(password);
        if (vo.getEmail() == null) {
            rst.put("code", 101);
            rst.put("messsage", "아이디 없음");
            return rst;
        } else if (vo.getPassword() == null) {
            rst.put("code", 103);
            rst.put("messsage", "비밀번호 없음");
            return rst;
        }
        User login = userService.login(vo);
        if (login.getPassword() != vo.getPassword()) {
            rst.put("code", 104);
            rst.put("messsage", "비밀번호 불일치");
            return rst;
        }
        if (login == null) {
            rst.put("code", 200);
            rst.put("messsage", "로그인 실패");
        } else {
            Cookie loginCookie = new Cookie("email", login.getEmail());
            loginCookie.setPath("/");
            loginCookie.setMaxAge(-1);
            response.addCookie(loginCookie);
            rst.put("code", 200);
            rst.put("messsage", "로그인 성공");
        }
        return rst;
    }

    //로그아웃
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletResponse response, HttpServletRequest request) throws Exception {
        Cookie[] cookies = request.getCookies(); // 모든 쿠키의 정보를 cookies에 저장
        if (cookies != null) { // 쿠키가 한개라도 있으면 실행
            for (int i = 0; i < cookies.length; i++) {
                cookies[i].setMaxAge(0); // 유효시간을 0으로 설정
                response.addCookie(cookies[i]); // 응답 헤더에 추가
            }
        }
        return "redirect:/";
    }

    @Autowired
    private JavaMailSender javaMailSender;


    //  비밀번호 찾기
    @RequestMapping(value = "/findPw", method = RequestMethod.POST)
    public PwSec findPw(@ModelAttribute User vo) throws Exception {
        int chkNo = userService.findUserNo(vo);
        PwSec pwSec = new PwSec();
        pwSec.setUserNo(chkNo);
        pwSec.setSecCode(pwSecService.findCode(pwSec.getUserNo()));
        String codeBuf = "";
        if (pwSec.getSecCode() == null || pwSec.getSecCode() == "") {
            Random rnd = new Random(); // 랜덤코드를 씌우기 위해서
            StringBuffer buf = new StringBuffer();// 보안코드 값을
            for (int i = 0; i < 8; i++) {
                // rnd.nextBoolean() 는 랜덤으로 true, false 를 리턴. true일 시 랜덤 한 소문자를, false 일 시 랜덤 한
                // 숫자를 StringBuffer 에 append 한다.
                if (rnd.nextBoolean()) {
                    buf.append((char) ((int) (rnd.nextInt(26)) + 97));
                } else {
                    buf.append((rnd.nextInt(10)));
                }
                codeBuf = buf.toString();
            }
            pwSec.setSecCode(codeBuf);
            pwSecService.setCode(pwSec);
        } else {
            Random rnd = new Random(); // 랜덤코드를 씌우기 위해서
            StringBuffer buf = new StringBuffer();// 보안코드 값을
            for (int i = 0; i < 8; i++) {
                // rnd.nextBoolean() 는 랜덤으로 true, false 를 리턴. true일 시 랜덤 한 소문자를, false 일 시 랜덤 한
                // 숫자를 StringBuffer 에 append 한다.
                if (rnd.nextBoolean()) {
                    buf.append((char) ((int) (rnd.nextInt(26)) + 97));
                } else {
                    buf.append((rnd.nextInt(10)));
                }
                codeBuf = buf.toString();
            }
            pwSec.setSecCode(codeBuf);
            pwSecService.updateCode(pwSec);
        }
//      메일 발송 부분
        String to = vo.getEmail(); //받는 사람
        String from = ""; //보내는 사람
        String subject = "이 프로젝트의 비밀번호 찾기 메일입니다."; //제목
        String body = "내용@@" + pwSec.getSecCode(); //내용
        //        StringBuilder body = new StringBuilder();
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        mimeMessageHelper.setFrom(from, "수신자명");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(body, true);
        javaMailSender.send(message);
        return pwSec;
    }

    //비밀번호 재설정
    @RequestMapping(value = "/pwSet", method = RequestMethod.POST)
    public Map<String, Object> setPw(@ModelAttribute User user) throws Exception {
        Map<String, Object> rst = new HashMap<String, Object>();
        if (user.getPassword() == null || user.getPassword()=="") {
            rst.put("code", 100);
            rst.put("message", "필수값 없음");
            return  rst;
        }
        String password = user.getPassword();
        password = SHA256Util.getEncrypt(password, salt);
        user.setPassword(password);
        int result = userService.setPw(user);
        if (result != 0) {
            rst.put("code", 200);
            rst.put("message", "비밀번호 변경 정상 처리");
            return rst;
        }
        return rst;
    }
}
