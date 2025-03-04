import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
//        String[] employee = "1,John,Smith,USA,25".split(",");
//        String[] employee2 = "2,Inav,Petrov,RU,23".split(",");
//
//        List<String[]> employees = new ArrayList<>();
//        employees.add(employee);
//        employees.add(employee2);
//
//        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv"))){
//            employees.forEach(writer::writeNext);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);

//        for (Employee employee1 : list) {
//            System.out.println(employee1.firstName + " " + employee1.lastName + " " + employee1.country + " " + employee1.age);
//        }


        String json = listToJson(list);
        writeString(json);


//        List<Employee> list = parseXML("data.xml");
        list = parseXML("data.xml");
        json = listToJson(list);
        writeString(json);
    }

    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

//        Node root = doc.getDocumentElement();
        NodeList nodeList = doc.getElementsByTagName("employee");


        List<Employee> list = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
//            System.out.println("Teкyщий элeмeнт: " + node.getNodeName());
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element employee = (Element) node;

                NodeList childID = employee.getElementsByTagName("id");
                Element elmID = (Element) childID.item(0);
                long id = Long.parseLong(elmID.getTextContent());

                NodeList childName = employee.getElementsByTagName("firstName");
                Element elmFirstName = (Element) childName.item(0);
                String firstName = elmFirstName.getTextContent();

                NodeList childLastName = employee.getElementsByTagName("lastName");
                Element elmLastName = (Element) childLastName.item(0);
                String lastName = elmLastName.getTextContent();

                NodeList childCountry = employee.getElementsByTagName("country");
                Element elmCountry = (Element) childCountry.item(0);
                String country = elmCountry.getTextContent();

                NodeList childAge = employee.getElementsByTagName("age");
                Element elmAge = (Element) childAge.item(0);
                int age = Integer.parseInt(elmAge.getTextContent());


//                for (int j = 0; j < employee.getChildNodes().getLength(); j++) {
//                    Node child = employee.getChildNodes().item(j);
//                    if (Node.ELEMENT_NODE == child.getNodeType()) {
//                        switch (child.getNodeName()) {
//                            case "id":
//                                long id = Long.parseLong(child.getTextContent());
//                                System.out.println(id);
//                                break;
//                            case "firstName":
//                                String firstName = child.getTextContent();
//                                System.out.println(firstName);
//                                break;
//                            case "lastName":
//                                String lastName = child.getTextContent();
//                                System.out.println(lastName);
//                                break;
//                            case "country":
//                                String country = child.getTextContent();
//                                System.out.println(country);
//                                break;
//                            case "age":
//                                int age = Integer.parseInt(child.getTextContent());
//                                System.out.println(age);
//                                break;
//                        }
//                    }
//                }

                Employee emp = new Employee(id, firstName, lastName, country, age);
                list.add(emp);
            }
        }

        for (Employee employee : list) {
            System.out.println(employee.id + " " + employee.firstName + " " + employee.lastName + " " + employee.country);
        }

//        for (int i = 0; i < nodeList.getLength(); i++) {
//            Node node = nodeList.item(i);
//        }

        return list;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {

            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

//        System.out.println(gson.toJson(list));

        return gson.toJson(list, listType);
    }

    private static void writeString(String json) {
        try (Writer writer = new FileWriter("data.json")) {
            writer.write(json);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}