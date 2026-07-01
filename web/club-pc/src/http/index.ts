import axios, { type AxiosInstance, type AxiosRequestConfig } from "axios";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/store/user";
import router from "@/router";

const config: AxiosRequestConfig = {
    baseURL: "http://localhost:8888",
    timeout: 10000,
};

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
            (res: any) => {
                if (res.data.code !== 200) {
                    // token 过期或未登录
                    if (res.data.code === 401) {
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
