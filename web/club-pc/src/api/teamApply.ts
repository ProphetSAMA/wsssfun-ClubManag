import http from "@/http";

export type TeamApplyForm = {
  name: string;
  categoryId?: number;
  reason: string;
};

/** 提交成立社团申请 */
export const applyTeamApi = (parm: TeamApplyForm) => {
  return http.post("/api/team-apply", parm);
};

/** 我的申请记录 */
export const myTeamApplyApi = () => {
  return http.get("/api/team-apply/my");
};
