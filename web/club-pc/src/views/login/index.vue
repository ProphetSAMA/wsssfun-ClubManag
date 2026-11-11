<template>
  <div class="login-page">
    <div class="login-card">
      <h1>登录</h1>
      <p class="sub">高校社团信息化平台</p>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        label-width="0"
        size="large"
      >
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button class="submit-btn" :loading="loading" @click="handleLogin">
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
      <p class="switch">
        <span class="link" @click="openForgot">忘记密码？</span>
      </p>
      <p class="switch">
        还没有账号？
        <span class="link" @click="openRegister">立即注册</span>
      </p>
    </div>

    <!-- 忘记密码 -->
    <el-dialog v-model="forgotVisible" title="找回密码" width="440px">
      <el-form ref="forgotRef" :model="forgotForm" :rules="forgotRules" label-width="80px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="forgotForm.username" placeholder="注册时的登录账号" :disabled="questionLoaded" />
        </el-form-item>
        <el-form-item v-if="questionLoaded" label="密保问题">
          <span class="question">{{ question }}</span>
        </el-form-item>
        <el-form-item v-if="questionLoaded" label="答案" prop="answer">
          <el-input v-model="forgotForm.answer" placeholder="回答设置密保时填的答案" />
        </el-form-item>
        <el-form-item v-if="questionLoaded" label="新密码" prop="newPassword">
          <el-input v-model="forgotForm.newPassword" type="password" show-password placeholder="至少 8 位" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="forgotVisible = false">取消</el-button>
        <el-button v-if="!questionLoaded" type="primary" :loading="forgotLoading" @click="queryQuestion">下一步</el-button>
        <el-button v-else type="primary" :loading="forgotLoading" @click="resetPassword">重置密码</el-button>
      </template>
    </el-dialog>

    <!-- 注册 -->
    <el-dialog v-model="registerVisible" title="注册账号" width="420px">
      <el-form ref="registerRef" :model="registerForm" :rules="registerRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="3-20 位字母/数字/下划线" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="registerForm.nickname" placeholder="不填则默认用用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" show-password placeholder="至少 8 位" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="registerVisible = false">取消</el-button>
        <el-button type="primary" :loading="registering" @click="handleRegister">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { loginApi, registerApi, forgotQueryApi, forgotResetApi } from '@/api'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({ username: '', password: '' })

// 忘记密码
const forgotVisible = ref(false)
const forgotRef = ref<FormInstance>()
const forgotLoading = ref(false)
const questionLoaded = ref(false)
const question = ref('')
const forgotForm = reactive({ username: '', answer: '', newPassword: '' })
const forgotRules: FormRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  answer: [{ required: true, message: '请输入密保答案', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, message: '密码至少 8 位', trigger: 'blur' }
  ]
}

const openForgot = () => {
  forgotForm.username = loginForm.username
  forgotForm.answer = ''
  forgotForm.newPassword = ''
  questionLoaded.value = false
  question.value = ''
  forgotVisible.value = true
  forgotRef.value?.clearValidate()
}

const queryQuestion = async () => {
  if (!forgotForm.username) {
    ElMessage.warning('请输入账号')
    return
  }
  forgotLoading.value = true
  try {
    const res = (await forgotQueryApi(forgotForm.username)) as any
    if (res?.code === 200) {
      question.value = res.data.question
      questionLoaded.value = true
    }
  } finally {
    forgotLoading.value = false
  }
}

const resetPassword = () => {
  forgotRef.value?.validate(async (valid) => {
    if (!valid) return
    forgotLoading.value = true
    try {
      const res = (await forgotResetApi({ ...forgotForm })) as any
      if (res?.code === 200) {
        ElMessage.success(res.msg || '密码已重置')
        forgotVisible.value = false
        loginForm.username = forgotForm.username
        loginForm.password = ''
      }
    } finally {
      forgotLoading.value = false
    }
  })
}
const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  loginFormRef.value?.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await loginApi(loginForm) as any
      if (res && res.code === 200) {
        userStore.setLoginInfo(res.data)
        ElMessage.success('登录成功')
        router.push('/home')
      } else {
        ElMessage.error(res?.msg || '登录失败')
      }
    } catch (e: any) {
      ElMessage.error(e?.msg || '登录失败')
    } finally {
      loading.value = false
    }
  })
}

// ---------------- 注册 ----------------
const registerVisible = ref(false)
const registering = ref(false)
const registerRef = ref<FormInstance>()
const registerForm = reactive({ username: '', nickname: '', password: '', confirmPassword: '' })

const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9_]{3,20}$/, message: '3-20 位字母、数字或下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '密码至少 8 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: (e?: Error) => void) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const openRegister = () => {
  registerForm.username = ''
  registerForm.nickname = ''
  registerForm.password = ''
  registerForm.confirmPassword = ''
  registerVisible.value = true
  registerRef.value?.clearValidate()
}

const handleRegister = () => {
  registerRef.value?.validate(async (valid) => {
    if (!valid) return
    registering.value = true
    try {
      const res = await registerApi({
        username: registerForm.username,
        password: registerForm.password,
        nickname: registerForm.nickname || undefined
      }) as any
      if (res && res.code === 200) {
        ElMessage.success('注册成功，请登录')
        loginForm.username = registerForm.username
        registerVisible.value = false
      } else {
        ElMessage.error(res?.msg || '注册失败')
      }
    } catch (e: any) {
      ElMessage.error(e?.msg || '注册失败')
    } finally {
      registering.value = false
    }
  })
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg);
}

.login-card {
  width: 380px;
  background: var(--bg-white);
  border-radius: var(--radius-lg);
  padding: 40px 36px;
  box-shadow: var(--shadow-md);

  h1 {
    font-size: 22px;
    font-weight: 700;
    color: var(--text-1);
    margin-bottom: 4px;
  }

  .sub {
    font-size: 14px;
    color: var(--text-3);
    margin-bottom: 32px;
  }
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 8px !important;
  background: var(--primary) !important;
  border-color: var(--primary) !important;
  letter-spacing: 2px;

  &:hover {
    background: var(--primary-hover) !important;
    border-color: var(--primary-hover) !important;
  }
}

.switch {
  text-align: center;
  font-size: 13px;
  color: var(--text-3);
  margin-top: 4px;

  .link {
    color: var(--primary);
    cursor: pointer;

    &:hover { text-decoration: underline; }
  }
}
</style>
