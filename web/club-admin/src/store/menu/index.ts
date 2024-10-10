import { defineStore } from "pinia";

export const useMenuStore = defineStore('menuStore', {
    state: () => ({
        isCollapse: false // 初始状态
    }),
    getters: {
        getIsCollapse(state) { // 将 state 作为参数传入
            return state.isCollapse; // 正确访问 isCollapse
        }
    },
    actions: {
        setIsCollapse() {
            this.isCollapse = !this.isCollapse; // 正确切换状态
        }
    }
});
