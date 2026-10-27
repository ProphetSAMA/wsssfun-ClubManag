import http from '@/http'

// 登录
export const loginApi = (parm: { username: string, password: string }) => {
    return http.post('/api/user/login', parm)
}

// 注册（游客可调用）
export const registerApi = (parm: { username: string, password: string, nickname?: string }) => {
    return http.post('/api/user/register', parm)
}

// 获取当前用户信息
export const getUserInfoApi = () => {
    return http.get('/api/user/info')
}

// 修改自己的资料（昵称 / 手机号 / 性别）
export const updateProfileApi = (parm: { nickname: string, phone: string, sex: string }) => {
    return http.put('/api/user/profile', parm)
}

// 修改自己的密码（需要原密码）
export const changePasswordApi = (parm: { oldPassword: string, newPassword: string }) => {
    return http.put('/api/user/password', parm)
}
