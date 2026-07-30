
export interface RecruiterApplicationStatsResponse {
  totalApplications: number;
  pendingReview: number;
  acceptedThisMonth: number;
  refusedThisMonth: number;
  applicationsThisWeek: number;
  applicationsPreviousWeek: number;
  averageScore: number;
  weeklyData: { [day: string]: number };
}
