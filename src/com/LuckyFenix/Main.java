package com.LuckyFenix;

import java.util.ArrayList;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        DBC dbc = new DBC("dbfortask");
        List addList = new ArrayList<>();
        addList.add(null);
        addList.add("Lalala");
        addList.add("12345");
        dbc.addRow(addList, "users");
        dbc.getAll("users").forEach(System.out::println);
        dbc.close();
        POI poi = new POI("excel.xmlx");
    }
}
