package com.example.datingcourse;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;

public class Comments{

    private String userNick, context,userId,documentId;

    private Timestamp when;



    public Comments(){
    }

    public Comments(String userNick, String context, Timestamp when,String documentId,String userId) {
        this.userNick = userNick;
        this.context = context;
        this.when = when;
        this.documentId = documentId;
        this.userId = userId;
    }
    public Comments(String userNick, String context, Timestamp when,String userId) {
        this.userNick = userNick;
        this.context = context;
        this.when = when;
        this.userId = userId;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public com.google.firebase.Timestamp getWhen() {
        return when;
    }

    public void setWhen(com.google.firebase.Timestamp when) {
        this.when = when;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public String toString(){
        return "Comments {" + "documentId=" + documentId + "\\" + "userId=" + userId + "\\"+ '}';
    }
}