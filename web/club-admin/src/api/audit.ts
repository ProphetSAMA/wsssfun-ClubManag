import http from '@/http'

export type AuditListParm = {
  keyword?: string
  method?: string
  currentPage: number
  pageSize: number
}

/** 操作日志列表 */
export const getAuditListApi = (parm: AuditListParm) => {
  return http.get('/api/audit/list', parm)
}
