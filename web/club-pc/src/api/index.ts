import http from '@/http'

// 登录
export const loginApi = (parm: { username: string, password: string }) => {
    return http.post('/api/user/login', parm)
}

// 获取当前用户信息
export const getUserInfoApi = () => {
    return http.get('/api/user/info')
}
