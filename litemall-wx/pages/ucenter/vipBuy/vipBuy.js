var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

var app = getApp();

Page({
  data: {
    vipId:'',
    vip:{},
    checkedGoodsList: [],
    // checkedAddress: {},
    availableCouponLength: 0, // 可用的优惠券数量
    goodsTotalPrice: 0.00, //商品总价
    // freightPrice: 0.00, //快递费
    couponPrice: 0.00, //优惠券的价格
    grouponPrice: 0.00, //团购优惠价格
    orderTotalPrice: 0.00, //订单总价
    actualPrice: 0.00, //实际需要支付的总价
    cartId: 0,
    // addressId: 0,
    couponId: 0,
    message: '',
    grouponLinkId: 0, //参与的团购，如果是发起则为0
    grouponRulesId: 0 //团购规则ID
  },
  onLoad: function(options) {
    // 页面初始化 options为页面跳转所带来的参数
    this.setData({
      vipId: parseInt(options.id)
    });
    this.getVipInfo()
  },
  //获取checkou信息
  getVipInfo: function() {
    let that = this;
    util.request(api.GetVipById, {
      vipId: that.data.vipId
    }).then(function(res) {
      if (res.errno === 0) {
        that.setData({
          vip: res.data
        });
      }
      wx.hideLoading();
    });
  },
  bindMessageInput: function(e) {
    this.setData({
      message: e.detail.value
    });
  },
  onReady: function() {
    // 页面渲染完成

  },
  onShow: function() {
    // 页面显示
    
  },
  onHide: function() {
    // 页面隐藏

  },
  onUnload: function() {
    // 页面关闭

  },
  submitOrder: function(e) {
    // if (this.data.addressId <= 0) {
    //   util.showErrorToast('请选择收货地址');
    //   return false;
    // }
      // util.showErrorToast(e.currentTarget.dataset.paytype);
      // return false;
    let that = this;

    const payType = e.currentTarget.dataset.paytype
    const orderId = '';
    util.request(api.VipOrderSubmit, {
      vipId: that.data.vipId,
      payType: payType
    }, 'POST').then(res => {
      if (res.errno === 0) {
        // 下单成功，重置couponId
        // try {
        //   wx.setStorageSync('couponId', 0);
        // } catch (error) {

        // }
        this.orderId = res.data.id;

        //线下支付
        if(payType == 'offline'){
          wx.redirectTo({
            url: '/pages/payResult/payResult?status=1&orderId=' + orderId + '&payType='+payType+'&orderType=vip'
          });
        }else{
          util.request(api.VipOrderPrepay, {
            orderId: orderId
          }, 'POST').then(function(res) {
            if (res.errno === 0) {
              const payParam = res.data;
              console.log("支付过程开始");
              wx.requestPayment({
                'timeStamp': payParam.timeStamp,
                'nonceStr': payParam.nonceStr,
                'package': payParam.packageValue,
                'signType': payParam.signType,
                'paySign': payParam.paySign,
                'success': function(res) {
                  console.log("支付过程成功");
                  wx.redirectTo({
                    url: '/pages/payResult/payResult?status=1&orderId=' + orderId + '&payType='+payType+'&orderType=vip'
                  });
                },
                'fail': function(res) {
                  console.log("支付过程失败");
                  wx.redirectTo({
                    url: '/pages/payResult/payResult?status=0&orderId=' + orderId + '&payType='+payType+'&orderType=vip'
                  });
                },
                'complete': function(res) {
                  console.log("支付过程结束")
                }
              });
            } else {
              wx.redirectTo({
                url: '/pages/payResult/payResult?status=0&orderId=' + orderId + '&payType='+payType+'&orderType=vip'
              });
            }
          });
        }
      } else {
        wx.redirectTo({
          url: '/pages/payResult/payResult?status=0&orderId=' + orderId + '&payType='+payType+'&orderType=vip'
        });
      }
    });
  }
});