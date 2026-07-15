<template>

<div class="list">


<h2>
  陪诊师列表
</h2>



<el-row :gutter="20">


<el-col
  :span="8"
  v-for="item in companions"
  :key="item.name">


<el-card>


<div class="avatar">

<el-avatar
:size="70">

{{item.name}}

</el-avatar>

</div>



<h3>

{{item.name}}

</h3>



<p>
⭐ {{item.score}}
</p>


<p>
📍 {{item.area}}
</p>


<p>
服务：
{{item.service}}
</p>


<p>
已服务：
{{item.count}} 次
</p>



<el-button
type="primary"
size="mini"
@click="view(item)">

查看资料

</el-button>



</el-card>


</el-col>


</el-row>



</div>

</template>



<script>

export default {


data(){


return {


companions:[


{
name:"张三",
score:4.8,
area:"上海徐汇",
service:"医院陪诊",
count:128
},


{
name:"李女士",
score:4.9,
area:"上海浦东",
service:"老人陪伴",
count:96
},


{
name:"王师傅",
score:4.7,
area:"上海静安",
service:"检查陪同",
count:75
}


]


}


},



methods:{


view(item){


this.$router.push('/companion/profile')


}


}



}


</script>



<style scoped>


.list{

padding:30px;

}



.el-card{

text-align:center;

margin-bottom:20px;

}



.avatar{

margin-bottom:15px;

}


</style>