import http from "@/http";

export type NotificationItem = {
  id: number;
  type: string;
  title: string;
  content: string;
  link: string;
  isRead: number;
  createTime: string;
};

/** 我的通知 */
export const myNotificationsApi = (currentPage = 1, pageSize = 10) => {
  return http.get("/api/notification/my", { currentPage, pageSize });
};

/** 未读数量 */
export const unreadCountApi = () => {
  return http.get("/api/notification/unread");
};

/** 标记已读 */
export const readNotificationApi = (id: number) => {
  return http.put(`/api/notification/${id}/read`);
};

/** 全部已读 */
export const readAllNotificationApi = () => {
  return http.put("/api/notification/read-all");
};
