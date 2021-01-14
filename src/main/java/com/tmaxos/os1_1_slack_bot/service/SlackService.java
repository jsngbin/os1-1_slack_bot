package com.tmaxos.os1_1_slack_bot.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmaxos.os1_1_slack_bot.template.SlackCommandExecutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextException;
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

    private Logger logger = LoggerFactory.getLogger(SlackService.class);

    @Autowired
    private SlackCommandExecutable bobMenuService;

    @Autowired
    private SlackCommandExecutable teamScheduleService;

    private String oauthToken;
    private String webhookUrl;
    private String secret;

    @Value("${slack-post-message-url}")
    private String postMessageUrl;

    public SlackService(){
        // oauth token from env
        // webhook url from env
        ProcessBuilder pb = new ProcessBuilder();
        Map<String, String> env = pb.environment();
        oauthToken = env.get("OAUTH_TOKEN");
        webhookUrl = env.get("WEBHOOK_URL");
        secret = env.get("SECRET");

        //  필요한 정보가 하나라도 없으면 shutdown?..
        if(oauthToken == null || oauthToken.isEmpty()){
            logger.error("empty oauth token. it will not working with slack");
        }
        if(webhookUrl == null || webhookUrl.isEmpty()){
            logger.error("empty oauth token. it can`t send message to slack channel");
        }
        if(secret == null || secret.isEmpty()){
            logger.error("empty secret. it can`t receive message from slack channel");
        }
    }

    public boolean validateSlackEvent(String signature, String timestamp, String message){

        boolean validate = false;
        if(secret == null || secret.isEmpty()){
            return false;
        }
		try {

            byte[] baseString = ("v0:" + timestamp + ":" + message).getBytes();
            byte[] secretByte = secret.getBytes();

            final SecretKeySpec secretKey = new SecretKeySpec(secretByte, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");

            mac.init(secretKey);
            byte[] hash = mac.doFinal(baseString);

            // to hex string
            StringBuilder sb = new StringBuilder();
            for(byte b : hash){
                sb.append(String.format("%02x", b & 0xff));
            }

            String generatedHmac = "v0=" + sb.toString();
            logger.info(generatedHmac);
            logger.info(signature);
            if(generatedHmac.equals(signature)) validate = true;

		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
        }


        return validate;
    }

    // command from slack event api
    public String handleEvent(String message){
        try{
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> jsonData = mapper.readValue(message, Map.class); // FIXME: TypeReference ?..
            String commandType = (String)jsonData.get(EVENTAPI_FIELD_TYPE);

            if (commandType.equals(EVENTAPI_FIELD_VERIFICATION_VALUE)) {
                return "{\"challenge\" : \"" + jsonData.get("challenge") + "\"}";
            } else if(commandType.equals(EVENTAPI_FIELD_EVENTCALLBACK_VALUE)) {

                LinkedHashMap<String, String> eventField = (LinkedHashMap<String, String>)jsonData.get(EVENTAPI_FIELD_EVENT);
                String messageType = eventField.get(EVENTAPI_FIELD_MSGTYPE);
                String text = eventField.get(EVENTAPI_FIELD_MSG);
                String channelId = eventField.get(EVENTAPI_FIELD_CHANNELID);


                if(messageType.equals(EVENTAPI_FIELD_APPMENTION_VALUE)){
                    // post help message
                    postMessageToChannel("명령어", "점심 알려줘\n저녁 알려줘\n밥 알려줘\n", channelId);
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
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpEntity<String> entity = new HttpEntity<>(asJsonStr, headers);
        rest.postForEntity(postMessageUrl, entity, String.class);
    }
    private void handleSlackMessage(String command, String channelId){
        if(bobMenuService.matchCommand(command)){
            postMessageToChannel(
                bobMenuService.getCommandReplyTitle(command),
                bobMenuService.getCommandReplyMessage(command),
                channelId);
        }
    }
}
