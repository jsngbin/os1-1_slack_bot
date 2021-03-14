package com.tmaxos.os1_1_slack_bot.service;

import java.util.ArrayList;
import java.util.List;
import com.tmaxos.os1_1_slack_bot.template.SlackCommandExecutable;

public class TeamScheduleService implements SlackCommandExecutable {


    private List<String> commands;

    TeamScheduleService(){
        commands = new ArrayList<>();
    }

    @Override
    public boolean matchCommand(String command) {
        for(String cmd : commands){
            if(cmd.equals(command)) return true;
        }
        return false;
    }

    @Override
    public String getCommandReplyMessage(String command) {
        return null;
    }

    @Override
    public String getCommandReplyTitle(String command) {
        return null;
    }
}