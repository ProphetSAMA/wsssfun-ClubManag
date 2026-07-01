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
      <p class="hint">默认账号: admin / admin123</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { loginApi } from '@/api'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({ username: '', password: '' })
const rules = {
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

.hint {
  text-align: center;
  font-size: 12px;
  color: var(--text-3);
  margin-top: 16px;
}
</style>
