package com.example.datingcourse;

import android.content.res.AssetManager;

import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExcelReader {
    public static String readDataFromExcel(AssetManager assetManager, String file_path, String targetRegion) {
        StringBuilder result = new StringBuilder();
        try (InputStream inputStream = assetManager.open(file_path);
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            List<Row> matchingRows = new ArrayList<>();

            // 주어진 targetRegion과 일치하는 행들을 matchingRows에 저장
            for (Row row : sheet) {
                Cell addressCell = row.getCell(4);
                if (addressCell != null) {
                    String address = addressCell.getStringCellValue();
                    if (address.contains(targetRegion)) {
                        matchingRows.add(row);
                    }
                }
            }

            int numRows = matchingRows.size();

            if (numRows > 0) {
                // 랜덤한 인덱스 생성
                Random random = new Random();
                int randomIndex = random.nextInt(numRows);

                // 랜덤한 인덱스에 해당하는 행의 정보 가져오기
                Row randomRow = matchingRows.get(randomIndex);

                Cell nameCell = randomRow.getCell(0);  //가게 이름
                Cell phoneCell = randomRow.getCell(8);   //가게 번호
                Cell info = randomRow.getCell(7);   //개요
                Cell info2 = randomRow.getCell(9); //이용시간
                Cell info3 = randomRow.getCell(10);

                Cell latitudeCell = randomRow.getCell(5); //위도
                Cell longitudeCell = randomRow.getCell(6); //경도

                String name = nameCell.getStringCellValue();
                String phone = phoneCell.getStringCellValue();
                double latitude = 0;
                double longitude = 0;

                // 숫자 형식으로 변환하여 기본값으로 설정
                try {
                    if (latitudeCell.getCellType() == CellType.STRING) {
                        latitude = Double.parseDouble(latitudeCell.getStringCellValue());
                    }

                    if (longitudeCell.getCellType() == CellType.STRING) {
                        longitude = Double.parseDouble(longitudeCell.getStringCellValue());
                    }
                } catch (NumberFormatException e) {
                    // 숫자로 변환할 수 없는 경우에 대한 예외 처리
                    e.printStackTrace();
                }

                // 선택한 지역명을 포함하는 랜덤한 행의 '명칭'과 '전화번호' 추가
                result.append(name).append("  ").append(phone).append("  ").append(latitude).append("  ").append(longitude).append("  ").append(info).append("  ").append(info2).append("  ").append(info3).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}


//    public static List<MapPoint> readDataWithCoordinatesFromExcel(AssetManager assetManager, String file_path, String targetRegion) {
//        List<MapPoint> mapPoints = new ArrayList<>();
//
//        try (InputStream inputStream = assetManager.open(file_path);
//             Workbook workbook = WorkbookFactory.create(inputStream)) {
//
//            Sheet sheet = workbook.getSheetAt(0);
//
//            for (Row row : sheet) {
//                Cell addressCell = row.getCell(4);
//                if (addressCell != null) {
//                    String address = addressCell.getStringCellValue();
//                    if (address.contains(targetRegion)) {
//                        Cell latitudeCell = row.getCell(5);
//                        Cell longitudeCell = row.getCell(6);
//
//                        if (latitudeCell.getCellType() == CellType.NUMERIC && longitudeCell.getCellType() == CellType.NUMERIC) {
//                            double latitude = latitudeCell.getNumericCellValue();
//                            double longitude = longitudeCell.getNumericCellValue();
//
//                            // 선택한 지역명을 포함하는 행의 위도와 경도 추가
//                            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
//                            mapPoints.add(mapPoint);
//                        }else {
//                            // 데이터가 숫자 형식이 아닐 경우 기본값으로 0으로 설정
//                            double latitude = 0;
//                            double longitude = 0;
//
//                            // 숫자 형식으로 변환하여 기본값으로 설정
//                            try {
//                                if (latitudeCell.getCellType() == CellType.STRING) {
//                                    latitude = Double.parseDouble(latitudeCell.getStringCellValue());
//                                }
//
//                                if (longitudeCell.getCellType() == CellType.STRING) {
//                                    longitude = Double.parseDouble(longitudeCell.getStringCellValue());
//                                }
//                            } catch (NumberFormatException e) {
//                                // 숫자로 변환할 수 없는 경우에 대한 예외 처리
//                                e.printStackTrace();
//                            }
//
//                            // 기본값으로 생성한 위도와 경도를 추가
//                            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
//                            mapPoints.add(mapPoint);
//                        }
//                    }
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return mapPoints;
//    }

