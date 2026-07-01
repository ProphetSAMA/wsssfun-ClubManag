import axios, { type AxiosInstance, type AxiosRequestConfig } from "axios";
import { ElMessage } from "element-plus";

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
                const token = localStorage.getItem("token");
                if (token) {
                    config.headers!["token"] = token;
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
                const msg = msgMap[error.response?.status] || `连接错误${error.response?.status || ""}`;
                ElMessage.error(msg);
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
