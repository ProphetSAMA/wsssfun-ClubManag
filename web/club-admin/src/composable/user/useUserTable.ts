import {onMounted, reactive, ref} from "vue";
import {getListApi} from "@/api/user";
import {ListParm} from "@/api/user/UserModel";

export default function useUserTable() {
    // 表格数据
    const tableList = ref([]);

    // 搜索表单绑定数据
    const searchParm = reactive(<ListParm>{
        nickName: '',
        pageSize: 10,
        currentPage: 1,
        total: 0
    })
    // 获取列表
    const getList = async () => {
        let res = await getListApi(searchParm);
        if (res && res.code == 200) {
            // 将后端返回数据设置到表格数据
            tableList.value = res.data.record;
            // 设置总条数
            searchParm.total = res.data.total;
        }
    }

    // 搜索
    const searchBtn = () => {

    }
    // 重置
    const resetBtn = () => {
        searchParm.nickName = ''
    }

    onMounted(() => {
        getList();
    })
    return {
        searchParm,
        getList,
        searchBtn,
        resetBtn,
        tableList
    }
}