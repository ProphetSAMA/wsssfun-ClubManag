import { onMounted, reactive, ref } from "vue";
import { getListApi } from "@/api/user";
import { ListParm } from "@/api/user/UserModel";

export default function useUserTable() {
    // 表格数据
    const tableList = ref([]);
    // 加载状态
    const loading = ref(false);

    // 搜索表单绑定数据
    const searchParm = reactive(<ListParm>{
        nickName: '',
        pageSize: 10,
        currentPage: 1,
        total: 0
    });

    // 获取列表
    const getList = async () => {
        loading.value = true;
        try {
            let res = await getListApi(searchParm);
            if (res && res.code == 200) {
                // 将后端返回数据设置到表格数据
                tableList.value = res.data.records;
                // 设置总条数
                searchParm.total = res.data.total;
            }
        } finally {
            loading.value = false;
        }
    };

    // 搜索
    const searchBtn = () => {
        searchParm.currentPage = 1; // 搜索时重置到第一页
        getList();
    };

    // 重置
    const resetBtn = () => {
        searchParm.nickName = '';
        searchParm.currentPage = 1; // 重置时重置到第一页
        getList();
    };

    // 页容量改变时触发：改容量后回到第一页，避免停在越界页码
    const sizeChange = (size: number) => {
        searchParm.pageSize = size;
        searchParm.currentPage = 1;
        getList();
    }

    // 页数改变时触发
    const currentChange = (page: number) => {
        searchParm.currentPage = page;
        getList();
    }

    onMounted(() => {
        getList();
    });

    return {
        searchParm,
        getList,
        searchBtn,
        resetBtn,
        tableList,
        loading,
        sizeChange,
        currentChange
    };
}