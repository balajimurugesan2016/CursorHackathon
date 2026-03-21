package com.hackathon.locationsagent.service;

import com.hackathon.locationsagent.client.PlaceCatalogClient;
import com.hackathon.locationsagent.model.PlaceDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceCatalogService {

    private final PlaceCatalogClient catalogClient;
    private volatile List<PlaceDto> cache;

    public PlaceCatalogService(PlaceCatalogClient catalogClient) {
        this.catalogClient = catalogClient;
    }

    public List<PlaceDto> getOrLoad() {
        List<PlaceDto> c = cache;
        if (c == null) {
            synchronized (this) {
                c = cache;
                if (c == null) {
                    cache = catalogClient.fetchCatalog();
                    c = cache;
                }
            }
        }
        return c;
    }

    public void refresh() {
        synchronized (this) {
            cache = catalogClient.fetchCatalog();
        }
    }
}
