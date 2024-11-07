import {reactive} from "vue";

export default function useTeamTable() {

    // 列表参数
    const searchParm = reactive({
        currentPage: 1,
        pageSize: 10,
        name: '',
        total: 0,
    })

    // 列表查询
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
        resetBtn
    }

}