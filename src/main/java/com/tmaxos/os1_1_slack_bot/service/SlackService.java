package com.tmaxos.os1_1_slack_bot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmaxos.os1_1_slack_bot.template.SlackCommandExecutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    private SlackCommandExecutable bobMenuService;

    @Autowired
    private SlackCommandExecutable teamScheduleService;

 
    private String oauthToken;
    private String webhookUrl;

    @Value("${slack-post-message-url}")
    private String postMessageUrl;

    public SlackService(){
        // oauth token from env
        // webhook url from env
        ProcessBuilder pb = new ProcessBuilder();
        Map<String, String> env = pb.environment();
        oauthToken = env.get("OAUTH_TOKEN");
        webhookUrl = env.get("WEBHOOK_URL");
        if(oauthToken == null || oauthToken.equals("")){
            logger.warn("empty oauth token. it will not working with slack");
        }
        if(webhookUrl == null || webhookUrl.equals("")){
            logger.warn("empty oauth token. it can`t send message to slack channel");
        }
    }
    
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
                return "{\"challenge\" : \"" + jsonData.get("challenge") + "\"}";
            } else if(commandType.equals(EVENTAPI_FIELD_EVENTCALLBACK_VALUE)) {
                               
                LinkedHashMap<String, String> eventField = (LinkedHashMap<String, String>)jsonData.get(EVENTAPI_FIELD_EVENT);                
                String messageType = eventField.get(EVENTAPI_FIELD_MSGTYPE);
                String text = eventField.get(EVENTAPI_FIELD_MSG);
                String channelId = eventField.get(EVENTAPI_FIELD_CHANNELID);
                logger.info(text);
                logger.info(channelId);

                if(messageType.equals(EVENTAPI_FIELD_APPMENTION_VALUE)){
                    // post help message
                    // TODO
                }
                else{                 
                    // if we need reply for message, handle it!
                    if(eventField.get(EVENTAPI_FIELD_BOTID) == null)
                        handleSlackMessage(text, channelId);   
                    }
                }            
        }
        catch(JsonProcessingException ex){
            logger.error(ex.getMessage());
        }

        return ""; /* nothing to do for slack */

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
        rest.postForEntity(postMessageUrl, entity, String.class);
    }
    private void handleSlackMessage(String command, String channelId){
        logger.info("handle slack message");        
        if(bobMenuService.matchCommand(command)){
            postMessageToChannel(
                bobMenuService.getCommandReplyTitle(command),
                bobMenuService.getCommandReplyMessage(command),
                channelId);            
        }
    }    
}
