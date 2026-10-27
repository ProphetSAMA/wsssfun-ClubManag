/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

declare module '*.vue' {
    import {DefineComponent, defineComponent} from 'vue'
    const component: DefineComponent<{}, {}, any>
    export default component
}
