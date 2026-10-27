import axios, { type AxiosInstance, type AxiosRequestConfig } from "axios";
import { useUserStore } from "@/store/user";
import router from "@/router";

export const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL || "").replace(/\/$/, "");

export const apiUrl = (path: string) => {
    if (/^https?:\/\//i.test(path)) {
        return path;
    }

    const normalizedPath = path.startsWith("/") ? path : `/${path}`;
    return `${apiBaseUrl}${normalizedPath}`;
};

const config: AxiosRequestConfig = {
    baseURL: apiBaseUrl,
    timeout: 10000,
};

/**
 * 登录过期时先续签再重试原请求。用原生 axios，避免走进下面的拦截器递归。
 */
let refreshing: Promise<string | null> | null = null;

export async function refreshToken(): Promise<string | null> {
    const userStore = useUserStore();
    if (!userStore.token) return null;
    try {
        const res = await axios.post(apiUrl("/api/user/refresh"), null, {
            headers: { token: userStore.token },
            timeout: 10000,
        });
        const token = res.data?.data?.token;
        if (res.data?.code === 200 && token) {
            userStore.token = token;
            return token;
        }
    } catch (e) {
        console.error("token 续签失败", e);
    }
    return null;
}

class Http {
    private instance: AxiosInstance;

    constructor(config: AxiosRequestConfig) {
        this.instance = axios.create(config);
        this.interceptors();
    }

    private interceptors() {
        this.instance.interceptors.request.use(
            (config) => {
                const userStore = useUserStore();
                if (userStore.token) {
                    config.headers!["token"] = userStore.token;
                }
                return config;
            },
            (error) => {
                return Promise.reject(error);
            }
        );

        this.instance.interceptors.response.use(
            async (res: any) => {
                if (res.data.code !== 200) {
                    // token 过期：先续签并重试一次
                    if (res.data.code === 401) {
                        const original = res.config;
                        if (original && !original.__retried) {
                            refreshing = refreshing ?? refreshToken();
                            const token = await refreshing;
                            refreshing = null;
                            if (token) {
                                original.__retried = true;
                                original.headers = { ...original.headers, token };
                                return this.instance.request(original);
                            }
                        }
                        const userStore = useUserStore();
                        userStore.logout();
                        router.push('/login');
                        ElMessage.error(res.data.msg || '请重新登录');
                        return Promise.reject(res.data);
                    }
                    ElMessage.error(res.data.msg || "服务器出错");
                    return Promise.reject(res.data);
                }
                return res.data;
            },
            (error) => {
                const msgMap: Record<number, string> = {
                    400: "请求错误",
                    401: "未授权，请登录",
                    403: "拒绝访问",
                    404: "请求地址出错",
                    408: "请求超时",
                    500: "服务器内部错误",
                    502: "网关错误",
                    503: "服务不可用",
                    504: "网关超时",
                };
                const status = error.response?.status;
                const msg = msgMap[status] || `连接错误${status || ""}`;
                ElMessage.error(msg);
                // 401 时清除登录状态
                if (status === 401) {
                    const userStore = useUserStore();
                    userStore.logout();
                    router.push('/login');
                }
                return Promise.reject(error);
            }
        );
    }

    get(url: string, params?: object): Promise<any> {
        return this.instance.get(url, { params });
    }

    post(url: string, data?: object): Promise<any> {
        return this.instance.post(url, data);
    }

    put(url: string, data?: object): Promise<any> {
        return this.instance.put(url, data);
    }

    delete(url: string): Promise<any> {
        return this.instance.delete(url);
    }
}

export default new Http(config);
