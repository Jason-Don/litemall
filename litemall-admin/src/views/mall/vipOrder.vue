<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.userId" clearable class="filter-item" style="width: 200px;" placeholder="请输入用户ID"/>
      <el-input v-model="listQuery.orderSn" clearable class="filter-item" style="width: 200px;" placeholder="请输入订单编号"/>
      <el-select v-model="listQuery.orderStatusArray" multiple style="width: 200px" class="filter-item" placeholder="请选择订单状态">
        <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value"/>
      </el-select>
      <el-select v-model="listQuery.payType" clearable style="width: 200px" class="filter-item" placeholder="请选择付款方式">
        <el-option v-for="(key, value) in payTypeMap" :key="key" :label="key" :value="value"/>
      </el-select>
      <el-button v-permission="['GET /admin/vipOrder/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column align="center" min-width="100" label="订单编号" prop="orderSn"/>

      <el-table-column align="center" label="用户ID" prop="userId"/>

      <el-table-column align="center" label="订单状态" prop="orderStatus">
        <template slot-scope="scope">
          <el-tag :type="scope.row.orderStatus == '201' ? 'success' : 'error' ">{{ scope.row.orderStatus | orderStatusFilter }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column align="center" label="订单会员名称" prop="name"/>

      <el-table-column align="center" label="支付金额" prop="actualPrice"/>
      <el-table-column align="center" label="折扣" prop="discountName"/>
      <el-table-column align="center" label="折扣值" prop="discount"/>

      <!--<el-table-column align="center" label="支付时间" prop="payTime"/>-->

      <el-table-column align="center" label="支付方式" prop="payType">
        <template slot-scope="scope">
          <el-tag :type="scope.row.payType == 'offline' ? 'error' : 'success' ">{{ scope.row.payType | payTypeFilter }}</el-tag>
        </template>
      </el-table-column>

      <!--<el-table-column align="center" label="物流单号" prop="shipSn"/>-->

      <!--<el-table-column align="center" label="物流渠道" prop="shipChannel"/>-->

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['GET /admin/vipOrder/pay']" v-if="scope.row.payType == 'offline' && scope.row.orderStatus == '101'" type="primary" size="mini" @click="handlePay(scope.row)">确认付款</el-button>
          <el-button v-permission="['GET /admin/vipOrder/detail']" type="primary" size="mini" @click="handleDetail(scope.row)">详情</el-button>
          <!--<el-button v-permission="['POST /admin/order/ship']" v-if="scope.row.orderStatus==201" type="primary" size="mini" @click="handleShip(scope.row)">发货</el-button>-->
          <!--<el-button v-permission="['POST /admin/order/refund']" v-if="scope.row.orderStatus==202" type="primary" size="mini" @click="handleRefund(scope.row)">退款</el-button>-->
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 订单详情对话框 -->
    <el-dialog :visible.sync="orderDialogVisible" title="订单详情" width="800">

      <el-form :data="orderDetail" label-position="left">
        <el-form-item label="订单编号">
          <span>{{ orderDetail.order.orderSn }}</span>
        </el-form-item>
        <el-form-item label="下单时间">
          <!--<span>（支付渠道）微信支付</span>-->
          <span>{{ orderDetail.order.addTime }}</span>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-tag>{{ orderDetail.order.orderStatus | orderStatusFilter }}</el-tag>
        </el-form-item>
        <el-form-item label="订单用户">
          <span>{{ orderDetail.user.name }}</span>
        </el-form-item>
        <!--<el-form-item label="用户留言">-->
        <!--<span>{{ orderDetail.order.message }}</span>-->
        <!--</el-form-item>-->
        <!--<el-form-item label="收货信息">-->
        <!--<span>（收货人）{{ orderDetail.order.consignee }}</span>-->
        <!--<span>（手机号）{{ orderDetail.order.mobile }}</span>-->
        <!--<span>（地址）{{ orderDetail.order.address }}</span>-->
        <!--</el-form-item>-->

        <el-form-item label="会员名称">
          <span>{{ orderDetail.order.name }}</span>
        </el-form-item>
        <el-form-item label="会员价格">
          <span>{{ orderDetail.order.actualPrice }}</span>
        </el-form-item>
        <el-form-item label="折扣">
          <span>{{ orderDetail.order.discountName }}</span>
        </el-form-item>
        <el-form-item label="折扣值">
          <span>{{ orderDetail.order.discount }}</span>
        </el-form-item>
        <el-form-item label="有效天数">
          <span>{{ orderDetail.order.validDays }}</span>
        </el-form-item>
        <el-form-item label="有效时间">
          <span>{{ orderDetail.order.startTime }} 至 {{ orderDetail.order.endTime }}</span>
        </el-form-item>

        <!--<el-form-item label="会员信息">-->
        <!--<el-table :data="orderDetail.order" border fit highlight-current-row>-->
        <!--<el-table-column align="center" label="名称" prop="name" />-->
        <!--<el-table-column align="center" label="价格" prop="acturalPrice" />-->
        <!--<el-table-column align="center" label="折扣" prop="discountName" />-->
        <!--<el-table-column align="center" label="折扣值" prop="discount" />-->
        <!--<el-table-column align="center" label="有效天数" prop="validDays" />-->
        <!--<el-table-column align="center" label="有效时间起" prop="startTime" />-->
        <!--<el-table-column align="center" label="有效时间起" prop="startTime" />-->
        <!--<el-table-column align="center" label="有效时间止" prop="endTime" />-->
        <!--</el-table>-->
        <!--</el-form-item>-->

        <!--<el-form-item label="费用信息">-->
        <!--<span>-->
        <!--(实际费用){{ orderDetail.order.actualPrice }}元-->
        <!--=-->
        <!--(商品总价){{ orderDetail.order.goodsPrice }}元-->
        <!--+-->
        <!--(快递费用){{ orderDetail.order.freightPrice }}元-->
        <!-- - -->
        <!--(优惠减免){{ orderDetail.order.couponPrice }}元-->
        <!-- - -->
        <!--(积分减免){{ orderDetail.order.integralPrice }}元-->
        <!--</span>-->
        <!--</el-form-item>-->
        <el-form-item label="付款方式">
          <el-tag :type="orderDetail.order.payType == 'offline' ? 'error' : 'success' ">{{ orderDetail.order.payType | payTypeFilter }}</el-tag>
        </el-form-item>

        <!--<el-form-item label="快递信息">-->
        <!--<span>（快递公司）{{ orderDetail.order.shipChannel }}</span>-->
        <!--<span>（快递单号）{{ orderDetail.order.shipSn }}</span>-->
        <!--<span>（发货时间）{{ orderDetail.order.shipTime }}</span>-->
        <!--</el-form-item>-->
        <!--<el-form-item label="收货信息">-->
        <!--<span>（确认收货时间）{{ orderDetail.order.confirmTime }}</span>-->
        <!--</el-form-item>-->
      </el-form>
    </el-dialog>

    <!-- 发货对话框 -->
    <!--<el-dialog :visible.sync="shipDialogVisible" title="发货">-->
    <!--<el-form ref="shipForm" :model="shipForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">-->
    <!--<el-form-item label="快递公司" prop="shipChannel">-->
    <!--<el-input v-model="shipForm.shipChannel"/>-->
    <!--</el-form-item>-->
    <!--<el-form-item label="快递编号" prop="shipSn">-->
    <!--<el-input v-model="shipForm.shipSn"/>-->
    <!--</el-form-item>-->
    <!--</el-form>-->
    <!--<div slot="footer" class="dialog-footer">-->
    <!--<el-button @click="shipDialogVisible = false">取消</el-button>-->
    <!--<el-button type="primary" @click="confirmShip">确定</el-button>-->
    <!--</div>-->
    <!--</el-dialog>-->

    <!--确认付款-->
    <el-dialog :visible.sync="payDialogVisible" title="确认付款">
      <el-form ref="payForm" :model="payForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="付款金额" prop="actualPrice">
          <el-input v-model="payForm.actualPrice" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPay">确定</el-button>
      </div>
    </el-dialog>

    <!-- 退款对话框 -->
    <el-dialog :visible.sync="refundDialogVisible" title="退款">
      <el-form ref="refundForm" :model="refundForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="退款金额" prop="refundMoney">
          <el-input v-model="refundForm.refundMoney" :disabled="true"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRefund">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<style>

</style>

<script>
import { listOrder, refundOrder, detailOrder, payOrder } from '@/api/vipOrder'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import checkPermission from '@/utils/permission' // 权限判断函数

const statusMap = {
  101: '未付款',
  102: '用户取消',
  103: '系统取消',
  201: '已付款'
  // 202: '申请退款',
  // 203: '已退款',
  // 301: '已发货',
  // 401: '用户收货',
  // 402: '系统收货'
}

const payTypeMap = {
  'online': '线上支付',
  'offline': '线下支付'
}

export default {
  name: 'Order',
  components: { Pagination },
  filters: {
    orderStatusFilter(status) {
      return statusMap[status]
    },
    payTypeFilter(payType) {
      return payTypeMap[payType]
    }
  },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        id: undefined,
        name: undefined,
        orderStatusArray: [],
        sort: 'add_time',
        order: 'desc'
      },
      statusMap,
      payTypeMap,
      orderDialogVisible: false,
      orderDetail: {
        order: {},
        user: {}
      },
      shipForm: {
        orderId: undefined,
        shipChannel: undefined,
        shipSn: undefined
      },
      shipDialogVisible: false,
      payForm: {
        orderId: undefined,
        actualPrice: undefined
      },
      payDialogVisible: false,
      refundForm: {
        orderId: undefined,
        refundMoney: undefined
      },
      refundDialogVisible: false,
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    checkPermission,
    getList() {
      this.listLoading = true
      listOrder(this.listQuery).then(response => {
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
    handleDetail(row) {
      detailOrder(row.id).then(response => {
        this.orderDetail = response.data.data
      })
      this.orderDialogVisible = true
    },
    handlePay(row) {
      this.payForm.orderId = row.id
      this.payForm.actualPrice = row.actualPrice
      this.payDialogVisible = true
    },
    confirmPay() {
      this.$refs['payForm'].validate((valid) => {
        if (valid) {
          payOrder(this.payForm).then(response => {
            this.payDialogVisible = false
            this.$notify.success({
              title: '成功',
              message: '确认付款成功'
            })
            this.getList()
          }).catch(response => {
            this.$notify.error({
              title: '失败',
              message: response.data.errmsg
            })
          })
        }
      })
    },
    handleShip(row) {
      this.shipForm.orderId = row.id
      this.shipForm.shipChannel = row.shipChannel
      this.shipForm.shipSn = row.shipSn

      this.shipDialogVisible = true
      this.$nextTick(() => {
        this.$refs['shipForm'].clearValidate()
      })
    },
    confirmShip() {
      this.$refs['shipForm'].validate((valid) => {
        if (valid) {
          shipOrder(this.shipForm).then(response => {
            this.shipDialogVisible = false
            this.$notify.success({
              title: '成功',
              message: '确认发货成功'
            })
            this.getList()
          }).catch(response => {
            this.$notify.error({
              title: '失败',
              message: response.data.errmsg
            })
          })
        }
      })
    },
    handleRefund(row) {
      this.refundForm.orderId = row.id
      this.refundForm.refundMoney = row.actualPrice

      this.refundDialogVisible = true
      this.$nextTick(() => {
        this.$refs['refundForm'].clearValidate()
      })
    },
    confirmRefund() {
      this.$refs['refundForm'].validate((valid) => {
        if (valid) {
          refundOrder(this.refundForm).then(response => {
            this.refundDialogVisible = false
            this.$notify.success({
              title: '成功',
              message: '确认退款成功'
            })
            this.getList()
          }).catch(response => {
            this.$notify.error({
              title: '失败',
              message: response.data.errmsg
            })
          })
        }
      })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['订单ID', '订单编号', '用户ID', '订单状态', '支付金额', '支付方式']
        const filterVal = ['id', 'orderSn', 'userId', 'orderStatus', 'actualPrice', 'payType'/* 'consignee', 'mobile', 'address'*/]
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '订单信息')
        this.downloadLoading = false
      })
    }
  }
}
</script>
