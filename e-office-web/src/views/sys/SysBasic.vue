<template>
  <div>
    <el-tabs v-model="activeName" type="card">
      <el-tab-pane label="部門管理" name="depmana">
        <DepMana></DepMana>
      </el-tab-pane>
      <el-tab-pane label="職位管理" name="posmana">
        <PosMana></PosMana>
      </el-tab-pane>
      <el-tab-pane label="職稱管理" name="JoblevelMana">
        <JoblevelMana>
        </JoblevelMana>
      </el-tab-pane>
      <el-tab-pane label="獎懲規則" name="ecmana">
        <EcMana></EcMana>
      </el-tab-pane>
      <el-tab-pane label="權限組" name="permissmana">
        <PermissMana>
        </PermissMana>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
  //導入組件_1
  import DepMana from '../../components/sys/basic/DepMana'
  import EcMana from '../../components/sys/basic/EcMana'
  import JoblevelMana from '../../components/sys/basic/JoblevelMana'
  import PermissMana from '../../components/sys/basic/PermissMana'
  import PosMana from '../../components/sys/basic/PosMana'

  export default {
    name: "SysBasic",
    data(){
      return{
        activeName: 'posmana'
      }
    },
    //導入組件_2
    components:{
      DepMana,
      EcMana,
      JoblevelMana,
      PermissMana,
      PosMana
    }
  }
</script>

<style scoped>

</style>
