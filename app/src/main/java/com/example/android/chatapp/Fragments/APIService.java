package com.example.android.chatapp.Fragments;

import com.example.android.chatapp.Notifications.MyResponse;
import com.example.android.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=XXXX"
            }
    )

    @POST("fcm/send")
    Call <MyResponse> sendNotification(@Body Sender body);

}
