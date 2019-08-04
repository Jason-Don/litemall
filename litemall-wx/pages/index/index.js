import { $stopWuxRefresher, $stopWuxLoader, $startWuxLoader} from '../../dist/index'

const util = require('../../utils/util.js');
const api = require('../../config/api.js');
const user = require('../../utils/user.js');

//获取应用实例
const app = getApp();

Page({
  data: {
    grade:'',
    channelNJ: [],
    chooseNJIndex : 0,
    subject:'',
    channelKM: [],
    chooseKMIndex: 0,
    mode:'',
    channelLX: [],
    chooseLXIndex: 0,
    address:'',
    channelSJDD: [],
    chooseSKDDIndex: 0,

    goods: [],
    newGoods: [],
    hotGoods: [],
    topics: [],
    brands: [],
    groupons: [],
    floorGoods: [],
    banner: [],
    channel: [],
    coupon: [],
    goodsCount: 0,

    scrollTop: 0,
    limit : 5,
    page: 1,
    totalPages: 1
  },

  onShareAppMessage: function() {
    return {
      title: '最厉害的培训机构小程序',
      desc: '最厉害的培训机构哈哈哈哈',
      path: '/pages/index/index'
    }
  },

  onPullDownRefresh() {
    wx.showNavigationBarLoading() //在标题栏中显示加载
    this.getIndexData();
    wx.hideNavigationBarLoading() //完成停止加载
    wx.stopPullDownRefresh() //停止下拉刷新
  },

  getIndexData: function() {
    let that = this;
    util.request(api.IndexUrl).then(function(res) {
      if (res.errno === 0) {

//
        for (let i = 0; i < res.data.channel.length; i++) {
          var array = [];
          array.push('全部'+res.data.channel[i].name);
          for (let j = 0; j < res.data.channel[i].subCategory.length; j++) {
            array.push(res.data.channel[i].subCategory[j].name);
          }
          res.data.channel[i].subCategoryArray = array;
        }
// console.log(res.data.couponList);
        that.setData({
          newGoods: res.data.newGoodsList,
          // hotGoods: res.data.hotGoodsList,
          topics: res.data.topicList,
          brands: res.data.brandList,
          floorGoods: res.data.floorGoodsList,
          banner: res.data.banner,
          groupons: res.data.grouponList,
          // channel: res.data.channel,
          channelNJ: res.data.channel[0],
          channelKM: res.data.channel[1],
          channelLX: res.data.channel[2],
          channelSJDD: res.data.channel[3],
          coupon: res.data.couponList
        });


      }
    });
    util.request(api.GoodsCount).then(function (res) {
      that.setData({
        goodsCount: res.data
      });
    });
    this.queryGoodsList();
  },
  searchButton: function(){    
    this.setData({
      page: 1
    })
    var that = this
    util.request(api.QueryGoodsList, {
      grade: this.data.grade,
      subject: this.data.subject,
      mode: this.data.mode,
      address: this.data.address,
      page: this.data.page,
      limit: this.data.limit
    }).then(function (res) {
    
    if (res.errno === 0){
      that.setData({
        goods: res.data.list,
        totalPages: res.data.pages
      });
      //初始化上拉组件
      $startWuxLoader()
      //
      // if (res.data.list.length == that.data.limit) {
      // if (res.data.total != that.data.goods.length) {
      if(that.data.totalPages > that.data.page){
        $stopWuxLoader()
      } else {
        console.log('没有更多数据')
        $stopWuxLoader('#wux-refresher', this, true)
      }
    }
    
    });

  },
  queryGoodsList: function(){
    this.setData({
      page: 1
    })
    var that = this
    util.request(api.QueryGoodsList, {
      grade: this.data.grade,
      subject: this.data.subject,
      mode: this.data.mode,
      address: this.data.address,
      page : this.data.page,
      limit : this.data.limit
    }).then(function (res) {
      // console.log(res)
      that.setData({
        goods: res.data.list,
        totalPages: res.data.pages
      });
      //初始化上拉组件
      $startWuxLoader()
      //
      // if (res.data.list.length == that.data.limit) {
      // if (res.data.total != that.data.goods.length) {
      if(that.data.totalPages > that.data.page){
        $stopWuxLoader()
      } else {
        console.log('没有更多数据')
        $stopWuxLoader('#wux-refresher', this, true)
      }
    });
  },
  onLoad: function(options) {

    // 页面初始化 options为页面跳转所带来的参数
    if (options.scene) {
      //这个scene的值存在则证明首页的开启来源于朋友圈分享的图,同时可以通过获取到的goodId的值跳转导航到对应的详情页
      var scene = decodeURIComponent(options.scene);
      console.log("scene:" + scene);

      let info_arr = [];
      info_arr = scene.split(',');
      let _type = info_arr[0];
      let id = info_arr[1];

      if (_type == 'goods') {
        wx.navigateTo({
          url: '../goods/goods?id=' + id
        });
      } else if (_type == 'groupon') {
        wx.navigateTo({
          url: '../goods/goods?grouponId=' + id
        });
      } else {
        wx.navigateTo({
          url: '../index/index'
        });
      }
    }

    // 页面初始化 options为页面跳转所带来的参数
    if (options.grouponId) {
      //这个pageId的值存在则证明首页的开启来源于用户点击来首页,同时可以通过获取到的pageId的值跳转导航到对应的详情页
      wx.navigateTo({
        url: '../goods/goods?grouponId=' + options.grouponId
      });
    }

    // 页面初始化 options为页面跳转所带来的参数
    if (options.goodId) {
      //这个goodId的值存在则证明首页的开启来源于分享,同时可以通过获取到的goodId的值跳转导航到对应的详情页
      wx.navigateTo({
        url: '../goods/goods?id=' + options.goodId
      });
    }

    // 页面初始化 options为页面跳转所带来的参数
    if (options.orderId) {
      //这个orderId的值存在则证明首页的开启来源于订单模版通知,同时可以通过获取到的pageId的值跳转导航到对应的详情页
      wx.navigateTo({
        url: '../ucenter/orderDetail/orderDetail?id=' + options.orderId
      });
    }

    this.getIndexData();
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
  getCoupon(e) {
    if (!app.globalData.hasLogin) {
      wx.navigateTo({
        url: "/pages/auth/login/login"
      });
    }

    let couponId = e.currentTarget.dataset.index
    util.request(api.CouponReceive, {
      couponId: couponId
    }, 'POST').then(res => {
      if (res.errno === 0) {
        wx.showToast({
          title: "领取成功"
        })
      }
      else{
        util.showErrorToast(res.errmsg);
      }
    })
  },
  bindNJChange: function (e) {
    let index = e.detail.value;
    this.setData({
      chooseNJIndex: index,
      grade: index==0?'':this.data.channelNJ.subCategory[index-1].id
    })
  },
  bindKMChange: function (e) {
    let index = e.detail.value;
    this.setData({
      chooseKMIndex: index,
      subject: index == 0 ? '' :this.data.channelKM.subCategory[index-1].id
    })
  },
  bindLXChange: function (e) {
    let index = e.detail.value;
    this.setData({
      chooseLXIndex: index,
      mode: index == 0 ? '' :this.data.channelLX.subCategory[index-1].id
    })
  },
  bindSKDDChange: function (e) {
    let index = e.detail.value;
    this.setData({
      chooseSKDDIndex: index,
      address: index == 0 ? '' :this.data.channelSJDD.subCategory[index-1].id
    })
  },
  // onLoad() {
  //   console.log('onLoad')
  //   this.queryGoodsList();
  // },
  onPageScroll(e) {
    // console.log('onPageScroll')
    this.setData({
      scrollTop: e.scrollTop
    })
  },
  onPulling() {
    console.log('onPulling')
  },
  onRefresh() {
    console.log('onRefresh')

    let that = this;
    util.request(api.IndexUrl).then(function (res) {
      if (res.errno === 0) {
        for (let i = 0; i < res.data.channel.length; i++) {
          var array = [];
          array.push('全部' + res.data.channel[i].name);
          for (let j = 0; j < res.data.channel[i].subCategory.length; j++) {
            array.push(res.data.channel[i].subCategory[j].name);
          }
          res.data.channel[i].subCategoryArray = array;
        }
        that.setData({
          newGoods: res.data.newGoodsList,
          // hotGoods: res.data.hotGoodsList,
          topics: res.data.topicList,
          brands: res.data.brandList,
          floorGoods: res.data.floorGoodsList,
          banner: res.data.banner,
          groupons: res.data.grouponList,
          // channel: res.data.channel,
          channelNJ: res.data.channel[0],
          channelKM: res.data.channel[1],
          channelLX: res.data.channel[2],
          channelSJDD: res.data.channel[3],
          coupon: res.data.couponList
        });
      }
    });
    util.request(api.GoodsCount).then(function (res) {
      that.setData({
        goodsCount: res.data
      });
    });
    //
    this.setData({
      page: 1
    })
    util.request(api.QueryGoodsList, {
      grade: this.data.grade,
      subject: this.data.subject,
      mode: this.data.mode,
      address: this.data.address,
      page: this.data.page,
      limit: this.data.limit
    }).then(function (res) {
      // console.log(res)
      that.setData({
        goods: res.data.list
      });
      $stopWuxRefresher()
    });
  },
  onLoadmore() {
    console.log('onLoadmore')
    if (this.data.totalPages > this.data.page) {
      var that = this
      util.request(api.QueryGoodsList, {
        grade: this.data.grade,
        subject: this.data.subject,
        mode: this.data.mode,
        address: this.data.address,
        page: this.data.page+1,
        limit: this.data.limit
      }).then(function (res) {
        // console.log(res)
        // let items = that.data.goods
        // res.data.list.forEach((item) => {
        //   items.push(item)
        // })
        that.setData({
          page: that.data.page+1,
          goods: that.data.goods.concat(res.data.list),
          totalPages: res.data.pages
        });
    
        // if (res.data.list.length == that.data.limit) {
        if(that.data.totalPages > that.data.page){
          $stopWuxLoader()
        } else {
          console.log('没有更多数据')
          $stopWuxLoader('#wux-refresher', this, true)
        }
      });
    }
  }
})