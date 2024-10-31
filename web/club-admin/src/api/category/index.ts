import http from '@/http'
import {CategoryModel, CategoryPage} from "@/api/category/CategoryModel";

// 新增
export const addCategoryApi = (parm: CategoryModel) => {
    return http.post("/api/category", parm)
}

// 列表
 export const getCategoryApi = (parm: CategoryPage) => {
    return http.get("/api/category/list", parm)
 }