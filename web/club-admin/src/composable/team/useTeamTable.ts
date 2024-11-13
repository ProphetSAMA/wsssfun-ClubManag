import {nextTick, onMounted, reactive, ref} from "vue";
// @ts-ignore
import {getListApi} from "@/api/team";

export default function useTeamTable() {

    // 表格高度
    const tableHeight = ref(0)

    // 表格数据
    const tableList = ref([])

    // 列表参数
    const searchParm = reactive({
        currentPage: 1,
        pageSize: 10,
        name: '',
        total: 0,
    })

    // 列表查询
    const getList = async () => {
        let res = await getListApi(searchParm);
        if (res && res.code == 200) {
            tableList.value = res.data.records;
            searchParm.total = res.data.total;
        }
    }
    // 搜索
    const searchBtn = () => {
        getList();
    }
    // 重置
    const resetBtn = () => {
        searchParm.name = ''
        getList();
    }

    // 页容量改变时触发
    const sizeChange = (size: number) => {
        searchParm.pageSize = size;
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
        sizeChange,
        currentChange,
        tableHeight
    }

}