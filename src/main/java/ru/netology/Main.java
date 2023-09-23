package ru.netology;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        // json
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json);

        // xml
        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(json2);
    }

    private static List<Employee> parseXML(String string) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(string));

            Node root = document.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            List<Employee> employees = new ArrayList<>();

            for (int i = 0; i< nodeList.getLength(); i++) {

                if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                if (!nodeList.item(i).getNodeName().equals("employee")) {
                    continue;
                }

                NodeList nodeChild = nodeList.item(i).getChildNodes();

                int id = 0;
                String firstName = "";
                String lastName = "";
                String country = "";
                int age = 0;

                for (int j = 0; j< nodeChild.getLength(); j++) {

                    if (nodeChild.item(j).getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    switch (nodeChild.item(j).getNodeName()) {
                        case "id": {
                            id = Integer.valueOf(nodeChild.item(j).getTextContent());
                            break;
                        }
                        case "firstName": {
                            firstName = nodeChild.item(j).getTextContent();
                            break;
                        }
                        case "lastName": {
                            lastName = nodeChild.item(j).getTextContent();
                            break;
                        }
                        case "country": {
                            country = nodeChild.item(j).getTextContent();
                            break;
                        }
                        case "age": {
                            age = Integer.valueOf(nodeChild.item(j).getTextContent());
                            break;
                        }
                    }

                }

                Employee employee = new Employee(id, firstName, lastName, country, age);
                employees.add(employee);
            }
            return employees;
        } catch (Exception e) {
        e.printStackTrace();
    }
        return null;
    }

    public static void writeString(String string) {

        try (FileWriter file = new
                FileWriter("data.json")) {
            file.write(string);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {

        try (CSVReader csvReader = new CSVReader(new FileReader("data.csv"))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> staff = csv.parse();
            return staff;
        } catch (IOException e) {
             e.printStackTrace();
        }
        return null;
    }
}