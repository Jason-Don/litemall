var util = require('../../utils/util.js');
var api = require('../../config/api.js');

var app = getApp();

Page({
  data: {
    hasLogin: false,
    orderList: [],
    showType: 5,//已付款
    page: 1,
    limit: 10,
    totalPages: 1
  },
  onLoad: function(options) {
    // 页面初始化 options为页面跳转所带来的参数
    // let that = this
    // try {
    //   var tab = wx.getStorageSync('tab');

    //   this.setData({
    //     showType: tab
    //   });
    // } catch (e) {}

  },
  getOrderList() {
    let that = this;
    util.request(api.OrderList, {
      isVerifyTime: true,
      showType: that.data.showType,
      page: that.data.page,
      limit: that.data.limit
    }).then(function(res) {
      if (res.errno === 0) {
        // console.log(res.data);
        that.setData({
          // orderList: that.data.orderList.concat(res.data.list),
          orderList: res.data.list,
          totalPages: res.data.pages
        });
      }
    });
  },
  onReachBottom() {
    if (this.data.totalPages > this.data.page) {
      this.setData({
        page: this.data.page + 1
      });
      this.getOrderList();
    } else {
      wx.showToast({
        title: '没有更多订单了',
        icon: 'none',
        duration: 2000
      });
      return false;
    }
  },
  onReady: function() {
    // 页面渲染完成
  },
  onShow: function() {
    // this.setData({
    //   orderList: [],
    //   totalPages: 1
    // });
    // 页面显示
    if (app.globalData.hasLogin) {
      this.getOrderList();
    }

    this.setData({
      hasLogin: app.globalData.hasLogin
    });
  },
  onHide: function() {
    // 页面隐藏
  },
  onUnload: function() {
    // 页面关闭
  },
  goLogin() {
    wx.navigateTo({
      url: "/pages/auth/login/login"
    });
  },
  goOrder(e) {
    wx.navigateTo({
      url: "/pages/ucenter/orderDetail/orderDetail?id="+e.currentTarget.dataset.orderid
    });
  },
})