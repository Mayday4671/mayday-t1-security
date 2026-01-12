<template>
  <div class="dashboard">
    <a-row :gutter="16">
      <a-col :span="6">
        <a-card>
          <a-statistic title="用户总数" :value="stats.userCount" prefix="👥" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic title="角色总数" :value="stats.roleCount" prefix="🎭" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic title="部门总数" :value="stats.deptCount" prefix="🏢" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic title="菜单总数" :value="stats.menuCount" prefix="📋" />
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" style="margin-top: 16px">
      <a-col :span="16">
        <a-card title="📊 数据权限演示">
          <a-alert 
            style="margin-bottom: 16px"
            type="info"
            show-icon
          >
            <template #message>
              当前数据权限: <strong>{{ dataScopeInfo }}</strong>
              | 可见用户: <strong>{{ userList.length }}</strong> 人
            </template>
          </a-alert>

          <a-table 
            :columns="columns" 
            :dataSource="userList" 
            :loading="loading"
            rowKey="userId"
            :pagination="{ pageSize: 5 }"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="record.status === '正常' ? 'green' : 'red'">
                  {{ record.status }}
                </a-tag>
              </template>
              <template v-if="column.key === 'deptNames'">
                <a-tag v-for="dept in record.deptNames" :key="dept" color="purple" style="margin: 2px">
                  {{ dept }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>

      <a-col :span="8">
        <a-card title="📝 快速入门">
          <a-timeline>
            <a-timeline-item color="green">登录系统</a-timeline-item>
            <a-timeline-item color="blue">查看侧边菜单</a-timeline-item>
            <a-timeline-item color="blue">操作用户管理</a-timeline-item>
            <a-timeline-item color="gray">更多功能开发中...</a-timeline-item>
          </a-timeline>
          
          <a-divider />
          
          <a-descriptions title="测试账号" :column="1" size="small">
            <a-descriptions-item label="管理员">admin / 123456</a-descriptions-item>
            <a-descriptions-item label="经理">zhangsan / 123456</a-descriptions-item>
            <a-descriptions-item label="员工">lisi / 123456</a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const loading = ref(false)
const userList = ref<any[]>([])
const dataScopeInfo = ref('未知')

const stats = ref({
  userCount: 3,
  roleCount: 3,
  deptCount: 5,
  menuCount: 15
})

const columns = [
  { title: '用户ID', dataIndex: 'userId', key: 'userId', width: 80 },
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '状态', dataIndex: 'status', key: 'status', width: 80 },
  { title: '部门', dataIndex: 'deptNames', key: 'deptNames' },
]

onMounted(async () => {
  await loadUserList()
})

const loadUserList = async () => {
  loading.value = true
  try {
    const res = await request.get('/demo/userList')
    userList.value = res.userList || []
    dataScopeInfo.value = res.dataScope || '未知'
  } catch (e) {
    console.error('加载数据失败:', e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.dashboard {
  padding: 8px;
}
</style>
