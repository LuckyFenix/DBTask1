package com.LuckyFenix;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame
{
    DBC dbc;
    TableModel tableModel;
    JTable table;

    public Main()
    {
        dbc = new DBC("dbfortask");

        setLayout(new BorderLayout());
        JPanel btnPanel = new JPanel(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel tablePanel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(dbc.getTableColumns("users").toArray(), 0);
        table = new JTable(tableModel);

        initTable();

        JButton addRow = new JButton("Новая строка");
        JButton toExcelBtn = new JButton("Экспорт в Excel");


        List addList = new ArrayList<>();
        addList.add(null);
        addList.add("Lalala");
        addList.add("12345");
        dbc.addRow(addList, "users");

        List<List> allRow = dbc.getAll("users");
        allRow.forEach(System.out::println);
        dbc.close();

        POI poi = new POI("excel.xlsx");
        Workbook workbook = poi.getWorkbook();
        Sheet sheet = workbook.createSheet("DBTask");
        for (int i = 0; i < allRow.size(); i++)
        {
            Row row = sheet.createRow(i);
            for (int i1 = 0; i1 < allRow.get(i).size(); i1++)
            {
                row.createCell(i1).setCellValue((String) allRow.get(i).get(i1));
            }
        }
        poi.writeWorkbook(workbook);
    }

    private void initTable()
    {
        table.removeAll();

        List<List> list = dbc.getAll("users");
        Object[][] array = new Object[list.size()][tableModel.getColumnCount()];

        for (int i = 0; i < list.size(); i++)
        {
            for (int i1 = 0; i1 < tableModel.getColumnCount(); i1++)
            {
                array[i][i1] = list.get(i).get(i1);
            }
        }

        //table.addColumn(new); = new DefaultTableModel(array);
    }


    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            Main main = new Main();
            main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            main.setVisible(false);
        });
    }
}
