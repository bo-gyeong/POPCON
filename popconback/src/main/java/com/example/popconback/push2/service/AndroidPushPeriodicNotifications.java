package com.example.popconback.push2.service;


import com.example.popconback.user.domain.User;
import com.example.popconback.user.repository.UserRepository;
import com.example.popconback.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
public class AndroidPushPeriodicNotifications {
    private static UserRepository userRepository;
    public static String PeriodicNotificationJson() throws JSONException {

        List<User> userList = userRepository.findAll();

        LocalDate localDate = LocalDate.now();

        //String sampleData[] = {"cGpMxomNuJ4:APA91bEz6YNYs5L1JJMLoaEADgI0mZjE9TkNHRmXzJCi3AUnTtGX16YuV4qxpLb2E6qg0JgKfxcNzM6H-hhOzY0Hqa5U9g25p06lI1nGpJwGYmAehWTd_J6_ehsJVVH15GPVtr5c0tav"
       // };

        JSONObject body = new JSONObject();

        List<String> tokenlist = new ArrayList<String>();

        for(int i=0; i<userList.size(); i++){
            tokenlist.add(userList.get(i).getEmail());// 나중에 토큰 값으로 바꾸기
        }

        JSONArray array = new JSONArray();

        for(int i=0; i<tokenlist.size(); i++) {
            array.put(tokenlist.get(i));
        }

        body.put("registration_ids", array);

        JSONObject notification = new JSONObject();
        notification.put("title","hello!");
        notification.put("body","Today is "+localDate.getDayOfWeek().name()+"!");

        body.put("notification", notification);

        System.out.println(body.toString());

        return body.toString();
    }
}
