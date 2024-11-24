import axios, { InternalAxiosRequestConfig, AxiosInstance, AxiosResponse, AxiosRequestConfig } from "axios";
import { ElMessage } from "element-plus";

const config = {
    // axios 配置
    // axios.defaults.baseURL = "http://localhost:8888";
    baseURL: "/api",
    timeout: 10000,
};

class Http {
    // axios 实例
    private instance: AxiosInstance;
    // 构造函数 初始化
    constructor(config: AxiosRequestConfig) {
        this.instance = axios.create(config);
        // 拦截器
        this.interceptors();
    }

    // 拦截器
    private interceptors() {
        // axios发送请求之前的处理
        this.instance.interceptors.request.use(
            (config: InternalAxiosRequestConfig) => {
                let token = "";
                if (token) {
                    config.headers!["token"] = token;
                }
                console.log(config);
                return config;
            },
            (error: any) => {
                error.data = {};
                error.data.msg = "服务器异常，请联系管理员";
                return error;
            }
        );
        // axios请求返回之后的处理
        // 请求返回之后的处理
        this.instance.interceptors.response.use(
            (res: AxiosResponse) => {
                if (res.data.code !== 200) {
                    ElMessage.error(res.data.msg || "服务器出错");
                } else {
                    return res.data;
                }
            },
            (error) => {
                error.data = {};
                if (error && error.response) {
                    switch (error.response.status) {
                        case 400:
                            error.data.msg = "请求错误";
                            ElMessage.error(error.data.msg);
                            break;
                        case 401:
                            error.data.msg = "未授权，请登录";
                            ElMessage.error(error.data.msg);
                            break;
                        case 403:
                            error.data.msg = "拒绝访问";
                            ElMessage.error(error.data.msg);
                            break;
                        case 404:
                            error.data.msg = "请求地址出错";
                            ElMessage.error(error.data.msg);
                            break;
                        case 408:
                            error.data.msg = "请求超时";
                            ElMessage.error(error.data.msg);
                            break;
                        case 500:
                            error.data.msg = "服务器内部错误";
                            ElMessage.error(error.data.msg);
                            break;
                        case 501:
                            error.data.msg = "服务未实现";
                            ElMessage.error(error.data.msg);
                            break;
                        case 502:
                            error.data.msg = "网关错误";
                            ElMessage.error(error.data.msg);
                            break;
                        case 503:
                            error.data.msg = "服务不可用";
                            ElMessage.error(error.data.msg);
                            break;
                        case 504:
                            error.data.msg = "网关超时";
                            ElMessage.error(error.data.msg);
                            break;
                        case 505:
                            error.data.msg = "HTTP版本不受支持";
                            ElMessage.error(error.data.msg);
                            break;
                        default:
                            error.data.msg = `连接错误${error.response.status}`;
                            ElMessage.error(error.data.msg);
                            break;
                    }
                } else {
                    error.data.msg = "连接服务器失败";
                    ElMessage.error(error.data.msg);
                }
                return Promise.reject(error);
            }
        );
    }
    /* GET请求 */
    get(url: string, params?: object): Promise<any> {
        return this.instance.get(url, { params });
    }
    /* POST请求 */
    post(url: string, data?: object): Promise<any> {
        return this.instance.post(url, data);
    }
    /* PUT请求 */
    put(url: string, data?: object): Promise<any> {
        return this.instance.put(url, data);
    }
    /* DELETE请求 */
    delete(url: string, params?: object): Promise<any> {
        return this.instance.delete(url, { params });
    }
}
