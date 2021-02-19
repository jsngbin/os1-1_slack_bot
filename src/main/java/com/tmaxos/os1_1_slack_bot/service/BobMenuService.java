package com.tmaxos.os1_1_slack_bot.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.tmaxos.os1_1_slack_bot.dto.MenuDTO;
import com.tmaxos.os1_1_slack_bot.template.SlackCommandExecutable;

import com.tmaxos.os1_1_slack_bot.util.TmaxMenuParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BobMenuService implements SlackCommandExecutable{

    private final Logger logger = LoggerFactory.getLogger(BobMenuService.class);
 
    private List<String> messageCommands;

    static public String LUNCH = "lunch";
    static public String DINNER = "dinner";

    @Autowired
    private TmaxMenuParser parser;

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
            replyMessage.append(convertDtoToSlackMessage(getTodayLunch()));
            replyMessage.append(convertDtoToSlackMessage(getTodayDinner()));
        }
        else if(command.contains("점심 알려줘")){
            replyMessage.append(convertDtoToSlackMessage(getTodayLunch()));
        }
        else if(command.contains("저녁 알려줘")){
            replyMessage.append(convertDtoToSlackMessage(getTodayDinner()));
        }
        else{
            replyMessage.append("메뉴정보가 없거나 잘못된 입력입니다.");
        }        
        return replyMessage.toString();
    }

    public MenuDTO getMenu(String lunchOrDinner, int day){

        TmaxMenuParser.Menu menu;
        if(lunchOrDinner.equals(LUNCH)) {
            menu = TmaxMenuParser.Menu.LUNCH;
        }
        else if(lunchOrDinner.equals(DINNER)){
            menu = TmaxMenuParser.Menu.DINNER;
        }
        else {
            logger.error("invalid menu request. it is lunch neither dinner.");
            return null;
        }

        MenuDTO dto = new MenuDTO();
        dto.setLunchOrDinner(lunchOrDinner);

        TmaxMenuParser.Day dayForParser = TmaxMenuParser.Day.fromInt(day);
        parser.load(false); // FIXME. i don't want to write this code here.
        List<String> menuContents = parser.getMenuContents(menu, dayForParser);

        dto.setMenus(menuContents);
        return dto;
    }

    public MenuDTO getTodayMenu(String lunchOrDinner){
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return getMenu(lunchOrDinner, cal.get(Calendar.DAY_OF_WEEK));
    }

    private MenuDTO getTodayLunch(){
        return getTodayMenu(LUNCH);
    }
    private MenuDTO getTodayDinner(){
        return getTodayMenu(DINNER);
    }

    private String convertDtoToSlackMessage(MenuDTO dto){
        StringBuilder builder = new StringBuilder();
        builder.append("*<");
        builder.append(dto.getLunchOrDinner());
        builder.append(">*\n");

        for(String menu: dto.getMenus()) {
            builder.append(menu);
            builder.append("\t");
        }
        builder.append("\n");
        return builder.toString();
    }
}
