package com.tmaxos.os1_1_slack_bot.dto;

import lombok.Data;

import java.util.List;

@Data
public class MenuDTO {
    String lunchOrDinner;
    List<List<String>> Menus;
}