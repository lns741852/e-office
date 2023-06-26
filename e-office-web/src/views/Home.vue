<template>
  <div>
    <el-container>
      <el-header class="homeHeader">
        <div class="title">線上班公系統</div>
        <div>
          <el-button
            icon="el-icon-bell"
            type="text"
            style="margin-right: 10px; color: black"
            size="normal"
            @click="goChat"
          ></el-button>
          <!--用戶訊息-->
          <el-dropdown class="userInfo" @command="commandHandler">
            <span class="el-dropdown-link"
              >{{ user.name }}<i><img :src="user.userFace" /></i
            ></span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="userinfo">個人資料</el-dropdown-item>
              <el-dropdown-item command="setting">設置中心</el-dropdown-item>
              <el-dropdown-item command="logout">登出</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      <el-container>
        <!--側邊攔-->
        <el-aside width="200px">
          <!--父循環，router自動對應路由-->
          <el-menu router unique-opened>
            <!--
              1.使用hidden屬性判斷使否循環
              2.直接獲取routes()返回值
              3.index必須為字串         
            -->
            <el-submenu
              :index="index + ''"
              v-for="(item, index) in routes"
              :v-if="!item.hidden"
              :key="index"
            >
              <template slot="title">
                <i
                  style="color: #1accff; margin-right: 5px"
                  :class="item.iconCls"
                >
                </i>
                <span>{{ item.name }}</span>
              </template>
              <!--子循環-->
              <el-menu-item
                :index="children.path"
                v-for="(children, indexj) in item.children"
                :key="indexj"
                >{{ children.name }}
              </el-menu-item>
            </el-submenu>
          </el-menu>
        </el-aside>
        <!--主頁-->
        <el-main>
          <!--麵包削-->
          <el-breadcrumb
            separator-class="el-icon-arrow-right"
            v-if="this.$router.currentRoute.path != '/home'"
          >
            <el-breadcrumb-item :to="{ path: '/home' }"
              >首頁</el-breadcrumb-item
            >
            <el-breadcrumb-item>{{
              this.$router.currentRoute.name
            }}</el-breadcrumb-item>
          </el-breadcrumb>
          <!--歡迎訊息-->
          <div
            class="homeWelcome"
            v-if="this.$router.currentRoute.path == '/home'"
          >
            歡迎來到本站！
          </div>
          <!--路由影響模塊，css影響所有內容-->
          <router-view class="homeRouterView" />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      //字串轉換成對象
      user: JSON.parse(window.sessionStorage.getItem("user")),
    };
  },
  //頁面刷新不會重複獲取
  computed: {
    routes() {
      return this.$store.state.routes;
    },
  },
  methods: {
    goChat() {
      this.$router.push("/chat");
    },
    commandHandler(cmd) {
      if (cmd == "logout") {
        this.$confirm("確定要登出?", "提示", {
          confirmButtonText: "確定",
          cancelButtonText: "取消",
          type: "warning",
        })
          .then(() => {
            //登出
            this.postRequest("/logout");
            //刪除用戶訊息
            window.sessionStorage.removeItem("user");
            window.sessionStorage.removeItem("tokenStr");
            //清空用戶菜單
            this.$store.commit("initRoutes", []);
            //跳轉
            this.$router.replace("/");
          })
          .catch(() => {
            this.$message({
              type: "info",
              message: "已取消操作",
            });
          });
      }
    },
  },
};
</script>

<style>
.homeHeader {
  background-color: #409eff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0px 15px;
  box-sizing: border-box;
}

.homeHeader .title {
  font-size: 30px;
  font-family: 华文行楷;
  color: #ffffff;
}
/**點選用戶頭像顯示下拉選單 */
.homeHeader .userInfo {
  cursor: pointer;
}

.homeWelcome {
  text-align: center;
  font-size: 30px;
  font-family: 华文行楷;
  color: #409eff;
  padding-top: 50px;
}

.homeRouterView {
  margin-top: 10px;
}

.el-dropdown-link img {
  width: 48px;
  height: 48px;
  border-radius: 24px;
  margin-left: 8px;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
}
</style>
