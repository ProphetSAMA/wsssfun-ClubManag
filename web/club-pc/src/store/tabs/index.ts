import { defineStore } from 'pinia'
// 定义Store
export const tabsStore = defineStore('tableStore', {
    // 定义状态
    state: () => {
        return {
            fatable: 'home',
            mineTabs: '/mine/mycenter'
        }
    },
    // 获取state中的数据
    getters: {
        getCount(state) {
            return state.count
        }
    },
    actions: {
        // 设置值
        setCount(count: number) {
            this.count = count;
        }
    }
})