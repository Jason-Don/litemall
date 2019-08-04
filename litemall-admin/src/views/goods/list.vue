<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.id" clearable class="filter-item" style="width: 200px;" placeholder="请输入课程编号"/>
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入课程名称"/>
      <el-select v-model="listQuery.brandId" clearable style="width: 200px" class="filter-item" placeholder="教师姓名">
        <el-option v-for="item in brandList" :key="item.value" :label="item.label" :value="item.value"/>
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin:goods:create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <!--<el-table-column type="expand">-->
      <!--<template slot-scope="props">-->
      <!--<el-form label-position="left" class="table-expand">-->
      <!--<el-form-item label="宣传画廊">-->
      <!--<img v-for="pic in props.row.gallery" :key="pic" :src="pic" class="gallery">-->
      <!--</el-form-item>-->
      <!--<el-form-item label="课程介绍">-->
      <!--<span>{{ props.row.brief }}</span>-->
      <!--</el-form-item>-->
      <!--&lt;!&ndash;<el-form-item label="课程单位">&ndash;&gt;-->
      <!--&lt;!&ndash;<span>{{ props.row.unit }}</span>&ndash;&gt;-->
      <!--&lt;!&ndash;</el-form-item>&ndash;&gt;-->
      <!--&lt;!&ndash;<el-form-item label="关键字">&ndash;&gt;-->
      <!--&lt;!&ndash;<span>{{ props.row.keywords }}</span>&ndash;&gt;-->
      <!--&lt;!&ndash;</el-form-item>&ndash;&gt;-->
      <!--&lt;!&ndash;<el-form-item label="类目ID">&ndash;&gt;-->
      <!--&lt;!&ndash;<span>{{ props.row.categoryId }}</span>&ndash;&gt;-->
      <!--&lt;!&ndash;</el-form-item>&ndash;&gt;-->
      <!--<el-form-item label="教师ID">-->
      <!--<span>{{ props.row.brandId }}</span>-->
      <!--</el-form-item>-->
      <!--</el-form>-->
      <!--</template>-->
      <!--</el-table-column>-->

      <el-table-column align="center" label="课程编号" prop="id"/>

      <el-table-column align="center" min-width="100" label="名称" prop="name"/>

      <el-table-column align="center" property="iconUrl" label="图片">
        <template slot-scope="scope">
          <img :src="scope.row.picUrl" width="40">
        </template>
      </el-table-column>

      <el-table-column align="center" property="iconUrl" label="分享图">
        <template slot-scope="scope">
          <img :src="scope.row.shareUrl" width="40">
        </template>
      </el-table-column>

      <el-table-column align="center" label="详情" prop="detail">
        <template slot-scope="scope">
          <el-dialog :visible.sync="detailDialogVisible" title="课程详情">
            <div v-html="goodsDetail"/>
          </el-dialog>
          <el-button type="primary" size="mini" @click="showDetail(scope.row.detail)">查看</el-button>
        </template>
      </el-table-column>

      <el-table-column align="center" label="原价" prop="counterPrice"/>

      <el-table-column align="center" label="当前价格" prop="retailPrice"/>

      <el-table-column align="center" label="课程开始时间" prop="starttime"/>

      <el-table-column align="center" label="课程结束时间" prop="endtime"/>

      <el-table-column align="center" label="课程状态" prop="status"/>

      <!--<el-table-column align="center" label="是否新品" prop="isNew">-->
      <!--<template slot-scope="scope">-->
      <!--<el-tag :type="scope.row.isNew ? 'success' : 'error' ">{{ scope.row.isNew ? '新品' : '非新品' }}</el-tag>-->
      <!--</template>-->
      <!--</el-table-column>-->

      <!--<el-table-column align="center" label="是否热品" prop="isHot">-->
      <!--<template slot-scope="scope">-->
      <!--<el-tag :type="scope.row.isHot ? 'success' : 'error' ">{{ scope.row.isHot ? '热品' : '非热品' }}</el-tag>-->
      <!--</template>-->
      <!--</el-table-column>-->

      <!--<el-table-column align="center" label="是否在售" prop="isOnSale">-->
      <!--<template slot-scope="scope">-->
      <!--<el-tag :type="scope.row.isOnSale ? 'success' : 'error' ">{{ scope.row.isOnSale ? '在售' : '未售' }}</el-tag>-->
      <!--</template>-->
      <!--</el-table-column>-->

      <el-table-column align="center" label="操作" width="300" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleOrderUsers(scope.row)">购买用户</el-button>
          <el-button v-permission="['POST /admin:goods:update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin:goods:delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibility-height="100" />
    </el-tooltip>

  </div>
</template>

<style>
  .table-expand {
    font-size: 0;
  }
  .table-expand label {
    width: 100px;
    color: #99a9bf;
  }
  .table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
  }
  .gallery {
    width: 80px;
    margin-right: 10px;
  }
</style>

<script>
import { listGoods, deleteGoods, listCatAndBrand } from '@/api/goods'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'GoodsList',
  components: { BackToTop, Pagination },
  data() {
    return {
      brandList: [],
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        goodsSn: undefined,
        name: undefined,
        brandId: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      goodsDetail: '',
      detailDialogVisible: false,
      downloadLoading: false
    }
  },
  created() {
    this.init()
    this.getList()
  },
  methods: {
    init: function() {
      listCatAndBrand().then(response => {
        this.brandList = response.data.data.brandList
      })
    },
    getList() {
      this.listLoading = true
      listGoods(this.listQuery).then(response => {
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
    handleCreate() {
      this.$router.push({ path: '/goods/create' })
    },
    handleOrderUsers(row) {
      this.$router.push({ path: '/user/user', query: { id: row.id, name: row.name }})
    },
    handleUpdate(row) {
      this.$router.push({ path: '/goods/edit', query: { id: row.id }})
    },
    showDetail(detail) {
      this.goodsDetail = detail
      this.detailDialogVisible = true
    },
    handleDelete(row) {
      deleteGoods(row).then(response => {
        this.$notify.success({
          title: '成功',
          message: '删除成功'
        })
        const index = this.list.indexOf(row)
        this.list.splice(index, 1)
      }).catch(response => {
        this.$notify.error({
          title: '失败',
          message: response.data.errmsg
        })
      })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['课程编号', '名称', '专柜价格', '当前价格', '是否新品', '是否热品', '是否在售', '首页主图', '宣传图片列表', '课程介绍', '详细介绍', '课程图片', '课程单位', '关键字', '类目ID', '教师ID']
        const filterVal = ['id', 'name', 'counterPrice', 'retailPrice', 'isNew', 'isHot', 'isOnSale', 'listPicUrl', 'gallery', 'brief', 'detail', 'picUrl', 'goodsUnit', 'keywords', 'categoryId', 'brandId']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '课程信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
