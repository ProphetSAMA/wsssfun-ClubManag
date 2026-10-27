import { defineStore } from 'pinia'
// 定义Store
export const tabsStore = defineStore('tableStore', {
    // 定义状态
    state: () => {
        return {
            fatable: 'home', // 当前选中的 tab
            mineTabs: '/mine/mycenter', // 其他状态
            activeName: 'home' // 新增状态，默认值为 'home'
        }
    },
    persist: {
        storage: localStorage,
        pick: ['fatable', 'mineTabs', 'activeName'],
    }
})
