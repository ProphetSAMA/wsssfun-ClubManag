import { defineStore } from "pinia";

export const useTestStore = defineStore('test',{
    state: ()=> ({ count: 0 }),
    getters: {
        getCount() {
            return state.count
        }
    },
    actions: {
        setCount(count: number) {
            this.count = count

        }
    }
})