package com.mockservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockservice.model.Place;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only catalog of all mock places (name + coordinates). Used by the locations agent for lookup.
 */
@RestController
@RequestMapping("/api/v1/places")
public class PlaceCatalogController {

    @Value("classpath:mock_places.json")
    private Resource mockPlacesResource;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(produces = "application/json")
    public List<Place> listAll() throws Exception {
        if (!mockPlacesResource.exists()) {
            throw new IllegalStateException("mock_places.json not found in classpath");
        }
        return objectMapper.readValue(
                mockPlacesResource.getInputStream(),
                new TypeReference<List<Place>>() {}
        );
    }
}
