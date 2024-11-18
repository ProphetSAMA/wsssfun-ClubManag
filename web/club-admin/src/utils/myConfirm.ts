import { ElMessageBox } from 'element-plus'

export default function myConfirm(text: string) {
    return new Promise((resolve, reject) => {
        // 弹框
        ElMessageBox.confirm(
            text,
            '提示',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                dangerouslyUseHTMLString: true, // 支持html语法
                type: 'warning',
            }
        )
            // 点击确定
            .then(() => {
                resolve(true);
            })
            // 点击取消
            .catch(() => {
                return false;
            })
    })

}