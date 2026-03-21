/** JSON from enterpriseservice (Jackson default naming). */
export interface Plant {
  id: number;
  plantName: string;
  location?: string | null;
  latitude?: string | null;
  longitude?: string | null;
  status?: string | null;
  capacityPct?: number | null;
  totalLines?: number | null;
  linesActive?: number | null;
}

export interface Supplier {
  id: number;
  supplierName: string;
  location?: string | null;
  latitude?: string | null;
  longitude?: string | null;
  contractStatus?: string | null;
}

export interface SupplierSupplyRiskDto {
  supplierId: number | null;
  supplierName: string;
  riskScore: number;
  disturbanceCertainty: number;
  estimatedHoursToImpact: number | null;
  contributingArticleTitles: string[];
  signals: string[];
}

export interface PlantSupplyRiskDto {
  plantId: number | null;
  plantName: string;
  location?: string | null;
  plantRiskScore: number;
  disturbanceCertainty: number;
  estimatedHoursToImpact: number | null;
  rationale: string;
  suppliers: SupplierSupplyRiskDto[];
}

export interface SupplyChainRiskReportResponse {
  portfolioRiskScore: number;
  portfolioDisturbanceCertainty: number;
  portfolioRationale: string;
  portfolioDisturbanceRationale: string;
  portfolioEstimatedHoursToImpact: number | null;
  reasoningArticleCount: number;
  searchRadiusNm: number;
  plantCount: number;
  plants: PlantSupplyRiskDto[];
}
