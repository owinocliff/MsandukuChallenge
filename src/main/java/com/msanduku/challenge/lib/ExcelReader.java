package com.msanduku.challenge.lib;

import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Clifford Owino
 */
public class ExcelReader implements Callable<String> {

    private String fileName;
    private String filePath = "C:\\Users\\Clifford Owino\\Desktop\\Challenge\\";
    private final Logger LOG;

    public ExcelReader(String fileName) {
        this.fileName = filePath + fileName;
        LOG = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public String call() throws Exception {
        
        LOG.info("request start : " + (new Timestamp(System.currentTimeMillis())));
        
        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create(new File(fileName));
        
        // Getting the Sheet at index zero
        Sheet sheet = workbook.getSheetAt(0);
        
        LOG.info("Sheet has " + sheet.getPhysicalNumberOfRows() + " rows : ");
        LOG.info("request stop : " + (new Timestamp(System.currentTimeMillis())));

        sheet.getPhysicalNumberOfRows();

        // Create a DataFormatter to format and get each cell's value as String
        DataFormatter dataFormatter = new DataFormatter();

//        LOG.info("\n\nIterating over Rows and Columns using Java 8 forEach with lambda\n");
//        sheet.forEach(row -> {
//            row.forEach(cell -> {
//                String cellValue = dataFormatter.formatCellValue(cell);
//                System.out.print(cellValue + "\t");
//            });
//            System.out.println();
//        });
        // Closing the workbook
        workbook.close();

        Map<String, String> myMap = new HashMap<>();
        myMap.put("state", "success");

        return myMap.toString();

    }

}
