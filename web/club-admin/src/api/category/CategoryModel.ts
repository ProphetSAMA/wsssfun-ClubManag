// 定义分类数据类型
export type CategoryModel = {
    id: string;
    name: string;
    orderNum: number;
}

// 定义分页查询数据类型
export type CategoryPage = {
    currentPage: number;
    pageSize: number;
    name: string;
    total: number;
}