import {ref} from "vue";

export  default function useUser() {
    // 弹框的ref属性
    const addUserRef = ref<{ show: () => void }>()

    // 新增
    const addBtn = () => {
        addUserRef.value?.show()
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
        addUserRef
    }
}