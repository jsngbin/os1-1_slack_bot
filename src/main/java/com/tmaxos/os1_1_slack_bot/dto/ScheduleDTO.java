package com.tmaxos.os1_1_slack_bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ScheduleDTO {
    String description;
    String message;
    String dateString;
    int repeat;
}