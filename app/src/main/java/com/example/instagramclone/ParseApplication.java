package com.example.instagramclone;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("KivyecGGiyIPTMFViA2TtVdZ4Kt1CCvSmNdY6U7f")
                .clientKey("3wEa6ZbZLAaMFwtSIuPDRMAET1zmYggbdifaJt7O")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
