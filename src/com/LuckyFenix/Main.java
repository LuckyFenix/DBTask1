package com.LuckyFenix;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Font;

public class Main extends JFrame
{
    private final JPanel tablePanel;
    private final JTextField loginField = new JTextField(15);
    private final JTextField passwdField = new JTextField(15);
    DBC dbc;
    DefaultTableModel tableModel;
    JTable table;
    private String login;
    private String pass;

    public Main()
    {
        dbc = new DBC("dbfortask");

        getContentPane().setLayout(new BorderLayout());
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        tablePanel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(dbc.getTableColumns("users").toArray(), 0);
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        initTable();

        JButton addRowBtn = new JButton("Новая строка");
        JButton toExcelBtn = new JButton("Экспорт в Excel");
        JButton toPdfBtn = new JButton("Экспорт в PDF");

        btnPanel.add(addRowBtn);
        btnPanel.add(toExcelBtn);
        btnPanel.add(toPdfBtn);


        addRowBtn.addActionListener(e ->
        {
            int value = JOptionPane.showOptionDialog(this,
                    new DialogPanel(),
                    "Новая строка",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new String[]{"Принять", "Отмена"},
                    null);

            try
            {
                switch (value)
                {
                    case JOptionPane.OK_OPTION:
                    {
                        List<String> list = new ArrayList<>();
                        list.add(null);
                        if (!login.trim().equals(""))
                        {
                            list.add(login);
                        } else
                        {
                            throw new Exception("");
                        }
                        if (!pass.trim().equals(""))
                        {
                            list.add(pass);
                        } else
                        {
                            throw new Exception("");
                        }
                        loginField.setText("");
                        passwdField.setText("");
                        dbc.addRow(list, "users");
                        initTable();
                        break;
                    }
                    case JOptionPane.NO_OPTION:
                    {
                        loginField.setText("");
                        passwdField.setText("");
                        break;
                    }
                }
            } catch (Exception e1)
            {
                JOptionPane.showMessageDialog(this, "Одно или больше полей пусты", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        toExcelBtn.addActionListener(e ->
        {
            List<List<String>> allRow = new ArrayList<>();
            List<String> list = new ArrayList<>();

            for (int i = 0; i < this.table.getColumnCount(); i++)
            {
                list.add(tableModel.getColumnName(i));
            }
            allRow.add(list);

            for (int i = 0; i < this.table.getRowCount(); i++)
            {
                list = new ArrayList<>();
                for (int i1 = 0; i1 < this.table.getColumnCount(); i1++)
                {
                    String value = (String) this.table.getValueAt(i, i1);
                    list.add(value);
                }
                allRow.add(list);
            }

            POI poi = new POI();
            poi.setFile("excel.xlsx");
            Workbook workbook = poi.getWorkbook();
            Sheet sheet = workbook.createSheet("DBTask");
            CellStyle headStyle = workbook.createCellStyle();
            headStyle.setBorderBottom(CellStyle.BORDER_THICK);
            headStyle.setBorderLeft(CellStyle.BORDER_THICK);
            headStyle.setBorderRight(CellStyle.BORDER_THICK);
            headStyle.setBorderTop(CellStyle.BORDER_THICK);
            CellStyle style = workbook.createCellStyle();
            style.setBorderBottom(CellStyle.BORDER_MEDIUM);
            style.setBorderLeft(CellStyle.BORDER_MEDIUM);
            style.setBorderRight(CellStyle.BORDER_MEDIUM);

            for (int i = 0; i < allRow.size(); i++)
            {
                Row row = sheet.createRow(i);
                for (int i1 = 0; i1 < allRow.get(i).size(); i1++)
                {
                    Cell cell = row.createCell(i1);
                    cell.setCellValue(allRow.get(i).get(i1));
                    if (i == 0)
                        cell.setCellStyle(headStyle);
                    else
                        cell.setCellStyle(style);
                }
            }
            poi.writeWorkbook(workbook);
        });

        toPdfBtn.addActionListener(e ->
        {
            try
            {
                Document doc = new Document();

                File pdfFile = new File("pdf.pdf");
                pdfFile.createNewFile();

                doc.open();

                PdfPTable table = new PdfPTable(3);

                for (int i = 0; i < this.table.getColumnCount(); i++)
                {
                    table.addCell(new PdfPCell(new Phrase(this.tableModel.getColumnName(i), new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD))));
                }

                for (int i = 0; i < this.table.getRowCount(); i++)
                {
                    for (int i1 = 0; i1 < this.table.getColumnCount(); i1++)
                    {
                        String value = (String) this.table.getValueAt(i, i1);
                        table.addCell(new PdfPCell(new Phrase(value, new Font(Font.FontFamily.TIMES_ROMAN, 12))));
                    }
                }
                doc.add(table);
                doc.close();
            } catch (IOException | DocumentException e1)
            {
                e1.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Экспорт завершен");
        });

        tablePanel.add(new JScrollPane(table));

        getContentPane().add(tablePanel, BorderLayout.CENTER);
        getContentPane().add(btnPanel, BorderLayout.EAST);
        /*List addList = new ArrayList<>();
        addList.add(null);
        addList.add("Lalala");
        addList.add("12345");
        dbc.addRow(addList, "users");

        List<List> allRow = dbc.getAll("users");
        allRow.forEach(System.out::println);
        dbc.close();

        */
    }

    private void initTable()
    {
        int count = tableModel.getRowCount();
        for (int i = 0; i < count; i++)
        {
            tableModel.removeRow(0);
        }

        List<List> list = dbc.getAll("users");

        for (List aList : list)
        {
            tableModel.addRow(aList.toArray());
        }

        tablePanel.updateUI();
    }


    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            Main main = new Main();
            main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            main.setSize(500, 300);
            main.setLocationRelativeTo(null);
            main.setVisible(true);
        });
    }

    public class DialogPanel extends JPanel
    {
        public DialogPanel()
        {
            setLayout(new GridLayout(2, 2));
            add(new JLabel("Логин:"));
            add(loginField);
            add(new JLabel("Пароль:"));
            add(passwdField);

            loginField.getDocument().addDocumentListener(new DocumentListener()
            {
                @Override
                public void insertUpdate(DocumentEvent e)
                {
                    login = loginField.getText();
                }

                @Override
                public void removeUpdate(DocumentEvent e)
                {
                    login = loginField.getText();
                }

                @Override
                public void changedUpdate(DocumentEvent e)
                {
                    login = loginField.getText();
                }
            });
            passwdField.getDocument().addDocumentListener(new DocumentListener()
            {
                @Override
                public void insertUpdate(DocumentEvent e)
                {
                    pass = passwdField.getText();
                }

                @Override
                public void removeUpdate(DocumentEvent e)
                {
                    pass = passwdField.getText();
                }

                @Override
                public void changedUpdate(DocumentEvent e)
                {
                    pass = passwdField.getText();
                }
            });
        }
    }
}
