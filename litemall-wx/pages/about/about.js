// about.js
var app = getApp()
var util = require("../../utils/util.js");


var api = require("../../config/api.js");
Page({

  /**
   * 页面的初始数据
   */
  data: {
    load_statue: true,
    shopInfo: {
      name: '',
      address: '',
      latitude: 0,
      longitude: 0,
      linkPhone: '',
      qqNumber: ''
    },
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    util.request(api.ConfigMall).then(function(res) {
      that.setData({
        'shopInfo.name':res.data.litemall_mall_name,
        'shopInfo.address': res.data.litemall_mall_address,
        'shopInfo.linkPhone': res.data.litemall_mall_phone,
        'shopInfo.qqNumber': res.data.litemall_mall_qq,
        'shopInfo.latitude': Number(res.data.litemall_mall_latitude),
        'shopInfo.longitude': Number(res.data.litemall_mall_longitude)
      });
    });
  },

  showLocation: function (e) {
    var that = this
    wx.openLocation({
      latitude: that.data.shopInfo.latitude,
      longitude: that.data.shopInfo.longitude,
      name: that.data.shopInfo.name,
      address: that.data.shopInfo.address,
    })
  },
  callPhone: function (e) {
    var that = this
    wx.makePhoneCall({
      phoneNumber: that.data.shopInfo.linkPhone,
    })
  },
  copyQQ: function(){
    let that = this
    wx.setClipboardData({
      data: that.data.shopInfo.qqNumber
    })
  },
  reLoad: function (e) {
    this.loadShopInfo();
  }
})