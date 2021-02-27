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
                    "Authorization:key=AAAArnuG0Z8:APA91bGGF2j-8PBdKS4NbnS0miNBLNmBYxcDDccJ-LOt0itrvHG7u1fLHkTrbuLGVxu0MRGBzmzty7k2Qo15nkxiJab3RhWlwZw-o8pnnj3go5vFbpzgo_nukKaxXfn71zTuP-2uLQsp"
            }
    )

    @POST("fcm/send")
    Call <MyResponse> sendNotification(@Body Sender body);

}
