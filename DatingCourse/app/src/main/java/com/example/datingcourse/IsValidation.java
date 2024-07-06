package com.example.datingcourse;

import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// 유효성 검사 클래스
public class IsValidation {

    //이름을 입력했을 때 해당 이름이 완성된 한글로 이루어져있는지 확인하는 메소드
    //문자열의 시작(^)과 끝($) 사이 완성된 한글 문자와 공백 문자(\s)만 허용하는 메소드, * -> 는 앞에 문자가 반복 가능하다는 뜻
    public static boolean isValidName(String name) {
        return name.matches("^[가-힣\\s]*$");
    }

    //닉네임을 입력했을 때 그 닉네임이 영어 대소문자 A-za-z, 완성된 한글 가-힣, 숫자 0-9 중 하나 이상 포함하는지 matches( ) 메소드로 확인
    //{3,16}을 이용해서 3~16자까지 허용
    public static boolean isValidNickName(String nickName) {
        return nickName.matches("^[A-Za-z가-힣0-9]{3,16}$");
    }

    //이메일 유효성 검사 메소드 -> 안드로이드 util.Patterns 클래스에 포함된 정규식 패턴
    //이메일 주소의 형식은 사용자명@도메인명.최상위도메인 (예: example@example.com).
    //입력받은 문자열이 알맞은 이메일 형식을 가지고 있는지 True,False로 return해줌
    //이메일을 입력했을 때 해당 이메일이 기본 이메일 형식에 부합하는지 확인
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    //비밀번호 유효성 검사 메소드
    //최소 하나의 숫자, 영어 소문자, 특수문자, 공백문자는 포함하지 않음
    //8~16자리 비밀번호
    // ?= : 정규식이 계속 검색할 수 있도록 탐색 위치를 다시 원래 위치로 되돌림(긍정 전방 탐색)
    public static boolean isValidPassword(String pwd) {
        return pwd.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!?@#$%^&+=])[a-zA-Z0-9!?@#$%^&+=]{8,16}$");
    }
    //회원가입시 생년월일 유효성 검사
    public static boolean isValidDate(String dateStr){
        //Locale.getDefault( ) : 해당 구성 요소가 사용자가 사는 국가와 언어에 따라 올바른 형식을 나타냄
        //dateFormat : yyyy-MM-dd로 설정 setLenient(false) : 엄격하게 검사하기

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat.setLenient(false);
        try{
            //앞서 설정한 dateFormat에 맞게 dateStr을 Date 객체로 변환하기
            Date date = dateFormat.parse(dateStr);
            //생년월일이기 때문에 미래는 올 수 없다
            //new Date() -> 현재 날짜
            if(date.compareTo(new Date()) > 0){
                return false;
            }
            //현재 시간을 기반으로 새 Calendar 객체를 생성하기(minAge)
            //그리고 생성한 객체의 YEAR값을 -15해줌 -> 최소 15살
            Calendar minAge = Calendar.getInstance();
            minAge.add(Calendar.YEAR,-15);
            //입력된 문자열과 비교해서 date가 minAge보다 최근이면 false 리턴해줌
            if(date.compareTo(minAge.getTime())>0){
                return false;
            }
        }
        catch (ParseException e){
            return false;
        }
        return true;
    }
}
