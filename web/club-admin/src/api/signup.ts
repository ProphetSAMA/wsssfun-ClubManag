import http from '@/http'

/** 某个活动的报名名单（管理员） */
export const getSignupListApi = (newsId: number, currentPage = 1, pageSize = 50) => {
  return http.get('/api/signup/list', { newsId, currentPage, pageSize })
}
