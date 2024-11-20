import { NewsType } from "@/api/news/NewsModel";
import { ref } from "vue";

/**
 * 新闻活动
 */
export default function useActivity() {
    // 新增弹框
    const addActivityRef = ref<{show: () => void}>();
    // 新增
    const addBtn = () => {
        addActivityRef.value?.show();
    }
    // 编辑
    const editBtn = (row: NewsType) => {
    }
    // 删除
    const deleteBtn = (row: NewsType) => {
    }

    return {
        addBtn,
        editBtn,
        deleteBtn,
        addActivityRef,
    }
}