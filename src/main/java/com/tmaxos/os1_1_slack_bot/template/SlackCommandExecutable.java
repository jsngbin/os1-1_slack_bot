package com.tmaxos.os1_1_slack_bot.template;

public interface SlackCommandExecutable {
    
    boolean matchCommand(String command);
    String getCommandReplyTitle(String command);
    String getCommandReplyMessage(String command); // return as Json
}
