import {defineStore} from "pinia";

export const useMenuStore = defineStore('menuStore', {
    state: () => ({
        // 初始状态
        isCollapse: false
    }),
    getters: {
        // 将 state 作为参数传入
        getIsCollapse(state) {
            return state.isCollapse;
        }
    },
    actions: {
        setIsCollapse() {
            // 正确切换状态
            this.isCollapse = !this.isCollapse;
        }
    }
});
