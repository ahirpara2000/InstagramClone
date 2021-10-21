package com.example.instagramclone.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser {
    public static final String KEY_USERNAME = "username";
    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_BIO = "bio";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";

    public User(){
        super();
    }

    public String getKeyUsername(){
        return getString(KEY_USERNAME);
    }

    public String getFullname() {
        return getString(KEY_FULLNAME);
    }

    public void setFullName(String fullname){
        put(KEY_FULLNAME, fullname);
    }

    public String getBio(){
        return getString(KEY_BIO);
    }

    public void setBio(String bio){put(KEY_BIO, bio);}

    public ParseFile getProfileImage(){
        return getParseFile(KEY_PROFILE_PICTURE);
    }

    public void setProfileImage(ParseFile image){
        put(KEY_PROFILE_PICTURE, image);
    }

}