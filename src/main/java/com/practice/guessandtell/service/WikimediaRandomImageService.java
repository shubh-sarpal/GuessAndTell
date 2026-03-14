package com.practice.guessandtell.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.guessandtell.dto.RandomGeoImage;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;

@Service
public class WikimediaRandomImageService {

    private static final String API_URL =
            "https://commons.wikimedia.org/w/api.php" +
                    "?action=query" +
                    "&generator=random" +
                    "&grnnamespace=6" +          // Files only
                    "&prop=coordinates|imageinfo" +
                    "&iiprop=url" +
                    "&colimit=1" +
                    "&format=json";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RandomGeoImage fetchRandomGeoImage() {

        int attempts = 0;

        while (attempts < 15) {   // a bit more retries for safety
            attempts++;

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "GuessAndTellGame/1.0 (shubhsarpal1@gmail.com)");

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        API_URL,
                        HttpMethod.GET,
                        entity,
                        String.class
                );

                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode pages = root.path("query").path("pages");

                if (pages.isMissingNode() || pages.isEmpty()) {
                    continue;
                }

                Iterator<JsonNode> elements = pages.elements();
                if (!elements.hasNext()) {
                    continue;
                }

                JsonNode page = elements.next();

                JsonNode imageInfoArray = page.path("imageinfo");
                JsonNode coordinatesArray = page.path("coordinates");

                // If no coordinates, skip and retry
                if (imageInfoArray.isEmpty() || coordinatesArray.isEmpty()) {
                    continue;
                }

                String imageUrl = imageInfoArray.get(0).path("url").asText();

                JsonNode coordinate = coordinatesArray.get(0);
                double lat = coordinate.path("lat").asDouble();
                double lon = coordinate.path("lon").asDouble();

                return new RandomGeoImage(imageUrl, lat, lon);

            } catch (Exception e) {
                System.out.println("Attempt " + attempts + " failed: " + e.getMessage());
            }
        }

        throw new RuntimeException("Failed to fetch random geo image after retries");
    }
}