package com.example.datingcourse;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InformationAPI extends AppCompatActivity {

    int contentTypeId;
    int sigunguCode;
    String cat1;
    String cat2;
    String cat3;

    public InformationAPI(int contentTypeId, int sigunguCode, String cat1, String cat2, String cat3){
        this.contentTypeId = contentTypeId;
        this.sigunguCode = sigunguCode;
        this.cat1 = cat1;
        this.cat2 = cat2;
        this.cat3 = cat3;
    }

    public interface DataListener {
        void onDataReceived(List<String> titles, List<String> images, List<String> address, List<String> x, List<String> y, List<String> tel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        new NetworkTask().execute();
    }
    class NetworkTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String url = "http://apis.data.go.kr/B551011/KorService1/areaBasedList1?numOfRows=400&pageNo=1&MobileOS=AND&MobileApp=AppTest"
                    + "&contentTypeId=" + contentTypeId
                    + "&areaCode=1"
                    + "&sigunguCode=" + sigunguCode
                    + "&cat1=" + cat1
                    + "&cat2=" + cat2
                    + "&cat3=" + cat3
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
                List<String> address = extractAddress(xmlResponse);
                List<String> x = extractX(xmlResponse);
                List<String> y = extractY(xmlResponse);
                List<String> tel = extractTel(xmlResponse);

                if (PlaceList.dataListener != null) {
                    PlaceList.dataListener.onDataReceived(titles, images, address, x, y, tel);
                } else {
                    Log.d("error", "error");
                }
                if (CourseMakingActivity.dataListener != null) {
                    CourseMakingActivity.dataListener.onDataReceived(titles, images,address,x,y,tel);
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

        // 응답 XML에서 모든 주소 추출
        private List<String> extractAddress(String xmlResponse) {
            List<String> address = new ArrayList<>();
            Pattern pattern = Pattern.compile("<addr1>(.*?)</addr1>");
            Matcher matcher = pattern.matcher(xmlResponse);
            while (matcher.find()) {
                address.add(matcher.group(1));
            }
            return address;
        }

        private List<String> extractX(String xmlResponse) {
            List<String> x = new ArrayList<>();
            Pattern pattern = Pattern.compile("<mapx>(.*?)</mapx>");
            Matcher matcher = pattern.matcher(xmlResponse);
            while (matcher.find()) {
                x.add(matcher.group(1));
            }
            return x;
        }

        private List<String> extractY(String xmlResponse) {
            List<String> y = new ArrayList<>();
            Pattern pattern = Pattern.compile("<mapy>(.*?)</mapy>");
            Matcher matcher = pattern.matcher(xmlResponse);
            while (matcher.find()) {
                y.add(matcher.group(1));
            }
            return y;
        }

        private List<String> extractTel(String xmlResponse) {
            List<String> tel = new ArrayList<>();
            Pattern pattern = Pattern.compile("<tel>(.*?)</tel>");
            Matcher matcher = pattern.matcher(xmlResponse);
            while (matcher.find()) {
                tel.add(matcher.group(1));
            }
            return tel;
        }
    }
}