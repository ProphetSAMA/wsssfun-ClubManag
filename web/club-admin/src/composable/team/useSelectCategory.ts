import {ref} from "vue";
import {getSelectApi} from "@/api/team";

export default function useSelectCategory() {
    // 接收下拉列表数据
    const selectData = ref([]);

    // 获取下拉列表
    const getSelect = async () => {
        let res = await getSelectApi();
        if (res && res.code === 200 && res.data) {
            console.log(res);
            selectData.value = res.data;
        }
    }

    return {
        selectData,
        getSelect
    }
}