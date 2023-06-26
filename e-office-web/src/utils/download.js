/**
 * 數據導入_導出封裝
 */
import axios from 'axios'

const service = axios.create({
    responseType: 'arraybuffer'
})
service.interceptors.request.use(config => {
    config.headers['Authorization'] = window.sessionStorage.getItem('tokenStr');
    return config
}, error => {
    console.log(error)
});

service.interceptors.response.use(
    resp => {
        const headers = resp.headers;
        //正規表達式
        let reg = RegExp(/application\/json/);
        //以JSON返回
        if (headers['content-type'].match(reg)) {
            resp.data = uintToString(resp.data);
        }
        //以數據流返回
        else {
            //匯入插件
            let fileDownload = require('js-file-download');
            //文件名
            let fileName = headers["content-disposition"].split(";")[1].split("filename=")[1];
            //響應類型
            let contentType = headers["content-type"];
            //中文轉換，防止亂碼
            fileName = decodeURIComponent(fileName);
            //下載
            fileDownload(resp.data, fileName, contentType)
        }
    }, error => {
        console.log(error);
    }
);
let base = '';
export const downloadRequest = (url, params) => {
    return service({
        method: 'get',
        url: `${base}${url}`,
        data: params
    })
}

//圖片字符串解碼
function uintToString(uintArray) {
    let encodedString = String.fromCharCode.apply(null, new Uint8Array(uintArray)),
        decodedString = decodeURIComponent(escape(encodedString));
    return JSON.parse(decodedString);
}
export default service