import {reactive} from "vue";

export default function useTableCategory() {
    // 搜搜栏绑定数据
    const searchParm = reactive({
        currentPage: 1,
        pageSize: 10,
        name: '',
        total: 0
    })

    // 列表
    const getList = () => {

    }

    return {
        searchParm,
        getList
    }
}