import axios, { type AxiosInstance } from "axios";
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

const config = {
    baseURL: apiBaseUrl,
    timeout: 10000,
}

/**
 * 登录过期时先续签再重试原请求，避免用户填到一半被踢回登录页。
 * 用原生 axios 调续签接口，绕开下面的拦截器，防止递归。
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

    constructor(config: { baseURL: string; timeout: number }) {
        this.instance = axios.create(config);
        this.interceptors();
    }

    private interceptors() {
        this.instance.interceptors.request.use((config) => {
            const userStore = useUserStore()
            if (userStore.token) {
                config.headers!['token'] = userStore.token
            }
            return config;
        }, (error: any) => {
            error.data = {};
            error.data.msg = '服务器宕机了,请火速联系管理员!';
            return error;
        });

        this.instance.interceptors.response.use(async (res: any) => {
            if (res.data.code !== 200) {
                // token 过期或未登录
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
                    const userStore = useUserStore()
                    userStore.logout()
                    router.push('/login')
                    ElMessage.error(res.data.msg || '请重新登录')
                    return Promise.reject(res.data.msg || '请重新登录')
                }
                ElMessage.error(res.data.msg || '服务器宕机了');
                return Promise.reject(res.data.msg || '服务器宕机了');
            } else {
                return res.data;
            }
        }, (error) => {
            if (error && error.response) {
                const msgMap: Record<number, string> = {
                    400: '错误请求',
                    401: '未授权的操作,请重新登录',
                    403: '拒绝访问',
                    404: '请求错误,未找到该资源',
                    405: '不允许的请求方法',
                    408: '请求超时',
                    500: '接口异常',
                    501: '网络未实现',
                    502: '网络错误',
                    503: '服务不可用',
                    504: '网络超时',
                    505: 'http版本不支持该请求',
                }
                const msg = msgMap[error.response.status] || `连接错误${error.response.status}`;
                ElMessage.error(msg);
                if (error.response.status === 401) {
                    const userStore = useUserStore();
                    userStore.logout();
                    router.push('/login');
                }
            } else {
                ElMessage.error('连接到服务器失败');
            }
            return Promise.reject(error);
        })
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

    upload(url: string, params?: object): Promise<any> {
        return this.instance.post(url, params, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    }
}

export default new Http(config);
