<template>
  <div class="card" v-loading="loading">
    <el-alert v-if="error" :title="error" type="error" :closable="false" show-icon class="err" />

    <template v-if="teams.length">
      <div class="head">
        <el-select v-model="currentTeamId" style="width: 220px" @change="onTeamChange">
          <el-option v-for="t in teams" :key="t.teamId" :label="t.teamName" :value="t.teamId" />
        </el-select>
        <el-tag :type="currentRole === 2 ? 'danger' : 'warning'" size="small">
          {{ currentRole === 2 ? '社长' : '社团管理员' }}
        </el-tag>
      </div>

      <el-tabs v-model="activeTab" class="tabs">
        <!-- 成员 -->
        <el-tab-pane label="成员管理" name="members">
          <el-table :data="members" border stripe size="default">
            <el-table-column prop="userName" label="姓名" />
            <el-table-column prop="phone" label="手机号" width="150" />
            <el-table-column label="角色" width="130">
              <template #default="scope">
                <el-tag v-if="scope.row.role === 2" type="danger" size="small">社长</el-tag>
                <el-tag v-else-if="scope.row.role === 1" type="warning" size="small">社团管理员</el-tag>
                <el-tag v-else type="info" size="small">普通成员</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="joinTime" label="加入时间" width="140" />
            <el-table-column label="操作" width="240" align="center">
              <template #default="scope">
                <el-button
                  v-if="scope.row.role !== 2"
                  size="small"
                  :disabled="scope.row.userId === myUserId"
                  @click="toggleRole(scope.row)"
                >{{ scope.row.role === 1 ? '设为成员' : '设为管理员' }}</el-button>
                <el-button
                  v-if="scope.row.role !== 2"
                  size="small"
                  type="danger"
                  plain
                  :disabled="scope.row.userId === myUserId"
                  @click="remove(scope.row)"
                >移出</el-button>
                <span v-if="scope.row.role === 2" class="tip">社长</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 社团资料 -->
        <el-tab-pane label="社团资料" name="profile">
          <el-form label-width="80px" style="max-width: 560px">
            <el-form-item label="封面">
              <div class="cover-box">
                <el-image v-if="profileForm.image" class="cover" :src="imgUrl(profileForm.image)" fit="cover" />
                <div v-else class="cover cover-empty">暂无封面</div>
                <div class="cover-actions">
                  <el-button size="small" :loading="uploadingCover" @click="pickCover">上传封面</el-button>
                  <el-button v-if="profileForm.image" size="small" plain @click="clearCover">移除</el-button>
                  <div class="tip">jpg / png / gif / webp，不超过 3MB</div>
                </div>
              </div>
            </el-form-item>
            <el-form-item label="简介">
              <el-input v-model="profileForm.details" type="textarea" :rows="6" maxlength="500" show-word-limit
                placeholder="介绍一下你的社团，会显示在门户的社团详情页" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="savingProfile" @click="saveProfile">保存资料</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 活动 -->
        <el-tab-pane label="活动管理" name="news">
          <div class="toolbar">
            <el-button type="primary" icon="Plus" @click="openAdd">发布活动</el-button>
          </div>
          <el-table :data="newsList" border stripe size="default">
            <el-table-column prop="title" label="标题" />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 0 ? 'info' : 'success'" size="small">
                  {{ scope.row.status === 0 ? '草稿' : '已发布' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="名额" width="110" align="center">
              <template #default="scope">
                {{ scope.row.maxParticipants ? scope.row.maxParticipants + ' 人' : '不限' }}
              </template>
            </el-table-column>
            <el-table-column prop="viewCount" label="浏览" width="80" align="center" />
            <el-table-column prop="createTime" label="发布时间" width="120" />
            <el-table-column label="操作" width="260" align="center">
              <template #default="scope">
                <el-button size="small" @click="openSignups(scope.row)">报名名单</el-button>
                <el-button size="small" type="primary" @click="openEdit(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" plain @click="removeNews(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            class="pager"
            layout="total, prev, pager, next"
            :total="total"
            :page-size="pageSize"
            :current-page="currentPage"
            background
            @current-change="onPageChange"
          />
        </el-tab-pane>
      </el-tabs>
    </template>

    <el-empty v-else-if="!loading" description="你还不是任何社团的管理员" />

    <!-- 发布/编辑活动 -->
    <el-dialog v-model="newsDialog" :title="newsForm.id ? '编辑活动' : '发布活动'" width="560px">
      <el-form ref="newsRef" :model="newsForm" :rules="newsRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="newsForm.title" maxlength="60" show-word-limit />
        </el-form-item>
        <el-form-item label="详情" prop="details">
          <el-input v-model="newsForm.details" type="textarea" :rows="6" placeholder="支持纯文本，换行会保留" />
        </el-form-item>
        <el-form-item label="名额">
          <el-input-number v-model="newsForm.maxParticipants" :min="0" :max="9999" />
          <span class="tip">0 表示不限人数</span>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="newsForm.status">
            <el-radio :value="1">发布</el-radio>
            <el-radio :value="0">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="newsDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveNews">保存</el-button>
      </template>
    </el-dialog>

    <input ref="coverInput" type="file" accept="image/*" class="hidden-file" @change="onCoverChange" />

    <!-- 报名名单 -->
    <el-dialog v-model="signupDialog" :title="'报名名单（' + signupMeta + '）'" width="640px">
      <el-table :data="signups" border size="small">
        <el-table-column prop="userName" label="姓名" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="signupTime" label="报名时间" width="130" />
        <el-table-column label="签到" width="150" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.checkedIn === 1" type="success" size="small">已签到</el-tag>
            <el-button v-else size="small" type="primary" plain @click="toggleCheckin(scope.row)">签到</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" align="center">
          <template #default="scope">
            <el-button v-if="scope.row.checkedIn === 1" size="small" @click="toggleCheckin(scope.row)">取消签到</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="signups.length === 0" description="还没有人报名" :image-size="60" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { imgUrl } from '@/utils/image'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { useUserStore } from '@/store/user'
import {
  myTeamsApi, clubMembersApi, setMemberRoleApi, removeMemberApi,
  clubNewsApi, addClubNewsApi, editClubNewsApi, deleteClubNewsApi, clubSignupsApi, checkinApi,
  updateClubApi, uploadClubCoverApi, getClubDetailApi,
  type ClubMember, type ClubTeam
} from '@/api/club'

const userStore = useUserStore()
const myUserId = computed(() => userStore.userId)

const loading = ref(false)
const error = ref('')
const saving = ref(false)

const teams = ref<ClubTeam[]>([])
const currentTeamId = ref<number | undefined>(undefined)
const currentRole = computed(() => teams.value.find(t => t.teamId === currentTeamId.value)?.role ?? 0)
const activeTab = ref('members')

const members = ref<ClubMember[]>([])
const newsList = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const newsDialog = ref(false)
const newsRef = ref<FormInstance>()
const newsForm = reactive({ id: 0, title: '', details: '', status: 1, maxParticipants: 0 })
const newsRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  details: [{ required: true, message: '请输入活动详情', trigger: 'blur' }]
}

// ---------- 社团资料 ----------
const profileForm = reactive({ details: '', image: '' })
const savingProfile = ref(false)
const uploadingCover = ref(false)
const coverInput = ref<HTMLInputElement>()

const loadProfile = async () => {
  if (!currentTeamId.value) return
  const res = (await getClubDetailApi(currentTeamId.value)) as any
  if (res?.code === 200 && res.data) {
    profileForm.details = res.data.details || ''
    profileForm.image = res.data.image || ''
  }
}

const pickCover = () => coverInput.value?.click()

const clearCover = () => {
  profileForm.image = ''
}

const onCoverChange = async (event: Event) => {
  const input = event.target as HTMLInputElement
  const file = input.files && input.files[0]
  if (!file) return
  if (file.size > 3 * 1024 * 1024) {
    ElMessage.warning('封面不能超过 3MB')
    input.value = ''
    return
  }
  uploadingCover.value = true
  try {
    const form = new FormData()
    form.append('file', file)
    const res = (await uploadClubCoverApi(form)) as any
    if (res?.code === 200 && res.data) {
      profileForm.image = res.data
      ElMessage.success('封面已上传，记得点保存')
    }
  } finally {
    uploadingCover.value = false
    input.value = ''
  }
}

const saveProfile = async () => {
  savingProfile.value = true
  try {
    const res = (await updateClubApi(currentTeamId.value!, { ...profileForm })) as any
    if (res?.code === 200) {
      ElMessage.success(res.msg || '已保存')
    }
  } finally {
    savingProfile.value = false
  }
}

const signupDialog = ref(false)
const signups = ref<any[]>([])
const signupRows = ref<any[]>([])
const signupMeta = computed(() => {
  const max = signupRows.value[0]?.maxParticipants
  return `${signups.value.length} 人` + (max ? ` / 名额 ${max}` : '')
})

const loadTeams = async () => {
  const res = (await myTeamsApi()) as any
  if (res?.code === 200) {
    teams.value = res.data || []
    if (teams.value.length) {
      currentTeamId.value = teams.value[0].teamId
    }
  }
}

const loadMembers = async () => {
  if (!currentTeamId.value) return
  const res = (await clubMembersApi(currentTeamId.value)) as any
  if (res?.code === 200) members.value = res.data || []
}

const loadNews = async () => {
  if (!currentTeamId.value) return
  const res = (await clubNewsApi(currentTeamId.value, currentPage.value, pageSize.value)) as any
  if (res?.code === 200) {
    newsList.value = res.data.records || []
    total.value = res.data.total || 0
  }
}

const refresh = async () => {
  loading.value = true
  error.value = ''
  try {
    await loadTeams()
    if (currentTeamId.value) {
      await Promise.all([loadMembers(), loadNews(), loadProfile()])
    }
  } catch (e) {
    console.error(e)
    error.value = '加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const onTeamChange = async () => {
  currentPage.value = 1
  await Promise.all([loadMembers(), loadNews(), loadProfile()])
}

const onPageChange = (page: number) => {
  currentPage.value = page
  loadNews()
}

const toggleRole = async (row: ClubMember) => {
  const nextRole = row.role === 1 ? 0 : 1
  const label = nextRole === 1 ? '社团管理员' : '普通成员'
  try {
    await ElMessageBox.confirm(`确定把「${row.userName || row.userId}」设为${label}吗？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  const res = (await setMemberRoleApi(currentTeamId.value!, row.id, nextRole)) as any
  if (res?.code === 200) {
    ElMessage.success(res.msg || '设置成功')
    await loadMembers()
  }
}

const remove = async (row: ClubMember) => {
  try {
    await ElMessageBox.confirm(`确定把「${row.userName || row.userId}」移出社团吗？`, '提示', { type: 'warning', confirmButtonText: '移出', cancelButtonText: '取消' })
  } catch {
    return
  }
  const res = (await removeMemberApi(currentTeamId.value!, row.id)) as any
  if (res?.code === 200) {
    ElMessage.success(res.msg || '已移出')
    await loadMembers()
  }
}

const openAdd = () => {
  newsForm.id = 0
  newsForm.title = ''
  newsForm.details = ''
  newsForm.status = 1
  newsForm.maxParticipants = 0
  newsDialog.value = true
  newsRef.value?.clearValidate()
}

const openEdit = (row: any) => {
  newsForm.id = row.id
  newsForm.title = row.title
  newsForm.details = row.details
  newsForm.status = row.status ?? 1
  newsForm.maxParticipants = row.maxParticipants ?? 0
  newsDialog.value = true
  newsRef.value?.clearValidate()
}

const saveNews = () => {
  newsRef.value?.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      const res = newsForm.id
        ? (await editClubNewsApi(currentTeamId.value!, newsForm.id, { ...newsForm })) as any
        : (await addClubNewsApi(currentTeamId.value!, { ...newsForm })) as any
      if (res?.code === 200) {
        ElMessage.success(res.msg || '保存成功')
        newsDialog.value = false
        await loadNews()
      }
    } finally {
      saving.value = false
    }
  })
}

const removeNews = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定删除「${row.title}」吗？`, '提示', { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' })
  } catch {
    return
  }
  const res = (await deleteClubNewsApi(currentTeamId.value!, row.id)) as any
  if (res?.code === 200) {
    ElMessage.success(res.msg || '删除成功')
    await loadNews()
  }
}

const signupDialogNewsId = ref(0)
const openSignups = async (row: any) => {
  signupDialogNewsId.value = row.id
  const res = (await clubSignupsApi(currentTeamId.value!, row.id)) as any
  if (res?.code === 200) {
    signups.value = res.data.list || []
    signupRows.value = [{ maxParticipants: res.data.maxParticipants }]
    signupDialog.value = true
  }
}

const toggleCheckin = async (row: any) => {
  const next = row.checkedIn !== 1
  const res = (await checkinApi(currentTeamId.value!, signupDialogNewsId.value, row.id, next)) as any
  if (res?.code === 200) {
    ElMessage.success(res.msg || '操作成功')
    openSignups({ id: signupDialogNewsId.value })
  }
}

onMounted(refresh)
</script>

<style scoped lang="scss">
.card {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  padding: 20px;
}

.err { margin-bottom: 12px; }

.head {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}

.pager {
  margin-top: 12px;
  justify-content: flex-end;
  display: flex;
}

.tip {
  font-size: 12px;
  color: #909399;
}

.cover-box {
  display: flex;
  gap: 14px;
  align-items: flex-start;
}

.cover {
  width: 160px;
  height: 100px;
  border-radius: 6px;
  flex-shrink: 0;
}

.cover-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-hover);
  color: var(--text-3);
  font-size: 12px;
}

.cover-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
</style>
