package com.tmaxos.os1_1_slack_bot.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MenuDTO {
    String lunchOrDinner;
    List<String> Menus;
}
