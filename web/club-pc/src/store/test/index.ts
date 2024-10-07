import { defineStore } from 'pinia'
// 定义Store
export const testStore = defineStore('testStore', {
    // 定义状态
    state: () => {
        return {
            count: 0
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