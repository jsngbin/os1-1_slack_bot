package com.tmaxos.os1_1_slack_bot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmaxos.os1_1_slack_bot.template.SlackCommandExecutable;

import com.tmaxos.os1_1_slack_bot.util.SlackCommandCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SlackService {

    private static final String EVENTAPI_FIELD_TYPE = "type";
    private static final String EVENTAPI_FIELD_EVENT = "event";
    private static final String EVENTAPI_FIELD_MSG = "text";
    private static final String EVENTAPI_FIELD_MSGTYPE = EVENTAPI_FIELD_TYPE;
    private static final String EVENTAPI_FIELD_CHANNELID = "channel";
    private static final String EVENTAPI_FIELD_VERIFICATION_VALUE = "url_verification";
    private static final String EVENTAPI_FIELD_EVENTCALLBACK_VALUE = "event_callback";
    private static final String EVENTAPI_FIELD_BOTID = "bot_id";
    private static final String EVENTAPI_FIELD_APPMENTION_VALUE = "app_mention";
    private static final String POST_FIELD_CHANNELID = EVENTAPI_FIELD_CHANNELID;
    private static final String POST_FIELD_TEXT = EVENTAPI_FIELD_MSG;


    private final Logger logger = LoggerFactory.getLogger(SlackService.class);

    @Autowired
    private SlackCommandCenter commandCenter;

    @Value("${slack-oauth-token}")
    private String oauthToken;

    @Value("${slack-post-message-url}")
    private String webhookUrl;

    @Value("${slack-post-message-url}")
    private String postMessageUrl;

    // command from slack event api
    public String handleEvent(String message){
        try{
            logger.info("event message got!");
            logger.info(message);
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> jsonData = mapper.readValue(message, Map.class); // FIXME: TypeReference ?..
            String commandType = (String)jsonData.get(EVENTAPI_FIELD_TYPE);

            if(commandType == null){
                logger.error("invalid slack message format. ignore it");
                return "";
            }

            if (commandType.equals(EVENTAPI_FIELD_VERIFICATION_VALUE)) {
                return getVerificationReplyMessage((String)jsonData.get("challenge"));
            } else if(commandType.equals(EVENTAPI_FIELD_EVENTCALLBACK_VALUE)) {
                LinkedHashMap<String, String> eventData = (LinkedHashMap<String, String>)jsonData.get(EVENTAPI_FIELD_EVENT);
                handleSlackEvent(eventData);
            }
        }
        catch(JsonProcessingException ex){
            logger.error(ex.getMessage());
        }

        return ""; /* nothing to do for slack */
    }

    private String getVerificationReplyMessage(String challenge){
        return "{\"challenge\" : \"" + challenge + "\"}";
    }

    private void handleSlackEvent(Map<String, String> eventData){
        String messageType = eventData.get(EVENTAPI_FIELD_MSGTYPE);
        String text = eventData.get(EVENTAPI_FIELD_MSG);
        String channelId = eventData.get(EVENTAPI_FIELD_CHANNELID);

        if(messageType.equals(EVENTAPI_FIELD_APPMENTION_VALUE)){
            // post help message
            // TODO
        }
        else{
            // if we need reply for message, handle it!
            if(eventData.get(EVENTAPI_FIELD_BOTID) == null)
                handleSlackMessage(text, channelId);
        }
    }

    private void postMessageToChannel(String title, String text, String channelId){

        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + oauthToken);

        // TODO: need a Slack Message Builder
        Map<String, Object> jsonBody = new HashMap<>();
        List<Object> attachments = new ArrayList<>();
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("text", text);
        attachment.put("mrkdwn_in", "[\"text\"]");
        attachments.add(attachment);
        jsonBody.put("text", title);
        jsonBody.put(POST_FIELD_CHANNELID, channelId);
        jsonBody.put("attachments", attachments);

        ObjectMapper mapper = new ObjectMapper();
        String asJsonStr = "";
        try {
            asJsonStr = mapper.writeValueAsString(jsonBody);
            logger.info(asJsonStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpEntity<String> entity = new HttpEntity<>(asJsonStr, headers);
        ResponseEntity<String> res = rest.postForEntity(postMessageUrl, entity, String.class);
        logger.info("post message response status code : " + res.getStatusCode());
        logger.info("post message response body : " + res.getBody());
    }
    private void handleSlackMessage(String command, String channelId){
        logger.info("handle slack message");
        List<String> titleAndMessage = commandCenter.execute(command);
        if(titleAndMessage != null)
            postMessageToChannel(
                    titleAndMessage.get(0),
                    titleAndMessage.get(1),
                    channelId
            );
    }
}