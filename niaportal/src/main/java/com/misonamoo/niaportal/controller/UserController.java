package com.misonamoo.niaportal.controller;

import com.misonamoo.niaportal.service.UserService;
import com.misonamoo.niaportal.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/User")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    // 로그인
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public UserVO login(@ModelAttribute UserVO vo, HttpServletRequest req, RedirectAttributes rttr) throws Exception {
        HttpSession session = req.getSession();
        UserVO result = userService.login(vo);
        log.info("=========UserVO========== " + result);
        if (result == null) {
            session.setAttribute("User", null);
            rttr.addFlashAttribute("msg", false);
        } else {
            session.setAttribute("User", result);
        }
        log.info("=========UserVO========== " + result);
        return result;
    }



    //로그인 되어 있는지 체크
    @ResponseBody
    @RequestMapping(value = "/loginChk", method = RequestMethod.GET)
    public Map loginChk(HttpServletRequest req, RedirectAttributes rttr) throws Exception {
        Map<String, String> result = new HashMap<String, String>();
        HttpSession session = req.getSession();
        String id = "";
        String loginYN = "N";
        if (session != null && session.getAttribute("User") != null) {
            id = ((UserVO) session.getAttribute("User")).getId();
            loginYN = "Y";
        }
        result.put("id", id);
        result.put("loginYN", loginYN);
        return result;
    }

    // 로그아웃
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) throws Exception {
        session.invalidate();
        return "redirect:/";
    }

    //아이디 찾기
    @RequestMapping(value = "/findId", method = RequestMethod.GET)
    public String findId(@ModelAttribute UserVO vo) throws Exception {
        String result = userService.findId(vo);
        return result;
    }


}
