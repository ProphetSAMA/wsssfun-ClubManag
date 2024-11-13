import {ref} from "vue";
import {TeamType} from "@/api/team/TeamModel";

export default function useTeam() {
    // 新增弹框的ref属性
    const addTeamRef = ref<{ show: () => void }>();
    // 新增
    const addBtn = () => {
        addTeamRef.value?.show();
    }

    // 编辑
    const editBtn = (row: TeamType) => {

    }

    // 删除
    const deleteBtn = (row: TeamType) => {

    }
    return {
        addBtn,
        editBtn,
        deleteBtn,
        addTeamRef
    }
}