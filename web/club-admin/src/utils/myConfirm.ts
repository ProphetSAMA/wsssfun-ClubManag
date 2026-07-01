import { ElMessageBox } from 'element-plus'

export default function myConfirm(text: string): Promise<boolean> {
    return new Promise((resolve) => {
        ElMessageBox.confirm(
            text,
            '提示',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                dangerouslyUseHTMLString: true,
                type: 'warning',
            }
        )
            .then(() => {
                resolve(true)
            })
            .catch(() => {
                resolve(false)
            })
    })
}
