package com.tmaxos.os1_1_slack_bot.service;

import com.tmaxos.os1_1_slack_bot.template.SlackCommandExecutable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CommandServiceConfig {

    @Bean
    public BobMenuService bobMenuService(){
        return new BobMenuService();
    }
    @Bean
    public TeamScheduleService teamScheduleService(){
        return new TeamScheduleService();
    }

    @Bean(name="command-list")
    public List<SlackCommandExecutable> commands() {
        List<SlackCommandExecutable> tmp = new ArrayList<>();
        tmp.add(bobMenuService());
        tmp.add(teamScheduleService());
        return tmp;
    }
}