package com.tmaxos.os1_1_slack_bot.service;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import com.tmaxos.os1_1_slack_bot.repository.ScheduleEntity;
import com.tmaxos.os1_1_slack_bot.repository.ScheduleRepository;
import com.tmaxos.os1_1_slack_bot.template.SlackCommandExecutable;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.PostConstruct;

public class TeamScheduleService implements SlackCommandExecutable {

    private final Logger logger = LoggerFactory.getLogger(TeamScheduleService.class);

    @Autowired
    ScheduleRepository repository;

    private ThreadPoolTaskScheduler taskScheduler;
    private List<String> commands;

    TeamScheduleService(){
        commands = new ArrayList<>();
    }

    @Override
    public boolean matchCommand(String command) {
        for(String cmd : commands){
            if(cmd.equals(command)) return true;
        }
        return false;
    }

    @Override
    public String getCommandReplyMessage(String command) {
        return null;
    }

    @Override
    public String getCommandReplyTitle(String command) {
        return null;
    }


    private Map<ScheduleEntity, ScheduledFuture<?>> runningSchedules;

    @SneakyThrows
    @PostConstruct
    void test(){
        runningSchedules = new HashMap<>();
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();

        List<ScheduleEntity> schedules = getSchedules();

        schedules.forEach(entity -> {
            addScheduleTask(entity);
        });
    }

    private void addScheduleTask(ScheduleEntity entity){
        ScheduledFuture future = taskScheduler.schedule(
                ()->{
                    // TODO: post message to our channel
                },
                new CronTrigger(toCronString(entity.getDateString(), entity.getRepeat()))
        );
        runningSchedules.put(entity, future);
        logger.info(entity.toString() + " added cron Task");
    }
    private void delScheduleTask(ScheduleEntity entity){
        ScheduledFuture<?> future = runningSchedules.get(entity);
        future.cancel(true);
        runningSchedules.remove(entity);
        logger.info(entity.toString() + " removed cron Task");
    }



    public List<ScheduleEntity> getSchedules(){
        return repository.findAll();
    }

    public void addSchedule(ScheduleEntity entity){
        addScheduleTask(entity);
        repository.save(entity);
        logger.info(entity.toString() + " added");
    }

    public void delSchedule(String dateString){
        Iterator<ScheduleEntity> keyI = runningSchedules.keySet().iterator();
        while(keyI.hasNext()){
            ScheduleEntity entity = keyI.next();
            if(entity.getDateString().equals(dateString)){
                delScheduleTask(entity);
                repository.delete(entity);
                logger.info(entity.toString() + " removed");
                break;
            }
        }
    }

    private String toCronString(String dateString, boolean repeat){
        ArrayList<String> arr = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        AtomicInteger n = new AtomicInteger();

        if(repeat){
            // has ':'
            dateString.split(":")[0].chars().forEach(c -> {
                builder.append((char)c);
                n.getAndIncrement();
                if(n.get() == 2){
                    arr.add(builder.toString());
                    builder.setLength(0);
                    n.set(0);
                }
            });

            arr.add("00");
            Collections.reverse(arr);
            arr.add("?");
            arr.add("*");

            dateString.split(":")[1].chars()
                    .mapToObj(a -> {
                        if(a == '월') return "MON";
                        else if(a == '화') return "TUE";
                        else if(a == '수') return "WED";
                        else if(a == '목') return "THU";
                        else if(a == '금') return "FRI";
                        else if(a == '토') return "SAT";
                        else if(a == '일') return "SUN";
                        return (char)a;
                    }).forEach(builder::append);
            arr.add(builder.toString());
            builder.setLength(0);
        }
        else{
            dateString.chars().forEach(c -> {
                builder.append((char)c);
                n.getAndIncrement();
                if(n.get() == 2){
                    arr.add(builder.toString());
                    builder.setLength(0);
                    n.set(0);
                }
            });

            arr.add("00");
            Collections.reverse(arr);
            arr.add("?");
        }
        return String.join(" ", arr);
    }

}