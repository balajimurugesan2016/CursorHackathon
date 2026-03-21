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

export interface ArticleReasoningDto {
  classified: ClassifiedArticleDto;
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
