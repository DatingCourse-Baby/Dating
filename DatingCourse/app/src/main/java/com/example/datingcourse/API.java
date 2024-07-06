package com.example.datingcourse;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class API extends AppCompatActivity {
    int contentTypeId;
    int sigunguCode;
    public API(int contentTypeId, int sigunguCode){
        this.contentTypeId = contentTypeId;
        this.sigunguCode = sigunguCode;
    }
    public interface DataListener {
        void onDataReceived(List<String> titles, List<String> images);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        new NetworkTask().execute();
    }



    class NetworkTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

//            String url = "http://apis.data.go.kr/B551011/KorService1/areaBasedList1?numOfRows=400&pageNo=1&MobileOS=AND&MobileApp=AppTest&contentTypeId=39&areaCode=1&sigunguCode=1&cat1=&cat2=&cat3=" +
//                    "&ServiceKey=rCuGpcOUXRWuiw%2BNFCufONVDvLJehhabXPhAOB2uihD01gPnNfqqdwr2wDEznka6MMa1%2Bs7im%2FMidLje1z4U5Q%3D%3D";
            String url = "http://apis.data.go.kr/B551011/KorService1/areaBasedList1?numOfRows=400&pageNo=1&MobileOS=AND&MobileApp=AppTest"
                    + "&contentTypeId=" + contentTypeId
                    + "&areaCode=1"
                    + "&sigunguCode=" + sigunguCode
                    + "&cat1=&cat2=&cat3="
                    + "&ServiceKey=rCuGpcOUXRWuiw%2BNFCufONVDvLJehhabXPhAOB2uihD01gPnNfqqdwr2wDEznka6MMa1%2Bs7im%2FMidLje1z4U5Q%3D%3D";
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String xmlResponse) {
            super.onPostExecute(xmlResponse);
            if (xmlResponse != null) {
                // 응답 XML을 파싱하여 모든 정보를 추출하고 출력합니다.
                List<String> titles = extractTitles(xmlResponse);
                List<String> images = extractImages(xmlResponse);


                if (RandomActivity2.dataListener != null) {
                    RandomActivity2.dataListener.onDataReceived(titles, images);
                }
            }
        }

        // 응답 XML에서 모든 상호명 추출
        private List<String> extractTitles(String xmlResponse) {
            List<String> titles = new ArrayList<>();
            Pattern pattern = Pattern.compile("<title>(.*?)</title>");
            Matcher matcher = pattern.matcher(xmlResponse);
            while (matcher.find()) {
                titles.add(matcher.group(1));
            }
            return titles;
        }

        // 응답 XML에서 모든 이미지 링크 추출
        private List<String> extractImages(String xmlResponse) {
            List<String> images = new ArrayList<>();
            Pattern pattern = Pattern.compile("<firstimage>(.*?)</firstimage>");
            Matcher matcher = pattern.matcher(xmlResponse);
            while (matcher.find()) {
                images.add(matcher.group(1));
            }
            return images;
        }


    }
}


