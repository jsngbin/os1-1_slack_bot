package com.tmaxos.os1_1_slack_bot.controller;

import com.tmaxos.os1_1_slack_bot.dto.ScheduleDTO;
import com.tmaxos.os1_1_slack_bot.repository.ScheduleEntity;
import com.tmaxos.os1_1_slack_bot.repository.ScheduleRepository;
import com.tmaxos.os1_1_slack_bot.service.TeamScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ScheduleController {

    @Autowired
    TeamScheduleService scheduleService;

    @GetMapping(path="/api/schedule")
    public ResponseEntity<List<ScheduleDTO>> getSchedule(){

        List<ScheduleEntity> entities = scheduleService.getSchedules();
        List<ScheduleDTO> results = new ArrayList<>();
        for (ScheduleEntity entity: entities) {
            ScheduleDTO dto = ScheduleDTO.builder()
                    .description(entity.getDesc())
                    .message(entity.getMessage())
                    .dateString(entity.getDateString())
                    .repeat(entity.getRepeat() ? 1 : 0).build();
            results.add(dto);
        }
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @PostMapping(path="/api/schedule", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScheduleDTO> addScheduleJson(
            @RequestBody ScheduleDTO schedule){

        // TODO: entity <-> dto
        ScheduleEntity entity =
                ScheduleEntity.builder()
                        .desc(schedule.getDescription())
                        .message(schedule.getMessage())
                        .dateString(schedule.getDateString())
                        .repeat(schedule.getRepeat() == 1)
                .build();

        scheduleService.addSchedule(entity);
        return new ResponseEntity<>(schedule, HttpStatus.CREATED);
    }
    @PostMapping(path="/api/schedule", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ScheduleDTO> addSchedule(
            ScheduleDTO schedule){

        // TODO: entity <-> dto
        ScheduleEntity entity =
                ScheduleEntity.builder()
                        .desc(schedule.getDescription())
                        .message(schedule.getMessage())
                        .dateString(schedule.getDateString())
                        .repeat(schedule.getRepeat() == 1)
                        .build();

        scheduleService.addSchedule(entity);
        return new ResponseEntity<>(schedule, HttpStatus.CREATED);
    }

    @DeleteMapping(path="/api/schedule", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> delSchedule(String dateString){
        scheduleService.delSchedule(dateString);
        return new ResponseEntity("deleted schedule", HttpStatus.OK);
    }
}