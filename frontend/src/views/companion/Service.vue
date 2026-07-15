<template>

<div class="service">


<h2>
  服务状态
</h2>



<el-card>


<h3>
订单编号：YP002
</h3>


<p>
老人：魏奶奶
</p>


<p>
服务内容：检查陪同
</p>


<el-divider></el-divider>



<h3>
服务流程
</h3>



<el-steps
:active="active"
finish-status="success"
direction="vertical">


<el-step
title="已接单"
description="陪诊师已经接受订单">
</el-step>


<el-step
title="到达医院"
description="陪诊师已到达服务地点">
</el-step>


<el-step
title="陪同检查"
description="正在陪同老人完成检查">
</el-step>


<el-step
title="服务完成"
description="等待确认">
</el-step>


</el-steps>



<el-divider></el-divider>



<h3>
服务记录
</h3>


<el-timeline>


<el-timeline-item
timestamp="08:00">

到达医院

</el-timeline-item>



<el-timeline-item
timestamp="08:20">

完成挂号

</el-timeline-item>



<el-timeline-item
timestamp="10:30">

陪同检查

</el-timeline-item>



</el-timeline>



<el-button
type="primary"
@click="complete">


确认完成服务


</el-button>



</el-card>


</div>

</template>



<script>

export default {


data(){


return {


active:2


}


},



methods:{


complete(){


this.active=4


this.$message.success(
"服务已完成"
)


}


}


}


</script>



<style scoped>


.service{

padding:30px;

width:700px;

}


</style>