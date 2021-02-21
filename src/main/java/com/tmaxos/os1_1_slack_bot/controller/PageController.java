package com.tmaxos.os1_1_slack_bot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/")
    public String handleHello(){
        return "index.html";
    }
}
