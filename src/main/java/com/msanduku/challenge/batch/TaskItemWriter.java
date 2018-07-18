package com.msanduku.challenge.batch;

import com.msanduku.challenge.model.Users;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

/**
 *
 * @author Clifford Owino
 */
public class TaskItemWriter implements ItemStreamWriter<Users> {

    private XSSFWorkbook wb;
    FileOutputStream fos;
    private int row;

    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        wb = new XSSFWorkbook();
        XSSFSheet s = wb.createSheet();
        try {
            fos = new FileOutputStream("C:\\Users\\Clifford Owino\\Desktop\\Challenge\\output.xlsx");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        row = 0;
        createHeaderRow(s);
    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {
        //not updating anything yet
    }

    @Override
    public void close() throws ItemStreamException {
        if (wb == null) {
            return;
        }
        try {
            wb.write(fos);
//            wb.close();
        } catch (IOException ex) {
            ex.printStackTrace();
//            try {
//                wb.close();
//            } catch (IOException ex1) {
//                ex1.printStackTrace();
//            }

        }
        row = 0;
    }

    @Override
    public void write(List<? extends Users> list) throws Exception {
        XSSFSheet s = wb.getSheetAt(0);

        list.stream().forEach((o) -> {
            Row r = s.createRow(row++);

            Cell c = r.createCell(0);
            c.setCellType(Cell.CELL_TYPE_STRING);
            c.setCellValue(o.getEmpID());

            c = r.createCell(1);
            c.setCellType(Cell.CELL_TYPE_STRING);
            c.setCellValue(o.getNamePrefix());

            c = r.createCell(2);
            c.setCellType(Cell.CELL_TYPE_STRING);
            c.setCellValue(o.getFirstName());

            c = r.createCell(3);
            c.setCellType(Cell.CELL_TYPE_STRING);
            c.setCellValue(o.getMiddleInitial());

            c = r.createCell(4);
            c.setCellType(Cell.CELL_TYPE_STRING);
            c.setCellValue(o.getLastName());

            c = r.createCell(5);
            c.setCellType(Cell.CELL_TYPE_STRING);
            c.setCellValue(o.getGender());

            c = r.createCell(6);
            c.setCellType(Cell.CELL_TYPE_STRING);
            c.setCellValue(o.geteMail());

            c = r.createCell(7);
            c.setCellType(Cell.CELL_TYPE_STRING);
            c.setCellValue(o.getFatherName());

            c = r.createCell(8);
            c.setCellType(Cell.CELL_TYPE_STRING);
            c.setCellValue(o.getMotherName());

            c = r.createCell(9);
            c.setCellType(Cell.CELL_TYPE_STRING);
            c.setCellValue(o.getMotherMaidenName());

            c = r.createCell(10);
            c.setCellType(Cell.CELL_TYPE_STRING);
            c.setCellValue(o.getDateofBirth());
        });
    }

    private void createHeaderRow(XSSFSheet s) {

        XSSFRow r = s.createRow(row);

        Cell c = r.createCell(0);
        c.setCellValue("EmpID");
        c = r.createCell(1);
        c.setCellValue("NamePrefix");
        c = r.createCell(2);
        c.setCellValue("FirstName");
        c = r.createCell(3);
        c.setCellValue("MiddleInitial");
        c = r.createCell(4);
        c.setCellValue("LastName");
        c = r.createCell(5);
        c.setCellValue("Gender");
        c = r.createCell(6);
        c.setCellValue("EMail");
        c = r.createCell(7);
        c.setCellValue("FatherName");
        c = r.createCell(8);
        c.setCellValue("MotherName");
        c = r.createCell(9);
        c.setCellValue("MotherMaidenName");
        c = r.createCell(10);
        c.setCellValue("DateofBirth");

        row++;
    }

}
