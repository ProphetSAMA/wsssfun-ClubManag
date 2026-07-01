// 成员数据类型
export type Member = {
    id: number,
    teamId: number,
    userId: number,
    role: number,
    status: number,
    joinTime: string,
    // 关联显示字段
    teamName?: string,
    userName?: string,
}

// 成员查询参数
export type MemberParm = {
    currentPage: number,
    pageSize: number,
    teamId?: number,
    userId?: number,
    userName?: string,
    total?: number
}
