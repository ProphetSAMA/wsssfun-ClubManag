import http from '@/http'
import {CategoryModel} from "@/api/category/CategoryModel";

export const addCategoryApi = (parm: CategoryModel) => {
    return http.post("/api/category", parm)
}