export interface CategoryAssignmentDto {
  categoryId: string;
  categoryLabel: string;
  categoryDescription: string;
  score: number;
  matchedSignals: string[];
}

export interface ShippingRouteImpactDto {
  probability: number;
  matchedSignals: string[];
}

export interface ClassifiedArticleDto {
  uri: string;
  title: string;
  body: string;
  url: string;
  date: string;
  dateTime: string;
  categories: CategoryAssignmentDto[];
  /** Top themes + excerpt from news-agent (optional for older responses). */
  summary?: string | null;
  shippingRouteImpact: ShippingRouteImpactDto | null;
}

export interface ResolvedLocationDto {
  query: string;
  matchedName: string;
  placeType: string;
  latitude: number;
  longitude: number;
  matchKind: string;
  confidence: number;
}

export interface VesselDto {
  mmsi: string;
  name: string;
  latitude: string;
  longitude: string;
  speed: string;
  course: string;
  heading: string;
}

export interface VesselNearLocationDto {
  anchorMatchedName: string;
  latitude: number;
  longitude: number;
  /** Search radius in nautical miles (international NM). */
  radiusNm: number;
  vesselCount: number;
  vessels: VesselDto[];
}

/** Per-category composite risk from reasoning-agent (news score + pipeline context). */
export interface CategoryRiskFactorDto {
  categoryId: string;
  categoryLabel: string;
  newsCategoryScore: number;
  riskFactor: number;
  rationale: string;
}

export interface ArticleReasoningDto {
  classified: ClassifiedArticleDto;
  categoryRisks: CategoryRiskFactorDto[];
  catalogMentions: string[];
  resolvedLocations: ResolvedLocationDto[];
  vesselsNearLocations: VesselNearLocationDto[];
}

export interface ReasoningReportResponse {
  articleCount: number;
  articles: ArticleReasoningDto[];
  /** Radius in nautical miles used for vessel-agent searches on this run. */
  searchRadiusNm: number;
}

/** supply-chain-risk-agent (port 8094) — enterprise + reasoning, disturbance certainty from vessel ETA. */
export interface SupplierSupplyRiskDto {
  supplierId: number;
  supplierName: string;
  riskScore: number;
  disturbanceCertainty: number;
  estimatedHoursToImpact: number | null;
  contributingArticleTitles: string[];
  signals: string[];
}

export interface PlantSupplyRiskDto {
  plantId: number;
  plantName: string;
  location: string | null;
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

export interface ErrorBody {
  message: string;
}
