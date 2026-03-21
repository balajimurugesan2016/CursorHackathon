package com.mockservice.controller;

import com.mockservice.model.Place;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vessels_operations")
public class PlaceController {

    @Value("classpath:mock_places.json")
    private Resource mockPlacesResource;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(value = "/get-places", produces = "application/json")
    public List<Place> getPlaces(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) Double circle_radius
    ) throws Exception {

        if (!mockPlacesResource.exists()) {
            throw new IllegalStateException("mock_places.json not found in classpath");
        }

        List<Place> allPlaces = objectMapper.readValue(
                mockPlacesResource.getInputStream(),
                new TypeReference<List<Place>>() {}
        );

        // Default: 100 Nautical Miles = 185.2 km. Caller may override with circle_radius (km).
        double radiusKm = (circle_radius != null && circle_radius > 0) ? circle_radius : 185.2;

        return allPlaces.stream()
                .filter(place -> {
                    try {
                        double pLat = Double.parseDouble(place.latitude());
                        double pLon = Double.parseDouble(place.longitude());
                        return haversine(latitude, longitude, pLat, pLon) <= radiusKm;
                    } catch (NumberFormatException e) {
                        return false; // Skip invalid
                    }
                })
                .collect(Collectors.toList());
    }

    // Haversine formula to calculate distance in Kilometers
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // returns km
    }
}
