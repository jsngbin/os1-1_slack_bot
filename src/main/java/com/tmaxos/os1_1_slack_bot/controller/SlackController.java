package com.tmaxos.os1_1_slack_bot.controller;

import com.tmaxos.os1_1_slack_bot.dto.MenuDTO;
import com.tmaxos.os1_1_slack_bot.service.BobMenuService;
import com.tmaxos.os1_1_slack_bot.service.SlackService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@RestController
public class SlackController {

    private Logger logger = LoggerFactory.getLogger(SlackController.class);

    @Autowired
    private SlackService slackService;

    @Autowired
    private BobMenuService bobMenuService;

    @PostMapping(path="/events", consumes=MediaType.APPLICATION_JSON_VALUE)
    public String handleEvents(@RequestBody String message){
        return slackService.handleEvent(message);
    }

    @GetMapping(path="/api/menu")
    public MenuDTO getMenu(@RequestParam(value = "type", required = false) String lunchOrDinner){
        return bobMenuService.getTodayMenu(lunchOrDinner);
    }

}
