// @ts-ignore
import http from "@/http/index";

// 获取下拉列表
export const getSelectApi = () => {
    return http.get('api/category/getSelectList');
}