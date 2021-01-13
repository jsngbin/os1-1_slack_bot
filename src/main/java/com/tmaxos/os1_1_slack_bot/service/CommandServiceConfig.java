package com.tmaxos.os1_1_slack_bot.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
