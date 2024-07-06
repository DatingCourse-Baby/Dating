package com.example.datingcourse;
import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Bundle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.Timer;
import java.util.TimerTask;


import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


public class FragHome extends Fragment {
    private View view;
    private ViewPager viewPager;
    private int currentPage = 0;
    private InterstitialAd mInterstitialAd;
    private int[] imageIds = {R.drawable.ad1, R.drawable.ad2, R.drawable.ad3,R.drawable.ad4};
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_main, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        ImagePagerAdapter adapter = new ImagePagerAdapter(getActivity(), imageIds);
        viewPager.setAdapter(adapter);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            public void run() {
                if (currentPage == imageIds.length) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        // 자동 슬라이드를 위한 타이머 설정
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, 100, 4000); // 4초마다 슬라이드

        // 카페 페이지로 이동
        ImageButton imageButton1 = view.findViewById(R.id.imageButton1);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다른 액티비티로 이동
                int sigunguCode = 1;
                int contentTypeId = 39;
                String cat1 = "A05";
                String cat2 = "A0502";
                String cat3 = "A05020900";
                Intent intent = new Intent(getActivity(), PlaceList.class);    // Cafe
                intent.putExtra("contentTypeId", contentTypeId);
                intent.putExtra("sigunguCode", sigunguCode);
                intent.putExtra("cat1", cat1);
                intent.putExtra("cat2", cat2);
                intent.putExtra("cat3", cat3);
                intent.putExtra("selectedRegion", "서울특별시 강남구");
                startActivity(intent);
            }
        });

        // 식당 페이지로 이동
        ImageButton imageButton2 = view.findViewById(R.id.imageButton2);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sigunguCode = 1;
                int contentTypeId = 39;
                String cat1 = "";
                String cat2 = "";
                String cat3 = "";
                Intent intent = new Intent(getActivity(), PlaceList.class);    // Cafe
                intent.putExtra("contentTypeId", contentTypeId);
                intent.putExtra("sigunguCode", sigunguCode);
                intent.putExtra("cat1", cat1);
                intent.putExtra("cat2", cat2);
                intent.putExtra("cat3", cat3);
                intent.putExtra("selectedRegion", "서울특별시 강남구");
                startActivity(intent);
            }
        });

        // 숙박 페이지로 이동
        ImageButton imageButton3 = view.findViewById(R.id.imageButton3);
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다른 액티비티로 이동
                int sigunguCode = 1;
                int contentTypeId = 32;
                String cat1 = "";
                String cat2 = "";
                String cat3 = "";
                Intent intent = new Intent(getActivity(), PlaceList.class);    // Cafe
                intent.putExtra("contentTypeId", contentTypeId);
                intent.putExtra("sigunguCode", sigunguCode);
                intent.putExtra("cat1", cat1);
                intent.putExtra("cat2", cat2);
                intent.putExtra("cat3", cat3);
                intent.putExtra("selectedRegion", "서울특별시 강남구");
                startActivity(intent);
            }
        });

        // 코스 만들기 페이지로 이동
        ImageButton imageButton4 = view.findViewById(R.id.imageButton4);
        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다른 액티비티로 이동
                Intent intent = new Intent(getActivity(), CourseMakingActivity.class);
                startActivity(intent);
            }
        });

        // 코스 추천 페이지로 이동
        ImageButton imageButton5 = view.findViewById(R.id.imageButton5);
        imageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다른 액티비티로 이동
                Intent intent = new Intent(getActivity(), BoardActivity.class);
                startActivity(intent);
            }
        });

        // 랜덤 코스 페이지로 이동
        ImageButton imageButton6 = view.findViewById(R.id.imageButton6);
        imageButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다른 액티비티로 이동
                Intent intent = new Intent(getActivity(), RandomActivity.class);
                startActivity(intent);
            }
        });

        ImageView btn_chulseok = view.findViewById(R.id.btn_chulseok);
        btn_chulseok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.popup_chulseok, null);
                builder.setView(dialogView);

                Button ccheck = dialogView.findViewById((R.id.check));
                ccheck.setOnClickListener(new View.OnClickListener() {
                    int cnt = 0;
                    @Override
                    public void onClick(View v) {
                        ImageView stampImageView1 = dialogView.findViewById(R.id.stamp1);
                        ImageView stampImageView2 = dialogView.findViewById(R.id.stamp2);
                        ImageView stampImageView3 = dialogView.findViewById(R.id.stamp3);
                        ImageView stampImageView4 = dialogView.findViewById(R.id.stamp4);
                        ImageView stampImageView5 = dialogView.findViewById(R.id.stamp5);
                        ImageView stampImageView6 = dialogView.findViewById(R.id.stamp6);
                        ImageView stampImageView7 = dialogView.findViewById(R.id.stamp7);

                        if(cnt==0){
                            stampImageView1.setVisibility(View.VISIBLE);
                            cnt++;
                        }
                        else if(cnt==1) {
                            stampImageView2.setVisibility(View.VISIBLE);
                            cnt++;
                        }
                        else if(cnt==2) {
                            stampImageView3.setVisibility(View.VISIBLE);
                            cnt++;
                        }
                        else if(cnt==3) {
                            stampImageView4.setVisibility(View.VISIBLE);
                            cnt++;
                        }
                        else if(cnt==4) {
                            stampImageView5.setVisibility(View.VISIBLE);
                            cnt++;
                        }
                        else if(cnt==5) {
                            stampImageView6.setVisibility(View.VISIBLE);
                            cnt++;
                        }
                        else if(cnt==6) {
                            stampImageView7.setVisibility(View.VISIBLE);
                            cnt++;
                        }
                        else if(cnt==7){
                            cnt=0;
                            stampImageView1.setVisibility(View.INVISIBLE);
                            stampImageView2.setVisibility(View.INVISIBLE);
                            stampImageView3.setVisibility(View.INVISIBLE);
                            stampImageView4.setVisibility(View.INVISIBLE);
                            stampImageView5.setVisibility(View.INVISIBLE);
                            stampImageView6.setVisibility(View.INVISIBLE);
                            stampImageView7.setVisibility(View.INVISIBLE);
                        }
                    }
                });

                builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        ImageButton mbti_i = view.findViewById(R.id.mbti_i);
        mbti_i.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), PointItem.class);
                startActivity(intent);
//                String mbti = "i";
//                Intent intent = new Intent(getActivity(),MBTI.class);
//                intent.putExtra("mbti", mbti);
//                startActivity(intent);
            }
        });

        ImageButton mbti_e = view.findViewById(R.id.mbti_e);
        mbti_e.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                loadFrontAd();
                String mbti = "e";
                Intent intent = new Intent(getActivity(),MBTI.class);
                intent.putExtra("mbti", mbti);
                startActivity(intent);
            }
        });
        ImageButton mbti_p = view.findViewById(R.id.mbti_p);
        mbti_p.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String mbti = "p";
                Intent intent = new Intent(getActivity(),MBTI.class);
                intent.putExtra("mbti", mbti);
                startActivity(intent);
            }
        });

        ImageButton mbti_j = view.findViewById(R.id.mbti_j);
        mbti_j.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String mbti = "j";
                Intent intent = new Intent(getActivity(),MBTI.class);
                intent.putExtra("mbti", mbti);
                startActivity(intent);
            }
        });

        return view;




    }
    private void loadFrontAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        // ca-app-pub 부분은 나중에 마켓등록시 변경하면 된다.
        InterstitialAd.load(getActivity(), "ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // 광고 로드가 성공하면 보여 주는 로직을 타면 된다.
                        mInterstitialAd = interstitialAd;
                        showFrontAd();
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
    }
    private void showFrontAd() {

        // callback을 통해 광고의 상태를 확인 해서 추가적인 동작을 할 수 있다.
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.");
                mInterstitialAd = null;
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                mInterstitialAd = null;
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.");
            }
        });

        // show 호출
        mInterstitialAd.show(getActivity());
    }


}


