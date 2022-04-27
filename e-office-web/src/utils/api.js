import axios from 'axios'
import { Message } from "element-ui";
import router from '../router'
import Vue from 'vue'

axios.defaults.baseURL = '/'
Vue.prototype.axios = axios

// 請求攔截器
axios.interceptors.request.use(config => {
    if (window.sessionStorage.getItem('tokenStr')) {
        //請求攜帶token
        config.headers['Authorization'] =
            window.sessionStorage.getItem('tokenStr');
    }
    return config
}, error => {
    console.log(error);
})

//響應攔截器
axios.interceptors.response.use(success => {
        //http響應代碼
        if (success.status && success.status == 200) {
            //自訂響應代碼
            if (success.data.code == 500 || success.data.code == 401 ||
                success.data.code == 403) {
                Message.error({ message: success.data.message });
                return;
            }
            if (success.data.message) {
                Message.success({ message: success.data.message });
            }
        }
        return success.data;
    },
    //當http代碼不等於200時 
    error => {
        if (error.response.code == 504 || error.response.code == 404) {
            Message.error({ message: '伺服器發生異常' });
        } else if (error.response.code == 403) {
            Message.error({ message: '權限不足，無法查看內容！' });
        } else if (error.response.code == 401) {
            Message.error({ message: '尚未登入，請登入' });
            router.replace('/');
        } else {
            if (error.response.data.message) {
                Message.error({ message: error.response.data.message });
            } else {
                Message.error({ message: '訪問錯誤！' });
            }
        }
        return;
    });

let base = '';

//傳送json格式的post請求
export const postRequest = (url, params) => {
    return axios({
        method: 'post',
        url: `${base}${url}`,
        data: params
    })
};
//傳送json的put請求
export const putRequest = (url, params) => {
    return axios({
        method: 'put',
        url: `${base}${url}`,
        data: params
    })
};
//傳送json的get請求
export const getRequest = (url, params) => {
    return axios({
        method: 'get',
        url: `${base}${url}`,
        data: params
    })
};
//傳送json的delete請求
export const deleteRequest = (url, params) => {
    return axios({
        method: 'delete',
        url: `${base}${url}`,
        data: params
    })
};