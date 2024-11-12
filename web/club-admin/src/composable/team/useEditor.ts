// @ts-ignore
import {onBeforeUnmount, ref, shallowRef, onMounted} from "vue";
export default function useEditor() {

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
    const editorConfig = { placeholder: '请输入内容', toolbar: toolbarConfig };
    const handleCreated = (editor: any) => {
        // 记录编辑器实例
        editorRef.value = editor;
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