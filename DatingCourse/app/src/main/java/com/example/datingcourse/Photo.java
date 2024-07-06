package com.example.datingcourse;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Photo implements Serializable {
    private String imgUrl;
    private String titleName;
    private String addressName;
    private Double x;
    private Double y;
    private String tel;
    private String userUid;
    private String photoId;
    private String courseId;

    //기본 생성자
    public Photo() {
    }

    public Photo(String courseId,String imgUrl, String titleName, String addressName, Double x, Double y, String tel,String userUid) {
        this.imgUrl = imgUrl;
        this.titleName = titleName;
        this.addressName = addressName;
        this.x = x;
        this.y = y;
        this.tel = tel;
        this.courseId = courseId;
        this.userUid = userUid;
    }

    public Photo(String imgUrl, String titleName, String addressName, Double x, Double y, String tel, String userUid) {
        this.imgUrl = imgUrl;
        this.titleName = titleName;
        this.addressName = addressName;
        this.x = x;
        this.y = y;
        this.tel = tel;
        this.userUid = userUid;

    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @NonNull
    @Override
    public String toString() {
        return imgUrl+ " " +titleName+ " " +addressName+ " " +x+ " " +y+ " " + tel;
    }
}