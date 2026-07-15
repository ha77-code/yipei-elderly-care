<template>
  <div class="apply">
    <div class="apply-card">
      <div class="apply-header">
        <span class="apply-header-icon">📝</span>
        <div>
          <h2 class="apply-title">陪诊师入驻申请</h2>
          <p class="apply-subtitle">填写以下信息，提交后等待管理员审核</p>
        </div>
      </div>

      <el-form
        :model="form"
        label-width="100px"
        class="apply-form">

        <el-form-item label="姓名">
          <el-input
            v-model="form.name"
            placeholder="请输入真实姓名">
          </el-input>
        </el-form-item>

        <el-form-item label="年龄">
          <el-input
            v-model="form.age"
            placeholder="请输入年龄">
          </el-input>
        </el-form-item>

        <el-form-item label="手机号">
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号">
          </el-input>
        </el-form-item>

        <el-form-item label="服务区域">
          <el-select
            v-model="form.area"
            placeholder="请选择服务区域"
            style="width:100%">
            <el-option
              label="上海徐汇"
              value="上海徐汇">
            </el-option>
            <el-option
              label="上海浦东"
              value="上海浦东">
            </el-option>
            <el-option
              label="上海静安"
              value="上海静安">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="服务类型">
          <el-checkbox-group
            v-model="form.services">
            <el-checkbox
              label="医院陪诊">
            </el-checkbox>
            <el-checkbox
              label="检查陪同">
            </el-checkbox>
            <el-checkbox
              label="老人陪伴">
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="个人介绍">
          <el-input
            type="textarea"
            :rows="4"
            v-model="form.introduction"
            placeholder="请介绍一下您的相关经验和特长">
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            @click="submit"
            class="submit-btn">
            <i class="el-icon-s-promotion"></i> 提交申请
          </el-button>
          <el-button @click="reset" class="reset-btn">
            <i class="el-icon-refresh"></i> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        name: "",
        age: "",
        phone: "",
        area: "",
        services: [],
        introduction: ""
      }
    }
  },
  methods: {
    submit() {
      this.$message.success("申请提交成功，请等待审核")
    },
    reset() {
      this.form = {
        name: "",
        age: "",
        phone: "",
        area: "",
        services: [],
        introduction: ""
      }
      this.$message.info("表单已重置")
    }
  }
}
</script>

<style scoped>
.apply {
  max-width: 680px;
  margin: 0 auto;
}

.apply-card {
  background: #ffffff;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  border: 1px solid #f1f5f9;
}

.apply-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f1f5f9;
}

.apply-header-icon {
  font-size: 40px;
  flex-shrink: 0;
}

.apply-title {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.apply-subtitle {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
}

.apply-form {
  margin-top: 8px;
}

.submit-btn {
  min-width: 120px;
  font-size: 15px !important;
  padding: 12px 28px !important;
}

.submit-btn i {
  margin-right: 4px;
}

.reset-btn {
  font-size: 14px;
  padding: 12px 24px;
}

.reset-btn i {
  margin-right: 4px;
}
</style>