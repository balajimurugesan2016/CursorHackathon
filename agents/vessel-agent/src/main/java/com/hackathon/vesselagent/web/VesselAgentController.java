package com.hackathon.vesselagent.web;

import com.hackathon.vesselagent.client.VesselApiClient;
import com.hackathon.vesselagent.client.VesselApiUnavailableException;
import com.hackathon.vesselagent.config.VesselSearchProperties;
import com.hackathon.vesselagent.model.VesselDto;
import com.hackathon.vesselagent.web.dto.VesselsNearbyResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/agent")
public class VesselAgentController {

    private final VesselApiClient vesselApiClient;
    private final VesselSearchProperties searchProperties;

    public VesselAgentController(VesselApiClient vesselApiClient, VesselSearchProperties searchProperties) {
        this.vesselApiClient = vesselApiClient;
        this.searchProperties = searchProperties;
    }

    /**
     * Returns all vessels within {@code circle_radius} (km) of the given point, per mock service Haversine logic.
     *
     * @param latitude  WGS84 latitude in degrees
     * @param longitude WGS84 longitude in degrees
     * @param radiusKm  search radius in kilometers (optional; default from {@code vessels.search.default-radius-km})
     */
    @GetMapping(value = "/vessels-nearby", produces = "application/json")
    public VesselsNearbyResponse vesselsNearby(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam(value = "radiusKm", required = false) Double radiusKm
    ) {
        validateCoordinates(latitude, longitude);
        double radius = radiusKm != null ? radiusKm : searchProperties.defaultRadiusKm();
        if (radius <= 0 || Double.isNaN(radius) || Double.isInfinite(radius)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "radiusKm must be a positive finite number");
        }

        List<VesselDto> vessels = vesselApiClient.fetchVesselsInRadius(latitude, longitude, radius);
        return new VesselsNearbyResponse(latitude, longitude, radius, vessels.size(), vessels);
    }

    private static void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "latitude must be between -90 and 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "longitude must be between -180 and 180");
        }
    }

    @ExceptionHandler(VesselApiUnavailableException.class)
    public ResponseEntity<ErrorBody> handleVesselApiDown(VesselApiUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorBody(ex.getMessage()));
    }

    public record ErrorBody(String message) {
    }
}
