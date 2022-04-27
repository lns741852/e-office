/**
 * 跨域配置
 */
let proxyObj = {}
proxyObj['/ws'] = {
    ws: true,
    target: 'ws://localhost:8081'
};
proxyObj['/'] = {
    //websocket
    ws: false,
    //目標地址
    target: 'http://localhost:8081',
    //請求頭中的host會被配置target
    changeOrigin: true,
    //不重寫請求地址
    pathRewrite: {
        '^/': '/'
    }
}
module.exports = {
    devServer: {
        host: 'localhost',
        port: 8080,
        proxy: proxyObj
    }
}