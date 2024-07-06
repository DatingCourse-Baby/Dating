package com.example.datingcourse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RandomActivity extends AppCompatActivity {
    private Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        final TextView textView1 = findViewById(R.id.textView1);
        final TextView textView2 = findViewById(R.id.textView2);
        final TextView textView3 = findViewById(R.id.textView3);

        ImageButton btnCulture = findViewById(R.id.btn_culture);
        ImageButton btnEat = findViewById(R.id.btn_eat);
        ImageButton btnPlay = findViewById(R.id.btn_play);
        ImageButton btnSleep = findViewById(R.id.btn_sleep);
        ImageButton btnShopping = findViewById(R.id.btn_shopping);
        ImageButton btnTour = findViewById(R.id.btn_tour);

        ImageButton btnReset = findViewById(R.id.btn_reset);


        textView1.setText("");
        textView2.setText("");
        textView3.setText("");

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.seoul_regions,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        btnCulture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView1.getText().toString().isEmpty()) {
                    textView1.setText("문화시설");
                } else if (textView2.getText().toString().isEmpty()) {
                    textView2.setText("문화시설");
                } else {
                    textView3.setText("문화시설");
                }
            }
        });

        btnEat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView1.getText().toString().isEmpty()) {
                    textView1.setText("먹기");
                } else if (textView2.getText().toString().isEmpty()) {
                    textView2.setText("먹기");
                } else {
                    textView3.setText("먹기");
                }
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView1.getText().toString().isEmpty()) {
                    textView1.setText("놀기");
                } else if (textView2.getText().toString().isEmpty()) {
                    textView2.setText("놀기");
                } else {
                    textView3.setText("놀기");
                }
            }
        });

        btnShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView1.getText().toString().isEmpty()) {
                    textView1.setText("쇼핑");
                } else if (textView2.getText().toString().isEmpty()) {
                    textView2.setText("쇼핑");
                } else {
                    textView3.setText("쇼핑");
                }
            }
        });

        btnSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView1.getText().toString().isEmpty()) {
                    textView1.setText("숙박");
                } else if (textView2.getText().toString().isEmpty()) {
                    textView2.setText("숙박");
                } else {
                    textView3.setText("숙박");
                }
            }
        });

        btnTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView1.getText().toString().isEmpty()) {
                    textView1.setText("관광지");
                } else if (textView2.getText().toString().isEmpty()) {
                    textView2.setText("관광지");
                } else {
                    textView3.setText("관광지");
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView1.setText("");
                textView2.setText("");
                textView3.setText("");
            }
        });

        Button btnOk = findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (textView1 != null && textView2 != null && textView3 != null) {
                    if (textView1.getText().toString().isEmpty() || textView2.getText().toString().isEmpty() || textView3.getText().toString().isEmpty()) {
                        // 하나라도 값이 비어있을 경우 메시지 띄우기
                        Toast.makeText(RandomActivity.this, "버튼을 클릭해주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        // 모든 값이 입력되었을 경우 인텐트 실행
                        if (spinner != null && spinner.getSelectedItem() != null) {
                            String list = spinner.getSelectedItem().toString();
                            Intent intent = new Intent(RandomActivity.this, RandomActivity2.class);
                            intent.putExtra("list", list);
                            intent.putExtra("textView1", textView1.getText().toString());
                            intent.putExtra("textView2", textView2.getText().toString());
                            intent.putExtra("textView3", textView3.getText().toString());
                            startActivity(intent);
                        }
                    }
                } else {
                    // textView1, textView2, textView3 중 하나라도 null일 경우 메시지 띄우기
                    Toast.makeText(RandomActivity.this, "버튼을 클릭해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}