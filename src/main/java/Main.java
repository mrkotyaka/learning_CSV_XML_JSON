import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, ParseException {

        //task1
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        //task2
        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");

        //task3
        String json3 = readString("data.json");

        List<Employee> list3 = jsonToList(json3);

        for (Employee employee : list3) {
            System.out.println(employee);
        }
    }

    private static List<Employee> jsonToList(String json) throws ParseException {

        List<Employee> list = new ArrayList<>();

        JSONParser parser = new JSONParser();
        JSONArray employee = (JSONArray) parser.parse(json);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        for (Object emp : employee) {
            list.add(gson.fromJson(emp.toString(), Employee.class));
        }

        return list;
    }

    private static String readString(String fileName) {

        String line;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line.trim());
            }
        } catch (IOException er) {
            er.printStackTrace();
        }

        return stringBuilder.toString();
    }

    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        NodeList nodeList = doc.getElementsByTagName("employee");
        List<Employee> list = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
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

                Employee emp = new Employee(id, firstName, lastName, country, age);
                list.add(emp);
            }
        }

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

        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String fileName) {

        try (Writer writer = new FileWriter(fileName)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}