package com.example.datingcourse;

public class Cafe {
    private String mImgName;    // 이미지
    private String mMainText;   // 장소 이름
    private String mSubText;    // 주소

    private String x;

    private String y;

    private String tel; // 전화번호

    public Cafe(String mImgName, String mMainText, String mSubText, String x, String y, String tel) {
        this.mImgName = mImgName;
        this.mMainText = mMainText;
        this.mSubText = mSubText;
        this.x = x;
        this.y = y;
        this.tel = tel;
    }

    public String getImgName() {
        return mImgName;
    }

    public void setImgName(String imgName) {
        this.mImgName = imgName;
    }

    public String getMainText() {
        return mMainText;
    }

    public void setMainText(String mMainCount) {
        this.mMainText = mMainCount;
    }

    public String getSubText() {
        return mSubText;
    }

    public void setSubText(String subText) {
        this.mSubText = subText;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
