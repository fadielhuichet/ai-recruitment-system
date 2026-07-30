
export interface AdminApplicationStatsResponse {
  totalApplications: number;
  pendingApplications: number;
  analyzedApplications: number;
  acceptedApplications: number;
  refusedApplications: number;
  acceptedThisMonth: number;
  refusedThisMonth: number;
  averageScore: number;
  monthlyData: { [month: string]: number };
}
