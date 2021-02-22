package com.tmaxos.os1_1_slack_bot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/")
    public String handleHello(Model model){
        model.addAttribute("serviceName", "OS1-1 SlackBot");
        return "index";
    }

    @RequestMapping("/admin")
    public String handleAdmin(Model model){
        model.addAttribute("serviceName", "OS1-1 SlackBot");
        return "admin";
    }

    @RequestMapping("/login_page")
    public String handleLogin(Model model){
        model.addAttribute("serviceName", "OS1-1 SlackBot");
        return "login_page";
    }
}
