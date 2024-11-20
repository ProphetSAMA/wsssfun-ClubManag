import http from "@/http";
import { NewsType, PageList } from "./NewsModel";

// 新增
export const addNewsApi = (parm: NewsType) => {
  return http.post("/api/news", parm);
};

// 编辑
export const editNewsApi = (parm: NewsType) => {
    return http.put("/api/news", parm);
};

// 删除
export const deleteNewsApi = (id: string) => {
  return http.delete(`/api/news/${id}`);
};

// 列表
export const getNewsListApi = (parm: PageList) => {
  return http.get("/api/news/list", parm);
};
