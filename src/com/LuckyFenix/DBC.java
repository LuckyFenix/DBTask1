package com.LuckyFenix;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by LuckyFenix on 25.05.2014.
 */
public class DBC
{
    private final String dbName;
    private Connection db;
    private Statement st;
    private String tableName;

    public DBC(String dbName)
    {
        this.dbName = dbName;
        Properties connInfo = new Properties();
        connInfo.put("user", "root");
        connInfo.put("password", "root");
        connInfo.put("charSet", "UTF8");
        String host = "localhost";
        String url = "jdbc:mysql://" + host + "/" + dbName + "?useUnicode=true&characterEncoding=utf8";

        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            db = DriverManager.getConnection(url, connInfo);
            st = db.createStatement();
        } catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
        } catch (SQLException e)
        {
            try
            {
                url = "jdbc:mysql://" + host + "/information_schema";
                Connection db = DriverManager.getConnection(url, connInfo);
                Statement st = db.createStatement();
                st.execute("CREATE DATABASE " + dbName + " CHARACTER SET utf8 COLLATE utf8_general_ci;");
                url = "jdbc:mysql://" + host + "/" + dbName + "?useUnicode=true&characterEncoding=utf8";
                db = DriverManager.getConnection(url, connInfo);
            } catch (SQLException e1)
            {
                System.err.println(dbName);
                e1.printStackTrace();
            }
        }
    }

    public List<String> getTableColumns(String tableName)
    {
        List<String> columns = new ArrayList<>();
        try
        {
            DBC dbc = new DBC(dbName);
            ResultSet rs = dbc.st.executeQuery("SELECT * FROM `" + tableName + "`;");
            if (rs.next())
            {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
                {
                    columns.add(rs.getMetaData().getColumnLabel(i));
                }
            }
            rs.close();
            dbc.close();
        } catch (SQLException e) {e.printStackTrace();}

        return columns;
    }

    public boolean addRow(List list, String tableName)
    {
        this.tableName = tableName;
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO `").append(tableName).append("` ");
        builder.append("(");
        List columns = getTableColumns(tableName);
        for (int i = 0; i < columns.size(); i++)
        {
            String s = (String) columns.get(i);
            if (list.get(i) != null)
            {
                if (columns.get(0) == s)
                {
                    builder.append("`").append(s).append("`");
                } else
                {
                    builder.append(", `").append(s).append("`");
                }
            } else
            {
                columns.remove(i);
                list.remove(i);
                i--;
            }
        }
        builder.append(") ").append("VALUES (");
        for (Object s : list)
        {
            if (list.get(0).equals(s))
            {
                builder.append("'").append(s).append("'");
            } else
            {
                builder.append(", '").append(s).append("'");
            }
        }
        builder.append(");");
        try
        {
            DBC dbc = new DBC(dbName);
            st.execute(builder.toString());
            dbc.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return true;
    }

    private boolean addRow(List list)
    {
        return addRow(list, tableName);
    }

    public boolean addRows(List<List> list, String tableName)
    {
        this.tableName = tableName;
        list.forEach(this::addRow);
        return true;
    }

    public List<List> getAll(String tableName)
    {
        this.tableName = tableName;
        List<List> userList = new ArrayList<>();
        try
        {
            DBC dbc = new DBC(dbName);
            ResultSet rs = dbc.st.executeQuery("SELECT * FROM `" + tableName + "`;");
            while (rs.next())
            {
                List<String> list = new ArrayList<>();
                list.add(rs.getString("id"));
                list.add(rs.getString("login"));
                list.add(rs.getString("password"));
                userList.add(list);
            }
            dbc.close();
            rs.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
        return userList;
    }

    public void close()
    {
        try
        {
            db.close();
            st.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
