import { nextTick, onMounted, reactive, ref } from "vue";
// @ts-ignore
import { getListApi } from "@/api/team";

export default function useTeamTable() {

    // 表格高度
    const tableHeight = ref(0)

    // 表格数据
    const tableList = ref([])

    // 加载状态
    const loading = ref(false)

    // 列表参数
    const searchParm = reactive({
        currentPage: 1,
        pageSize: 10,
        name: '',
        total: 0,
    })

    // 列表查询
    const getList = async () => {
        loading.value = true
        try {
            let res = await getListApi(searchParm);
            if (res && res.code == 200) {
                tableList.value = res.data.records;
                searchParm.total = res.data.total;
            }
        } finally {
            loading.value = false
        }
    }
    // 搜索：条件变化后必须回到第一页，否则会停在可能不存在的页码上
    const searchBtn = () => {
        searchParm.currentPage = 1;
        getList();
    }
    // 重置
    const resetBtn = () => {
        searchParm.name = ''
        searchParm.currentPage = 1;
        getList();
    }

    // 页容量改变时触发
    const sizeChange = (size: number) => {
        searchParm.pageSize = size;
        searchParm.currentPage = 1;
        getList();
    }
    const currentChange = (page: number) => {
        searchParm.currentPage = page;
        getList();
    }

    onMounted(() => {
        getList();
        nextTick(() => {
            tableHeight.value = window.innerHeight - 200
        })

    })

    return {
        searchParm,
        getList,
        searchBtn,
        resetBtn,
        tableList,
        loading,
        sizeChange,
        currentChange,
        tableHeight
    }

}