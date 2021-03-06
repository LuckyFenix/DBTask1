package com.LuckyFenix;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * Created by LuckyFenix on 25.05.2014.
 */
public class POI
{
    private XSSFWorkbook workbook;
    private File file;

    public POI(String name)
    {
        try
        {
            file = new File(name);
            InputStream is = new FileInputStream(file);
            workbook = new XSSFWorkbook(is);
        } catch (FileNotFoundException e)
        {
            workbook = new XSSFWorkbook();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public POI()
    {
        workbook = new XSSFWorkbook();
    }

    public XSSFWorkbook getWorkbook()
    {
        return workbook;
    }

    public boolean writeWorkbook(Workbook workbook)
    {
        OutputStream fileOut;
        try
        {
            fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void setFile(String file)
    {
        this.file = new File(file);
    }
}
