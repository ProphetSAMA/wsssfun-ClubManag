import http from '@/http'
import {User} from "@/api/user/UserModel";

export const addUserApi = (parm: User) => {
    return http.post('/api/user', parm)
}