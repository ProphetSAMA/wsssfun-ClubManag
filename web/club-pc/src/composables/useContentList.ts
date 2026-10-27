import { ref } from "vue";
import http from "@/http";

/**
 * 门户内容列表（活动 / 新闻 / 公告共用）。
 *
 * 后端 /api/news/list 支持按 type + title 模糊 + 分页，这里把状态和请求收敛到一处，
 * 三个页面只管渲染。
 */
export function useContentList(type: string, pageSize = 10) {
  const list = ref<any[]>([]);
  const total = ref(0);
  const currentPage = ref(1);
  const size = ref(pageSize);
  const keyword = ref("");
  const loading = ref(true);
  const error = ref("");

  const fetchList = async () => {
    loading.value = true;
    error.value = "";
    try {
      const res = (await http.get("/api/news/list", {
        currentPage: currentPage.value,
        pageSize: size.value,
        type,
        title: keyword.value.trim(),
      })) as any;
      if (res && res.code === 200) {
        list.value = res.data.records || [];
        total.value = res.data.total || 0;
      }
    } catch (e) {
      console.error(e);
      error.value = "内容加载失败，请稍后重试";
    } finally {
      loading.value = false;
    }
  };

  /** 搜索：条件变化后回到第一页 */
  const search = () => {
    currentPage.value = 1;
    fetchList();
  };

  const reset = () => {
    keyword.value = "";
    currentPage.value = 1;
    fetchList();
  };

  const pageChange = (page: number) => {
    currentPage.value = page;
    fetchList();
  };

  return { list, total, currentPage, size, keyword, loading, error, fetchList, search, reset, pageChange };
}
