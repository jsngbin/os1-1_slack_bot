package com.tmaxos.os1_1_slack_bot.template;

public interface SlackCommandExecutable {
    
    public boolean matchCommand(String command);
    public String getCommandReplyTitle(String command);
    public String getCommandReplyMessage(String command); // return as Json
}
