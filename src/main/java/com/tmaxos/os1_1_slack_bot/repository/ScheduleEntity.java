package com.tmaxos.os1_1_slack_bot.repository;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "schedules")
@ToString
@Getter
@Builder
public class ScheduleEntity {

    @Id
    private String id;
    private String desc;
    private String message;
    private String dateString;
    private Boolean repeat;

    /*
    dateString
    ex)
        1. 0930:월,수  -> 0 30 9 ? * MON,WED
        2. 04030930 -> 0 30 9 3 4 ?
     */
}