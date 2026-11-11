import http from '@/http'

export type ApplyListParm = {
  status?: number
  keyword?: string
  currentPage: number
  pageSize: number
}

/** 申请列表 */
export const getApplyListApi = (parm: ApplyListParm) => {
  return http.get('/api/team-apply/list', parm)
}

/** 通过申请 */
export const approveApplyApi = (id: number) => {
  return http.put(`/api/team-apply/${id}/approve`)
}

/** 驳回申请 */
export const rejectApplyApi = (id: number, reason: string) => {
  return http.put(`/api/team-apply/${id}/reject`, { reason })
}
