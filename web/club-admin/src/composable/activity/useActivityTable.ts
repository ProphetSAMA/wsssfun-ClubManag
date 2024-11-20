import { reactive, onMounted, ref, nextTick } from "vue";
import { getNewsListApi } from "@/api/news";

/**
 * 新闻活动
 */
export default function useActivityTable() {
    const options = ref([
        { value: '0', label: '活动' },
        { value: '1', label: '新闻' },
        { value: '2', label: '公告' },
    ])

    // 搜索
    const searchParm = reactive({
        currentPage: 1,
        pageSize: 10,
        title: '',
        type: '',
        total: 0,
    })

    // 表格数据
    const tableList = ref([]);
    // 列表
    const getList = async () => {
        let res = await getNewsListApi(searchParm);
        if (res && res.code == 200) {
            console.log(res.data);
            tableList.value = res.data.records;
            searchParm.total = res.data.total;
        }
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
        searchParm.title = '';
        searchParm.type = '';
        await getList();
    }

    // 页数改变时触发
    const sizeChange = async (size: number) => {
        searchParm.pageSize = size;
        await getList();
    }

    // 当前页改变时触发
    const currentChange = async (page: number) => {
        searchParm.currentPage = page;
        await getList();
    }
    
    // 表格高度
    const tableHeight = ref(0);

    onMounted(async () => {
        await getList();
        nextTick(() => {
            tableHeight.value = window.innerHeight - 200;
        })
    })

    return {
        searchParm,
        getList,
        searchBtn,
        resetBtn,
        options,
        tableList,
        sizeChange,
        currentChange,
        tableHeight,
    }
}