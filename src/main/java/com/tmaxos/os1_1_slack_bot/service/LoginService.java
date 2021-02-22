package com.tmaxos.os1_1_slack_bot.service;

import java.util.ArrayList;

import com.tmaxos.os1_1_slack_bot.repository.AccountEntity;
import com.tmaxos.os1_1_slack_bot.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LoginService {

    @Autowired
    AccountRepository accountRepository;

    private final Logger logger = LoggerFactory.getLogger(LoginService.class);

    public boolean loginCheck(String user, String pass){
        AccountEntity entity = accountRepository.findByNameAndPassword(user, pass);
        return entity != null;
    }

}
