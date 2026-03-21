package com.hackathon.vesselagent.util;

/**
 * International nautical mile (NM): exactly <strong>1852 m</strong> (1.852 km) per SI/BIPM definition.
 * The mock vessel API uses kilometers for Haversine distance; callers of this agent use NM at the HTTP boundary.
 */
public final class NauticalMiles {

    /** 1 international NM in kilometers (exact). */
    public static final double NM_TO_KM = 1.852;

    private NauticalMiles() {
    }

    public static double toKilometers(double nauticalMiles) {
        return nauticalMiles * NM_TO_KM;
    }
}
