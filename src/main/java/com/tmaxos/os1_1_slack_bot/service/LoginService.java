package com.tmaxos.os1_1_slack_bot.service;

import com.tmaxos.os1_1_slack_bot.repository.AccountEntity;
import com.tmaxos.os1_1_slack_bot.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    AccountRepository accountRepository;

    public boolean loginCheck(String user, String pass){
        AccountEntity entity = accountRepository.findByNameAndPassword(user, pass);
        return entity != null;
    }

}
