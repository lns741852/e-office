<template>
  <div>
     <!--添加-->
    <div>
      <el-input size="small" v-model="jl.name" style="width: 300px" prefix-icon="el-icon-plus"
                placeholder="職稱管理..."></el-input>
      <el-select v-model="jl.titleLevel" palaceholder="職稱等級" size="small" style="margin-left: 6px;margin-right: 6px">
        <el-option v-for="item in titleLevels"
                   :key="item"
                   :label="item"
                   :value="item">
        </el-option>
      </el-select>
      <el-button type="primary" icon="el-icon-plus" size="small" @click="addJl">添加</el-button>
    </div>
    <!--列表-->
    <div style="margin-top: 10px">
      <el-table :data="jls"
                border
                stripe
                size="small"
                @selection-change="handleSelectionChange"
                style="width: 80%">
        <el-table-column
          type="selection"
          width="55">
        </el-table-column>
        <el-table-column
          prop="id"
          label="編號"
          width="55">
        </el-table-column>
        <el-table-column
          prop="name"
          label="職稱名稱"
          width="150">
        </el-table-column>
        <el-table-column
          prop="titleLevel"
          label="職稱等級"
          width="150">
        </el-table-column>
        <el-table-column
          prop="createDate"
          label="創建時間"
          width="150">
        </el-table-column>
        <!--狀態查詢-->
        <el-table-column prop="enabled"
                         label="是否啟用"
                         width="150">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.enabled" type="success">已啟用</el-tag>
            <el-tag v-else type="danger">未啟用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button size="small" @click="showEditView(scope.row)">編輯</el-button>
            <el-button size="small" type="danger" @click="deleteHandler(scope.row)">刪除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button type="danger" size="small" style="margin-top: 10px" :disabled="multipleSelection.length == 0"
                 @click="deleteMany">批量刪除
      </el-button>
    </div>
    <!--編輯-->
    <el-dialog
      title="編輯職稱"
      :visible.sync="dialogVisible"
      width="30%">
      <div>
        <table>
          <tr>
            <td>
              <el-tag>職稱名</el-tag>
            </td>
            <td>
              <el-input v-model="updateJl.name" size="small"></el-input>
            </td>
          </tr>
          <tr>
            <td>
              <el-tag>職稱級別</el-tag>
            </td>
            <td>
              <el-select v-model="updateJl.titleLevel"
                         placeholder="職稱等級" size="small"
                         style="margin-left: 6px;margin-right:6px">
                <el-option
                  v-for="item in titleLevels"
                  :key="item"
                  :label="item"
                  :value="item">
                </el-option>
              </el-select>
            </td>
          </tr>
          <tr>
            <td>
              <el-tag>是否啟用</el-tag>
            </td>
            <td>
              <el-switch
                v-model="updateJl.enabled"
                activecolor="#13ce66"
                inactive-color="#ff4949"
                active-text="啟用"
                inactive-text="禁用">
              </el-switch>
            </td>
          </tr>
        </table>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible = false">取消</el-button>
        <el-button size="small" type="primary" @click="doUpdate">確定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "JoblevelMana",
    data() {
      return {
        jls: [],
        jl: {
          name: '',
          titleLevel: ''
        },
        updateJl: {
          name: '',
          titleLevel: '',
          enabled: false
        },
        dialogVisible: false,
        titleLevels: [
          '正高級',
          '副高級',
          '中級',
          '初級',
          '員級',
        ],
        multipleSelection: []
      }
    },
    mounted() {
      this.initJls();
    },
    methods: {
      //批量删除
      deleteMany() {
        this.$confirm('確定要刪除【' + this.multipleSelection.length + '】條紀錄？', '提示', {
          confirmButtonText: '確定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          //拚串
          let ids = '?';
          this.multipleSelection.forEach(item => {
            ids += 'ids=' + item.id + '&';
          })
          this.deleteRequest('/system/basic/joblevel/' + ids).then(resp => {
            if (resp) {

              this.initJls();
            }
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
      },
      //將選中值以陣列儲存，每次選中都會更新
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      //修改職稱
      doUpdate() {
        this.putRequest('/system/basic/joblevel/', this.updateJl).then(resp => {
          if (resp) {
            this.initJls();
            this.updateJl.name = '';
            this.updateJl.titleLevel = '';
            this.dialogVisible = false;
          }
        })
      },
      //顯示編輯框
      showEditView(data) {
        //因為頁面關閉不刷因，防止傳入值被更改
        Object.assign(this.updateJl, data);
        this.dialogVisible = true;
      },
      deleteHandler(data) {
        //删除
        this.$confirm('確定要刪除【' + data.name + '】職稱?', '提示', {
          confirmButtonText: '確定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.deleteRequest('/system/basic/joblevel/' +
            data.id).then(resp => {
            if (resp) {
              this.initJls();
            }
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },
      addJl() {
        //添加職稱
        if (this.jl.name && this.jl.titleLevel) {
          this.postRequest('/system/basic/joblevel/', this.jl).then(resp => {
            if (resp) {
              this.initJls();
              this.jl.name = '';
              this.jl.titleLevel = '';
            }
          })
        } else {
          this.$message.error("字段不能為空！");
        }

      },
      initJls() {
        this.getRequest('/system/basic/joblevel/').then(resp => {
          if (resp) {
            this.jls = resp;
            /*this.jl = {
              name: '',
              titleLevel: ''
            }*/
          }
        })
      }
    }
  }
</script>

<style>

</style>
