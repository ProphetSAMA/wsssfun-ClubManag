import { defineStore } from 'pinia'
import { ref } from 'vue'
import { LoginResult } from '@/api/user/UserModel'

export const useUserStore = defineStore('user', () => {
    const token = ref('')
    const userId = ref(0)
    const username = ref('')
    const nickname = ref('')
    const isAdmin = ref(0)

    function setLoginInfo(data: LoginResult) {
        token.value = data.token
        userId.value = data.userId
        username.value = data.username
        nickname.value = data.nickname
        isAdmin.value = data.isAdmin
    }

    function logout() {
        token.value = ''
        userId.value = 0
        username.value = ''
        nickname.value = ''
        isAdmin.value = 0
    }

    return { token, userId, username, nickname, isAdmin, setLoginInfo, logout }
}, {
    persist: true
})
