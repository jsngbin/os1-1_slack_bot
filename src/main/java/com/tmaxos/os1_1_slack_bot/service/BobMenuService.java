package com.tmaxos.os1_1_slack_bot.service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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
            if(command.contains(message)) return true;
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

        List<String> options = parseCommand(command);
        Collections.reverse(options);

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        if(options.get(0).equals("알려줘")){
            String menu = options.get(1);
            int day = cal.get(Calendar.DAY_OF_WEEK);
            if(options.size() > 2){
                String dayStr = options.get(2);
                if(dayStr.equals("내일")) day += 1;
                else if(dayStr.contains("요일"))
                    day = getDayFromChar(dayStr.charAt(0));
            }
            if(menu.equals("all")){
                replyMessage.append(convertDtoToSlackMessage(getMenu("lunch", day)));
                replyMessage.append(convertDtoToSlackMessage(getMenu("dinner", day)));
            }
            else {
                replyMessage.append(convertDtoToSlackMessage(getMenu(menu, day)));
            }
        }
        else{
            replyMessage.append("메뉴정보가 없거나 잘못된 입력입니다.");
        }

        return replyMessage.toString();
    }
    public void updateMenuFile(File newMenu){
        newMenu.renameTo(new File(parser.getMenuPath()));
        parser.reload();
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
        List<List<String>> menuContents = parser.getMenuContents(menu, dayForParser);

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

        if(dto.getMenus() == null || dto.getMenus().isEmpty()){
            builder.append("메뉴 정보가 없습니다.");
        }
        else{
            for(List<String> menu: dto.getMenus()) {
                builder.append(menu);
                builder.append("\n");
            }
        }
        builder.append("\n");
        return builder.toString();
    }

    private List<String> parseCommand(String command){
        return Arrays.stream(command.split(" "))
                .filter(t -> t.contains("알려줘")
                        || t.contains("밥") || t.contains("저녁") || t.contains("점심")
                        || t.contains(("요일")) || t.contains("내일") || t.contains("오늘"))
                .map(t -> {
                    switch (t) {
                        case "저녁":
                            return "dinner";
                        case "점심":
                            return "lunch";
                        case "밥":
                            return "all";
                    }
                    return t;
                })
                .collect(Collectors.toList());
    }
    private int getDayFromChar(char c) throws IllegalFormatConversionException{

        int day;
        switch(c){
            case '월':
                day = 2;
                break;
            case '화':
                day = 3;
                break;
            case '수':
                day = 4;
                break;
            case '목':
                day = 5;
                break;
            case '금':
                day = 6;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + c);
        }
        return day;
    }

}