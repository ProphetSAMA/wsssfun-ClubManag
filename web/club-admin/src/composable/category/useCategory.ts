import {ref} from "vue";

export default function useCategory() {
    // 弹框属性
    const addFormRef = ref<{ show: () => void }>();
    // 新增
    const addBtn = () => {
        addFormRef.value?.show();
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
        addFormRef
    }
}