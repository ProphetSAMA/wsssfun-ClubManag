import { apiBaseUrl } from "@/http";

/**
 * 把后端返回的 image 字段转成可直接放进 <el-image> 的绝对 URL。
 *
 * 兼容两类历史脏数据：
 * 1. 一个字段里存了多张图（逗号分隔），只取第一张；
 * 2. 早期上传逻辑拼错前缀，库里留下 "undefined/images/xxx.jpg"。
 *
 * 后端只会返回 "/images/xxx.jpg" 这种相对路径，BASE 由 VITE_API_BASE_URL 决定，
 * 因此这里统一补全，页面里不再各自拼字符串。
 */
export const imgUrl = (image?: string | null): string => {
  if (!image) return "";
  let url = image.split(",")[0].trim();
  if (!url) return "";
  if (url.startsWith("undefined")) url = url.replace(/^undefined/, "");
  if (/^https?:\/\//i.test(url)) return url;
  if (url.startsWith("/")) return apiBaseUrl + url;
  return url;
};
