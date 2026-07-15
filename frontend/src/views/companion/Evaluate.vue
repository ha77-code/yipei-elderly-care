<template>

<div class="evaluate">


<h2>
  我的评价
</h2>


<el-card>


<h3>
综合评分
</h3>


<div class="score">

<el-rate
v-model="score"
disabled>
</el-rate>


<span>
{{score}} 分
</span>


</div>



<el-divider></el-divider>



<h3>
用户评价
</h3>



<el-card
v-for="item in evaluations"
:key="item.id"
class="comment">


<div>

<strong>
{{item.user}}
</strong>


<el-rate
v-model="item.score"
disabled>
</el-rate>


</div>



<p>

{{item.content}}

</p>


<small>

{{item.time}}

</small>


</el-card>



</el-card>


</div>

</template>



<script>

export default {


data(){


return {


score:4.8,


evaluations:[


{
id:1,

user:"王爷爷家属",

score:5,

content:"陪诊师非常耐心，整个流程帮助很多。",

time:"2026-07-20"

},


{
id:2,

user:"魏奶奶",

score:4,

content:"服务认真，沟通及时。",

time:"2026-07-21"

}


]


}


}


}


</script>



<style scoped>


.evaluate{

padding:30px;

width:700px;

}


.score{

display:flex;

align-items:center;

gap:20px;

}


.comment{

margin-top:15px;

}


</style>