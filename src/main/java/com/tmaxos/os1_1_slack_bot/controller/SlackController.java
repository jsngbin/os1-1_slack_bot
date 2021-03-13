package com.tmaxos.os1_1_slack_bot.controller;

import com.tmaxos.os1_1_slack_bot.dto.MenuDTO;
import com.tmaxos.os1_1_slack_bot.service.BobMenuService;
import com.tmaxos.os1_1_slack_bot.service.SlackService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

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
    public MenuDTO getMenu(@RequestParam(value = "type", required = false) String lunchOrDinner,
                           @RequestParam(value = "day", required = false) Integer day){
        if(day == null)
            return bobMenuService.getTodayMenu(lunchOrDinner);
        return bobMenuService.getMenu(lunchOrDinner, day);
    }

    @PostMapping(path="/api/menu")
    public String menuUpload(@RequestPart MultipartFile file){
        logger.info("Menu upload requested");
        logger.info("file Content Type : " + file.getContentType());
        logger.info("file name : " + file.getOriginalFilename());
        logger.info("file size : " + file.getSize() + "byte");
        File dest = new File("/tmp/" + "menu_latest.xlsx");
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
            return "upload failed";
        }
        bobMenuService.updateMenuFile(dest);
        return "upload success";
    }

}