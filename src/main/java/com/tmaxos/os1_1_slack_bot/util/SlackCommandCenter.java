package com.tmaxos.os1_1_slack_bot.util;

import com.tmaxos.os1_1_slack_bot.service.BobMenuService;
import com.tmaxos.os1_1_slack_bot.template.SlackCommandExecutable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Component
public class SlackCommandCenter {

    // see CommandServiceConfig.java
    @Qualifier("command-list")
    @Autowired
    List<SlackCommandExecutable> commands;

    private SlackCommandExecutable getMatchCommand(String command){

        for(SlackCommandExecutable cmd : commands){
            if(cmd.matchCommand(command)) return cmd;
        }
        return null;
    }

    public List<String> execute(String command){
        SlackCommandExecutable executable = getMatchCommand(command);
        if(executable != null){
            List<String> result = new ArrayList<>();
            result.add(executable.getCommandReplyTitle(command));
            result.add(executable.getCommandReplyMessage(command));
            return result;
        }
        return null;
    }
}