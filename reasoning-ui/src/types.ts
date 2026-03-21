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

export interface ErrorBody {
  message: string;
}
