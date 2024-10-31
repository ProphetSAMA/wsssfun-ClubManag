import {ref} from "vue";
import {CategoryModel} from "@/api/category/CategoryModel";

export default function useCategory() {
    // 弹框属性
    const addFormRef = ref<{ show: () => void }>();
    // 新增
    const addBtn = () => {
        addFormRef.value?.show();
    }

    // 编辑
    const editBtn = (row: CategoryModel) => {

    }

    // 删除
    const deleteBtn = (row: CategoryModel) => {

    }

    return {
        addBtn,
        editBtn,
        deleteBtn,
        addFormRef
    }
}