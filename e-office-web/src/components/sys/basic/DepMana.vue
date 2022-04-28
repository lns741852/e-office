<template>
  <!--查詢-->
  <div style="width: 500px">
    <el-input
      placeholder="請輸入部門名稱..."
      prefix-icon="el-icon-search"
      v-model="filterText"
    >
    </el-input>
    <!--樹形資料-->
    <el-tree
      :data="deps"
      :props="defaultProps"
      :filter-node-method="filterNode"
      ref="tree"
    >
      <!--
        node前端節點內容:filter-node-method="filterNode"
        data後端資料內容:data="deps"
      -->
      <div
        class="custom-tree-node"
        style="display: flex; justify-content: space-between; width: 100%"
        slot-scope="{ node, data }"
      >
        <span>{{ node.label }}</span>
        <span>
          <el-button
            type="primary"
            size="mini"
            class="depBtn"
            @click="() => showAddDepView(data)"
          >
            添加部門
          </el-button>
          <el-button
            type="danger"
            size="mini"
            class="depBtn"
            @click="() => deleteDep(data)"
          >
            刪除部門
          </el-button>
        </span>
      </div>
    </el-tree>
    <!--添加-->
    <el-dialog title="添加部門" :visible.sync="dialogVisible" width="30%">
      <div>
        <table>
          <tr>
            <td>
              <el-tag>上級部門</el-tag>
            </td>
            <td>{{ pname }}</td>
          </tr>
          <tr>
            <td>
              <el-tag>部門名稱</el-tag>
            </td>
            <td>
              <el-input
                v-model="dep.name"
                placeholder="請輸入部門名稱..."
              ></el-input>
            </td>
          </tr>
        </table>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doAddDep">確定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "DepMana",
  data() {
    return {
      filterText: "",
      deps: [],
      defaultProps: {
        children: "children",
        label: "name",
      },
      dep: {
        name: "",
        parentId: -1,
      },
      pname: "",
      dialogVisible: false,
    };
  },
  watch: {
    //監聽搜索框
    filterText(val) {
      //:filter-node-method
      this.$refs.tree.filter(val);
    },
  },
  mounted() {
    this.initDeps();
  },
  methods: {
    //清空添加框數據
    initDep() {
      this.dep = {
        name: "",
        parentId: -1,
      };
      this.pname = "";
    },
    //列表
    initDeps() {
      this.getRequest("/system/basic/department/").then((resp) => {
        if (resp) {
          this.deps = resp;
        }
      });
    },
    //刪除前端處理
    removeDepFromDeps(p, deps, id) {
       //循環所有同級節點
      for (let i = 0; i < deps.length; i++) {
        let d = deps[i];
        //找到刪除對象
        if (d.id == id) {
          //刪除當前索引
          deps.splice(i, 1);      
          //不是父部門
          if (deps.length == 0) {
            p.parent = false;
          }
          return;
        } else {
          //d=父部門
          this.removeDepFromDeps(d, d.children, id);
        }
      }
    },
    //添加前端處理，deps目前結構，dep資料庫返回資料
    addDep2Deps(deps, dep) {
      //循環所有同級節點
      for (let i = 0; i < deps.length; i++) {
        let d = deps[i];
        //判斷當前節點是否為返回資料的parentId
        if (d.id == dep.parentId) {
          //加入子節點
          d.children = d.children.concat(dep);
          //是父部門
          if (d.children.length > 0) {
            d.parent = true;
          }
          return;
        } else {
          //往下一級找
          this.addDep2Deps(d.children, dep);
        }
      }
    },
    //添加
    doAddDep() {
      this.postRequest("/system/basic/department/", this.dep).then((resp) => {
        if (resp) {
          this.addDep2Deps(this.deps, resp.obj);
          this.dialogVisible = false;
          this.initDep();
        }
      });
    },
    //顯示添加框
    showAddDepView(data) {
      this.pname = data.name;
      this.dep.parentId = data.id;
      this.dialogVisible = true;
    },
     //刪除
    deleteDep(data) {
      if (data.parent) {
        this.$message.error("父部門刪除失敗！");
      } else {
        this.$confirm("確定要刪除【" + data.name + "】部門?", "提示", {
          confirmButtonText: "確定",
          cancelButtonText: "取消",
          type: "warning",
        })
          .then(() => {
            this.deleteRequest("/system/basic/department/" + data.id).then(
              (resp) => {
                if (resp) {
                  //最上級部門沒有父部門所以傳null，this.deps為所有部門
                  this.removeDepFromDeps(null, this.deps, data.id);
                }
              }
            );
          })
          .catch(() => {
            this.$message({
              type: "info",
              message: "已取消刪除",
            });
          });
      }
    },
    //展開節點，執行多次，value:查詢參數，data:每個節點的資料
    filterNode(value, data) {
      //返回所有數據
      if (!value) return true;
      //若節點中包含查詢參數既顯示
      return data.name.indexOf(value) !== -1;
    },
  },
};
</script>

<style>
.depBtn {
  padding: 2px;
}
</style>
