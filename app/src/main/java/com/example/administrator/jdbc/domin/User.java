package com.example.administrator.jdbc.domin;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/14.
 */
public class User implements Serializable{
    private String username;
    private String id;
    private String password;
    private String create_time;
    private String my_note_id;
    private String commented_note_id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getMy_note_id() {
        return my_note_id;
    }

    public void setMy_note_id(String my_note_id) {
        this.my_note_id = my_note_id;
    }

    public String getCommented_note_id() {
        return commented_note_id;
    }

    public void setCommented_note_id(String commented_note_id) {
        this.commented_note_id = commented_note_id;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", create_time='" + create_time + '\'' +
                ", my_note_id='" + my_note_id + '\'' +
                ", comment_note_id='" + commented_note_id + '\'' +
                '}';
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
