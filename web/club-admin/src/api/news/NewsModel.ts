// 活动的参数类型
export type NewsType = {
  id: string;
  type: string;
  createId: string;
  image: string;
  title: string;
  details: string;
  hasTop: string;
  /** 0-草稿 1-已发布 */
  status?: number;
  /** 浏览量 */
  viewCount?: number;
};

// 列表查询参数类型
export type PageList = {
  currentPage: number;
  pageSize: number;
  title: string;
  type: string;
  /** 仅管理员可用：0-草稿 1-已发布 */
  status?: number;
  total?: number;
};
