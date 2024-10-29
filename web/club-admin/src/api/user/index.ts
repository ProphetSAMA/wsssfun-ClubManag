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

// 编辑用户
export const editUserApi = (parm: User) => {
    return http.put('/api/user', parm)
}

// 删除用户
export const deleteUserApi = (parm: User) => {
    return http.delete(`/api/user/${parm.userId}`)
}