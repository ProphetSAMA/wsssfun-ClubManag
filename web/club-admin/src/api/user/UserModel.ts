// 用户的数据类型
export type User = {
    userId: number,
    username: string,
    password: string,
    nickname: string,
    phone: string,
    sex: number,
    status: number
    isadmin: number,
}

// 表格查询参数
export type ListParm = {
    currentPage: number,
    pageSize: number,
    nickname: string,
    total?: number
}