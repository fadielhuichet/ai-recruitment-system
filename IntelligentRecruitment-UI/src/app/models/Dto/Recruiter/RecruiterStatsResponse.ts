export interface RecruiterStatsResponse{
  total:number;
  active:number;
  suspended:number;
  thisMonth:number;
  monthlyData: { [month: string]: number };
}
