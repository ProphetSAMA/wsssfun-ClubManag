import {ref} from "vue";
import {User} from "@/api/user/UserModel";

export  default function useUser() {
    // 弹框的ref属性
    const addUserRef = ref<{ show: () => void }>()

    // 新增
    const addBtn = () => {
        addUserRef.value?.show()
    }
    // 编辑
    const editBtn = (row:User) => {

    }
    // 删除
    const deleteBtn = (row:User) => {

    }

    return {
        addBtn,
        editBtn,
        deleteBtn,
        addUserRef
    }
}