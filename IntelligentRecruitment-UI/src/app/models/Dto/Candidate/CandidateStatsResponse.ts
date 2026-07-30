export interface CandidateStatsResponse {
  total: number;
  active: number;
  suspended: number;
  thisMonth: number;
  monthlyData: Record<string, number>;
}

