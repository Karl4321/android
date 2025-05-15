package ru.mirea.yakovlev.mireaproject.ui.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserService {
    @GET("api/")
    Call<UserResponse> getUsers(@Query("results") int count);
}