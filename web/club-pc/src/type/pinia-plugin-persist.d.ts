// src/types/pinia-plugin-persist.d.ts
import 'pinia';

declare module 'pinia' {
  export interface DefineStoreOptionsBase<S, Store> {
    persist?: boolean | {
      enabled: boolean,
      strategies?: Array<{
        key?: string,
        storage?: Storage,
        paths?: string[]
      }>
    };
  }
}
