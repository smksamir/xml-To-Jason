package com.example;

import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            String filePath = "src/main/resources/input.xml";
            String xmlData = XMLToJsonConverter.readXmlFile(filePath);
            JSONObject jsonOutput = XMLToJsonConverter.convertXmlToJson(xmlData);
            System.out.println(jsonOutput.toString(4));
            logger.info("XML to JSON conversion completed successfully.");
        } catch (IOException e) {
            logger.error("Error reading XML file: ", e);
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
        }
    }
}
