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
    @PostMapping(value = "/inquiry")
    public Map<String, Object> delete(@ModelAttribute User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("status", 200);
        if (isNull(user.getEmail())) {
            ret.put("status", 100);
            return returnMap(ret);
        }
        if (user.getEmail().equals(getCookieValue(request, "email"))) {
            User info = userService.inquiry(user);
            Map<String, Object> data = new HashMap();
            Map<String, Object> memberInfo = new HashMap();
            memberInfo.put("email", user.getEmail());
            memberInfo.put("userName", info.getUserName());
            memberInfo.put("tel", info.getTel());
            memberInfo.put("agency", info.getAgency());
            memberInfo.put("companyTypeCode", info.getCompanyTypeCode());
            memberInfo.put("companyTypeName", info.getCompanyTypeName());
            data.put("memberInfo", memberInfo);
            ret.put("data", data);
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
            ret.put("status", 109);
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
            ret.put("status", 109);
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
            ret.put("status", 100);
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
                ret.put("status", 104);
            }
        } else {
            ret.put("status", 102);
        }
        return returnMap(ret);
    }

    //중복가입 확인
    @GetMapping(value = "/same")
    public Map<String, Object> same(@ModelAttribute User user) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("status", 200);
        if (isNull(user.getEmail())) {
            ret.put("status", 100);
            return returnMap(ret);
        }
        int deletedUser = userService.deletedUser(user); // 1: 탈퇴한 유저 , 0: 탈퇴하지 않은 유저
        int emailCnt = userService.dupEmail(user);
        Map<String, Object> data = new HashMap();
        String memberYn = (emailCnt == 1) ? "Y" : "N";
        data.put("memberYn", memberYn);
        if (deletedUser == 0) {
            ret.put("data", data);
        } else {
            ret.put("status", 103);
        }
        return returnMap(ret);
    }


    // 로그인
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map login(@ModelAttribute User user, HttpServletRequest request ,HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("status", 200);
        if (isNull(user.getEmail()) || isNull(user.getPassword())) {
            ret.put("status", 100);
            return returnMap(ret);
        }
        int emailCnt = userService.dupEmail(user);  //1이면 아이디 존재, 0이면 아이디 없음
        int emailPassCnt = userService.checkEmailPass(user); //이메일과 비밀번호가 일치하면 1, 불일치하면 0 반환
        int deletedUser = userService.deletedUser(user); // 1: 탈퇴한 유저 , 0: 탈퇴하지 않은 유저
        if ((emailCnt == 0) || (emailPassCnt == 0) || (deletedUser > 0)) {
            ret.put("status", 102);
            return returnMap(ret);
        }
        // login
        User login = userService.login(user);
        Cookie[] loginCookies = new Cookie[4];   // 쿠키 설정
        loginCookies[0] = new Cookie("email", URLEncoder.encode(login.getEmail(), "UTF-8")); //UTF-8로 인코딩
        loginCookies[1] = new Cookie("userNo", URLEncoder.encode(login.getUserNo() + "", "UTF-8"));
        loginCookies[2] = new Cookie("userGbCode", URLEncoder.encode(login.getUserGbCode(), "UTF-8"));
        loginCookies[3] = new Cookie("userName", URLEncoder.encode(login.getUserName(), "UTF-8"));
        for (Cookie c : loginCookies) {
            c.setPath("/");
            c.setMaxAge(60 * 60 * 24); // 쿠키 지속시간 하루
            response.addCookie(c);
        }

        User info = userService.inquiry(user);
        Map<String, Object> data = new HashMap();
        Map<String, Object> memberInfo = new HashMap();
        memberInfo.put("email", user.getEmail());
        memberInfo.put("userName", info.getUserName());
        memberInfo.put("tel", info.getTel());
        memberInfo.put("agency", info.getAgency());
        memberInfo.put("CompanyTypeCode", info.getCompanyTypeCode());
        memberInfo.put("CompanyTypeName", info.getCompanyTypeName());
        data.put("memberInfo", memberInfo);
        ret.put("data", data);

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
            ret.put("status", 109);
            return returnMap(ret);
        }
        int deletedUser = userService.deletedUser(user); // 1: 탈퇴한 유저 , 0: 탈퇴하지 않은 유저
        if (deletedUser > 0) {
            ret.put("status", 103);
            return returnMap(ret);
        }

        int chkNo = userService.findUserNo(user);
        Map<String, Object> data = new HashMap();
        data.put("userNo", chkNo);
        ret.put("data", data);

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
        if (isNull(pwSec.getSecCode())) {
            pwSec.setSecCode(codeBuf);
            pwSecService.setCode(pwSec);
        } else {
            pwSec.setSecCode(codeBuf);
            pwSecService.updateCode(pwSec);
        }
//      메일 발송 부분
        String to = user.getEmail(); //받는 사람
        String from = "misonamoo03@gmail.com"; //보내는 사람
        String subject = "이 프로젝트의 비밀번호 찾기 메일입니다."; //제목
        String body =
                    "<!DOCTYPE html>\n" +
                            "<html lang=\"ko\" style=\"margin:0;padding:0;width:100%;\">\n" +
                            "<head>\n" +
                            "<meta charset=\"utf-8\">\n" +
                            "<title>메</title>\n" +
                            "</head>\n" +
                            "<body style=\"margin:0;padding:0;width:100%;background:#fff;min-width:320px;-webkit-text-size-adjust:none;word-wrap:break-word;word-break:keep-all;letter-spacing:-.5px;border:none;font-size:16px;font-family:'Noto Sans KR', sans-serif;color:#000;\">\n" +
                            "\t\t<div id=\"mail_wrap\" style=\"margin:0 auto;padding:0;width:800px;border:1px solid #ddd;font-size:15px;border-top:4px solid #000;\"> \n" +
                            "\t\t\t<div id=\"mail_header\" style='margin:0;padding:60px 0 0 35px;height:232px;background-image:url(\"mail_topbg.jpg\");background-position:top center;background-repeat:no-repeat;border-bottom:1px solid #ddd;'> \n" +
                            "\t\t\t\t<img src=\"http://sportsaihub.com/_nuxt/mail_logo.png\" alt=\"mail_logo\" style=\"border:0;vertical-align:middle;margin-bottom:20px;\"><h1 style=\"margin:0;padding:0;font-size:32px;\">비밀번호 재설정 요청</h1>\n" +
                            "\t\t\t</div>\n" +
                            "\t\t\t<div id=\"mail_contents\" style=\"margin:0;padding:50px 45px;background-color:#f1f2f7;text-align:center;line-height:1.6;\"> \n" +
                            "\t\t\t\t<p style=\"margin:0;padding:0;\">\n" +
                            "\t\t\t\t\t누군가(귀하이길 바랍니다) 귀하의 sportsaihub 계정 비밀번호 재설정을 요청했습니다.<br>\n" +
                            "\t\t\t\t\t아래 버튼을 클릭하여 인증번호를 입력 후 비밀번호를 재설정해주세요.<br>\n" +
                            "\t\t\t\t\t귀하께서 비밀번호 재설정을 요청하지 않았으면 본 이메일을 무시하세요<br></p>\n" +
                            "\t\t\t\t<div class=\"mail_numbox\" style=\"margin:0;padding:30px 0;width:100%;background-color:#fff;margin-top:30px;\">\n" +
                            "\t\t\t\t\t<p class=\"mail_num\" style=\"margin:0;padding:0;font-size:25px;font-weight:bold;margin-bottom:10px;\">인증번호 : " + pwSec.getSecCode() + "</p>\n" +
                            "\t\t\t\t\t<p style=\"margin:0;padding:0;\">※ 인증번호 유효시간은 1시간 입니다.</p>\n" +
                            "\t\t\t\t</div>\n" +
                            " \n" +
                            "\t\t\t\t<div class=\"btn_area\" style=\"margin:0;padding:0;margin-top:30px;\">\n" +
                            "\t\t\t\t\t\t<a href= 'http://sportsaihub.com/member/verification/" + chkNo + "' id=\"btnlogin\" class=\"btn_type btn_primary\" style=\"text-decoration:none; margin:0;padding:0;border:none;font-size:18px;font-family:'Noto Sans KR', sans-serif;color:#fff;vertical-align:middle;display:block;width:100%;text-align:center;cursor:pointer;line-height:55px;border-radius:100px;background-color:#2046b3;\"><span>비밀번호 재설정하기</span></button>\n" +
                            "\t\t\t\t\t</div>\n" +
                            "\t\t\t</div> \n" +
                            "\t\t\t<div id=\"mail_footer\" style=\"margin:0;padding:30px 0;text-align:center;\"> \n" +
                            "\t\t\t\t본 메일은 발신전용 메일입니다.\n" +
                            "\t\t\t</div> \n" +
                            "\t\t</div> \n" +
                            "\t</body>\n" +
                            "</html>";

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
        if (isNull(String.valueOf(user.getUserNo())) || isNull(pwSec.getSecCode())) {
            ret.put("status", 100);
            return returnMap(ret);
        }
        String endTime = pwSecService.getEndTime((int) user.getUserNo());
        Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime);
        Date now = new Date();
        if (now.after(endDate)) {
            ret.put("status", 106);
            return returnMap(ret);
        }
        String secCode = pwSecService.findCode((int) user.getUserNo()); // DB에 저장된 회원의 인증 코드를 가져옴
        if (!pwSec.getSecCode().equals(secCode)) {
            ret.put("status", 107);
            return returnMap(ret);
        } else {
            Cookie secCodeCookie = new Cookie("secCode", URLEncoder.encode("right", "UTF-8"));
            secCodeCookie.setPath("/");
            secCodeCookie.setMaxAge(60 * 60); // 인증 후 비밀번호 재설정 유효시간 1시간
            response.addCookie(secCodeCookie);
        }
        return returnMap(ret);
    }

    //비밀번호 재설정
    @RequestMapping(value = "/setPw", method = RequestMethod.POST)
    public Map<String, Object> setPw(@ModelAttribute User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> ret = new HashMap();
        ret.put("status", 200);
        if (isNull(String.valueOf(user.getUserNo())) || isNull(user.getPassword())) {
            ret.put("status", 100);
            return returnMap(ret);
        }
        System.out.println(request.getCookies());
        if("right".equals(getCookieValue(request, "secCode"))){
            String password = user.getPassword();
            password = SHA256Util.getEncrypt(password, salt);
            user.setPassword(password);
            userService.setPw(user);
            setLogout(request, response);
        } else {
            ret.put("status", 108);
        }
        return returnMap(ret);
    }
}