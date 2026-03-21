package com.hackathon.locationsagent.web;

import com.hackathon.locationsagent.client.PlaceCatalogUnavailableException;
import com.hackathon.locationsagent.resolve.ResolvedLocation;
import com.hackathon.locationsagent.service.LocationResolverService;
import com.hackathon.locationsagent.service.PlaceCatalogService;
import com.hackathon.locationsagent.web.dto.ResolveBatchRequest;
import com.hackathon.locationsagent.web.dto.ResolveBatchResponse;
import com.hackathon.locationsagent.web.dto.ResolveBatchResponse.ResolveBatchItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/agent")
public class LocationAgentController {

    private final PlaceCatalogService catalogService;
    private final LocationResolverService resolverService;

    public LocationAgentController(PlaceCatalogService catalogService, LocationResolverService resolverService) {
        this.catalogService = catalogService;
        this.resolverService = resolverService;
    }

    /**
     * Resolve a single place name to coordinates using the mock place catalog (fuzzy / overlap matching).
     */
    @GetMapping(value = "/resolve-location", produces = "application/json")
    public ResolvedLocation resolveLocation(@RequestParam("name") String name) {
        return resolverService
                .resolveOne(name, catalogService.getOrLoad())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No location matched for query: " + name
                ));
    }

    /**
     * Resolve many names; each entry returns a match or null if nothing clears the confidence threshold.
     */
    @PostMapping(value = "/resolve-locations", produces = "application/json")
    public ResolveBatchResponse resolveLocations(@RequestBody ResolveBatchRequest request) {
        List<String> queries = request.queries() != null ? request.queries() : List.of();
        List<ResolveBatchItem> items = new ArrayList<>();
        var places = catalogService.getOrLoad();
        for (String q : queries) {
            items.add(new ResolveBatchItem(
                    q,
                    resolverService.resolveOne(q, places).orElse(null)
            ));
        }
        return new ResolveBatchResponse(items);
    }

    /**
     * Reload the place catalog from the mock service (useful after mock data changes).
     */
    @PostMapping("/refresh-catalog")
    public ResponseEntity<Void> refreshCatalog() {
        catalogService.refresh();
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(PlaceCatalogUnavailableException.class)
    public ResponseEntity<ErrorBody> handleCatalogDown(PlaceCatalogUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorBody(ex.getMessage()));
    }

    public record ErrorBody(String message) {
    }
}
