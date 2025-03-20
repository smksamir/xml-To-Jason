package com.example;

import org.json.JSONObject;
import org.json.XML;
import org.json.JSONArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XMLToJsonConverter {
    private static final Logger logger = LogManager.getLogger(XMLToJsonConverter.class);
    private static final int MAX_SCORE = Integer.MAX_VALUE;

    public static String readXmlFile(String filePath) throws IOException {
        logger.info("Reading XML file: " + filePath);
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static JSONObject convertXmlToJson(String xmlData) {
        try {
            JSONObject jsonObject = XML.toJSONObject(xmlData);
            logger.info("XML successfully converted to JSON.");

            JSONObject resultBlock = jsonObject.getJSONObject("Response").getJSONObject("ResultBlock");

            // Calculate TotalMatchScore
            int totalMatchScore = calculateTotalMatchScore(resultBlock);

            // Add MatchSummary
            JSONObject matchSummary = new JSONObject();
            matchSummary.put("TotalMatchScore", totalMatchScore);
            resultBlock.put("MatchSummary", matchSummary);

            // Fix MatchDetails structure to match required output
            if (resultBlock.has("MatchDetails")) {
                JSONObject matchDetails = resultBlock.getJSONObject("MatchDetails");
                JSONArray matches = matchDetails.optJSONArray("Match");

                if (matches == null) {
                    // Convert single Match object into an array
                    JSONArray matchArray = new JSONArray();
                    matchArray.put(matchDetails.getJSONObject("Match"));
                    resultBlock.put("MatchDetails", matchArray);
                } else {
                    resultBlock.put("MatchDetails", matches);
                }
            }

            // Ensure API fields are explicitly set to null where necessary
            if (resultBlock.has("API")) {
                JSONObject api = resultBlock.getJSONObject("API");
                api.put("ErrorMessage", JSONObject.NULL);
                api.put("SysErrorCode", JSONObject.NULL);
                api.put("SysErrorMessage", JSONObject.NULL);
            }

            logger.info("Final JSON structure created successfully.");
            return jsonObject;
        } catch (Exception e) {
            logger.error("Error while converting XML to JSON: ", e);
            throw new RuntimeException("Failed to convert XML to JSON", e);
        }
    }

    private static int calculateTotalMatchScore(JSONObject resultBlock) {
        JSONObject matchDetails = resultBlock.optJSONObject("MatchDetails");
        int totalScore = 0;

        if (matchDetails != null) {
            JSONArray matches = matchDetails.optJSONArray("Match");

            if (matches == null) {
                totalScore = matchDetails.getJSONObject("Match").optInt("Score", 0);
            } else {
                for (int i = 0; i < matches.length(); i++) {
                    int score = matches.getJSONObject(i).optInt("Score", 0);

                    if (totalScore > MAX_SCORE - score) {
                        logger.error("Total match score exceeds integer limit.");
                        throw new ArithmeticException("Total match score exceeds maximum integer limit.");
                    }

                    totalScore += score;
                }
            }
        }
        return totalScore;
    }
}
