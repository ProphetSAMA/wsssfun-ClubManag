<template>
  <el-main>
    <!-- 搜索栏 -->
    <el-form :model="searchParm" :inline="true" size="default">
      <el-form-item>
        <el-select
          v-model="searchParm.teamId"
          placeholder="选择社团"
          clearable
          style="width: 200px"
        >
          <el-option
            v-for="item in teamList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="search" @click="searchBtn">搜索</el-button>
        <el-button icon="delete" @click="resetBtn" plain circle type="danger" />
        <el-button icon="plus" type="primary" @click="addBtn">新增成员</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table :data="tableList" border style="width: 100%" stripe>
      <el-table-column prop="teamName" label="社团名称" />
      <el-table-column prop="userName" label="成员姓名" />
      <el-table-column prop="role" label="角色">
        <template #default="scope">
          <el-tag v-if="scope.row.role === 2" type="danger">社长</el-tag>
          <el-tag v-else-if="scope.row.role === 1" type="warning">管理员</el-tag>
          <el-tag v-else type="info">普通成员</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="joinTime" label="加入时间" />
      <el-table-column label="操作" width="220" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" @click="editBtn(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteBtn(scope.row)">移除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      @size-change="sizeChange"
      @current-change="currentChange"
      :current-page.sync="searchParm.currentPage"
      :page-sizes="[10, 20, 30, 40]"
      :page-size="searchParm.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="searchParm.total"
      background
    />

    <!-- 新增/编辑弹框 -->
    <SysDialog
      :title="dialog.title"
      :visible="dialog.visible"
      :width="dialog.width"
      :height="dialog.height"
      @onClose="onDialogClose"
      @onConfirm="onDialogConfirm"
    >
      <template v-slot:content>
        <el-form
          ref="formRef"
          :model="formModel"
          :rules="rules"
          label-width="80px"
          size="default"
        >
          <el-form-item prop="teamId" label="社团">
            <el-select v-model="formModel.teamId" placeholder="选择社团" style="width: 100%">
              <el-option
                v-for="item in teamList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item prop="userId" label="用户ID">
            <el-input v-model.number="formModel.userId" placeholder="请输入用户ID" />
          </el-form-item>
          <el-form-item prop="role" label="角色">
            <el-select v-model="formModel.role" placeholder="选择角色" style="width: 100%">
              <el-option label="普通成员" :value="0" />
              <el-option label="管理员" :value="1" />
              <el-option label="社长" :value="2" />
            </el-select>
          </el-form-item>
        </el-form>
      </template>
    </SysDialog>
  </el-main>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import SysDialog from '@/components/SysDialog.vue'
import { getMemberListApi, addMemberApi, editMemberApi, deleteMemberApi } from '@/api/member'
import { getListApi as getTeamListApi } from '@/api/team'
import type { Member, MemberParm } from '@/api/member/MemberModel'

const tableList = ref<Member[]>([])
const teamList = ref<any[]>([])
const formRef = ref<FormInstance>()
const editId = ref(0)

const searchParm = reactive<MemberParm>({
  teamId: undefined,
  pageSize: 10,
  currentPage: 1,
  total: 0
})

const dialog = reactive({
  title: '',
  visible: false,
  width: '500px',
  height: '250px'
})

const formModel = reactive({
  teamId: undefined as number | undefined,
  userId: undefined as number | undefined,
  role: 0
})

const rules = {
  teamId: [{ required: true, message: '请选择社团', trigger: 'change' }],
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

// 获取成员列表
const getList = async () => {
  const res = await getMemberListApi(searchParm)
  if (res && res.code === 200) {
    tableList.value = res.data.records
    searchParm.total = res.data.total
  }
}

// 获取社团列表（用于下拉选择）
const getTeamList = async () => {
  const res = await getTeamListApi({ currentPage: 1, pageSize: 999 })
  if (res && res.code === 200) {
    teamList.value = res.data.records
  }
}

const searchBtn = () => {
  searchParm.currentPage = 1
  getList()
}

const resetBtn = () => {
  searchParm.teamId = undefined
  searchParm.currentPage = 1
  getList()
}

const sizeChange = () => {}
const currentChange = () => {}

const addBtn = () => {
  editId.value = 0
  dialog.title = '新增成员'
  dialog.visible = true
  formModel.teamId = undefined
  formModel.userId = undefined
  formModel.role = 0
}

const editBtn = (row: Member) => {
  editId.value = row.id
  dialog.title = '编辑成员'
  dialog.visible = true
  formModel.teamId = row.teamId
  formModel.userId = row.userId
  formModel.role = row.role
}

const deleteBtn = async (row: Member) => {
  const res = await deleteMemberApi(row.id)
  if (res && res.code === 200) {
    ElMessage.success(res.msg)
    getList()
  }
}

const onDialogClose = () => {
  dialog.visible = false
}

const onDialogConfirm = () => {
  formRef.value?.validate(async (valid) => {
    if (!valid) return
    let res
    if (editId.value === 0) {
      res = await addMemberApi(formModel as any)
    } else {
      res = await editMemberApi({ id: editId.value, ...formModel } as any)
    }
    if (res && res.code === 200) {
      ElMessage.success(res.msg)
      dialog.visible = false
      getList()
    }
  })
}

onMounted(() => {
  getList()
  getTeamList()
})
</script>

<style scoped></style>
