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
    const editBtn = () => {
    }
    // 删除
    const deleteBtn = () => {
    }

    return {
        addBtn,
        editBtn,
        deleteBtn,
        addActivityRef,
    }
}