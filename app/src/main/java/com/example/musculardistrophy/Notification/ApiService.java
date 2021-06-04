package com.example.musculardistrophy.Notification;

import com.squareup.okhttp.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA0Ctok4M:APA91bHMc1YSZxZ0F4v8suqPjuNZBXb_wSjlpFErlfMqyVFtU4EhGcAwkt1FoUon51Xx3qoMUDc7QYZUL04lXaI8pV-hGvF7RoWz1TYKMFC4rdPKk7IlNQcTd4Hw1RMgoS3VOLCZl5Lm"
    })

    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body com.example.musculardistrophy.Notification.NotificationSender body);
}
