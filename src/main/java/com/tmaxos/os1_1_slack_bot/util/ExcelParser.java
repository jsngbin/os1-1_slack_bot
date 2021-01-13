package com.tmaxos.os1_1_slack_bot.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExcelParser {

    private Logger logger = LoggerFactory.getLogger(ExcelParser.class);
    private XSSFWorkbook workbook = null;

    @Value("${excel-path}")
    private String path;

    public enum RowIndex {
        LUNCH(3), DINNER(6);

        private int index;

        private RowIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        }
    };

    public enum ColumnIndex {
        MONDAY(2), TUESDAY(3), WEDNESDAY(4), THURSDAY(5), FRIDAY(6);

        private int index;

        private ColumnIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        }
    };

    public void loadExcel(boolean force) {

        if (workbook == null || force) {
            try (FileInputStream stream = new FileInputStream(path)) {
                workbook = new XSSFWorkbook(stream);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public String getCellDatas(RowIndex index) {
        if (workbook == null)
            return "메뉴 정보가 없어요";

        Sheet sheet = workbook.getSheetAt(0);        
        List<CellRangeAddress> mergedRange = new ArrayList<>();

        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);

            // 메뉴 구분 range
            if (range.getFirstColumn() == 1 && range.getLastColumn() == 1) {
                mergedRange.add(range);
            }
        }

        mergedRange.sort((arg0, arg1) -> {
            int row0 = arg0.getFirstRow();
            int row1 = arg1.getFirstRow();
            if (row0 > row1)
                return 1;
            else if (row0 == row1)
                return 0;
            else
                return -1;
        });
        

        int start = 0;
        int end = 0;
        String title = "";
        if(index == RowIndex.LUNCH){
            start = RowIndex.LUNCH.getIndex();
            end = RowIndex.DINNER.getIndex();
            title = "*<점심>*";
        }
        else if(index == RowIndex.DINNER){
            start = RowIndex.DINNER.getIndex();
            end = mergedRange.size();
            title = "*<저녁>*";
        }

        StringBuilder builder = new StringBuilder();
        Calendar cal = Calendar.getInstance();        
        int todayIndex = cal.get(Calendar.DAY_OF_WEEK); // == column index.        

        builder.append(title + "\n");
        for(int i = start, cnt = 0; i < end && cnt < 2; i++, cnt++){
            CellRangeAddress range = mergedRange.get(i);
            for(int j = range.getFirstRow(); j <= range.getLastRow(); j++){
                Row row = sheet.getRow(j);
                Cell cell = row.getCell(todayIndex);                
                builder.append(cell.getStringCellValue() + "\t");
            }            
            builder.append("\n\n");
        }
        return builder.toString();

    }
}
