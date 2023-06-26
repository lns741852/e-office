<template>
  <div>
    <!--搜索-->
    <div style="margin-top: 10px;display: flex;justify-content: center">
      <el-input placeholder="通過用戶名搜索..." prefix-icon="el-icon-search" v-model="keywords"
                style="width: 400px;margin-right: 10px" @keydown.enter.native="doSearch"></el-input>
      <el-button type="primary" icon="wl-icon-search" @click="doSearch">搜索</el-button>
    </div>
    <!--內容-->
    <div class="admin-container">
      <!--卡片迴圈-->
      <el-card class="admin-card" v-for="(admin,index) in admins" :key="index">
        <div slot="header" class="clearfix">
          <span>{{admin.name}}</span>
          <el-button style="float: right;padding: 3px 0;color: #ff0000" type="text" icon="el-icon-delete"
                     @click="deleteAdmin(admin)"></el-button>
        </div>
        <div>
          <!--頭像-->
          <div class="img-container">
            <img :src="admin.userFace" :alt="admin.name" :title="admin.name" class="userface-img">
          </div>
        </div>
         <!--用戶訊息-->
        <div class="userinfo-container">
          <div>用戶名:{{admin.name}}</div>
          <div>手機號碼:{{admin.phone}}</div>
          <div>電話號碼:{{admin.telephone}}</div>
          <div>地址:{{admin.address}}</div>
          <div>用戶狀態:
            <el-switch
              v-model="admin.enabled"
              active-color="#13ce66"
              inactive-color="#ff4949"
              @change="enabledChange(admin)"
              active-text="啟用"
              inactive-text="禁用">
            </el-switch>
          </div>
           <!--用戶角色-->
          <div>用戶角色:
            <el-tag type="success" style="margin-right: 4px" v-for="(role,indexj) in admin.roles" :key="indexj">
              {{role.nameZh}}
            </el-tag>
            <!--彈出框-->
            <el-popover
              placement="right"
              title="角色列表"
              @show="showPop(admin)"
              @hide="hidePop(admin)"
              width="200"
              trigger="click">
              <el-select v-model="selectedRoles" multiple placeholder="請選擇...">
                <el-option
                  v-for="(r,indexk) in allRoles"
                  :key="indexk"
                  :label="r.nameZh"
                  :value="r.id"></el-option>
              </el-select>
              <el-button slot="reference" icon="el-icon-more" type="text"></el-button>
            </el-popover>
            <!--<el-button icon="el-icon-more" type="text"></el-button>-->
          </div>
           <!--備註-->
          <div>備註:{{admin.remark}}</div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
  export default {
    name: "SysAdmin",
    data() {
      return {
        keywords: '',
        admins: [],
        allRoles: [],
        selectedRoles: []
      }
    },
    mounted() {
      this.initAdmins();
    },
    methods: {
      //刪除
      deleteAdmin(admin) {
        this.$confirm('確認是否刪除【' + admin.name + '】用戶?', '提示', {
          confirmButtonText: '確定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.deleteRequest('/system/admin/' + admin.id).then(resp => {
            this.initAdmins();
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },
      //搜索
      doSearch() {
        this.initAdmins();
      },
      //關閉彈出框
      hidePop(admin) {
        let roles = [];
        Object.assign(roles, admin.roles);
        let flag = false;
        //判斷值是否有調整
        if (roles.length != this.selectedRoles.length) {
          flag = true;
        } else {
          //刪除相同的值
          for (let i = 0; i < roles.length; i++) {
            let role = roles[i];
            for (let j = 0; j < this.selectedRoles.length; j++) {
              let sr = this.selectedRoles[j];
              if (role.id == sr) {
                roles.splice(i, 1);
                //刪除後修改roles長度
                i--;
                break;
              }
            }
          }
          //相同的值不等於0
          if (roles.length != 0) {
            flag = true;
          }
        }
        if (flag) {
          //拚串選中id
          let url = '/system/admin/role?adminId=' + admin.id;
          this.selectedRoles.forEach(sr => {
            url += '&rids=' + sr;
          })
          this.putRequest(url).then(resp => {
            if (resp) {
              this.initAdmins();
            }
          })
        }
      },
      //彈出框，加入擁有角色
      showPop(admin) {
        this.initAllRoles();
        let roles = admin.roles;
        this.selectedRoles = [];
        roles.forEach(r => {
          this.selectedRoles.push(r.id);
        })
      },
      //所有腳色
      initAllRoles() {
        this.getRequest('/system/admin/roles').then(resp => {
          if (resp) {
            this.allRoles = resp;
          }
        })
      },
      //更新用戶狀態
      enabledChange(admin) {
        this.putRequest('/system/admin/', admin).then(resp => {
          if (resp) {
            this.initAdmins();
          }
        })
      },
      //列表
      initAdmins() {
        this.getRequest('/system/admin/?keywords=' + this.keywords).then(resp => {
          if (resp) {
            this.admins = resp;
          }
        })
      }
    }
  }
</script>

<style>
  .userinfo-container div {
    font-size: 12px;
    color: #409eff;
  }

  .userinfo-container {
    margin-top: 20px;
  }

  .img-container {
    width: 100%;
    display: flex;
    justify-content: center;
  }

  .userface-img {
    width: 72px;
    height: 72px;
    border-radius: 72px;
  }

  .admin-container {
    margin-top: 10px;
    display: flex;
    justify-content: space-around;
    flex-wrap: wrap;
  }

  .admin-card {
    width: 300px;
    margin-bottom: 20px;
  }
</style>
