import http from "@/http";

/** 报名活动 */
export const signupApi = (newsId: number) => {
  return http.post("/api/signup", { newsId });
};

/** 取消报名 */
export const cancelSignupApi = (newsId: number) => {
  return http.delete(`/api/signup/${newsId}`);
};

/** 我报名的活动 */
export const mySignupsApi = (currentPage = 1, pageSize = 50) => {
  return http.get("/api/signup/my", { currentPage, pageSize });
};

/** 活动报名人数（游客可读） */
export const signupCountApi = (newsId: number) => {
  return http.get("/api/signup/count", { newsId });
};

/** 内容详情：顺带把浏览量 +1，草稿仅管理员可见 */
export const newsDetailApi = (id: number) => {
  return http.get(`/api/news/detail/${id}`);
};
