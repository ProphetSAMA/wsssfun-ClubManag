import { ComponentInternalInstance, getCurrentInstance } from 'vue'

export default function useInstance() {
    // 获取当前实例
    const { appContext, proxy } = getCurrentInstance() as ComponentInternalInstance;
    // 获取全局属性
    const global = appContext.config.globalProperties;
    return {
        global,
        proxy
    }
}