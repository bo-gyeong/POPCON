package com.example.popconback.push.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.example.popconback.push.message.FcmDataMessage;
import com.example.popconback.push.message.FcmMessage.Message;
import okhttp3.*;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * FCM 알림 메시지 생성
 * background 대응을 위해서 data로 전송한다.
 *   
 * @author taeshik.heo
 *
 */
@Component
public class FirebaseCloudMessageDataService {
	private static final Logger logger = LoggerFactory.getLogger(FirebaseCloudMessageDataService.class);

    public final ObjectMapper objectMapper;

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/sample-f5f82/messages:send";
     
    /**
     * FCM에 push 요청을 보낼 때 인증을 위해 Header에 포함시킬 AccessToken 생성
     * @return
     * @throws IOException
     */
    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/firebase_service_key.json";

        // GoogleApi를 사용하기 위해 oAuth2를 이용해 인증한 대상을 나타내는객체
        GoogleCredentials googleCredentials = GoogleCredentials
                // 서버로부터 받은 service key 파일 활용
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                // 인증하는 서버에서 필요로 하는 권한 지정
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
        
        googleCredentials.refreshIfExpired();
        String token = googleCredentials.getAccessToken().getTokenValue();
        
        return token;
    }
    
    /**
     * FCM 알림 메시지 생성
     * background 대응을 위해서 data로 전송한다.  
     * @param targetToken
     * @param title
     * @param body
     * @return
     * @throws JsonProcessingException
     */
    private String makeDataMessage(String targetToken, String title, String body) throws JsonProcessingException {
//        Notification noti = new FcmMessage.Notification(title, body, null);
    	Map<String,String> map = new HashMap<>();
    	map.put("title", title);
    	map.put("body", body);
    	
    	FcmDataMessage.Message message = new FcmDataMessage.Message();
        message.setToken(targetToken);
        message.setData(map);
        
        FcmDataMessage fcmMessage = new FcmDataMessage(false, message);
        
        return objectMapper.writeValueAsString(fcmMessage);
    }
    

    /**
     * targetToken에 해당하는 device로 FCM 푸시 알림 전송
     * background 대응을 위해서 data로 전송한다.  
     * @param targetToken
     * @param title
     * @param body
     * @throws IOException
     */
    public void sendDataMessageTo(String targetToken, String title, String body) throws IOException {
        String message = makeDataMessage(targetToken, title, body);
        logger.info("message : {}", message);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                // 전송 토큰 추가
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
//        logger.info("message : {}", message);
    }


    private List<String> clientTokens = new ArrayList<>();
    
    public FirebaseCloudMessageDataService(ObjectMapper objectMapper){
    	this.objectMapper = objectMapper;
    }

    
    // 클라이언트 토큰 관리
    public void addToken(String token) {
        clientTokens.add(token);
    }
    
    // 등록된 모든 토큰을 이용해서 broadcasting
    public int broadCastDataMessage(String title, String body) throws IOException {
       for(String token: clientTokens) {
    	   logger.debug("broadcastmessage : {},{},{}",token, title, body);
    	   sendDataMessageTo(token, title, body);
       }
       return clientTokens.size();
    }


}
