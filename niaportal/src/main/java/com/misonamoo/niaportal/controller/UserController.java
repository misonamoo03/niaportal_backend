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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.misonamoo.niaportal.common.CommonUtil.*;

@RestController
@RequestMapping("/User")
public class UserController extends BaseController{

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
        ret.put("status", 200);
        int emailCnt = userService.dupEmail(user);  // 0이면 이메일 중복 x, 1이면 이메일 중복
        if (isNull(user.getEmail()) ||    // 필수 변수값 없음
                isNull(user.getPassword()) ||
                isNull(user.getUserName()) ||
                isNull(user.getTel()) ||
                isNull(user.getAgency()) ||
                isNull(user.getCompanyTypeCode())) {
            ret.put("status", 100);
        } else if (emailCnt > 0) {
            ret.put("status", 101);
        } else {
            // 회원가입처리
            userService.register(user);
        }
        return returnMap(ret);
    }

    //회원 조회
    @GetMapping(value = "/inquiry")
    public Map<String, Object> delete(@ModelAttribute User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("status", 200);

        if (user.getEmail().equals(getCookieValue(request, "email"))) {
            User info = userService.inquiry(user);
            Map<String, Object> rst = new HashMap();
            rst.put("email", user.getEmail());
            rst.put("userName", info.getUserName());
            rst.put("tel", info.getTel());
            rst.put("agency", info.getAgency());
            rst.put("CompanyTypeCode", info.getCompanyTypeCode());
            rst.put("CompanyTypeName", info.getEmail());

            ret.put("data", rst);
        } else {
            ret.put("status", 104);
        }
        return returnMap(ret);
    }


    //회원삭제
    @PostMapping(value = "/delete")
    public Map<String, Object> delete(@ModelAttribute User user, HttpServletResponse response, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("status", 200);
        if (CommonUtil.isNull(user.getEmail())) {
            ret.put("status", 100);
            return returnMap(ret);
        }
        int emailCnt = userService.dupEmail(user);
        if (emailCnt > 0) {
            if (user.getEmail().equals(getCookieValue(request, "email"))) {
                //회원삭제처리
                userService.delete(user);
                //회원삭제가 제대로 처리 되면 쿠키를 삭제한다.
                setLogout(request, response);
            } else {
                ret.put("status", 104);
            }
        } else {
            ret.put("status", 105);
            ret.put("message", "아이디 없음");
        }
        return returnMap(ret);
    }

    //회원탈퇴
    @PostMapping(value = "/withdraw")
    public Map<String, Object> withdraw(@ModelAttribute User user, HttpServletResponse response, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("status", 200);
        if (isNull(user.getEmail())) {
            ret.put("status", 100);
            return returnMap(ret);
        }
        int emailCnt = userService.dupEmail(user);
        if (emailCnt > 0) {
            if (user.getEmail().equals(getCookieValue(request, "email"))) {
                //회원탈퇴처리
                userService.withdraw(user);
                //회원탈퇴가 제대로 처리 되면 쿠키를 삭제한다.
                setLogout(request, response);
            } else {
                ret.put("status", 104);
            }
        } else {
            ret.put("status", 105);
            ret.put("message", "아이디 없음");
        }
        return returnMap(ret);
    }

    //회원 정보 수정
    @PostMapping(value = "/edit")
    public Map<String, Object> edit(@RequestBody User user, HttpServletResponse response, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("status", 200);
        if (isNull(user.getEmail()) ||    // 필수 변수값 없음
                isNull(user.getPassword()) ||
                isNull(user.getUserName()) ||
                isNull(user.getTel()) ||
                isNull(user.getAgency()) ||
                isNull(user.getCompanyTypeCode())) {
            ret.put("code", 100);
            return returnMap(ret);
        }
        int emailPassCnt = userService.checkEmailPass(user); //이메일과 비밀번호가 일치하면 1, 불일치하면 0 반환
        if (emailPassCnt > 0) {
            if (user.getEmail().equals(getCookieValue(request, "email"))) {
                if (isNull(user.getNewPassword())) {
                    //회원정보 수정처리
                    user.setUpdStyle("ONLYINFO");
                } else { // 비밀번호 변경처리
                    //userService.editPassword(user);
                    user.setUpdStyle("WITHPASS");
                }
                userService.edit(user);
            } else { // 받아온 user의 email과 쿠키에 담겨있는 user의 email정보가 다른 경우
                ret.put("code", 104);
            }
        } else {
            ret.put("status", 102);
        }
        return returnMap(ret);
    }

    //중복가입 확인
    @PostMapping(value = "/same")
    public Map<String, Object> same(@ModelAttribute User user) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("status", 200);
        if (isNull(user.getEmail())) {
            ret.put("status", 100);
            return returnMap(ret);
        }
        int deletedUser = userService.deletedUser(user); // 1: 탈퇴한 유저 , 0: 탈퇴하지 않은 유저
        int emailCnt = userService.dupEmail(user);
        Map<String, Object> rst = new HashMap();
        String memberYn = (emailCnt == 1) ? "Y" : "N";
        rst.put("memberYn", memberYn);
        if (deletedUser == 0) {
            ret.put("data", rst);
        } else {
            ret.put("status", 103);
        }
        return returnMap(ret);
    }


    // 로그인
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map login(@ModelAttribute User user, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("status", 200);
        if (isNull(user.getEmail()) || isNull(user.getPassword())) {
            ret.put("status", 100);
            return returnMap(ret);
        }
        int emailCnt = userService.dupEmail(user);  //1이면 아이디 존재, 0이면 아이디 없음
        if (emailCnt == 0) {
            ret.put("code", 105);
            return returnMap(ret);
        }
        int emailPassCnt = userService.checkEmailPass(user); //이메일과 비밀번호가 일치하면 1, 불일치하면 0 반환
        if (emailPassCnt == 0) {
            ret.put("status", 102);
            return returnMap(ret);
        }
        int deletedUser = userService.deletedUser(user); // 1: 탈퇴한 유저 , 0: 탈퇴하지 않은 유저
        if (deletedUser > 0) {
            ret.put("status", 103);
        } else {
            // login
            User login = userService.login(user);
            Cookie[] loginCookies = new Cookie[4];   // 쿠키 설정
            loginCookies[0] = new Cookie("email", URLEncoder.encode(login.getEmail(), "UTF-8")); //UTF-8로 인코딩
            loginCookies[1] = new Cookie("userNo", URLEncoder.encode(login.getUserNo() + "", "UTF-8"));
            loginCookies[2] = new Cookie("userGbCode", URLEncoder.encode(login.getUserGbCode(), "UTF-8"));
            loginCookies[3] = new Cookie("userName", URLEncoder.encode(login.getUserName(), "UTF-8"));
            for (Cookie c : loginCookies) {
                c.setPath("/");
                c.setMaxAge(-1);
                response.addCookie(c);
            }
        }
        return returnMap(ret);
    }

    //로그아웃
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public Map logout(@ModelAttribute User user, HttpServletResponse response, HttpServletRequest request) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("status", 200);
        setLogout(request, response);

        return returnMap(ret);
    }

    @Autowired
    private JavaMailSender javaMailSender;


    // 비밀번호 찾기 요청
    @RequestMapping(value = "/findPw", method = RequestMethod.POST)
    public Map<String, Object> findPw(@ModelAttribute User user) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("status", 200);
        if (isNull(user.getEmail())) {
            ret.put("status", 100);
            return returnMap(ret);
        }
        int emailCnt = userService.dupEmail(user);  //1이면 아이디 존재, 0이면 아이디 없음
        if (emailCnt == 0) {
            ret.put("status", 105);
            return returnMap(ret);
        }
        int deletedUser = userService.deletedUser(user); // 1: 탈퇴한 유저 , 0: 탈퇴하지 않은 유저
        if (deletedUser > 0) {
            ret.put("status", 103);
            return returnMap(ret);
        }

        Map<String, Object> rst = new HashMap();
        rst.put("email", user.getEmail());
        ret.put("data", rst);

        int chkNo = userService.findUserNo(user);
        PwSec pwSec = new PwSec();
        pwSec.setUserNo(chkNo);
        pwSec.setSecCode(pwSecService.findCode(pwSec.getUserNo()));
        String codeBuf = "";
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
        if (isNull(pwSec.getSecCode())) {
            pwSecService.setCode(pwSec);
        } else {
            pwSecService.updateCode(pwSec);
        }
//      메일 발송 부분
        String to = user.getEmail(); //받는 사람
        String from = "misonamoo03@gmail.com"; //보내는 사람
        String subject = "이 프로젝트의 비밀번호 찾기 메일입니다."; //제목
        String body = pwSec.getSecCode(); //내용

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        mimeMessageHelper.setFrom(from, "수신자명");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(body, true);
        javaMailSender.send(message);
        return returnMap(ret);
    }

    //인증요청
    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    public Map<String, Object> validate(@ModelAttribute User user, @ModelAttribute PwSec pwSec, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("status", 200);
        if (isNull(user.getEmail()) || isNull(pwSec.getSecCode())) {
            ret.put("code", 100);
            return returnMap(ret);
        }
        int chkNo = userService.findUserNo(user); // 회원 번호를 저장
        String endTime = pwSecService.getEndTime(chkNo);
        Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime);
        Date now = new Date();
        if (now.after(endDate)) {
            ret.put("status", 106);
            ret.put("message", "인증 기간 만료");
            return returnMap(ret);
        }
        String secCode = pwSecService.findCode(chkNo); // DB에 저장된 회원의 인증 코드를 가져옴
        if (!pwSec.getSecCode().equals(secCode)) {
            ret.put("status", 107);
            ret.put("message", "인증 코드 불일치");
            return returnMap(ret);
        } else {
            Cookie secCodeCookie = new Cookie("secCode", URLEncoder.encode("재설정 권한 부여", "UTF-8"));
            secCodeCookie.setPath("/");
            secCodeCookie.setMaxAge(60 * 10); //쿠키 유효시간 10분으로 설정
            response.addCookie(secCodeCookie);
        }
        return returnMap(ret);
    }

    //비밀번호 재설정
    @RequestMapping(value = "/setPw", method = RequestMethod.POST)
    public Map<String, Object> setPw(@ModelAttribute User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("status", 200);
        if (isNull(user.getEmail()) || isNull(user.getPassword())) {
            ret.put("status", 100);
            return returnMap(ret);
        }
        if("재설정 권한 부여".equals(getCookieValue(request, "secCode"))){
            String password = user.getPassword();
            password = SHA256Util.getEncrypt(password, salt);
            user.setPassword(password);
            userService.setPw(user);
            setLogout(request, response);
        } else {
            ret.put("status", 108);
            ret.put("message", "비밀번호 재설정 유효기간 만료");
        }
        return returnMap(ret);
    }
}
