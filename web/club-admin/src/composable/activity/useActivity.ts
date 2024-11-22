import { ref } from "vue";
import { NewsType } from "@/api/news/NewsModel";
import { EditType } from "@/type/BaseType";
import useInstance from "@/hooks/useInstance";
import { deleteNewsApi } from "@/api/news";
import { ElMessage } from "element-plus";

/**
 * 新闻活动
 */
export default function useActivity(getList: () => void) {
    const { global } = useInstance();
    
    // 明确定义 ref 类型
    const addActivityRef = ref<{ show: (type: EditType, row?: NewsType) => void }>();
    
    // 新增
    const addBtn = async () => {
        try {
            console.log('开始添加操作');
            await addActivityRef.value?.show(EditType.ADD);
            console.log('添加操作完成');
        } catch (error: unknown) {
            console.error('添加操作出错:', error);
            ElMessage.error('添加失败：' + (error instanceof Error ? error.message : '未知错误'));
        }
    }
    
    // 编辑
    const editBtn = (row: NewsType) => {
        addActivityRef.value?.show(EditType.EDIT, row);
    }
    
    // 删除
    const deleteBtn = async (row: NewsType) => {
        const confirm = await global.$myConfirm('确定要删除吗？');
        if (confirm) {
            let res = await deleteNewsApi(row);
            if (res && res.code == 200) {
                ElMessage.success(res.msg);
                getList();
            }
        }
    }
    
    return {
        addBtn,
        editBtn,
        deleteBtn,
        addActivityRef
    }
}