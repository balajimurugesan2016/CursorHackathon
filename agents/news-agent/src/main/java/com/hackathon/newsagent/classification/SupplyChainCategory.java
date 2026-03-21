package com.hackathon.newsagent.classification;

/**
 * Supply-chain risk themes aligned with analyst-style news buckets.
 */
public enum SupplyChainCategory {

    GEOPOLITICAL_UNREST_SECURITY(
            "Geopolitical Unrest & Security",
            "Conflicts, regional tensions, sanctions, and maritime or border security that affect shipping lanes, "
                    + "insurance, and freight risk (e.g., Red Sea, Gulf, Iran–Qatar dynamics)."
    ),
    TRADE_POLICY_TARIFFS(
            "Trade Policy and Tariffs",
            "Tariffs, trade policy shifts, customs, and compliance burdens that reconfigure sourcing and raise landed costs."
    ),
    ENVIRONMENTAL_NATURAL_DISASTERS(
            "Environmental & Natural Disasters",
            "Earthquakes, hurricanes, floods, and severe weather that halt production or disrupt logistics hubs."
    ),
    INFRASTRUCTURE_LOGISTICS_DISRUPTIONS(
            "Infrastructure & Logistics Disruptions",
            "Port congestion, inland bottlenecks, container or trucking capacity issues, strikes, and transport network failures."
    ),
    RAW_MATERIAL_RESOURCE_SCARCITY(
            "Raw Material & Resource Scarcity",
            "Shortages of critical inputs (e.g., semiconductors, specialty gases, metals) that constrain manufacturing."
    ),
    TECHNOLOGY_CYBERSECURITY(
            "Technology & Cybersecurity",
            "Ransomware, cyber incidents, and critical IT/OT failures that stop planning, production, or distribution systems."
    ),
    CORPORATE_RESTRUCTURING(
            "Corporate Restructuring",
            "Layoffs, facility closures, and footprint changes by major operators that reshape regional supply networks."
    );

    private final String displayName;
    private final String description;

    SupplyChainCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String displayName() {
        return displayName;
    }

    public String description() {
        return description;
    }
}
