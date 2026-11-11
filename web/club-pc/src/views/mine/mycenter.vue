<template>
  <div class="card">
    <div class="card-title">
      <span>个人信息</span>
      <div class="actions">
        <el-button size="small" @click="openProfile">编辑资料</el-button>
        <el-button size="small" type="primary" plain @click="openPassword">修改密码</el-button>
        <el-button size="small" @click="openSecurity">密保问题</el-button>
      </div>
    </div>
    <div class="avatar-row">
      <el-avatar :size="64" :src="user.avatar ? imgUrl(user.avatar) : undefined">
        {{ (user.nickname || user.username || '?').slice(0, 1) }}
      </el-avatar>
      <div class="avatar-actions">
        <span class="tip">支持 jpg / png / gif / webp，不超过 2MB</span>
        <div>
          <el-button size="small" :loading="uploading" @click="pickAvatar">更换头像</el-button>
          <el-button v-if="user.avatar" size="small" plain @click="removeAvatar">恢复默认</el-button>
        </div>
        <input ref="fileInput" type="file" accept="image/*" class="hidden-file" @change="onFileChange" />
      </div>
    </div>

    <el-descriptions :column="1" border>
      <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
      <el-descriptions-item label="昵称">{{ user.nickname }}</el-descriptions-item>
      <el-descriptions-item label="性别">{{ user.sex === '0' ? '男' : '女' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ user.phone }}</el-descriptions-item>
      <el-descriptions-item label="角色">
        <el-tag :type="user.isAdmin === 1 ? 'danger' : 'info'" size="small">
          {{ user.isAdmin === 1 ? '管理员' : '普通用户' }}
        </el-tag>
      </el-descriptions-item>
    </el-descriptions>

    <!-- 编辑资料 -->
    <el-dialog v-model="profileVisible" title="编辑资料" width="420px">
      <el-form ref="profileRef" :model="profileForm" :rules="profileRules" label-width="70px">
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="profileForm.nickname" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="profileForm.sex">
            <el-radio value="0">男</el-radio>
            <el-radio value="1">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="profileForm.phone" maxlength="11" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="profileVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>

    <!-- 密保问题 -->
    <el-dialog v-model="securityVisible" title="密保问题" width="460px">
      <el-alert
        type="info"
        :closable="false"
        show-icon
        title="忘记密码时可以通过密保问题自助重置，设置后请牢记答案"
        class="tip-alert"
      />
      <el-form ref="securityRef" :model="securityForm" :rules="securityRules" label-width="90px">
        <el-form-item label="当前密码" prop="password">
          <el-input v-model="securityForm.password" type="password" show-password placeholder="验证身份用" />
        </el-form-item>
        <el-form-item label="密保问题" prop="question">
          <el-input v-model="securityForm.question" maxlength="50" show-word-limit
            placeholder="例如：我最喜欢的社团是？" />
        </el-form-item>
        <el-form-item label="密保答案" prop="answer">
          <el-input v-model="securityForm.answer" maxlength="50" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="securityVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveSecurity">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码 -->
    <el-dialog v-model="passwordVisible" title="修改密码" width="420px">
      <el-form ref="passwordRef" :model="passwordForm" :rules="passwordRules" label-width="90px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="至少 8 位" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="savePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { imgUrl } from '@/utils/image'
import { useUserStore } from '@/store/user'
import {
  getUserInfoApi, updateProfileApi, changePasswordApi,
  updateAvatarApi, uploadAvatarApi, securityQuestionApi, updateSecurityQuestionApi
} from '@/api'

const userStore = useUserStore()
const user = reactive({ username: '', nickname: '', sex: '', phone: '', isAdmin: 0, avatar: '' })

const saving = ref(false)
const profileVisible = ref(false)
const passwordVisible = ref(false)
const profileRef = ref<FormInstance>()
const passwordRef = ref<FormInstance>()

const profileForm = reactive({ nickname: '', sex: '0', phone: '' })
const uploading = ref(false)
const fileInput = ref<HTMLInputElement>()

const securityVisible = ref(false)
const securityRef = ref<FormInstance>()
const securityForm = reactive({ password: '', question: '', answer: '' })
const securityRules: FormRules = {
  password: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  question: [{ required: true, message: '请填写密保问题', trigger: 'blur' }],
  answer: [{ required: true, message: '请填写密保答案', trigger: 'blur' }]
}
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const profileRules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const passwordRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, message: '新密码至少 8 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: (e?: Error) => void) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const loadUser = async () => {
  try {
    const res = await getUserInfoApi() as any
    if (res && res.code === 200) {
      Object.assign(user, res.data)
      // 头像要在头部也显示，顺手同步到 store
      userStore.setAvatar(res.data.avatar || '')
    }
  } catch {
    user.username = userStore.username
    user.nickname = userStore.nickname
    user.isAdmin = userStore.isAdmin
  }
}

const pickAvatar = () => {
  fileInput.value?.click()
}

const onFileChange = async (event: Event) => {
  const input = event.target as HTMLInputElement
  const file = input.files && input.files[0]
  if (!file) return
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.warning('头像不能超过 2MB')
    input.value = ''
    return
  }
  uploading.value = true
  try {
    const form = new FormData()
    form.append('file', file)
    const up = (await uploadAvatarApi(form)) as any
    if (up?.code === 200 && up.data) {
      const res = (await updateAvatarApi(up.data)) as any
      if (res?.code === 200) {
        user.avatar = up.data
        userStore.setAvatar(up.data)
        ElMessage.success(res.msg || '头像已更新')
      }
    }
  } finally {
    uploading.value = false
    input.value = ''
  }
}

const removeAvatar = async () => {
  const res = (await updateAvatarApi('')) as any
  if (res?.code === 200) {
    user.avatar = ''
    userStore.setAvatar('')
    ElMessage.success('已恢复默认头像')
  }
}

const openSecurity = async () => {
  securityForm.password = ''
  securityForm.question = ''
  securityForm.answer = ''
  const res = (await securityQuestionApi()) as any
  if (res?.code === 200 && res.data?.question) {
    securityForm.question = res.data.question
  }
  securityVisible.value = true
  securityRef.value?.clearValidate()
}

const saveSecurity = () => {
  securityRef.value?.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      const res = (await updateSecurityQuestionApi({ ...securityForm })) as any
      if (res?.code === 200) {
        ElMessage.success(res.msg || '密保问题已保存')
        securityVisible.value = false
      }
    } finally {
      saving.value = false
    }
  })
}

const openProfile = () => {
  profileForm.nickname = user.nickname
  profileForm.sex = user.sex === '1' ? '1' : '0'
  profileForm.phone = user.phone
  profileVisible.value = true
  profileRef.value?.clearValidate()
}

const saveProfile = () => {
  profileRef.value?.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      const res = await updateProfileApi({ ...profileForm }) as any
      if (res && res.code === 200) {
        ElMessage.success(res.msg || '保存成功')
        user.nickname = profileForm.nickname
        user.phone = profileForm.phone
        user.sex = profileForm.sex
        userStore.nickname = profileForm.nickname
        profileVisible.value = false
      }
    } finally {
      saving.value = false
    }
  })
}

const openPassword = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordVisible.value = true
  passwordRef.value?.clearValidate()
}

const savePassword = () => {
  passwordRef.value?.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      const res = await changePasswordApi({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      }) as any
      if (res && res.code === 200) {
        ElMessage.success(res.msg || '密码修改成功')
        passwordVisible.value = false
      }
    } finally {
      saving.value = false
    }
  })
}

onMounted(loadUser)
</script>

<style scoped lang="scss">
.card {
  background: var(--bg-white);
  border-radius: var(--radius);
  border: 1px solid var(--border-light);
  padding: 20px;
}

.card-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 15px;
  font-weight: 600;
  color: var(--text-1);
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-light);
}

.actions {
  display: flex;
  gap: 8px;
}
</style>
