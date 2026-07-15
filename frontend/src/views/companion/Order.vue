<template>

<div class="order">


<h2>
  我的订单
</h2>


<el-table
:data="orders"
border
style="width:100%">


<el-table-column
prop="id"
label="订单编号">
</el-table-column>


<el-table-column
prop="elder"
label="老人">
</el-table-column>


<el-table-column
prop="service"
label="服务内容">
</el-table-column>


<el-table-column
prop="time"
label="服务时间">
</el-table-column>



<el-table-column
prop="status"
label="订单状态">


<template slot-scope="scope">


<el-tag
v-if="scope.row.status==='待接单'"
type="warning">

{{scope.row.status}}

</el-tag>


<el-tag
v-else-if="scope.row.status==='服务中'"
type="primary">

{{scope.row.status}}

</el-tag>


<el-tag
v-else
type="success">

{{scope.row.status}}

</el-tag>


</template>


</el-table-column>




<el-table-column
label="操作">


<template slot-scope="scope">


<el-button
v-if="scope.row.status==='待接单'"
type="primary"
size="mini"
@click="accept(scope.row)">


接单


</el-button>



<el-button
size="mini"
@click="detail(scope.row)">


查看


</el-button>


</template>


</el-table-column>



</el-table>



</div>

</template>



<script>

export default {


data(){


return {


orders:[


{
id:"YP001",
elder:"王爷爷",
service:"瑞金医院陪诊",
time:"2026-07-20 08:00",
status:"待接单"
},


{
id:"YP002",
elder:"魏奶奶",
service:"检查陪同",
time:"2026-07-21 09:30",
status:"服务中"
},


{
id:"YP003",
elder:"张阿姨",
service:"老人陪伴",
time:"2026-07-22 14:00",
status:"已完成"
}


]


}


},



methods:{


accept(row){


row.status="服务中"


this.$message.success(
"接单成功"
)


},


detail(row){


this.$message.info(
"查看订单："+row.id
)


}


}


}


</script>



<style scoped>


.order{

padding:30px;

}


h2{

margin-bottom:25px;

}


</style>