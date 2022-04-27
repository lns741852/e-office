/**
 * Menu封裝，將字串轉換成對象
 */
import { getRequest } from "./api";

export const initMenu = (router, store) => {
    //判斷是否有拿過路由訊息
    if (store.state.routes.length > 0) {
        return;
    }
    getRequest("/system/cfg/menu").then(data => {
        if (data) {
            //格式化router
            let fmtRoutes = formatRoutes(data);
            //Vue路由自帶方法_將List添加到router
            router.addRoutes(fmtRoutes);
            //將數據存入vuex
            store.commit('initRoutes', fmtRoutes);
            //連接websocket
            store.dispatch('connect');
        }
    })
}

export const formatRoutes = (routes) => {
    let fmRoutes = [];
    routes.forEach(router => {
        //宣告
        let {
            path,
            component,
            name,
            meta,
            iconCls,
            children,
        } = router;
        //遞迴
        if (children && children instanceof Array) {
            children = formatRoutes(children);
        }

        //封裝路由
        let fmRouter = {
            path: path,
            name: name,
            meta: meta,
            iconCls: iconCls,
            children: children,
            //異步加載組件
            component(resolve) {
                //將component字串封裝成組件
                if (component.startsWith("Home")) {
                    //require調用組件，等於 import ('../views/Home.vue')
                    require(['../views/' + component + '.vue'], resolve);
                } else if (component.startsWith("Emp")) {
                    require(['../views/emp/' + component + '.vue'], resolve);
                } else if (component.startsWith("Per")) {
                    require(['../views/per/' + component + '.vue'], resolve);
                } else if (component.startsWith("Sal")) {
                    require(['../views/sal/' + component + '.vue'], resolve);
                } else if (component.startsWith("Sta")) {
                    require(['../views/sta/' + component + '.vue'], resolve);
                } else if (component.startsWith("Sys")) {
                    require(['../views/sys/' + component + '.vue'], resolve);
                }
            }
        }
        fmRoutes.push(fmRouter);
    })
    return fmRoutes;
}