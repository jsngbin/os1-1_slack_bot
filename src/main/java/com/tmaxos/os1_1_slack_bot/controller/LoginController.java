package com.tmaxos.os1_1_slack_bot.controller;

import com.tmaxos.os1_1_slack_bot.service.BobMenuService;
import com.tmaxos.os1_1_slack_bot.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    LoginService loginService;

    @PostMapping(path="/logout")
    public String logout(HttpServletRequest request, @RequestParam("user") String user){
        HttpSession session = request.getSession();

        if(user.equals(session.getAttribute("login"))){
            logger.debug(user + " logout.");
            session.invalidate();
            //session.setAttribute("login", null);
            return "Logout Success";
        }
        return "Logout Success";
    }

    @PostMapping(path="/login")
    public String login(HttpServletRequest request, @RequestParam("user") String user, @RequestParam("pass") String pass){

        HttpSession session = request.getSession();
        String userInfo = (String)session.getAttribute("login");
        if(userInfo == null) {
            if (loginService.loginCheck(user, pass)) {
                session.setAttribute("login", user);
                logger.debug(user + " login.");
                return "Login Success";
            }
            logger.debug("login failed..");
            return "Login Failed";
        }
        else if(!userInfo.equals(user)){
            return "Login Failed";
        }
        logger.debug(user + " login.(already login");
        return "Login Success";
    }
}