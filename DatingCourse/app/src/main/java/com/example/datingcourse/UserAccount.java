package com.example.datingcourse;

public class UserAccount {

    private String idToken; //Firebase 고유 토큰 정보, 사용자 하나 계정만 유일하게 가질수 있는 키값
    private String emailId;
    private String password,role;

    // 새로 추가한 회원가입 입력값
    private String Name,NickName; // 사용자 이름

    private String birthDate;

    private boolean memberShip;
    private int point;

    public UserAccount(){ } //클래스가 생성될 때 가장 먼저 호출, firebase에서는 빈 생성자를 안만들면 데이터베이스 조회시 오류 발생 가능

    public UserAccount(String idToken, String emailId, String password, String name, String nickName, String birthDate) {
        this.idToken = idToken;
        this.emailId = emailId;
        this.password = password;
        this.Name = name;
        this.NickName = nickName;
        this.birthDate = birthDate;
        this.memberShip = false;
        this.point = 0;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    //getter setter 메소드
    //Alt + insert 누르면 getter setter 편리하게 가능

    public boolean isMemberShip() {
        return memberShip;
    }

    public void setMemberShip(boolean memberShip) {
        this.memberShip = memberShip;
    }

    public String getUserNickName() {
        return NickName;
    }

    public void setUserNickName(String NickName) {
        this.NickName = NickName;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {
        return emailId;
    }
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
    public String getPassWord(){
        return password;
    }
    public void setPassWord(String passWord) {
        this.password = passWord;
    }

    public String getUserBirth() {
        return birthDate;
    }

    public void setUserBirth(String userBirth) {
        this.birthDate = userBirth;
    }

    public String getUserName() {
        return Name;
    }

    public void setUserName(String userName) {
        this.Name = userName;
    }


}