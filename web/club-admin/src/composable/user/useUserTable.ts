import {reactive} from "vue";

export default function useUserTable() {
    // 搜索表单绑定数据
    const searchParm = reactive({
        nickName: '',
    })
    // 获取列表
    const getList = () => {

    }

    // 搜索
    const searchBtn = () => {

    }
    // 重置
    const resetBtn = () => {
        searchParm.nickName = ''
    }

    return {
        searchParm,
        getList,
        searchBtn,
        resetBtn
    }
}