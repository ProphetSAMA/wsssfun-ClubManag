import axios, { type AxiosInstance } from "axios";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/store/user";
import router from "@/router";

const config = {
    baseURL: 'http://localhost:8888',
    timeout: 10000,
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

        this.instance.interceptors.response.use((res: any) => {
            if (res.data.code !== 200) {
                // token 过期或未登录
                if (res.data.code === 401) {
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
