import { reactive } from "vue";

/**
 * 新闻活动
 */
export default function useActivityTable() {
    // 搜索
    const searchParm = reactive({
        currentPage: 1,
        pageSize: 10,
        title: '',
        total: 0,
    })
    // 列表
    const getList = () => {
    }
    // 搜索
    const searchBtn = () => {

    }
    // 重置
    const resetBtn = () => {

    }

    return {
        searchParm,
        getList,
        searchBtn,
        resetBtn,
    }
}