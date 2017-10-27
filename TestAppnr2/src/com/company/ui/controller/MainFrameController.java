package com.company.ui.controller;

import com.company.ui.view.myGui;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Iterator;


public class MainFrameController {

    private myGui mainFrame;
    private JButton button1;
    private JTextArea textArea1;

    public MainFrameController(){
        initcomponents();
        initListeners();
    }

    public void show(){
        mainFrame.setVisible(true);
    }

    private void initListeners() {
        button1.addActionListener(new ButtonListener());
    }

    private void initcomponents() {
        mainFrame = new myGui();
        button1 = mainFrame.getButton1();
        textArea1 = mainFrame.getTextArea1();
    }

    /*public static void readXLSXFile() throws IOException
    {
        InputStream ExcelFileToRead = new FileInputStream("C:/Users/arnou/OneDrive/Documenten/test.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(ExcelFileToRead);

        XSSFWorkbook test = new XSSFWorkbook();

        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row;
        XSSFCell cell;

        Iterator rows = sheet.rowIterator();

        while (rows.hasNext())
        {
            row=(XSSFRow) rows.next();
            Iterator cells = row.cellIterator();
            while (cells.hasNext())
            {
                cell=(XSSFCell) cells.next();

                if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
                {
                    System.out.print(cell.getStringCellValue()+" ");
                }
                else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
                {
                    System.out.print(cell.getNumericCellValue()+" ");
                }
                else
                {
                    //U Can Handel Boolean, Formula, Errors
                }
            }
            System.out.println();
        }

    }*/

    private void ReadXLSFile(File file){
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file));
            HSSFSheet sheet = workbook.getSheetAt(0);
            HSSFRow row = sheet.getRow(0);
            if(row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_STRING)
                System.out.println(row.getCell(0).getStringCellValue());
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }
    }

    private void ReadXLSXFile(File file){


        try {
            InputStream inputFS = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(inputFS);
            Sheet sheet = workbook.getSheetAt(0);
            Row row;
            Iterator rows = sheet.rowIterator();
            row = sheet.getRow(0);
            if(row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_STRING)
                System.out.println(row.getCell(0).getStringCellValue());


        }catch (Exception e) {
            System.out.println(e);
        }


    }

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            File file = null;
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                System.out.println(file);

                String extension = "";

                int i = file.toString().lastIndexOf('.');
                if (i > 0) {
                    extension = file.toString().substring(i+1);
                }
                System.out.println(extension);
                if(extension.equals("xls")){
                    ReadXLSFile(file);
                }
                else if(extension.equals("xlsx")){
                    ReadXLSXFile(file);
                }
                else{
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        StringBuilder sb = new StringBuilder();
                        String line = br.readLine();

                        while (line != null) {
                            sb.append(line);
                            sb.append(System.lineSeparator());
                            line = br.readLine();
                        }
                        textArea1.append(sb.toString());
                    } catch (IOException err) {
                        System.out.println(err);
                    }
                }
            }


        }

    }
}
