<template>
  <div class="app-container">


    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.username" clearable class="filter-item" style="width: 200px;" placeholder="请输入用户名"/>
      <el-input v-model="listQuery.mobile" clearable class="filter-item" style="width: 200px;" placeholder="请输入手机号"/>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <el-tag v-if="goodsName!=null" size="medium" type="warning">购买课程名称：{{goodsName}}</el-tag>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
        <el-table-column align="center" width="100px" label="用户ID" prop="id" sortable/>

        <el-table-column align="center" label="学生姓名" prop="name"/>
        <el-table-column align="center" label="学校" prop="school"/>
        <el-table-column align="center" label="年级" prop="grade"/>

      <el-table-column align="center" label="手机号码" prop="mobile"/>

        <el-table-column align="center" label="性别" prop="gender">
          <template slot-scope="scope">
            <el-tag >{{ genderDic[scope.row.gender] }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column align="center" label="生日" prop="birthday"/>

        <!--<el-table-column align="center" label="用户等级" prop="userLevel">-->
          <!--<template slot-scope="scope">-->
            <!--<el-tag >{{ levelDic[scope.row.userLevel] }}</el-tag>-->
          <!--</template>-->
        <!--</el-table-column>-->


      <el-table-column align="center" label="会员等级" prop="vipName">
        <template slot-scope="scope">
          <el-tag :type="scope.row.vipName == null ? 'error' : 'success' ">{{ scope.row.vipName == null ? '普通会员': scope.row.vipName}}</el-tag>
        </template>
      </el-table-column>


      <el-table-column align="center" label="余额" prop="balance"/>

        <el-table-column align="center" label="状态" prop="status">
          <template slot-scope="scope">
            <el-tag>{{ statusDic[scope.row.status] }}</el-tag>
          </template>
        </el-table-column>

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

  </div>
</template>

<script>
import { fetchList } from '@/api/user'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'User',
  components: { Pagination },
  data() {
    return {
      goodsName : null,
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        goodsId: undefined,
        page: 1,
        limit: 20,
        username: undefined,
        mobile: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      downloadLoading: false,
      genderDic: ['未知', '男', '女'],
      levelDic: ['普通用户', 'VIP用户', '高级VIP用户'],
      statusDic: ['可用', '禁用', '注销']
    }
  },
  created() {
    this.init()
    this.getList()
  },
  methods: {
    init: function() {
      if (this.$route.query.id != null) {
        this.listQuery.goodsId = this.$route.query.id,
        this.goodsName = this.$route.query.name
      }
    },
    getList() {
      this.listLoading = true
      fetchList(this.listQuery).then(response => {
        this.list = response.data.data.list
        this.total = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['学生姓名',  '学校', '年级', '手机号码', '性别', '生日', '状态']
        const filterVal = ['name', 'school', 'grade', 'mobile', 'gender', 'birthday', 'status']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '用户信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
