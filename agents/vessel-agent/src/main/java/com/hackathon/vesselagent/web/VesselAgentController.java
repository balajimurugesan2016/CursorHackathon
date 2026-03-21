package com.hackathon.vesselagent.web;

import com.hackathon.vesselagent.client.VesselApiClient;
import com.hackathon.vesselagent.client.VesselApiUnavailableException;
import com.hackathon.vesselagent.config.VesselSearchProperties;
import com.hackathon.vesselagent.model.VesselDto;
import com.hackathon.vesselagent.util.NauticalMiles;
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
     * Returns all vessels within the given radius of the point. Radius is in <strong>nautical miles</strong> (international NM);
     * it is converted to kilometers for the mock service, which applies Haversine distance in km.
     *
     * @param latitude  WGS84 latitude in degrees
     * @param longitude WGS84 longitude in degrees
     * @param radiusNm  search radius in nautical miles (optional; default from {@code vessels.search.default-radius-nm})
     */
    @GetMapping(value = "/vessels-nearby", produces = "application/json")
    public VesselsNearbyResponse vesselsNearby(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam(value = "radiusNm", required = false) Double radiusNm
    ) {
        validateCoordinates(latitude, longitude);
        double radiusNautical = radiusNm != null ? radiusNm : searchProperties.defaultRadiusNm();
        if (radiusNautical <= 0 || Double.isNaN(radiusNautical) || Double.isInfinite(radiusNautical)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "radiusNm must be a positive finite number");
        }

        double radiusKm = NauticalMiles.toKilometers(radiusNautical);
        List<VesselDto> vessels = vesselApiClient.fetchVesselsInRadius(latitude, longitude, radiusKm);
        return new VesselsNearbyResponse(latitude, longitude, radiusNautical, vessels.size(), vessels);
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
