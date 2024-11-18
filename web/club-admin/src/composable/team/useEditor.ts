// @ts-ignore
import { server } from "typescript";
import {onBeforeUnmount, ref, shallowRef, onMounted} from "vue";
import { IEditorConfig } from "@wangeditor/editor";
export default function useEditor() {

    type InsertFnType = (url: string, alt?: string, href?: string) => void;

    // 编辑器示例, 用于获取编辑器内容, 必须使用shallowRef
    const editorRef = shallowRef();
    const mode = ref('default');

    // 内容HTML
    const valueHtml = ref('');

    // 组件销毁时, 销毁编辑器
    onBeforeUnmount(() => {
        const editor = editorRef.value;
        if (editor == null) return;
        editor.destroy();
    });
    const toolbarConfig = {};
    // const editorConfig = { placeholder: '请输入内容', toolbar: toolbarConfig };
    const editorConfig: Partial<IEditorConfig> = { MENU_CONF: {}, placeholder: '请输入内容' }
    const handleCreated = (editor: any) => {
        // 记录编辑器实例
        editorRef.value = editor;
    }

    // 上传图片
    editorConfig.MENU_CONF!['uploadImage'] = {
        fieldName: 'file',
        // 上传图片后端地址
        server: process.env.BASE_API + '/api/upload/uploadImage',
        // 自定义插入图片
        customInsert(res: any, insertFn: InsertFnType) {
            // res为服务端返回结果
            console.log(res)
            // 从res中找到url、alt、href,然后插入图片
            insertFn(process.env.BASE_API + res.data)
        }
    }

    return {
        editorConfig,
        handleCreated,
        valueHtml,
        mode,
        toolbarConfig,
        editorRef
    }
}