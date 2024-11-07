import {ref} from "vue";

export default function useTeam() {
    // 新增弹框的ref属性
    const addTeamRef = ref<{ show: () => void }>();
    // 新增
    const addBtn = () => {
        addTeamRef.value?.show();
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
        addTeamRef
    }
}