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
        HttpSession session = request.getSession(false);

        if(session == null) {
            logger.debug(user + " logout.");
            return "Logout Success";
        }
        else if(user.equals(session.getAttribute("login"))){
            logger.debug(user + " logout.");
            session.invalidate();
            return "Logout Success";
        }
        return "Logout Failed";
    }

    @PostMapping(path="/login")
    public String login(HttpServletRequest request, @RequestParam("user") String user, @RequestParam("pass") String pass){

        HttpSession session = request.getSession(false);

        if(session == null) {
            if (loginService.loginCheck(user, pass)) {
                request.getSession(true).setAttribute("login", user);
                logger.debug(user + " login.");
                return "Login Success";
            }
        }
        else{
            if(user.equals(session.getAttribute("login"))){
                logger.debug(user + " login.(with session)");
                return "Login Success";
            }
        }
        return "Login Failed";
    }
}