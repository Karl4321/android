package ru.mirea.yakovlev.mireaproject.ui.network;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("name")
    private Name name;

    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("picture")
    private Picture picture;

    public String getFullName() {
        return name.first + " " + name.last;
    }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPhoto() { return picture.large; }

    private static class Name {
        String first;
        String last;
    }

    private static class Picture {
        String large;
    }
}