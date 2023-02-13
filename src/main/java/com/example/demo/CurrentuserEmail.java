package com.example.demo;

public class CurrentuserEmail {
    private static String email = null;

    public CurrentuserEmail(String email){
        CurrentuserEmail.email = email;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        CurrentuserEmail.email = email;
    }
}
