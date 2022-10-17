package com.vjit.vjittransit.Notifications;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAUcudgr8:APA91bFG6YE2ztbaZ913N29GOnRPIcj-MKVWH-rDT0-k0NlkMjNBfBxP9z6Aj4iZgnE3dgchJM07Fk__R1Px-UV3fvP3NRSurQ91yJaoaVckkDTcIWXB98-8e0IOU4FkgaE50JM-7AWU"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
