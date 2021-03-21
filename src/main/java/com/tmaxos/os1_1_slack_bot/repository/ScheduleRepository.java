package com.tmaxos.os1_1_slack_bot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ScheduleRepository extends MongoRepository<ScheduleEntity, String> {
    void deleteByDateString(String dateString);
}