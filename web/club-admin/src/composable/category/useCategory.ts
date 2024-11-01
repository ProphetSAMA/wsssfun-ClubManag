import {ref} from "vue";
import {CategoryModel} from "@/api/category/CategoryModel";
import {EditType, FuncList} from "@/type/BaseType";
import useInstance from "@/hooks/useInstance";
import {deleteCategoryApi} from "@/api/category";
import {ElMessage} from "element-plus";

export default function useCategory(getList: FuncList) {
    const {global} = useInstance()

    // 弹框属性
    const addFormRef = ref<{ show: (type: string, row?:CategoryModel) => void }>();
    // 新增
    const addBtn = () => {
        addFormRef.value?.show(EditType.ADD);
    }

    // 编辑
    const editBtn = (row: CategoryModel) => {
        addFormRef.value?.show(EditType.EDIT, row);
    }

    // 删除
    const deleteBtn = async (row: CategoryModel) => {
        const confirm = await global.$myConfirm('阁下请三思！<br/> 确定要删除吗？');
        if (confirm) {
            let res = await deleteCategoryApi(row);
            if (res && res.code == 200) {
                ElMessage.success('删除成功');
                // 刷新列表
                getList();
            }
        }
    }

    return {
        addBtn,
        editBtn,
        deleteBtn,
        addFormRef
    }
}