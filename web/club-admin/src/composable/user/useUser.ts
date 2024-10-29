import {ref} from "vue";
import {User} from "@/api/user/UserModel";
import {EditType, FuncList} from "@/type/BaseType";
import useInstance from "@/hooks/useInstance";
import {deleteUserApi} from "@/api/user";
import {ElMessage} from "element-plus";

export default function useUser(getList: FuncList) {
    // 获取全局属性
    const {global} = useInstance();

    // 弹框的ref属性
    const addUserRef = ref<{ show: (type: string, row?: User) => void }>()

    // 新增
    const addBtn = () => {
        addUserRef.value?.show(EditType.ADD);
    }
    // 编辑
    const editBtn = (row: User) => {
        // 显示弹框
        addUserRef.value?.show(EditType.EDIT, row);
    }
    // 删除
    const deleteBtn = async (row: User) => {
        // 提示
        const confirm = await global.$myConfirm('阁下请三思！<br/> 确定要删除吗？');
        if (confirm) {
            // 删除
            let res = await deleteUserApi(row);
            if (res && res.code == 200) {
                ElMessage.success(res.msg);
                // 刷新列表
                getList();
                console.log(res);
            }
        }
    }

    return {
        addBtn,
        editBtn,
        deleteBtn,
        addUserRef
    }
}