import http from '@/http'
import {ListParm, User} from "@/api/user/UserModel";

// 新增用户
export const addUserApi = (parm: User) => {
    return http.post('/api/user', parm)
}

// 列表查询用户
export const getListApi = (parm: ListParm) => {
    return http.get('/api/user/list', parm)
}