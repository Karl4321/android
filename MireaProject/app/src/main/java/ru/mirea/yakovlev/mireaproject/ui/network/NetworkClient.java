package ru.mirea.yakovlev.mireaproject.ui.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private static final String BASE_URL = "https://randomuser.me/";
    private static UserService userService;

    public static UserService getService() {
        if (userService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            userService = retrofit.create(UserService.class);
        }
        return userService;
    }
}