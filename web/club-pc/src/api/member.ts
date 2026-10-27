import http from "@/http";

/** 成员记录（后端 MemberVo，列表查询会带上社团名和用户姓名） */
export type MemberItem = {
  id: number;
  teamId: number;
  teamName?: string;
  userId: number;
  userName?: string;
  role: number;
  status: number;
  joinTime: string;
};

/** 加入社团 */
export const joinTeamApi = (parm: { teamId: number; userId: number }) => {
  return http.post("/api/member", parm);
};

/** 退出社团（后端逻辑删除，status 置 0） */
export const quitTeamApi = (id: number) => {
  return http.delete(`/api/member/${id}`);
};

/** 我的社团 */
export const myTeamsApi = (userId: number) => {
  return http.get("/api/member/list", { currentPage: 1, pageSize: 50, userId });
};
