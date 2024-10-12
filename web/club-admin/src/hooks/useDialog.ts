import {reactive} from "vue";

export default function useDialog() {
    // 定义弹窗属性
    const dialog = reactive({
        title: '新增',
        visible: false,
        width: 630,
        height: 280,
    })

    // 关闭弹窗
    const onClose = () => {
        dialog.visible = false
    }
    // 确认弹窗
    const onConfirm = () => {
        dialog.visible = false
    }
    // 显示弹窗
    const onShow = () => {
        dialog.visible = true
    }

    return {
        dialog,
        onClose,
        onConfirm,
        onShow
    }
}