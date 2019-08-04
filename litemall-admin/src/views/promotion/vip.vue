<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入会员名称"/>
      <!--<el-input v-model="listQuery.content" clearable class="filter-item" style="width: 200px;" placeholder="请输入会员内容"/>-->
      <el-button v-permission="['GET /admin/ad/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/ad/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <!--<el-table-column align="center" label="会员ID" prop="id" sortable/>-->

      <el-table-column align="center" label="会员等级" prop="level" sortable/>

      <el-table-column align="center" label="会员名称" prop="name"/>

      <el-table-column align="center" label="会员价格" prop="price"/>

      <el-table-column align="center" label="会员折扣" prop="discount"/>

      <el-table-column align="center" label="会员折扣名称" prop="discountName"/>


      <!--<el-table-column align="center" label="会员折扣名称" prop="discountName">-->
        <!--<template slot-scope="scope">-->
          <!--<img v-if="scope.row.url" :src="scope.row.url" width="80">-->
        <!--</template>-->
      <!--</el-table-column>-->

      <el-table-column align="center" label="会员有效天数" prop="validDays"/>

      <el-table-column align="center" label="添加时间" prop="addTime"/>

      <el-table-column align="center" label="更新时间" prop="updateTime"/>

      <!--<el-table-column align="center" label="是否启用" prop="enabled">-->
        <!--<template slot-scope="scope">-->
          <!--<el-tag :type="scope.row.enabled ? 'success' : 'error' ">{{ scope.row.enabled ? '启用' : '不启用' }}</el-tag>-->
        <!--</template>-->
      <!--</el-table-column>-->

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/ad/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin/ad/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="会员等级" prop="level">
          <el-input v-model="dataForm.level" :disabled="!isCreate"/>
        </el-form-item>
        <el-form-item label="会员名称" prop="name">
          <el-input v-model="dataForm.name" :disabled="!isCreate"/>
        </el-form-item>

        <el-form-item label="会员价格" prop="price">
          <el-input v-model="dataForm.price">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>

        <el-form-item label="会员折扣" prop="discount">
          <el-input v-model="dataForm.discount"/>
        </el-form-item>
        <el-form-item label="会员折扣名称" prop="discountName">
          <el-input v-model="dataForm.discountName"/>
        </el-form-item>

        <el-form-item label="会员有效天数" prop="validDays">
          <el-input v-model="dataForm.validDays">
            <template slot="append">天</template>
          </el-input>
        </el-form-item>

        <!--<el-form-item label="会员图片" prop="url">-->
          <!--<el-upload-->
            <!--:headers="headers"-->
            <!--:action="uploadPath"-->
            <!--:show-file-list="false"-->
            <!--:on-success="uploadUrl"-->
            <!--class="avatar-uploader"-->
            <!--accept=".jpg,.jpeg,.png,.gif">-->
            <!--<img v-if="dataForm.url" :src="dataForm.url" class="avatar">-->
            <!--<i v-else class="el-icon-plus avatar-uploader-icon"/>-->
          <!--</el-upload>-->
        <!--</el-form-item>-->


        <!--<el-form-item label="会员位置" prop="position">-->
          <!--<el-select v-model="dataForm.position" placeholder="请选择">-->
            <!--<el-option :value="1" label="首页"/>-->
          <!--</el-select>-->
        <!--</el-form-item>-->
        <!--<el-form-item label="活动链接" prop="link">-->
          <!--<el-input v-model="dataForm.link"/>-->
        <!--</el-form-item>-->
        <!--<el-form-item label="是否启用" prop="enabled">-->
          <!--<el-select v-model="dataForm.enabled" placeholder="请选择">-->
            <!--<el-option :value="true" label="启用"/>-->
            <!--<el-option :value="false" label="不启用"/>-->
          <!--</el-select>-->
        <!--</el-form-item>-->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #20a0ff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}
.avatar {
  width: 145px;
  height: 145px;
  display: block;
}
</style>

<script>
import { listAd, createAd, updateAd, deleteAd } from '@/api/vip'
import { uploadPath } from '@/api/storage'
import { getToken } from '@/utils/auth'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  // name: 'Ad',
  components: { Pagination },
  data() {
    return {
      isCreate: false,
      uploadPath,
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        name: undefined,
        sort: 'add_time',
        order: 'desc'
      },
      dataForm: {
        id: undefined,
        name: undefined,
        content: undefined,
        url: undefined,
        link: undefined,
        position: 1,
        enabled: true
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        level: [
          { required: true, message: '会员等级不能为空', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '会员名称不能为空', trigger: 'blur' }
        ],
        price: [
          { required: true, message: '会员价格不能为空', trigger: 'blur' }
        ],
        discount: [{ required: true, message: '会员折扣不能为空', trigger: 'blur' }]
        ,
        discountName: [{ required: true, message: '会员折扣名称不能为空', trigger: 'blur' }
        ]
        ,
        validDays: [{ required: true, message: '会员有效天数不能为空', trigger: 'blur' }
        ]
      },
      downloadLoading: false
    }
  },
  computed: {
    headers() {
      return {
        'X-Litemall-Admin-Token': getToken()
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listAd(this.listQuery)
        .then(response => {
          this.list = response.data.data.list
          this.total = response.data.data.total
          this.listLoading = false
        })
        .catch(() => {
          this.list = []
          this.total = 0
          this.listLoading = false
        })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    resetForm() {
      this.dataForm = {
        id: undefined,
        name: undefined,
        content: undefined,
        url: undefined,
        link: undefined,
        position: 1,
        enabled: true
      }
    },
    handleCreate() {
      this.isCreate = true
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    uploadUrl: function(response) {
      this.dataForm.url = response.data.url
    },
    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          createAd(this.dataForm)
            .then(response => {
              this.list.unshift(response.data.data)
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '创建成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
    },
    handleUpdate(row) {
      this.isCreate = false
      this.dataForm = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          updateAd(this.dataForm)
            .then(() => {
              for (const v of this.list) {
                if (v.id === this.dataForm.id) {
                  const index = this.list.indexOf(v)
                  this.list.splice(index, 1, this.dataForm)
                  break
                }
              }
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '更新会员成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
    },
    handleDelete(row) {
      deleteAd(row)
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '删除成功'
          })
          const index = this.list.indexOf(row)
          this.list.splice(index, 1)
        })
        .catch(response => {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = [
          '会员ID',
          '会员等级',
          '会员名称',
          '会员价格',
          '会员折扣',
          '会员折扣名称',
          '有效天数',
          '添加时间',
          '更新时间'
        ]
        const filterVal = [
          'id',
          'level',
          'name',
          'price',
          'discount',
          'discountName',
          'validDays',
          'addTime',
          'updateTime'
        ]
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '会员信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
