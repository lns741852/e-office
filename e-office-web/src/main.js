// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import store from "./store";

import Element from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
//字體圖標
import 'font-awesome/css/font-awesome.min.css'

Vue.config.productionTip = false

//全域樣式Vue.use(Element,{size:'small'})
Vue.use(Element)

import { postRequest } from "./utils/api";
import { putRequest } from "./utils/api";
import { getRequest } from "./utils/api";
import { deleteRequest } from "./utils/api";
import { downloadRequest } from "./utils/download";
import { initMenu } from "./utils/menus";
//插件
Vue.prototype.postRequest = postRequest;
Vue.prototype.putRequest = putRequest;
Vue.prototype.getRequest = getRequest;
Vue.prototype.deleteRequest = deleteRequest;
Vue.prototype.downloadRequest = downloadRequest;

//vue自帶路由導航守衛，(過濾器)
router.beforeEach((to, from, next) => {
    if (window.sessionStorage.getItem("tokenStr")) {
        initMenu(router, store);
        if (!window.sessionStorage.getItem("user")) {
            return getRequest('/admin/info').then(resp => {
                if (resp) {
                    alert(resp);
                    //存入用户信息，sessionStorage只能存字串
                    window.sessionStorage.setItem("user", JSON.stringify(resp));
                    store.commit('INIT_CURRENTAdmin', resp);
                    next();
                }
            });
        }
        next();
        //沒有權限
    } else {
        if (to.path == '/') {
            next();
        } else {
            //回到登入頁加上參數redirect，登入後會直接跳轉to.path
            next('/?redirect=' + to.path);
        }
    }
})

new Vue({
    el: '#app',
    router,
    store,
    components: { App },
    template: '<App/>'
})