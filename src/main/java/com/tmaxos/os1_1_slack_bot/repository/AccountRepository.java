package com.tmaxos.os1_1_slack_bot.repository;

import java.util.ArrayList;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepository extends MongoRepository<AccountEntity, String> {
    AccountEntity findByNameAndPassword(String name, String password);
}
