package com.tmaxos.os1_1_slack_bot.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExcelParser {

    private Logger logger = LoggerFactory.getLogger(ExcelParser.class);
    private XSSFWorkbook workbook = null;


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

    private void allocationWorkbook(String excelPath){
        try (FileInputStream stream = new FileInputStream(excelPath)) {
            workbook = new XSSFWorkbook(stream);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public ExcelParser(){
        logger.warn("internal excel");
    }

    public void loadExcel(String excelPath, boolean force) {

        if(workbook == null){
            allocationWorkbook(excelPath);
        }
        else if(force){
            try {
                workbook.close();
                allocationWorkbook(excelPath);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        /*
        else{
            logger.warn("already opened");
        }
        */
    }

    public Row getRow(int rowIndex, int sheetIndex){
        if (workbook == null) return null;
        return workbook.getSheetAt(sheetIndex).getRow(rowIndex);
    }

    public List<CellRangeAddress> getMergedRange(int sheetIndex){
        if (workbook == null) return null;

        Sheet sheet = workbook.getSheetAt(sheetIndex);
        List<CellRangeAddress> mergedRange = new ArrayList<>();

        for (int i = 0; i < sheet.getNumMergedRegions(); i++)
            mergedRange.add(sheet.getMergedRegion(i));

        if(mergedRange.isEmpty()) return null;

        return mergedRange;
    }
}
