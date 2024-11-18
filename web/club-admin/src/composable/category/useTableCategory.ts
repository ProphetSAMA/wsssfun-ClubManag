import { nextTick, onMounted, reactive, ref } from "vue";
import { getCategoryApi } from "@/api/category";

export default function useTableCategory() {
    // 表格数据
    const tableList = ref([]);
    // 表格高度
    const tableHeight = ref(0);
    // 搜搜栏绑定数据
    const searchParm = reactive({
        currentPage: 1,
        pageSize: 10,
        name: '',
        total: 0
    })

    // 列表
    const getList = async () => {
        let res = await getCategoryApi(searchParm);
        if (res && res.code == 200) {
            // 将返回值设置到表格数据
            tableList.value = res.data.records;
            searchParm.total = res.data.total;
        }
    }

    // 页容量改变时触发
    const sizeChange = async (size: number) => {
        searchParm.pageSize = size;
        await getList();
    }

    // 页数改变时触发
    const currentChange = async (page: number) => {
        searchParm.currentPage = page;
        await getList();
    }

    // 搜索
    const searchBtn = async () => {
        searchParm.currentPage = 1;
        await getList();
    }

    // 重置
    const resetBtn = async () => {
        searchParm.currentPage = 1;
        searchParm.pageSize = 10;
        searchParm.name = '';
        await getList();
    }

    onMounted(async () => {
        await getList();
        await nextTick(() => {
            tableHeight.value = window.innerHeight - 220;
        })
    })

    return {
        searchParm,
        getList,
        tableList,
        currentChange,
        sizeChange,
        tableHeight,
        searchBtn,
        resetBtn
    }
}