<template>
  <div>
    <!--新增-->
    <div>
      <el-input
        size="small"
        class="addPosInput"
        placeholder="添加職位..."
        prefix-icon="el-icon-plus"
        @keydown.enter.native="addPosition"
        v-model="pos.name">
      </el-input>
      <el-button icon="el-icon-plus" size="small" type="primary" @click="addPosition">添加</el-button>
    </div>
     <!--列表-->
    <div class="posManaMain">
      <el-table
        :data="positions"
        border
        stripe
        size="small"
        @selection-change="handleSelectionChange"
        style="width: 70%">
        <!--多選框-->
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="編號" width="55"></el-table-column>
        <el-table-column prop="name" label="職位名稱" width="120"></el-table-column>
        <el-table-column prop="createDate" label="創建時間"></el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button size="mini" @click="showEditView(scope.$index,scope.row)">編輯</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.$index,scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!--批量刪除按鈕，當為勾選時禁用-->
      <el-button type="danger" size="small" style="margin-top: 8px" :disabled="multipleSelection.length == 0"
                 @click="deleteMany">批量删除
      </el-button>
    </div>
    <!--編輯框-->
    <el-dialog title="編輯職位" :visible.sync="dialogVisible" width="30%">
      <div>
        <el-tag>職位名稱</el-tag>
        <el-input v-model="updatePos.name" size="small" class="updatePosInput"></el-input>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible = false">取消</el-button>
        <el-button size="small" type="primary" @click="doUpdate">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "PosMana",
    data() {
      return {
        pos: {
          name: ''
        },
        dialogVisible: false,
        updatePos: {
          name: ''
        },
        multipleSelection: [],
        positions: []
      }
    },
    mounted() {
      this.initPositions();
    },
    methods: {
      //批量刪除
      deleteMany() {
        this.$confirm('是否刪除【' + this.multipleSelection.length
          + '】條紀錄?', '提示', {
          confirmButtonText: '確認',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let ids = '?';
          this.multipleSelection.forEach(item => {
            ids += 'ids=' + item.id + '&';
          })
          this.deleteRequest('/system/basic/pos/' + ids).then(resp => {
            if (resp) {
              this.initPositions();
            }
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },
      //獲取批量勾選選項
      handleSelectionChange(val){
        this.multipleSelection = val;
      },
      //新增
      addPosition() {
        if (this.pos.name) {
          this.postRequest('/system/basic/pos/', this.pos).then(resp => {
            if (resp) {
              this.initPositions();
              this.pos.name = '';
            }
          })
        } else {
          this.$message.error("職位名稱不可為空！");
        }
      },
      //顯示編輯框
      showEditView(index, data) {
        //數據指派，防止修改updatePos時data的值跟著改變
        Object.assign(this.updatePos, data);
        this.updatePos.createDate = '';
        this.dialogVisible = true;
      },
      //更新
      doUpdate() {
        this.putRequest('/system/basic/pos/', this.updatePos).then(resp => {
          if (resp) {
            this.initPositions();
            this.updatePos.name = '';
            this.dialogVisible = false;
          }
        })
      },
      //刪除
      handleDelete(index, data) {
        this.$confirm('是否刪除【' + data.name + '】職位?', '提示', {
          confirmButtonText: '確認',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.deleteRequest('/system/basic/pos/' + data.id).then(resp => {
            if (resp) {
              this.initPositions();
            }
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },
      //查詢列表
      initPositions() {
        this.getRequest('/system/basic/pos/').then(resp => {
          if (resp) {
            this.positions = resp;
          }
        })
      }
    }
  }
</script>

<style scoped>
  .addPosInput {
    width: 300px;
    margin-right: 8px;
  }

  .posManaMain {
    margin-top: 10px;
  }

  .updatePosInput {
    width: 200px;
    margin-left: 8px;
  }
</style>
