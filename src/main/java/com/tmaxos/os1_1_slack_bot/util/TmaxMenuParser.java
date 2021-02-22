package com.tmaxos.os1_1_slack_bot.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TmaxMenuParser {

    private final Logger logger = LoggerFactory.getLogger(TmaxMenuParser.class);

    // value is row index
    public enum Menu {
        LUNCH(3), DINNER(6);

        private int index;

        Menu(int index) {
            this.index = index;
        }

        public int getValue() {
            return this.index;
        }
    }

    // value is column index
    public enum Day {
        MONDAY(2), TUESDAY(3), WEDNESDAY(4), THURSDAY(5), FRIDAY(6);

        private int index;

        Day(int index) {
            this.index = index;
        }

        public int getValue() {
            return this.index;
        }

        static public Day fromInt(int index) {
            Day day = Day.MONDAY;
            day.index = index;
            return day;
        }
    }

    @Autowired
    private ExcelParser parser;

    @Autowired
    @Value("${menu-path}")
    private String path;

    public TmaxMenuParser(ExcelParser p){
        parser = p;
    }

    public void load(Boolean forceLoad) {
        parser.loadExcel(path, forceLoad);
    }
    public void reload(){
        load(true);
    }

    public List<List<String>> getMenuContents(Menu menu, Day day) {

        List<List<String>> menuContents = new ArrayList<>();
        List<CellRangeAddress> menuCells = getMenuCell();

        if(menuCells == null || menuCells.isEmpty()) return null;

        int start = menu.getValue(), end;
        if(menu == Menu.LUNCH){
            end = Menu.DINNER.getValue();
        }
        else if(menu == Menu.DINNER){
            end = menuCells.size();
        }
        else{
            logger.error("invalid menu enum");
            return null;
        }

        // cnt < 2 -> main menu. without take-out menu.
        for(int i = start, cnt = 0; i < end && cnt < 2; i++, cnt++){
            List<String> menuList = new ArrayList<>();

            CellRangeAddress cell = menuCells.get(i);
            for(int j = cell.getFirstRow(); j <= cell.getLastRow(); j++){
                Row row = parser.getRow(j, 0);
                Cell contentCell = row.getCell(day.getValue());
                if(contentCell == null) continue;
                String value = contentCell.getStringCellValue();
                if(value != null && !value.isEmpty())
                    menuList.add(value);
            }
            menuContents.add(menuList);
        }

        return menuContents;
    }

    // menuCell -> Merged Cell
    private List<CellRangeAddress> getMenuCell(){
        List<CellRangeAddress> mergedRanges = parser.getMergedRange(0); // always get from first sheet
        if(mergedRanges == null || mergedRanges.isEmpty()){
            logger.error("excel format different from the previous one or excel file not found");
            return null;
        }

        List<CellRangeAddress> menuRanges = new ArrayList<>();
        for (CellRangeAddress range : mergedRanges) {

            // menu range`s column is always 1.
            if(range.getFirstColumn() == 1 && range.getLastColumn() == 1){
                menuRanges.add(range);
            }
        }

        menuRanges.sort((c0, c1) -> {
            int startRow0 = c0.getFirstRow();
            int startRow1 = c1.getFirstRow();
            if(startRow0 > startRow1) return 1;
            else if(startRow0 < startRow1) return -1;
            return 0;
        });

        return menuRanges;
    }

}
