package com.example.datingcourse;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView.CurrentLocationEventListener;

public class FragLocation extends Fragment implements CurrentLocationEventListener {
    private static final int PERMISSIONS_REQUEST_CODE = 1000;

    private View view;
    private MapView mapView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_frag_location, container, false);

        mapView = new MapView(getActivity());
        ViewGroup mapViewContainer = (ViewGroup) view.findViewById(R.id.location_mapView);
        mapViewContainer.addView(mapView);
        mapView.setCurrentLocationEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setShowCurrentLocationMarker(true);  // 현재 위치 마커 표시

        requestLocationPermission();

        return view;
    }

    private void requestLocationPermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapView.setCurrentLocationEventListener(this);
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                mapView.setShowCurrentLocationMarker(true);  // 현재 위치 마커 표시
            } else {
                // 권한이 거부되었을 때의 처리
                Toast.makeText(getActivity(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            mapView.setShowCurrentLocationMarker(false);
            mapView = null;
        }
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracy) {
        // 현재 위치 업데이트 시 호출되는 콜백
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude), true);
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float headingAngle) {
        // 디바이스 방향 변경 시 호출되는 콜백
    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
        // 현재 위치 업데이트 실패 시 호출되는 콜백
        Toast.makeText(getActivity(), "현재 위치 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
        // 현재 위치 업데이트 취소 시 호출되는 콜백
    }
}
