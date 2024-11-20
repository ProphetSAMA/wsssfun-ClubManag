// 活动的参数类型
export type NewsType = {
  id: string;
  type: string;
  createId: string;
  image: string;
  title: string;
  details: string;
  hasTop: string;
};

// 列表查询参数类型
export type PageList = {
  currentPage: number;
  pageSize: number;
  title: string;
  type: string;
  total?: number;
};
