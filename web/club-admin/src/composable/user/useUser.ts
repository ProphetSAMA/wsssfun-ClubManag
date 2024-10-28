import {ref} from "vue";
import {User} from "@/api/user/UserModel";
import {EditType} from "@/type/BaseType";

export  default function useUser() {
    // 弹框的ref属性
    const addUserRef = ref<{ show: (type: string, row?: User) => void }>()

    // 新增
    const addBtn = () => {
        addUserRef.value?.show(EditType.ADD);
    }
    // 编辑
    const editBtn = (row:User) => {
    // 显示弹框
        addUserRef.value?.show(EditType.EDIT, row);
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