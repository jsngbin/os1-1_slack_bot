package com.tmaxos.os1_1_slack_bot.repository;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
@ToString
@Getter
public class AccountEntity {

    @Id
    private String id;
    private String name;
    private String password;
}