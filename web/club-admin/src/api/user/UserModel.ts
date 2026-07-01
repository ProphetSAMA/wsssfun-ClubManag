// 登录参数
export type LoginParm = {
    username: string,
    password: string
}

// 登录返回数据
export type LoginResult = {
    token: string,
    userId: number,
    username: string,
    nickname: string,
    isAdmin: number
}

// 用户的数据类型
export type User = {
    userId: number,
    username: string,
    password: string,
    nickname: string,
    phone: string,
    sex: number,
    status: number,
    isAdmin: number,
}

// 表格查询参数
export type ListParm = {
    currentPage: number,
    pageSize: number,
    nickName: string,
    total?: number
}
