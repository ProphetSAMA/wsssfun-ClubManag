import http from "@/http/index";
import { PageList, TeamType } from "@/api/team/TeamModel";

// 获取下拉列表
export const getSelectApi = () => {
    return http.get('api/category/getSelectList');
}

// 新增社团
export const addTeamApi = (parm: TeamType) => {
    return http.post('api/team', parm);
}

// 编辑社团
export const editTeamApi = (parm: TeamType) => {
    return http.put('api/team', parm);
}

// 删除社团
export const deleteTeamApi = (parm: TeamType) => {
    return http.delete(`/api/team/${parm.id}`)
}

// 列表查询
export const getListApi = (parm: PageList) => {
    return http.get('api/team/list', parm);
}