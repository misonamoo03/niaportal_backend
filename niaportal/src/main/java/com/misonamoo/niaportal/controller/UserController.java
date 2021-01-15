package com.misonamoo.niaportal.controller;

import com.misonamoo.niaportal.common.CommonUtil;
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
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import static com.misonamoo.niaportal.common.CommonUtil.*;

@RestController
@RequestMapping("/User")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    PwSecService pwSecService;

    @Value("${key}")
    private String salt;    // 비밀번호 암호화 키

    //회원가입
    @PostMapping(value = "/register")
    public Map<String, Object> register(@RequestBody User user) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("code", 200);
        ret.put("message", "회원가입 정상 처리");
        int emailCnt = userService.dupEmail(user);  // 0이면 이메일 중복 x, 1이면 이메일 중복
        if (isNull(user.getEmail()) ||    // 필수 변수값 없음
            isNull(user.getPassword()) ||
            isNull(user.getUserName())||
            isNull(user.getTel()) ||
            isNull(user.getAgency()) ||
            isNull(user.getCompanyTypeCode())) {

            ret.put("code", 100);
            ret.put("message", "필수 변수값 없음");
        }
        else if (emailCnt > 0) {
            ret.put("code", 101);
            ret.put("message", "중복된 ID");
        }
        else {
            // 회원가입처리
            userService.register(user);
        }
        return ret;
    }

    //회원 조회
    @GetMapping(value = "/inquiry")
    public Map<String, Object> delete(@ModelAttribute User user) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("code", 200);
        ret.put("message", "회원조회 정상 처리");

        
        return ret;
    }


    //회원삭제
    @PostMapping(value = "/delete")
    public Map<String, Object> delete(@ModelAttribute User user, HttpServletResponse response, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("code", 200);
        ret.put("message", "회원삭제 정상 처리");
        if (CommonUtil.isNull(user.getEmail())) {
            ret.put("code", 100);
            ret.put("message", "필수 변수값 없음");
            return ret;
        }
        int emailCnt = userService.dupEmail(user);
        if (emailCnt > 0) {
            if(user.getEmail().equals(getCookieValue(request,"email"))) {
                //회원삭제처리
                userService.delete(user);
                //회원삭제가 제대로 처리 되면 쿠키를 삭제한다.
                setLogout(request, response);
            }
            else {
                ret.put("code", 104);
                ret.put("message", "접근권한 없음");
            }
        }
        else {
            ret.put("code", 102);
            ret.put("message", "아이디 없음");
        }
        return ret;
    }

    //회원탈퇴
    @PostMapping(value = "/withdraw")
    public Map<String, Object> withdraw(@ModelAttribute User user, HttpServletResponse response, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("code", 200);
        ret.put("message", "회원탈퇴 정상 처리");
        if (isNull(user.getEmail())) {
            ret.put("code", 100);
            ret.put("message", "필수 변수값 없음");
            return ret;
        }
        int emailCnt = userService.dupEmail(user);
        if (emailCnt > 0) {
            if(user.getEmail().equals(getCookieValue(request,"email"))) {
                //회원탈퇴처리
                userService.withdraw(user);
                //회원탈퇴가 제대로 처리 되면 쿠키를 삭제한다.
                setLogout(request, response);
            }
            else {
                ret.put("code", 104);
                ret.put("message", "접근권한 없음");
            }
        }
        else {
                ret.put("code", 102);
                ret.put("message", "아이디 없음");
        }
        return ret;
    }

    //회원 정보 수정
    @PostMapping(value = "/edit")
    public Map<String, Object> edit(@RequestBody User user, HttpServletResponse response, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("code", 200);
        ret.put("message", "회원정보 수정 정상 처리");
        if(isNull(user.getEmail()) ||    // 필수 변수값 없음
            isNull(user.getPassword()) ||
            isNull(user.getUserName()) ||
            isNull(user.getTel()) ||
            isNull(user.getAgency()) ||
            isNull(user.getCompanyTypeCode())) {
            ret.put("code", 100);
            ret.put("message", "필수 변수값 없음");
            return ret;
        }
        int emailPassCnt = userService.checkEmailPass(user); //이메일과 비밀번호가 일치하면 1, 불일치하면 0 반환
        if (emailPassCnt > 0) {
            if (user.getEmail().equals(getCookieValue(request,"email"))) {
                if (isNull(user.getNewPassword())) {
                    //회원정보 수정처리
                    user.setUpdStyle("ONLYINFO");
                    userService.edit(user);
                } else { // 비밀번호 변경처리
                    //userService.editPassword(user);
                    user.setUpdStyle("WITHPASS");
                    userService.edit(user);
                }
            } else { // 받아온 user의 email과 쿠키에 담겨있는 user의 email정보가 다른 경우
                ret.put("code", 104);
                ret.put("message", "접근권한 없음");
            }
        } else {
            ret.put("code", 103);
            ret.put("message", "비밀번호 불일치");
        }
        return ret;
    }

    //중복가입 확인
    @PostMapping(value = "/same")
    public Map<String, Object> same(@ModelAttribute User user) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("code", 200);
        ret.put("message", "중복가입 확인 정상 처리");
        if (isNull(user.getEmail())) {
            ret.put("code", 100);
            ret.put("message", "필수 변수값 없음");
            return ret;
        }
        int deletedUser = userService.deletedUser(user); // 1: 탈퇴한 유저 , 0: 탈퇴하지 않은 유저
        int emailCnt = userService.dupEmail(user);
        Map<String, Object> rst = new HashMap();
        String memberYn = (emailCnt == 1) ? "Y" : "N";
        rst.put("memberYn", memberYn);
        if(deletedUser == 0) {
            ret.put("result",  rst);
        } else {
            ret.put("code", 105);
            ret.put("message", "탈퇴한 회원 아이디");
        }
        return ret;
    }


    // 로그인
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map login(@ModelAttribute User user, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("code", 200);
        ret.put("message", "로그인 정상 처리");
        if (isNull(user.getEmail()) || isNull(user.getPassword())) {
            ret.put("code", 100);
            ret.put("message", "필수 변수값 없음");
            return ret;
        }
        int emailCnt = userService.dupEmail(user);  //1이면 아이디 존재, 0이면 아이디 없음
        if (emailCnt == 0) {
            ret.put("code", 102);
            ret.put("message", "아이디 없음");
            return ret;
        }
        int emailPassCnt = userService.checkEmailPass(user); //이메일과 비밀번호가 일치하면 1, 불일치하면 0 반환
        if (emailPassCnt == 0) {
            ret.put("code", 103);
            ret.put("message", "비밀번호 불일치");
            return ret;
        }
        int deletedUser = userService.deletedUser(user); // 1: 탈퇴한 유저 , 0: 탈퇴하지 않은 유저
        if (deletedUser > 0) {
            ret.put("code", 105);
            ret.put("message", "탈퇴한 회원 ID");
        } else {
            // login
            User login = userService.login(user);
            Cookie[] loginCookies = new Cookie[4];   // 쿠키 설정
            loginCookies[0] = new Cookie("email", URLEncoder.encode(login.getEmail(), "UTF-8")); //UTF-8로 인코딩
            loginCookies[1] = new Cookie("userNo",URLEncoder.encode(login.getUserNo() + "", "UTF-8"));
            loginCookies[2] = new Cookie("userGbCode", URLEncoder.encode(login.getUserGbCode(), "UTF-8"));
            loginCookies[3] = new Cookie("userName", URLEncoder.encode(login.getUserName(), "UTF-8"));
            for (Cookie c : loginCookies) {
                c.setPath("/");
                c.setMaxAge(-1);
                response.addCookie(c);
            }
        }
        return ret;
    }

    //로그아웃
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public Map logout(@ModelAttribute User user, HttpServletResponse response, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("code", 200);
        ret.put("message", "로그아웃 정상 처리");
        setLogout(request, response);

        return ret;
    }

    @Autowired
    private JavaMailSender javaMailSender;


    //  비밀번호 찾기
    @RequestMapping(value = "/findPw", method = RequestMethod.POST)
    public Map<String, Object> findPw(@ModelAttribute User user) throws Exception {
        Map<String, Object> rst = new HashMap();
        if (user.getEmail() == null) {
            rst.put("code", 101);
            rst.put("message", "아이디 없음");
            return rst;
        }
        int chkNo = userService.findUserNo(user);
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
        String to = user.getEmail(); //받는 사람
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
        rst.put("code", 200);
        rst.put("message", "인증 정상 처리");
        return rst;
    }

    //비밀번호 재설정
    @RequestMapping(value = "/pwSet", method = RequestMethod.POST)
    public Map<String, Object> setPw(@ModelAttribute User user) throws Exception {
        Map<String, Object> rst = new HashMap();
        if (user.getPassword() == null || user.getPassword().equals("")) {
            rst.put("code", 100);
            rst.put("message", "필수값 없음");
            return rst;
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
