import http from "@/http";

export type ClubTeam = {
  teamId: number;
  teamName: string;
  /** 2-社长 1-社团管理员 */
  role: number;
};

export type ClubMember = {
  id: number;
  userId: number;
  userName: string;
  phone: string;
  role: number;
  joinTime: string;
};

export type ClubNews = {
  id: number;
  title: string;
  details: string;
  image: string;
  status: number;
  hasTop: string;
  viewCount: number;
  createTime: string;
};

/** 我管理的社团 */
export const myTeamsApi = () => http.get("/api/club/my");

/** 社团成员 */
export const clubMembersApi = (teamId: number) => http.get(`/api/club/${teamId}/members`);

/** 设置成员角色 */
export const setMemberRoleApi = (teamId: number, memberId: number, role: number) =>
  http.put(`/api/club/${teamId}/member/${memberId}`, { role });

/** 移出成员 */
export const removeMemberApi = (teamId: number, memberId: number) =>
  http.delete(`/api/club/${teamId}/member/${memberId}`);

/** 本社团活动 */
export const clubNewsApi = (teamId: number, currentPage = 1, pageSize = 10) =>
  http.get(`/api/club/${teamId}/news`, { currentPage, pageSize });

/** 发布活动 */
export const addClubNewsApi = (teamId: number, parm: Partial<ClubNews>) =>
  http.post(`/api/club/${teamId}/news`, parm);

/** 编辑活动 */
export const editClubNewsApi = (teamId: number, id: number, parm: Partial<ClubNews>) =>
  http.put(`/api/club/${teamId}/news/${id}`, parm);

/** 删除活动 */
export const deleteClubNewsApi = (teamId: number, id: number) =>
  http.delete(`/api/club/${teamId}/news/${id}`);

/** 活动报名名单 */
export const clubSignupsApi = (teamId: number, newsId: number) =>
  http.get(`/api/club/${teamId}/news/${newsId}/signups`);
