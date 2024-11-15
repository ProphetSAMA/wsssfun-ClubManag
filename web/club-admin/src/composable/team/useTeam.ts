import {ref} from "vue";
import {TeamType} from "@/api/team/TeamModel";
import { EditType } from "@/type/BaseType";

export default function useTeam() {
    // 新增弹框的ref属性
    const addTeamRef = ref<{ show: (type: string, row?: TeamType) => void }>();
    // 新增
    const addBtn = () => {
        addTeamRef.value?.show(EditType.ADD);
    }

    // 编辑
    const editBtn = (row: TeamType) => {
        addTeamRef.value?.show(EditType.EDIT, row)
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