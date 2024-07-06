package com.example.datingcourse;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;     //firebase 인증처리 하기 위해 선언
    private DatabaseReference mDatabaseRef,nicknameFindRef; //실시간 서버에 연동 가능한 데이터 베이스
    private EditText mEtEmail,mEtPwd,mEtName,mEtCheckPwd,userBirth, mEtNickName;       //회원가입할 때 입력하는 text 박스 필드에 선언
    private Button mBtnRegister,mBtnDoubleCheck;
    private ImageButton mbtnGoBack;
    private boolean checkNickComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText et_Date = (EditText) findViewById(R.id.Date);
        et_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("생년월일 입력")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                datePicker.show(getSupportFragmentManager(),"DatePicker");
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        Calendar birthday = Calendar.getInstance();
                        birthday.setTimeInMillis(selection);
                        int year = birthday.get(Calendar.YEAR);
                        int month = birthday.get(Calendar.MONTH) + 1;
                        int day = birthday.get(Calendar.DAY_OF_MONTH);
                        String selectedDate = String.format("%04d-%02d-%02d",year,month,day);
                        if(IsValidation.isValidDate(selectedDate)){
                            et_Date.setText(selectedDate);
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "올바른 생년월일 입력이 아닙니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }
                });
            }
        });

        //필드에 선언한 것들 초기화 하기
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister"); //getReference안에 " " 앱이름, 프로젝트 이름

        mEtEmail = findViewById(R.id.et_emailRegister);     //xml에 설정한 Id로 초기화
        mEtPwd = findViewById(R.id.et_pwdRegister);
        mBtnRegister = findViewById(R.id.btn_registerReal);
        mEtName = findViewById(R.id.et_nameRegister); // 이름 입력받기
        mEtCheckPwd = findViewById(R.id.et_checkPwd);
        mEtNickName = findViewById(R.id.et_nickNameRegister);
        mBtnDoubleCheck = findViewById(R.id.check_nick);
        checkNickComplete = false;

        mEtNickName.addTextChangedListener(textWatcher);

        //닉네임 중복 확인 버튼
        mBtnDoubleCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText checkName = findViewById(R.id.et_nickNameRegister);
                String checkStr = checkName.getText().toString().trim();
                if(TextUtils.isEmpty(checkStr)) {
                    //중복확인 버튼 눌렀는데 그 칸이 비어있다면
                    mBtnDoubleCheck.setEnabled(false);
                }
                else {
                    nicknameFindRef = FirebaseDatabase.getInstance().getReference("FirebaseRegister").child("UserAccount");

                    //nicknameFindRef.orderByChild( )로 특정 속성에 따라 요소를 정렬 그리고 userNick과 equlTo( ) 같은게 있는지 탐색
                    Query query = nicknameFindRef.orderByChild("NickName").equalTo(checkStr);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean isDuplicate = false;

                            if (snapshot.exists()) {
                                isDuplicate = true;
                            }

                            if (isDuplicate) {
                                //닉네임 중복 O
                                checkNickComplete = false;
                                Toast.makeText(RegisterActivity.this, checkStr + "은(는) 이미 사용중인 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                Log.d("TAG", checkStr + "은(는) 이미 사용중인 닉네임입니다.");
                            } else {
                                //닉네임 중복 X
                                mBtnDoubleCheck.setEnabled(false);
                                checkNickComplete = true;
                                Toast.makeText(RegisterActivity.this, checkStr + "은(는) 사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                Log.d("TAG", checkStr + "은(는) 사용 가능한 닉네임입니다.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w("TAG", "중복확인창 닫힘 데이터베이스 에러", error.toException());
                        }
                    });
                }
            }
        });
        mBtnRegister.setOnClickListener(new View.OnClickListener() { //회원 가입 버튼이 클릭 되었을 때 액션들을 설정할 수 있다. onclick
            @Override
            public void onClick(View view) {

                userBirth = findViewById(R.id.Date);
                String userBirthDate = userBirth.getText().toString();
                //회원가입 처리 시작
                //회원가입 버튼을 눌렀을 때 입력 필드에 있는 값들을 getText()로 값을 받아와서 toString()으로 문자열로 변환해서 strEmail에 넣는다.
                String strEmail = mEtEmail.getText().toString().trim();
                String strPwd = mEtPwd.getText().toString().trim();
                String strName = mEtName.getText().toString().trim();
                String strNickName = mEtNickName.getText().toString().trim();

                //회원가입 유효성 검사


                if(TextUtils.isEmpty(strName)){
                    Toast.makeText(RegisterActivity.this,"이름을 입력해주세요",Toast.LENGTH_SHORT).show();
                    mEtName.requestFocus();
                    return;
                }
                // 이름 유효성 검사: 한글 또는 스페이스바가 아닌 문자가 포함된 경우
                if (!(IsValidation.isValidName(strName))) {
                    Toast.makeText(RegisterActivity.this, "이름은 한글로만 입력해주세요", Toast.LENGTH_SHORT).show();
                    mEtName.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(strNickName)){
                    Toast.makeText(RegisterActivity.this,"닉네임을 입력해주세요",Toast.LENGTH_SHORT).show();
                    mEtNickName.requestFocus();
                    return;
                }
                if(checkNickComplete == false){
                    Toast.makeText(RegisterActivity.this,"닉네임 중복 확인을 해주세요.",Toast.LENGTH_SHORT).show();
                    mEtNickName.requestFocus();
                    return;
                }
                if(!(IsValidation.isValidNickName(strNickName))){
                    Toast.makeText(RegisterActivity.this, "닉네임은 한글,문자,숫자만 사용 가능합니다", Toast.LENGTH_SHORT).show();
                    mEtNickName.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(strEmail)){
                    Toast.makeText(RegisterActivity.this,"이메일 아이디를 입력해주세요",Toast.LENGTH_SHORT).show();
                    mEtEmail.requestFocus();
                    return;
                }
                if(!(IsValidation.isValidEmail(strEmail))){
                    Toast.makeText(RegisterActivity.this,"올바른 이메일 형식이 아닙니다.",Toast.LENGTH_SHORT).show();
                    mEtEmail.requestFocus();
                    return;
                }
                if(strPwd.length() < 8 || strPwd.length() > 16){
                    Toast.makeText(RegisterActivity.this,"비밀번호는 8~16자까지 입력가능합니다.",Toast.LENGTH_SHORT).show();
                    mEtPwd.requestFocus();
                    return;
                }
                if(!(IsValidation.isValidPassword(strPwd))){
                    Toast.makeText(RegisterActivity.this,"비밀번호는 최소 하나의 숫자, 소문자, 특수문자(!?@#$%^&+=)를 가져야합니다.",Toast.LENGTH_SHORT).show();
                    mEtPwd.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(mEtCheckPwd.getText().toString())){
                    Toast.makeText(RegisterActivity.this,"비밀번호 확인란을 입력해주세요",Toast.LENGTH_SHORT).show();
                    mEtCheckPwd.requestFocus();
                    return;
                }
                if(!((strPwd).equals(mEtCheckPwd.getText().toString()))){
                    Toast.makeText(RegisterActivity.this,"비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show();
                    mEtPwd.setText("*******");
                    mEtPwd.setText("*******");
                    mEtPwd.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(userBirthDate)){
                    Toast.makeText(RegisterActivity.this,"생년월일을 입력해주세요",Toast.LENGTH_SHORT).show();
                    userBirth.requestFocus();
                    return;
                }


                //mFirebaseAuth 객체 이용하여 인증 절차 구현
                //firebase에서 이메일과 비밀번호를 이용하여 유저를 생성한다.
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail,strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //회원가입이 성공되었을 때

                        //파라미터에 있는 task -> 회원가입 처리를 하고 결과값을 받아온 것임
                        //정상적으로 가져와서 가입 성공을 했다면
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser(); //인증 객체에서 현재 회원 가입된 유저
                            UserAccount account = new UserAccount();

                            account.setUserNickName(strNickName);
                            account.setUserBirth(userBirthDate);
                            //UserAccount 객체에 값들 넣기(캡슐화)
                            account.setUserName(strName);
                            account.setIdToken(firebaseUser.getUid()); //고유값
                            account.setEmailId(firebaseUser.getEmail()); //유저 이메일을 정확하게 가져오는 것이 중요하기 때문에 데이터베이스에서 가져옴
                            account.setPassWord(strPwd);
                            HashMap<String,String> registerInfo = new HashMap<String,String>();
                            if(firebaseUser.getEmail().equals("admin@a.com")){
                                account.setRole("admin");
                            }else{
                                account.setRole("no");
                            }
                            // registerInfo 라는 HashMap에 account에 저장한 값들 가져와서 저장
                            registerInfo.put("Name",account.getUserName());
                            registerInfo.put("NickName",account.getUserNickName());
                            registerInfo.put("IdToken",account.getIdToken());
                            registerInfo.put("emailId",account.getEmailId());
                            registerInfo.put("password",account.getPassWord());
                            registerInfo.put("birthDate",account.getUserBirth());
                            registerInfo.put("role",account.getRole());
                            //getUid를 key값으로 해서
                            //setValue: database에 삽입하는 행위
                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(registerInfo); //mDatabaseReference에 설정한 path의 하위 개념(child)으로 넣는 코드


                            mFirebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //메세지 출력 -> 작은 메세지 박스 출력
                                        //LENGTH_SHORT -> 메세지를 짧게 출력해줌
                                        //maketext(컨텍스트,TXT,길이) -> 컨텍스트 : 사용할 어플리케이션 활동 개체 TXT : 출력할 메세지 입력
                                        Toast.makeText(RegisterActivity.this,"회원가입이 완료되었습니다. 이메일 인증 후 로그인 가능합니다.",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(RegisterActivity.this,"이메일 발송 중 오류가 발생하였습니다.",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                        else{
                            Toast.makeText(RegisterActivity.this,"회원가입에 실패하였습니다. 다시 시도해주세요.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


    }
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkNickComplete = false;
            //텍스트가 변경되면 버튼 활성화
            if(s.length()>=3 && s.length() <= 16){
                mBtnDoubleCheck.setEnabled(true);
            }else{
                mBtnDoubleCheck.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


}