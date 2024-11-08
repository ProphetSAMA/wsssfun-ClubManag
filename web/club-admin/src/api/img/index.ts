// @ts-ignore
import http from "@/http/index";

// 图片上传
export const UploadImageApi = (param: object) => {
    return http.upload('/api/upload/uploadImage', param);
}