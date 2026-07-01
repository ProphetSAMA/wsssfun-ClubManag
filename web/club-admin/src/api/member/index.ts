import http from '@/http'
import { Member, MemberParm } from '@/api/member/MemberModel'

// 新增成员
export const addMemberApi = (parm: Member) => {
    return http.post('/api/member', parm)
}

// 编辑成员
export const editMemberApi = (parm: Member) => {
    return http.put('/api/member', parm)
}

// 删除成员
export const deleteMemberApi = (id: number) => {
    return http.delete(`/api/member/${id}`)
}

// 查询成员列表
export const getMemberListApi = (parm: MemberParm) => {
    return http.get('/api/member/list', parm)
}
