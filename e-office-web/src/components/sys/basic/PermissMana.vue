<template>
  <div>
    <!--添加-->
    <div class="permissManaTool">
      <el-input size="small" placeholder="請輸入角色英文" v-model="role.name">
        <!--自訂前墜-->
        <template slot="prepend">ROLE_</template>
      </el-input>
      <el-input
        size="small"
        v-model="role.nameZh"
        placeholder="請輸入角色中文名"
        @keydown.enter.native="doAddRole"
      ></el-input>
      <el-button
        type="primary"
        size="small"
        icon="el-icon-plus"
        @click="doAddRole"
        >添加角色
      </el-button>
    </div>
    <!--角色摺疊面板-->
    <div class="permissManaMain">
      <el-collapse v-model="activeName" accordion @change="change">
        <el-collapse-item
          :title="r.nameZh"
          :name="r.id"
          v-for="(r, index) in roles"
          :key="index"
        >
          <el-card class="box-card">
            <div slot="header" class="clearfix">
              <span>可訪問資源</span>
              <el-button
                style="float: right; padding: 3px 0; color: #ff0000"
                icon="el-icon-delete"
                type="text"
                @click="doDeleteRole(r)"
              >
              </el-button>
            </div>
            <!--
              1.樹狀權限菜單
              2.props為response節點屬性名稱和子節點屬性名稱
              3.default-checked-keys為默認勾選內容
              4.加上key防止資料混亂
            -->
            <div>
              <el-tree
                show-checkbox
                node-key="id"
                ref="tree"
                :key="index"
                :default-checked-keys="selectedMenus"
                :data="allMenus"
                :props="defaultProps"
              >
              </el-tree>
              <div style="display: flex; justify-content: flex-end">
                <el-button @click="cancelUpdate">取消修改</el-button>
                <el-button type="primary" @click="doUpdate(r.id, index)"
                  >確認修改
                </el-button>
              </div>
            </div>
          </el-card>
        </el-collapse-item>
      </el-collapse>
    </div>
  </div>
</template>
<script>
export default {
  name: "PermissMana",
  data() {
    return {
      activeName: "2",
      role: {
        name: "",
        nameZh: "",
      },
      roles: [],
      allMenus: [],
      selectedMenus: [],
      defaultProps: {
        children: "children",
        label: "name",
      },
    };
  },
  mounted() {
    this.initRoles();
  },
  methods: {
    //查詢角色
    initRoles() {
      this.getRequest("/system/basic/permission/").then((resp) => {
        if (resp) {
          this.roles = resp;
        }
      });
    },
    //初始化權限菜單
    initAllMenus() {
      this.getRequest("/system/basic/permission/menus").then((resp) => {
        this.allMenus = resp;
      });
    },
    //查詢擁有權限
    initSelectedMenus(rid) {
      this.getRequest("/system/basic/permission/mid/" + rid).then((resp) => {
        this.selectedMenus = resp;
      });
    },
    //摺疊面板切換事件，參數取自:name="r.id"
    change(rid) {
      if (rid) {
        this.initAllMenus();
        this.initSelectedMenus(rid);
      }
    },
    //關閉摺疊面板
    cancelUpdate() {
      this.activeName = -1;
    },
    //更新角色
    doUpdate(rid, index) {
      //獲取展開角色
      let tree = this.$refs.tree[index];
      //獲取選中結點，加上true半勾選選項不會被選中
      let selectedKeys = tree.getCheckedKeys(true);
      let url = "/system/basic/permission/?rid=" + rid;
      selectedKeys.forEach((key) => {
        url += "&mids=" + key;
      });
      this.putRequest(url).then((resp) => {
        if (resp) {
          //關閉摺疊面板
          this.activeName = -1;
        }
      });
    },
    //刪除
    doDeleteRole(role) {
      this.$confirm("確定要刪除【" + role.nameZh + "】角色? ", "提示", {
        confirmButtonText: "確定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.deleteRequest("/system/basic/permission/role/" + role.id).then(
            (resp) => {
              if (resp) {
                this.initRoles();
              }
            }
          );
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除",
          });
        });
    },
    //新增
    doAddRole() {
      if (this.role.name && this.role.nameZh) {
        this.postRequest("/system/basic/permission/", this.role).then(
          (resp) => {
            if (resp) {
              this.name = "";
              this.nameZh = "";
              this.initRoles();
            }
          }
        );
      } else {
        this.$message.error("字段不能為空！");
      }
    },


  },
};
</script>

<style>
.permissManaTool {
  /**內容顯示在一行 */
  display: flex;
  justify-content: flex-start;
}

.permissManaTool .el-input {
  width: 300px;
  margin-right: 6px;
}

.permissManaMain {
  margin-top: 10px;
  width: 700px;
}
</style>
