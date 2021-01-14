package com.tmaxos.os1_1_slack_bot.controller;

import com.tmaxos.os1_1_slack_bot.service.SlackService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@RestController
public class SlackController {

    private Logger logger = LoggerFactory.getLogger(SlackController.class);

    @Autowired
    private SlackService slackService;

    @PostMapping(path="/events", consumes=MediaType.APPLICATION_JSON_VALUE)
    public String handleEvents(@RequestHeader("X-Slack-Signature") String signature, @RequestHeader("X-Slack-Request-Timestamp") String timestamp, @RequestBody String message){
        logger.info(timestamp);
        logger.info(signature);
        logger.info(message);

        // TODO: using interceptor?

        if(!slackService.validateSlackEvent(signature, timestamp, message)) return "";
        return slackService.handleEvent(message);
    }

    @RequestMapping("/")
    public String handleHello(){
        return "Hello OS1-1 Slack Bot";
    }
}
