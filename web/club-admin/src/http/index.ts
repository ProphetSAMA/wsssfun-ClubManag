// axios请求配置
import axios, { AxiosInstance } from "axios";
import { ElMessage } from "element-plus";

const config = {
    baseURL: 'http://localhost:8888',
    timeout: 10000,
}

class Http {
    // axios实例
    private instance: AxiosInstance;

    // 构造函数内初始化axios实例
    constructor(config: { baseURL: string; timeout: number }) {
        this.instance = axios.create(config);
        // 定义拦截器
        this.interceptors();
    }

    // 拦截器
    private interceptors() {
        // axios发送请求之前的处理
        this.instance.interceptors.request.use((config) => {
            // 在请求头中添加token
            let token = '';
            if (token) {
                config.headers!['token'] = token
                // 把token放进headers中
            }
            console.log(config);
            return config;
        }, (error: any) => {
            error.data = {};
            error.data.msg = '服务器宕机了,请火速联系管理员!';
            return error;
        });

        // axios响应数据(请求返回)之后的处理
        this.instance.interceptors.response.use((res: AxiosInstance) => {
            if (res.data.code !== 200) {
                ElMessage.error(res.data.msg || '服务器宕机了');
                return Promise.reject(res.data.msg || '服务器宕机了');
            } else {
                return res.data;
            }
        }, (error) => {
            console.log('出错了');
            error.data = {};
            if (error && error.response) {
                switch (error.response.status) {
                    case 400:
                        error.data.msg = '错误请求';
                        ElMessage.error(error.data.msg);
                        break
                    case 401:
                        error.data.msg = '未授权的操作,请重新登录';
                        ElMessage.error(error.data.msg);
                        break
                    case 403:
                        error.data.msg = '拒绝访问';
                        ElMessage.error(error.data.msg);
                        break
                    case 404:
                        error.data.msg = '请求错误,未找到该资源';
                        ElMessage.error(error.data.msg);
                        break
                    case 405:
                        error.data.msg = '不允许的请求方法';
                        ElMessage.error(error.data.msg);
                        break
                    case 408:
                        error.data.msg = '请求超时';
                        ElMessage.error(error.data.msg);
                        break
                    case 500:
                        error.data.msg = '接口异常';
                        ElMessage.error(error.data.msg);
                        break
                    case 501:
                        error.data.msg = '网络未实现';
                        ElMessage.error(error.data.msg);
                        break
                    case 502:
                        error.data.msg = '网络错误';
                        ElMessage.error(error.data.msg);
                        break
                    case 503:
                        error.data.msg = '服务不可用';
                        ElMessage.error(error.data.msg);
                        break
                    case 504:
                        error.data.msg = '网络超时';
                        ElMessage.error(error.data.msg);
                        break
                    case 505:
                        error.data.msg = 'http版本不支持该请求';
                        ElMessage.error(error.data.msg);
                        break
                    default:
                        error.data.msg = `连接错误${error.response.status}`;
                        ElMessage.error(error.data.msg);
                }
            } else {
                error.data.msg = '连接到服务器失败';
                ElMessage.error(error.data.msg);
            }
            return Promise.reject(error);
        })
    }
    // GET方法
    get(url: string, params?: object): Promise<any> {
        return this.instance.get(url, { params });
    }
    // POST方法
    post(url: string, data?: object): Promise<any> {
        return this.instance.post(url, data);
    }
    // PUT方法
    put(url: string, data?: object): Promise<any> {
        return this.instance.put(url, data);
    }
    // DELETE方法
    delete(url: string): Promise<any> {
        return this.instance.delete(url);
    }
    // 图片上传
    upload(url: string, params?: object): Promise<any> {
        return this.instance.post(url, params, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    }
}
export default new Http(config);