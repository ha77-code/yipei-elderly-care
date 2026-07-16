<template>

<div class="profile">


  <el-card>


    <div class="header">


      <el-avatar
        :size="80">
        {{profile.name}}
      </el-avatar>


      <div class="info">

        <h2>
          {{profile.name}}
        </h2>


        <p>
          ⭐ {{profile.score}} 分
        </p>


      </div>


    </div>



    <el-divider></el-divider>



    <el-descriptions
      title="基本信息"
      :column="2"
      border>


      <el-descriptions-item
        label="年龄">

        {{profile.age}}

      </el-descriptions-item>


      <el-descriptions-item
        label="服务区域">

        {{profile.area}}

      </el-descriptions-item>


      <el-descriptions-item
        label="服务类型">

        {{profile.service}}

      </el-descriptions-item>


      <el-descriptions-item
        label="服务次数">

        {{profile.count}} 次

      </el-descriptions-item>


    </el-descriptions>



    <el-divider></el-divider>



    <h3>
      个人介绍
    </h3>


    <p>
      {{profile.introduction}}
    </p>



    <el-divider></el-divider>



    <h3>
      用户评价
    </h3>


    <el-rate
      v-model="profile.score"
      disabled>
    </el-rate>


  </el-card>


</div>


</template>



<script>

export default {


data(){


return {


profile:{


name:"张三",

age:45,

score:4.8,

area:"上海徐汇",

service:"医院陪诊",

count:128,

introduction:
"具有多年老人陪诊经验，熟悉医院流程，耐心负责。"


}


}


}


}


</script>



<style scoped>


.profile{

padding:30px;

width:800px;

}



.header{

display:flex;

align-items:center;

}



.info{

margin-left:20px;

}



</style>