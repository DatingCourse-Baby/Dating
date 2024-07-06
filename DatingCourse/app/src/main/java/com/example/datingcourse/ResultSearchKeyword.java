package com.example.datingcourse;

import java.util.List;

// 검색 결과를 담는 클래스
public class ResultSearchKeyword {

    private PlaceMeta meta; // 장소 메타데이터
    private List<Place> documents;  // 검색 결과

    public ResultSearchKeyword(PlaceMeta meta, List<Place> documents) {
        this.meta = meta;
        this.documents = documents;
    }

    // getters and setters

    public PlaceMeta getMeta() {
        return meta;
    }

    public void setMeta(PlaceMeta meta) {
        this.meta = meta;
    }

    public List<Place> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Place> documents) {
        this.documents = documents;
    }
    @Override
    public String toString() {
        return  "ResultSearchKeyword{" +
                "documents=" + documents +
                ", meta=" + meta +
                '}';
    }
}

// 장소 메타데이터
class PlaceMeta {
    private int total_count; // 검색어에 검색된 문서 수
    private int pageable_count;  // total_count 중 노출 가능 문서 수, 최대 45 (API에서 최대 45개 정보만 제공)
    private boolean is_end;  // 현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음
    private RegionInfo same_name;    // 질의어의 지역 및 키워드 분석 정보

    public PlaceMeta(int total_count, int pageable_count, boolean is_end, RegionInfo same_name) {
        this.total_count = total_count;
        this.pageable_count = pageable_count;
        this.is_end = is_end;
        this.same_name = same_name;
    }

    // getters and setters
    public int getTotalCount() {
        return total_count;
    }

    public void setTotalCount(int totalCount) {
        this.total_count = totalCount;
    }

    public int getPageableCount() {
        return pageable_count;
    }

    public void setPageableCount(int pageableCount) {
        this.pageable_count = pageableCount;
    }

    public boolean isEnd() {
        return is_end;
    }

    public void setEnd(boolean end) {
        is_end = end;
    }

    public RegionInfo getSameName() {
        return same_name;
    }

    public void setSameName(RegionInfo sameName) {
        this.same_name = sameName;
    }
    @Override
    public String toString() {
        return "PlaceMeta{" +
                "total_count=" + total_count +
                ", pageable_count=" + pageable_count +
                ", is_end=" + is_end +
                ", same_name" + same_name +
                '}';
    }
}

// 질의어의 지역 및 키워드 분석 정보
class RegionInfo {
    private List<String> region;    // 질의어에서 인식된 지역의 리스트, ex) '중앙로 맛집' 에서 중앙로에 해당하는 지역 리스트
    private String keyword; // 질의어에서 지역 정보를 제외한 키워드, ex) '중앙로 맛집' 에서 '맛집'
    private String selected_region;  // 인식된 지역 리스트 중, 현재 검색에 사용된 지역 정보

    public RegionInfo(List<String> region, String keyword, String selected_region) {
        this.region = region;
        this.keyword = keyword;
        this.selected_region = selected_region;
    }

    // getters and setters

    public void setRegion(List<String> region) {
        this.region = region;
    }

    public List<String> getRegion() {
        return region;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSelectedRegion() {
        return selected_region;
    }

    public void setSelectedRegion(String selected_region) {
        this.selected_region = selected_region;
    }

    @Override
    public String toString() {
        return "RegionInfo{" +
                "region=" + region +
                ", keyword=" + keyword +
                ", selected_region" + selected_region +
                '}';
    }
}

// 장소 정보
class Place {
    private String id;  // 장소 ID
    private String place_name;   // 장소명, 업체명
    private String category_name;    // 카테고리 이름
    private String category_group_code;   // 중요 카테고리만 그룹핑한 카테고리 그룹 코드
    private String category_group_name;   // 중요 카테고리만 그룹핑한 카테고리 그룹명
    private String phone;   // 전화번호
    private String address_name; // 전체 지번 주소
    private String road_address_name; // 전체 도로명 주소
    private String x;   // X 좌표값 혹은 longitude
    private String y;   // Y 좌표값 혹은 latitude
    private String place_url;    // 장소 상세페이지 URL
    private String distance;    // 중심좌표까지의 거리. 단, x,y 파라미터를 준 경우에만 존재. 단위는 meter

    public Place(String id, String place_name, String category_name, String category_group_code, String category_group_name, String phone, String address_name, String road_address_name, String x, String y, String place_url, String distance) {
        this.id = id;
        this.place_name = place_name;
        this.category_name = category_name;
        this.category_group_code = category_group_code;
        this.category_group_name = category_group_name;
        this.phone = phone;
        this.address_name = address_name;
        this.road_address_name = road_address_name;
        this.x = x;
        this.y = y;
        this.place_url = place_url;
        this.distance = distance;
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceName() {
        return place_name;
    }

    public void setPlaceName(String place_name) {
        this.place_name = place_name;
    }

    public String getCategoryName() {
        return category_name;
    }

    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }

    public String getCategoryGroupCode() {
        return category_group_code;
    }

    public void setCategoryGroupCode(String category_group_code) {
        this.category_group_code = category_group_code;
    }

    public String getCategoryGroupName() {
        return category_group_name;
    }

    public void setCategoryGroupName(String category_group_name) {
        this.category_group_name = category_group_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressName() {
        return address_name;
    }

    public void setAddressName(String address_name) {
        this.address_name = address_name;
    }

    public String getRoadAddressName() {
        return road_address_name;
    }

    public void setRoadAddressName(String road_address_nameame) {
        this.road_address_name = road_address_nameame;
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

    public String getPlaceUrl() {
        return place_url;
    }

    public void setPlaceUrl(String place_url) {
        this.place_url = place_url;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        // 멤버 변수들을 문자열로 반환합니다.
        return "Place{" +
                "id = '" + id + '\'' +
                ",address_name='" + address_name + '\'' +
                ", category_name='" + category_name + '\'' +
                ", category_group_code='" + category_group_code + '\'' +
                ", category_group_name='" + category_group_name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", phone='" + phone + '\'' +
                ", place_url='" + place_url + '\'' +
                ", distance='" + distance + '\'' +
                '}';
    }
}
