import http from '@/http'

// 登录
export const loginApi = (parm: { username: string, password: string }) => {
    return http.post('/api/user/login', parm)
}

// 注册（游客可调用）
export const registerApi = (parm: { username: string, password: string, nickname?: string }) => {
    return http.post('/api/user/register', parm)
}

// 退出登录（把当前 token 拉黑）
export const logoutApi = () => {
    return http.post('/api/user/logout')
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

/** 上传头像文件（登录用户可用，2MB 内） */
export const uploadAvatarApi = (form: FormData) => {
    return http.post('/api/upload/avatar', form)
}

/** 保存头像路径，空字符串表示恢复默认 */
export const updateAvatarApi = (avatar: string) => {
    return http.put('/api/user/avatar', { avatar })
}

/** 查看自己的密保问题 */
export const securityQuestionApi = () => {
    return http.get('/api/user/security-question')
}

/** 设置 / 修改密保问题 */
export const updateSecurityQuestionApi = (parm: { password: string, question: string, answer: string }) => {
    return http.put('/api/user/security-question', parm)
}

/** 忘记密码：查询密保问题（未登录） */
export const forgotQueryApi = (username: string) => {
    return http.post('/api/user/forgot/query', { username })
}

/** 忘记密码：回答密保问题重置密码（未登录） */
export const forgotResetApi = (parm: { username: string, answer: string, newPassword: string }) => {
    return http.post('/api/user/forgot/reset', parm)
}
