var util = require('../../../utils/util.js');
var api = require('../../../config/api.js');

//获取应用实例
const app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    vips:[]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getVIPList()
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },
  // 获取会员列表
  getVIPList: function() {
    let that = this;
    util.request(api.VIPList, {
      // id: that.data.id
    }).then(function(res) {
      if (res.errno === 0) {
        that.setData({
          vips: res.data,
        });

      }
    });
  },
  buyVip(e) {
    if (!app.globalData.hasLogin) {
      wx.navigateTo({
        url: "/pages/auth/login/login"
      });
    }else{
      wx.navigateTo({
        url: "/pages/ucenter/vipBuy/vipBuy?id="+e.currentTarget.dataset.index
      });
    }
  },
})