// @ts-ignore
import http from "@/http/index";
// @ts-ignore
import {TeamType} from "@/api/team/TeamModel";

// 获取下拉列表
export const getSelectApi = () => {
    return http.get('api/category/getSelectList');
}

// 新增社团
export const addTeamApi = (parm: TeamType) => {
    return http.post('api/team', parm);
}