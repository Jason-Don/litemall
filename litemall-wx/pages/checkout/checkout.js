var util = require('../../utils/util.js');
var api = require('../../config/api.js');

var app = getApp();

Page({
  data: {
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
    grouponRulesId: 0, //团购规则ID
    discount: 0,
    balance: 0,
    balancePrice: 0,
    afterPayBalance: 0
  },
  onLoad: function(options) {
    // 页面初始化 options为页面跳转所带来的参数
  },

  //获取checkou信息
  getCheckoutInfo: function() {
    let that = this;
    util.request(api.CartCheckout, {
      cartId: that.data.cartId,
      // addressId: that.data.addressId,
      couponId: that.data.couponId,
      grouponRulesId: that.data.grouponRulesId
    }).then(function(res) {
      if (res.errno === 0) {
        that.setData({
          checkedGoodsList: res.data.checkedGoodsList,
          // checkedAddress: res.data.checkedAddress,
          availableCouponLength: res.data.availableCouponLength,
          actualPrice: res.data.actualPrice,
          couponPrice: res.data.couponPrice,
          grouponPrice: res.data.grouponPrice,
          // freightPrice: res.data.freightPrice,
          goodsTotalPrice: res.data.goodsTotalPrice,
          orderTotalPrice: res.data.orderTotalPrice,
          // addressId: res.data.addressId,
          couponId: res.data.couponId,
          grouponRulesId: res.data.grouponRulesId,
          discount:res.data.discount,
          balance:res.data.balance,
          balancePrice: res.data.balancePrice,
          afterPayBalance: res.data.afterPayBalance,
        });
      }
      wx.hideLoading();
    });
  },
  selectAddress() {
    wx.navigateTo({
      url: '/pages/ucenter/address/address',
    })
  },
  selectCoupon() {
    wx.navigateTo({
      url: '/pages/ucenter/couponSelect/couponSelect',
    })
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
    wx.showLoading({
      title: '加载中...',
    });
    try {
      var cartId = wx.getStorageSync('cartId');
      if (cartId === "") {
        cartId = 0;
      }
      var addressId = wx.getStorageSync('addressId');
      if (addressId === "") {
        addressId = 0;
      }
      var couponId = wx.getStorageSync('couponId');
      if (couponId === "") {
        couponId = 0;
      }
      var grouponRulesId = wx.getStorageSync('grouponRulesId');
      if (grouponRulesId === "") {
        grouponRulesId = 0;
      }
      var grouponLinkId = wx.getStorageSync('grouponLinkId');
      if (grouponLinkId === "") {
        grouponLinkId = 0;
      }

      this.setData({
        cartId: cartId,
        addressId: addressId,
        couponId: couponId,
        grouponRulesId: grouponRulesId,
        grouponLinkId: grouponLinkId
      });

    } catch (e) {
      // Do something when catch error
      console.log(e);
    }

    this.getCheckoutInfo();
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
    let that =this;
    const payType = e.currentTarget.dataset.paytype
    const orderId = '';

    if (this.data.actualPrice == 0 && payType == 'online') {
      // util.showErrorToast('实付0元，请选择“线下支付”！');
      wx.showToast({
        title: '实付0元，请选择“线下支付”！',
        icon: 'none'
      })
      return false;
    }

    wx.showModal({
      title: '',
      content: '确定要下订单？',
      success: function(res) {
        if (res.confirm) {
          
          util.request(api.OrderSubmit, {
            cartId: that.data.cartId,
            addressId: that.data.addressId,
            couponId: that.data.couponId,
            message: that.data.message,
            grouponRulesId: that.data.grouponRulesId,
            grouponLinkId: that.data.grouponLinkId,
            payType: payType
          }, 'POST').then(res => {
            if (res.errno === 0) {
              // 下单成功，重置couponId
              try {
                wx.setStorageSync('couponId', 0);
              } catch (error) {
      
              }
              that.orderId = res.data.orderId;
      
              //线下支付
              if(payType == 'offline'){
                wx.redirectTo({
                  url: '/pages/payResult/payResult?status=1&orderId=' + that.orderId + '&payType='+payType
                });
              }else{
                util.request(api.OrderPrepay, {
                  orderId: that.orderId
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
                          url: '/pages/payResult/payResult?status=1&orderId=' + that.orderId + '&payType='+payType
                        });
                      },
                      'fail': function(res) {
                        console.log("支付过程失败");
                        wx.redirectTo({
                          url: '/pages/payResult/payResult?status=0&orderId=' + that.orderId + '&payType='+payType
                        });
                      },
                      'complete': function(res) {
                        console.log("支付过程结束")
                      }
                    });
                  } else {
                    wx.redirectTo({
                      url: '/pages/payResult/payResult?status=0&orderId=' + that.orderId + '&payType='+payType
                    });
                  }
                });
              }
            } else {
              wx.redirectTo({
                url: '/pages/payResult/payResult?status=0&orderId=' + that.orderId + '&payType='+payType
              });
            }
          });

        }
      }
    });
    
  }
});