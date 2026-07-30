// models/Dto/PagedResponse.ts
export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;      // current page (0-based)
  size: number;
  first: boolean;
  last: boolean;
}
