package com.example.datingcourse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MBTI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mbti_i);

        Intent intent = getIntent();
        String mbti = intent.getStringExtra("mbti");

        ImageView mbti_image = findViewById(R.id.mbti_image);
        TextView title = findViewById(R.id.title);
        TextView description = findViewById(R.id.description);
        TextView time = findViewById(R.id.time);
        TextView price = findViewById(R.id.price);
        TextView geori = findViewById(R.id.geori);
        Button btn_ok = findViewById(R.id.btn_ok);


        ArrayList<String> titles = new ArrayList<>();
        ArrayList<Integer> images = new ArrayList<>();
        ArrayList<String> X = new ArrayList<>();
        ArrayList<String> Y = new ArrayList<>();
        ArrayList<String> overviews = new ArrayList<>();
        ArrayList<String> times = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();

        if(mbti.equals("i")) {
            mbti_image.setImageResource(R.drawable.mbti_i);
            title.setText("I 편");
            description.setText("감성적이면서도 철저한 자유로운 영혼의 소유자의 데이트 코스를 소개해요 :D");
            geori.setText("5.7km");
            time.setText("17분");
            price.setText("45,000원");
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    titles.add("에이치 450"); titles.add("갤러리 피치"); titles.add("청담공원");
                    X.add("127.0277092752"); X.add("127.0501323005"); X.add("127.0526683289");
                    Y.add("37.5287372997"); Y.add("37.5263824203"); Y.add("37.5211580726");
                    images.add(R.drawable.mbti_i1); images.add(R.drawable.mbti_i2); images.add(R.drawable.mbti_i3);
                    overviews.add("압구정역 1번 출구 조용한 아파트 단지 사이 유명하지는 않지만 아는 사람은 다 아는, 트렌디하지 않지만 진솔한, 음식을 매개로 사람과 사람 사이 행복을 전하는 식당");
                    overviews.add("갤러리 피치는 다양한 형태의 전시를 위해 마련된 공간이다. 지하1층, 지상 1, 2층으로 총 3층의 전시공간을 가지고 있다.");
                    overviews.add("오늘날 청담공원은 지극히 도시적인 청담동에 위치한 오염되지 않은 작고 아담한 공원이다.");
                    times.add("1시간");times.add("1시간30분");times.add("1시간");
                    prices.add("15,000원"); prices.add("30,000원");prices.add("무료");
                    Intent intent = new Intent(MBTI.this, MBTI_map.class);
                    intent.putExtra("price","45,000원");
                    intent.putExtra("time","17분");
                    intent.putStringArrayListExtra("titles",(ArrayList<String>) titles);
                    intent.putIntegerArrayListExtra("images",(ArrayList<Integer>) images);
                    intent.putStringArrayListExtra("X",(ArrayList<String>) X);
                    intent.putStringArrayListExtra("Y",(ArrayList<String>) Y);
                    intent.putStringArrayListExtra("overview",(ArrayList<String>) overviews);
                    intent.putStringArrayListExtra("times",(ArrayList<String>) times);
                    intent.putStringArrayListExtra("prices",(ArrayList<String>) prices);
                    startActivity(intent);

                }
            });
        }
        else if(mbti.equals("e")){
            mbti_image.setImageResource(R.drawable.mbti_e);
            title.setText("E 편");
            description.setText("활동적이고 에너지 넘치는 당신에게 데이트 코스를 소개해요 :D");
            geori.setText("1.5km");
            time.setText("11분");
            price.setText("70,000원");
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    titles.add("판타지보이 롤러클럽"); titles.add("현대백화점 천호점"); titles.add("브랜뉴 하이몬드");
                    X.add("127.1249917490"); X.add("127.1244557316"); X.add("127.1261723144");
                    Y.add("37.5406071165"); Y.add("37.5390267349"); Y.add("37.5364853944");
                    images.add(R.drawable.mbti_e1); images.add(R.drawable.mbti_e2); images.add(R.drawable.mbti_e3);
                    overviews.add("입장권을 구매하면 롤러스케이트와 보호장비를 대여할 수 있다. 초보자용 트랙과 고수들을 위한 트랙이 나누어져 있어 수준별로 즐길 수 있다. 트랙 주변으로 충분한 테이블이 있고 생일과 모임을 위한 파티룸과 게임 시설 및 영유아들을 위한 공간도 따로 마련되어 있다.");
                    overviews.add("현대백화점은 신생활 문화창조 기업으로서 고객에게 한 단계 높은 감동을 제공하고 저희 회사를 통해 고객의 개성을 표출하고 더욱 풍요로운 삶을 누릴 수 있도록 제안함으로써 고객의 일상을 더욱 풍요롭게 만들자는 신생활 문화 창조의 정신을 추구하고 있다.");
                    overviews.add("이곳은 80년대부터 지금까지의 서울을 모두 겪어낸 역사 속 빵집으로. 대표적인 빵지순례 장소로 식사용 샌드위치, 조리 빵와 케이크, 빵, 디저트, 빙수와 카페 음료 등 다양한 메뉴와 넓은 공간, 좌석이 준비되어 있다.");
                    times.add("1시간"); times.add("1시간"); times.add("1시간");
                    prices.add("20,000원"); prices.add("? 원"); prices.add("10,000원");
                    Intent intent = new Intent(MBTI.this, MBTI_map.class);
                    intent.putExtra("price","70,000원");
                    intent.putExtra("time","11분");
                    intent.putStringArrayListExtra("titles",(ArrayList<String>) titles);
                    intent.putIntegerArrayListExtra("images",(ArrayList<Integer>) images);
                    intent.putStringArrayListExtra("X",(ArrayList<String>) X);
                    intent.putStringArrayListExtra("Y",(ArrayList<String>) Y);
                    intent.putStringArrayListExtra("overview",(ArrayList<String>) overviews);
                    intent.putStringArrayListExtra("times",(ArrayList<String>) times);
                    intent.putStringArrayListExtra("prices",(ArrayList<String>) prices);
                    startActivity(intent);
                }
            });
        }
        else if(mbti.equals("p")){
            mbti_image.setImageResource(R.drawable.mbti_p);
            title.setText("P 편");
            description.setText("자율적이고 융통성있는 당신에게 데이트 코스를 소개해요 :D");
            geori.setText("1.1km");
            time.setText("5분");
            price.setText("80,000원");
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    titles.add("이닝"); titles.add("비비브라운"); titles.add("피규어 뮤지엄 W");
                    X.add("127.0412776644"); X.add("127.0407471394"); X.add("127.0404054231");
                    Y.add("37.5223993903"); Y.add("37.5230270857"); Y.add("37.5258461965");
                    images.add(R.drawable.mbti_p1); images.add(R.drawable.mbti_p2); images.add(R.drawable.mbti_p3);
                    overviews.add("이닝은 청담동에 드문 중국식 퓨전 레스토랑이다. 해삼과 송이를 넣어 만든 발채해물스프, 게살과 해삼을 사용한 샥스핀, 바다에서 나는 것들이 잔치를 벌이는 해산물 블랙빈 소스 등이 인기 많다.");
                    overviews.add("서울 강남구 청담동에 위치한 24시간 연중무휴인 카페이다. 비비브라운에서 판매하고 있는 커피는 산미가 적고 고소하며 깔끔한 맛이 특징이며 오전 7시~오후 4시 사이에만 판매하는 매장에서 직접 만든 샌드위치도 인기 있다.");
                    overviews.add("서울시 강남구 청담동에 위치한 피규어뮤지엄W는 피규어와 토이를 테마로 테마파크의 기능을 접목시킨 새로운 개념의 뮤지엄이다.");
                    times.add("1시간"); times.add("1시간"); times.add("1시간");
                    prices.add("50,000원"); prices.add("5,000원"); prices.add("25,000원");
                    Intent intent = new Intent(MBTI.this, MBTI_map.class);
                    intent.putExtra("price","80,000원");
                    intent.putExtra("time","5분");
                    intent.putStringArrayListExtra("titles",(ArrayList<String>) titles);
                    intent.putIntegerArrayListExtra("images",(ArrayList<Integer>) images);
                    intent.putStringArrayListExtra("X",(ArrayList<String>) X);
                    intent.putStringArrayListExtra("Y",(ArrayList<String>) Y);
                    intent.putStringArrayListExtra("overview",(ArrayList<String>) overviews);
                    intent.putStringArrayListExtra("times",(ArrayList<String>) times);
                    intent.putStringArrayListExtra("prices",(ArrayList<String>) prices);
                    startActivity(intent);
                }
            });
        }
        else if(mbti.equals("j")){
            mbti_image.setImageResource(R.drawable.mbti_j);
            title.setText("J 편");
            description.setText("계획적이고 구체적인 일정을 원하는 당신에게 데이트 코스를 소개해요 :D");
            geori.setText("8.4km");
            time.setText("30분");
            price.setText("50,000원");
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    titles.add("스케줄 청담"); titles.add("카카오프렌즈 브랜드 스토어"); titles.add("식물관PH");
                    X.add("127.0426969138"); X.add("127.0587569030"); X.add("127.0943461819");
                    Y.add("37.5246935939"); Y.add("37.5126897207"); Y.add("37.4826910661");
                    images.add(R.drawable.mbti_e1); images.add(R.drawable.mbti_e2); images.add(R.drawable.mbti_e3);
                    overviews.add("스케줄청담(Schedule Chungdam)은 고급스러운 인테리어와 소품들로 꾸며진 분위기 레스토랑이다. 파스타, 리조또, 스테이크 등 기본 식사메뉴와 디저트, 와인과 페어링할 수 있는 음식까지 다양한 메뉴가 준비되어 있다.");
                    overviews.add("카카오프렌즈 브랜드스토어는 스마트폰 메신져 카카오톡의 캐릭터인 카카오프렌즈의 캐릭터상품을 판매하는 브랜드스토어다. 모바일 메신저 카카오톡 이모티콘에서 탄생한 캐릭터를 활용한 각각이 가진 넘치는 개성을 담은 캐릭터 상품을 만나볼 수 있다.");
                    overviews.add("서울 강남에 자리한 식물관 PH는 ‘도심 속에서 식물과 사람이 함께 쉬는 집’을 콘셉트로 카페와 다양한 전시를 즐길 수 있는 복합문화공간이다.");
                    times.add("1시간"); times.add("30분"); times.add("1시간");
                    prices.add("40,000원"); prices.add("? 원"); prices.add("10,000원");
                    Intent intent = new Intent(MBTI.this, MBTI_map.class);
                    intent.putExtra("price","50,000원");
                    intent.putExtra("time","30분");
                    intent.putStringArrayListExtra("titles",(ArrayList<String>) titles);
                    intent.putIntegerArrayListExtra("images",(ArrayList<Integer>) images);
                    intent.putStringArrayListExtra("X",(ArrayList<String>) X);
                    intent.putStringArrayListExtra("Y",(ArrayList<String>) Y);
                    intent.putStringArrayListExtra("overview",(ArrayList<String>) overviews);
                    intent.putStringArrayListExtra("times",(ArrayList<String>) times);
                    intent.putStringArrayListExtra("prices",(ArrayList<String>) prices);
                    startActivity(intent);
                }
            });
        }
    }
}