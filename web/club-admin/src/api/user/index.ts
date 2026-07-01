import http from '@/http'
import { ListParm, User, LoginParm } from "@/api/user/UserModel";

// 登录
export const loginApi = (parm: LoginParm) => {
    return http.post('/api/user/login', parm)
}

// 获取当前用户信息
export const getUserInfoApi = () => {
    return http.get('/api/user/info')
}

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
