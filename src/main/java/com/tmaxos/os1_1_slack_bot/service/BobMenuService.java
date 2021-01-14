package com.tmaxos.os1_1_slack_bot.service;

import java.util.ArrayList;
import java.util.List;

import com.tmaxos.os1_1_slack_bot.template.SlackCommandExecutable;
import com.tmaxos.os1_1_slack_bot.util.ExcelParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BobMenuService implements SlackCommandExecutable{

    private Logger logger = LoggerFactory.getLogger(BobMenuService.class);

    private List<String> messageCommands;

    @Autowired
    private ExcelParser parser;

    public BobMenuService(){
        messageCommands = new ArrayList<>();

        messageCommands.add("저녁 알려줘");
        messageCommands.add("점심 알려줘");
        messageCommands.add("밥 알려줘");

    }

    @Override
    public boolean matchCommand(String command) {

        for(String message : messageCommands){
            if(message.equals(command)) return true;

        }
        return false;
    }
    @Override
    public String getCommandReplyTitle(String command) {
        return "메뉴를 알려드립니다.";
    }
    @Override
    public String getCommandReplyMessage(String command) {
        StringBuilder replyMessage = new StringBuilder();
        if(command.contains("밥 알려줘")){
            replyMessage.append(getTodayLunch());
            replyMessage.append(getTodayDinner());
        }
        else if(command.contains("점심 알려줘")){
            replyMessage.append(getTodayLunch());
        }
        else if(command.contains("저녁 알려줘")){

            replyMessage.append(getTodayDinner());
        }
        else{
            replyMessage.append("메뉴정보가 없거나 잘못된 입력입니다.");
        }
        return replyMessage.toString();
    }

    private String getTodayLunch(){
        parser.loadExcel(false);
        return parser.getCellDatas(ExcelParser.RowIndex.LUNCH);

    }
    private String getTodayDinner(){
        parser.loadExcel(false);
        return parser.getCellDatas(ExcelParser.RowIndex.DINNER);

    }


}
