// 新增社团数据类型
export type TeamType = {
    id: string,
    categoryId: string,
    name: string,
    image: string,
    details: string
}

// 列表查询参数类型
export type PageList = {
    currentPage: number,
    pageSize: number,
    name: string
}