<template>
  <div>
    <!--搜索框-->
    <div>
      <div style="display: flex;justify-content: space-between">
        <div>
          <el-input prefix-icon="el-icon-search"
                    placeholder="請輸入員工姓名..."
                    v-model="empName"
                    @keydown.enter.native="initEmps"
                    clearable
                    @clear="initEmps"
                    :disabled="showAdvanceSearchVisible"
                    style="width: 300px;margin-right: 10px"></el-input>
          <el-button type="primary"
                     icon="el-icon-search"
                     @click="initEmps"
                     :disabled="showAdvanceSearchVisible">搜索
          </el-button>
          <el-button type="primary"
                     @click="showAdvanceSearchView">
            <i :class="showAdvanceSearchVisible?'fa fa-angle-doubleup':'fa fa-angle-double-down'"
               aria-hidden="true"></i>
            高級搜索
          </el-button>
        </div>
        <div>
          <!--
            文件上傳
            1.show-file-list關閉文件列表展示
            2.disabled正在導入時禁用
            3.headers夾帶token
            4.action對應後端接口
            -->
          <el-upload
            action="/system/basic/employee/import"
            :before-upload="beforeUpload"
            :headers="headers"
            :on-success="onSuccess"
            :on-error="onError"
            :disabled="importDataDisabled"
            style="display: inline-flex;margin-right: 8px"
            :show-file-list="false">
            <el-button :disabled="importDataDisabled" type="success"
                       :icon="importDataBtnIcon">
              {{importDataBtnText}}
            </el-button>
          </el-upload>
          <el-button type="success" @click="exportData" icon="el-icondownload">
            導出數據
          </el-button>
          <el-button type="primary" icon="el-icon-plus" @click="showAddEmpView">
            添加用戶
          </el-button>
        </div>
      </div>
      <!--進階查詢-->
      <transition name="slide-fade">
        <div v-show="showAdvanceSearchVisible"
             style="border: 1px solid #409eff;border-radius: 5px;box-sizing:border-box;padding: 5px;margin: 10px 0px">
          <el-row>
            <el-col :span="5">
              政治面貌:
              <el-select v-model="searchValue.politicId"
                         placeholder="政治面貌" size="mini"
                         style="width: 130px">
                <el-option
                  v-for="item in politicsstatus"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="4">
              民族:
              <el-select v-model="searchValue.nationId"
                         placeholder="民族" size="mini" style="width: 130px">
                <el-option
                  v-for="item in nations"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="4">
              職位:
              <el-select v-model="searchValue.posId"
                         placeholder="職位" size="mini" style="width: 130px">
                <el-option
                  v-for="item in positions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="4">
              职称:
              <el-select v-model="searchValue.jobLevelId"
                         placeholder="职称" size="mini"
                         style="width: 130px">
                <el-option
                  v-for="item in joblevels"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="7">
              聘用形式:
              <el-radio-group v-model="searchValue.engageForm">
                <el-radio label="劳动合同">劳动合同</el-radio>
                <el-radio label="劳务合同">劳务合同</el-radio>
              </el-radio-group>
            </el-col>
          </el-row>
          <el-row style="margin-top: 10px">
            <el-col :span="5">
              所屬部門:
              <!--彈出框-->
              <el-popover
                placement="right"
                title="請選擇部門"
                width="200"
                trigger="manual"
                v-model="popVisible2">
                <!--:data為資料，:props定義屬性-->
                <el-tree :data="allDeps" :props="defaultProps"
                         default-expand-all
                         @node-click="searchHandleNodeClick">
                </el-tree>
                <!--自訂輸入框-->
                <div slot="reference"
                     style="width: 130px;display: inline-flex;
                        font-size: 13px;border: 1px solid #dedede;height: 24px;border-radius:
                        5px;cursor: pointer;align-items: center;padding-left: 8px;box-sizing: border-box;
                        margin-left: 3px"
                     @click="showDepView2">
                  {{inputDepName}}
                </div>
              </el-popover>
            </el-col>
            <el-col :span="10">
              入职日期:
              <el-date-picker
                v-model="searchValue.beginDateScope"
                size="mini"
                type="daterange"
                value-format="yyyy-MM-dd"
                unlink-panels
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期">
              </el-date-picker>
            </el-col>
            <el-col :span="5" :offset="4">
              <el-button>取消</el-button>
              <el-button type="primary"
                         @click="initEmps('advanced')">搜索
              </el-button>
            </el-col>
          </el-row>
        </div>
      </transition>
    </div>
    <!--表單-->
    <div style="margin-top: 10px">
      <el-table
        :data="emps"
        stripe
        border
        v-loading="loading"
        element-loading-text="正在加載..."
        element-loading-spinner="el-icon-loading"
        element-loading-background="rgba(0, 0, 0, 0.8)"
        style="width: 100%">
        <el-table-column
          type="selection"
          width="55">
        </el-table-column>
        <!--fixed為固定不被滑動-->
        <el-table-column
          prop="name"
          label="姓名"
          fixed
          align="left"
          width="90">
        </el-table-column>
        <el-table-column
          prop="workID"
          label="工號"
          align="left"
          width="85">
        </el-table-column>
        <el-table-column
          prop="gender"
          label="性别"
          width="85">
        </el-table-column>
        <el-table-column
          prop="birthday"
          align="left"
          width="95"
          label="出生日期">
        </el-table-column>
        <el-table-column
          prop="idCard"
          align="left"
          width="150"
          label="身份证号码">
        </el-table-column>
        <el-table-column
          prop="wedlock"
          width="70"
          label="婚姻状况">
        </el-table-column>
        <el-table-column
          prop="nation.name"
          width="50"
          label="民族">
        </el-table-column>
        <el-table-column
          prop="nativePlace"
          width="80"
          label="籍贯">
        </el-table-column>
        <el-table-column
          prop="politicsStatus.name"
          label="政治面貌">
        </el-table-column>
        <el-table-column
          prop="email"
          align="left"
          width="180"
          label="电子邮件">
        </el-table-column>
        <el-table-column
          prop="phone"
          align="left"
          width="100"
          label="电话号码">
        </el-table-column>
        <el-table-column
          prop="address"
          align="left"
          width="220"
          label="联系地址">
        </el-table-column>
        <el-table-column
          prop="department.name"
          align="left"
          width="100"
          label="所属部门">
        </el-table-column>
        <el-table-column
          prop="position.name"
          width="100"
          label="職位">
        </el-table-column>
        <el-table-column
          prop="joblevel.name"
          width="100"
          label="职称">
        </el-table-column>
        <el-table-column
          prop="engageForm"
          align="left"
          width="100"
          label="聘用形式">
        </el-table-column>
        <el-table-column
          prop="tiptopDegree"
          label="最高学历">
        </el-table-column>
        <el-table-column
          prop="school"
          align="left"
          width="150"
          label="毕业院校">
        </el-table-column>
        <el-table-column
          prop="specialty"
          align="left"
          width="150"
          label="专业">
        </el-table-column>
        <el-table-column
          prop="workState"
          align="left"
          width="70"
          label="在职状态">
        </el-table-column>
        <el-table-column
          prop="beginDate"
          align="left"
          width="95"
          label="入职日期">
        </el-table-column>
        <el-table-column
          prop="conversionTime"
          align="left"
          width="95"
          label="转正日期">
        </el-table-column>
        <el-table-column
          prop="beginContract"
          align="left"
          width="95"
          label="合同起始日期">
        </el-table-column>
        <el-table-column
          prop="endContract"
          align="left"
          width="95"
          label="合同截止日期">
        </el-table-column>
        <el-table-column
          align="left"
          width="100"
          label="合同期限">
          <template slot-scope="scope">
            <el-tag>{{scope.row.contractTerm}}</el-tag>
            年
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          width="200"
          label="操作">
          <template slot-scope="scope">
            <el-button style="padding: 3px" size="mini"
                       @click="showEditEmpView(scope.row)">编辑
            </el-button>
            <el-button style="padding: 3px" size="mini"
                       type="primary">查看高级资料
            </el-button>
            <el-button style="padding: 3px" size="mini"
                       type="danger" @click="deleteEmp(scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!--分頁-->
      <div style="display: flex;justify-content: flex-end">
        <el-pagination
          background
          @current-change="currentChange"
          @size-change="sizeChange"
          layout="sizes,prev,pager,next,jumper,->,total,slot" :total="total"></el-pagination>
      </div>
    </div>
    <!--添加-->
    <el-dialog
      :title="title"
      :visible.sync="dialogVisible"
      width="80%">
      <div>
        <el-form :model="emp" :rules="rules" ref="empForm">
          <!--第一行-->
          <el-row>
            <el-col :span="6">
              <el-form-item label="姓名：" prop="name">
                <el-input size="mini" style="width: 150px" v-model="emp.name" placeholder="请输入员工姓名"
                          prefix-icon="el-icon-edit"></el-input>
              </el-form-item>
            </el-col>
            <!--radio-->
            <el-col :span="5">
              <el-form-item label="性别：" prop="gender">
                <el-radio-group v-model="emp.gender">
                  <el-radio label="男">男</el-radio>
                  <el-radio label="女">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <!--日期-->
            <el-col :span="6">
              <el-form-item label="出生日期：" prop="birthday">
                <el-date-picker
                  v-model="emp.birthday"
                  size="mini"
                  type="date"
                  value-format="yyyy-MM-dd"
                  style="width: 150px"
                  placeholder="出生日期">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <!--下拉選單-->
            <el-col :span="7">
              <el-form-item label="政治面貌：" prop="politicId">
                <el-select v-model="emp.politicId"
                           placeholder="政治面貌" size="mini" style="width: 200px">
                  <el-option
                    v-for="item in politicsstatus"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <!--第二行-->
          <el-row>
             <!--下拉選單-->
            <el-col :span="6">
              <el-form-item label="民族：" prop="nationId">
                <el-select v-model="emp.nationId"
                           placeholder="民族" size="mini" style="width: 150px">
                  <el-option
                    v-for="item in nations"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="籍贯：" prop="nativePlace">
                <el-input size="mini" style="width: 120px" v-model="emp.nativePlace" placeholder="请输入籍贯"
                          prefix-icon="el-icon-edit"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="电子邮箱：" prop="email">
                <el-input size="mini" style="width: 150px" v-model="emp.email" placeholder="请输入电子邮箱"
                          prefix-icon="el-icon-message"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="7">
              <el-form-item label="联系地址：" prop="address">
                <el-input size="mini" style="width: 200px" v-model="emp.address" placeholder="请输入联系地址"
                          prefix-icon="el-icon-edit"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="6">
              <el-form-item label="職位：" prop="posId">
                <el-select v-model="emp.posId" placeholder="職位"
                           size="mini" style="width: 150px">
                  <el-option
                    v-for="item in positions"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="职称：" prop="jobLevelId">
                <el-select v-model="emp.jobLevelId"
                           placeholder="职称" size="mini" style="width: 150px">
                  <el-option
                    v-for="item in joblevels"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="所属部门：" prop="departmentId">
                <!--slot="reference"觸發事件-->
                <el-popover
                  placement="right"
                  title="请选择部门"
                  width="200"
                  trigger="manual"
                  v-model="popVisible">
                  <!--樹形列表-->
                  <el-tree :data="allDeps"
                           :props="defaultProps" default-expand-all
                           @node-click="handleNodeClick"></el-tree>
                  <!--自訂輸入框-->
                  <div slot="reference"
                       style="width: 150px;display: inline-flex;
                              font-size: 13px;border: 1px solid #dedede;height: 24px;border-radius:
                              5px;cursor: pointer;align-items: center;padding-left: 8px;box-sizing: border-box"
                       @click="showDepView">
                    {{inputDepName}}
                  </div>
                </el-popover>
              </el-form-item>
            </el-col>
            <el-col :span="7">
              <el-form-item label="电话号码：" prop="phone">
                <el-input size="mini" style="width: 200px" v-model="emp.phone" placeholder="请输入电话号码"
                          prefix-icon="el-icon-phone"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="6">
              <el-form-item label="工号：" prop="workID">
                <el-input size="mini" style="width: 150px" v-model="emp.workID" placeholder="请输入工号"
                          prefix-icon="el-icon-edit" disabled>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="学历：" prop="tiptopDegree">
                <el-select v-model="emp.tiptopDegree"
                           placeholder="学历" size="mini" style="width: 150px">
                  <el-option
                    v-for="item in tiptopDegrees"
                    :key="item"
                    :label="item"
                    :value="item">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="毕业院校：" prop="school">
                <el-input size="mini" style="width: 150px" v-model="emp.school" placeholder="请输入毕业院校"
                          prefix-icon="el-icon-edit"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="7">
              <el-form-item label="专业名称：" prop="specialty">
                <el-input size="mini" style="width: 200px" v-model="emp.specialty" placeholder="请输入专业名称"
                          prefix-icon="el-icon-edit"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="6">
              <el-form-item label="入职日期：" prop="beginDate">
                <el-date-picker
                  v-model="emp.beginDate"
                  size="mini"
                  type="date"
                  value-format="yyyy-MM-dd"
                  style="width: 130px"
                  placeholder="入职日期">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="转正日期："
                            prop="conversionTime">
                <el-date-picker
                  v-model="emp.conversionTime"
                  size="mini"
                  type="date"
                  value-format="yyyy-MM-dd"
                  style="width: 130px"
                  placeholder="转正日期">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="合同起始日期："
                            prop="beginContract">
                <el-date-picker
                  v-model="emp.beginContract"
                  size="mini"
                  type="date"
                  value-format="yyyy-MM-dd"
                  style="width: 130px"
                  placeholder="合同起始日期">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="合同截止日期："
                            prop="endContract">
                <el-date-picker
                  v-model="emp.endContract"
                  size="mini"
                  type="date"
                  value-format="yyyy-MM-dd"
                  style="width: 130px"
                  placeholder="合同截止日期">
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="8">
              <el-form-item label="身份证号码：" prop="idCard">
                <el-input size="mini" style="width: 180px" v-model="emp.idCard" placeholder="请输入身份证号码"
                          prefix-icon="el-icon-edit"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="聘用形式：" prop="engageForm">
                <el-radio-group v-model="emp.engageForm">
                  <el-radio label="劳动合同">劳动合同</el-radio>
                  <el-radio label="劳务合同">劳务合同</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="婚姻状况：" prop="wedlock">
                <el-radio-group v-model="emp.wedlock">
                  <el-radio label="已婚">已婚</el-radio>
                  <el-radio label="未婚">未婚</el-radio>
                  <el-radio label="离异">离异</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doAddEmp">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    name: "EmpBasic",
    data() {
      return {
        searchValue: {
          politicId: null,
          nationId: null,
          jobLevelId: null,
          posId: null,
          engageForm: '',
          departmentId: null,
          beginDateScope: null
        },
        showAdvanceSearchVisible: false,
        headers: {
          Authorization: window.sessionStorage.getItem('tokenStr')
        },
        importDataBtnText: '导入数据',
        importDataBtnIcon: 'el-icon-upload2',
        importDataDisabled: false,
        title: '',
        emps: [],
        loading: false,
        total: 0,
        currentPage: 1,
        size: 10,
        empName: '',
        dialogVisible: false,
        nations: [],
        joblevels: [],
        politicsstatus: [],
        positions: [],
        popVisible: false,
        popVisible2: false,
        allDeps: [],
        defaultProps: {
          children: 'children',
          label: 'name'
        },
        inputDepName: '',
        //基本上不會變動的值就可以寫死
        tiptopDegrees: ['本科', '大專', '碩士', '博士', '高中', '初中', '小学', '其他'],
        emp: {
          name: '',
          gender: '',
          birthday: '',
          idCard: '',
          wedlock: '',
          nationId: null,
          nativePlace: '',
          politicId: null,
          email: '',
          phone: '',
          address: '',
          departmentId: null,
          jobLevelId: null,
          posId: null,
          engageForm: '',
          tiptopDegree: '',
          specialty: '',
          school: '',
          beginDate: '',
          workID: '',
          contractTerm: null,
          conversionTime: '',
          notWorkDate: '',
          beginContract: '',
          endContract: '',
          workAge: null
        },
        //數據教驗
        rules: {
          name: [{
            required: true, message: '请输入员工姓名', trigger:
              'blur'
          }],
          gender: [{
            required: true, message: '请输入性别', trigger:
              'blur'
          }],
          birthday: [{
            required: true, message: '请输入出生日期',
            trigger: 'blur'
          }],
          idCard: [{
            required: true, message: '请输入身份证号码', trigger:
              'blur'
          }, {
            pattern: /(^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$)|(^[1-9]\d{5}\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{2}$)/,
            message: '身份证号码格式不正确',
            trigger: 'blur'
          }],
          wedlock: [{
            required: true, message: '请输入婚姻状况', trigger:
              'blur'
          }],
          nationId: [{
            required: true, message: '请输入民族', trigger:
              'blur'
          }],
          nativePlace: [{
            required: true, message: '请输入籍贯', trigger:
              'blur'
          }],
          politicId: [{
            required: true, message: '请输入政治面貌',
            trigger: 'blur'
          }],
          email: [{
            required: true, message: '请输入邮箱地址', trigger:
              'blur'
          }, {
            type: 'email',
            message: '邮箱格式不正确',
            trigger: 'blur'
          }],
          phone: [{
            required: true, message: '请输入电话号码', trigger:
              'blur'
          }],
          address: [{
            required: true, message: '请输入员工地址', trigger:
              'blur'
          }],
          departmentId: [{
            required: true, message: '请输入部门名称',
            trigger: 'blur'
          }],
          jobLevelId: [{
            required: true, message: '请输入职称', trigger:
              'blur'
          }],
          posId: [{
            required: true, message: '请输入職位', trigger:
              'blur'
          }],
          engageForm: [{
            required: true, message: '请输入聘用形式',
            trigger: 'blur'
          }],
          tiptopDegree: [{
            required: true, message: '请输入学历',
            trigger: 'blur'
          }],
          specialty: [{
            required: true, message: '请输入专业', trigger:
              'blur'
          }],
          school: [{
            required: true, message: '请输入毕业院校', trigger:
              'blur'
          }],
          beginDate: [{
            required: true, message: '请输入入职日期',
            trigger: 'blur'
          }],
          workState: [{
            required: true, message: '请输入工作状态',
            trigger: 'blur'
          }],
          workID: [{
            required: true, message: '请输入工号', trigger:
              'blur'
          }],
          contractTerm: [{
            required: true, message: '请输入合同期限',
            trigger: 'blur'
          }],
          conversionTime: [{
            required: true, message: '请输入转正日期',
            trigger: 'blur'
          }],
          notWorkDate: [{
            required: true, message: '请输入离职日期',
            trigger: 'blur'
          }],
          beginContract: [{
            required: true, message: '请输入合同起始日期',
            trigger: 'blur'
          }],
          endContract: [{
            required: true, message: '请输入合同结束日期',
            trigger: 'blur'
          }],
          workAge: [{
            required: true, message: '请输入工龄', trigger:
              'blur'
          }],
        }
      }
    },
    mounted() {
      this.initEmps();
      this.initData();
      this.initPositions();
    },
    methods: {
      showAdvanceSearchView() {
        this.showAdvanceSearchVisible = !this.showAdvanceSearchVisible;
        this.searchValue = {
          politicId: null,
          nationId: null,
          jobLevelId: null,
          posId: null,
          engageForm: '',
          departmentId: null,
          beginDateScope: null
        }
      },
      //進階查詢_所屬部門_選中
      searchHandleNodeClick(data) {
        this.inputDepName = data.name;
        this.searchValue.departmentId = data.id;
        this.popVisible2 = !this.popVisible2;
      },
      //文件導入失敗樣式
      onError() {
        this.importDataBtnText = '导入数据';
        this.importDataBtnIcon = 'el-icon-upload2';
        this.importDataDisabled = false;
      },
      //文件導入成功樣式
      onSuccess() {
        this.importDataBtnText = '導入數據';
        this.importDataBtnIcon = 'el-icon-upload2';
        this.importDataDisabled = false;
        this.initEmps();
      },
      //文件導入時樣式
      beforeUpload() {
        this.importDataBtnText = '正在導入';
        this.importDataBtnIcon = 'el-icon-loading';
        this.importDataDisabled = true;
      },
      //導出數據
      exportData() {
        this.downloadRequest('/system/basic/employee/export')
      },
      //編輯
      showEditEmpView(data) {
        this.title = '編輯員工';
        this.emp = data;
        this.inputDepName = data.department.name;
        this.dialogVisible = true;
      },
      //刪除
      deleteEmp(data) {
        this.$confirm('確定刪除數據【' + data.name + '】?', '提示', {
          confirmButtonText: '確定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.deleteRequest('/system/basic/employee/' + data.id).then(resp => {
            if (resp) {
              this.initEmps();
            }
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },
      //添加 + 修改
      doAddEmp() {
        //修改
        if (this.emp.id) {
          this.$refs['empForm'].validate(valid => {
            if (valid) {
              this.putRequest('/system/basic/employee/', this.emp).then(resp => {
                if (resp) {
                  this.dialogVisible = false;
                  this.initEmps();
                }
              })
            }
          })
          //添加
        } else {
          this.$refs['empForm'].validate(valid => {
            if (valid) {
              this.postRequest('/system/basic/employee/', this.emp).then(resp => {
                if (resp) {
                  this.dialogVisible = false;
                  this.initEmps();
                }
              })
            }
          })
        }
      },
      //關閉_新增_部門樹型列表
      handleNodeClick(data) {
        this.inputDepName = data.name;
        this.emp.departmentId = data.id;
        this.popVisible = !this.popVisible;
      },
      //新增_部門彈出框
      showDepView() {
        this.popVisible = !this.popVisible;
      },
      showDepView2() {
        this.popVisible2 = !this.popVisible2;
      },
      //職位查詢
      initPositions() {
        this.getRequest('/system/basic/employee/position').then(resp => {
          if (resp) {
            this.positions = resp;
          }
        })
      },
      //存放不會變動的值
      initData() {
        //沒有值
        if (!window.sessionStorage.getItem("nations")) {
          this.getRequest('/system/basic/employee/nation').then(resp => {
            if (resp) {
              this.nations = resp;
              window.sessionStorage.setItem("nations", JSON.stringify(resp));
            }
          })
          //有值
        } else {
          this.nations = JSON.parse(window.sessionStorage.getItem("nations"))
        }
        if (!window.sessionStorage.getItem("joblevels")) {
          this.getRequest('/system/basic/employee/joblevel').then(resp => {
            if (resp) {
              this.joblevels = resp;
              window.sessionStorage.setItem("joblevels", JSON.stringify(resp));
            }
          })
        } else {
          this.joblevels = JSON.parse(window.sessionStorage.getItem("joblevels"))
        }
        if (!window.sessionStorage.getItem("politicsstatus")) {
          this.getRequest('/system/basic/employee/politicsStatus').then(resp => {
            if (resp) {
              this.politicsstatus = resp;
              window.sessionStorage.setItem("politicsstatus", JSON.stringify(resp));
            }
          })
        } else {
          this.politicsstatus = JSON.parse(window.sessionStorage.getItem("politicsstatus"))
        }
        if (!window.sessionStorage.getItem("allDeps")) {
          this.getRequest('/system/basic/employee/deps').then(resp => {
            if (resp) {
              this.allDeps = resp;
              window.sessionStorage.setItem("allDeps", JSON.stringify(resp));
            }
          })
        } else {
          this.allDeps =
            JSON.parse(window.sessionStorage.getItem("allDeps"))
        }
      },
      //新增時獲取最大工號
      getMaxWorkID() {
        this.getRequest('/system/basic/employee/maxWorkID').then(resp => {
          if (resp) {
            this.emp.workID = resp.obj;
          }
        })
      },
      //顯示添加彈出框
      showAddEmpView() {
        this.title = '添加員工';
        this.emp = {
          name: '',
          gender: '',
          birthday: '',
          idCard: '',
          wedlock: '',
          nationId: null,
          nativePlace: '',
          politicId: null,
          email: '',
          phone: '',
          address: '',
          departmentId: null,
          jobLevelId: null,
          posId: null,
          engageForm: '',
          tiptopDegree: '',
          specialty: '',
          school: '',
          beginDate: '',
          workID: '',
          contractTerm: null,
          conversionTime: '',
          notWorkDate: '',
          beginContract: '',
          endContract: '',
          workAge: null
        }
        this.inputDepName = '';
        this.dialogVisible = true;
        this.getMaxWorkID();
        this.initPositions();
      },
      //刷新分頁每頁筆數
      sizeChange(currentSize) {
        this.size = currentSize;
        this.initEmps();
      },
      //分頁更新
      currentChange(currentPage) {
        this.currentPage = currentPage;
        this.initEmps();
      },
      //表單
      initEmps(type) {
        //加載數據
        this.loading = true;
        let url = '/system/basic/employee/?currentPage=' + this.currentPage + '&size=' + this.size;
        if (type && type == 'advanced') {
          if (this.searchValue.politicId) {
            url += '&politicId=' + this.searchValue.politicId;
          }
          if (this.searchValue.nationId) {
            url += '&nationId=' + this.searchValue.nationId;
          }
          if (this.searchValue.jobLevelId) {
            url += '&jobLevelId=' + this.searchValue.jobLevelId;
          }
          if (this.searchValue.posId) {
            url += '&posId=' + this.searchValue.posId
          }
          if (this.searchValue.engageForm) {
            url += '&engageForm=' + this.searchValue.engageForm;
          }
          if (this.searchValue.departmentId) {
            url += '&departmentId=' + this.searchValue.departmentId;
          }
          if (this.searchValue.beginDateScope) {
            url += '&beginDateScope=' + this.searchValue.beginDateScope;
          }
        } else {
          url += '&name=' + this.empName;
        }

        this.getRequest(url).then(resp => {
          //加載完成
          this.loading = false;
          if (resp) {
            this.emps = resp.data;
            this.total = resp.total;
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
