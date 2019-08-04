var api = require('../../../config/api.js');
var check = require('../../../utils/check.js');

var app = getApp();
Page({
  data: {
    // username: '',
    // password: '',
    // confirmPassword: '',
    genderArray:['-请选择-','男', '女'],
    name: '',
    school: '',
    grade:'',
    birthday: '',
    gender:0,
    mobile: '',
    wxCode: ''
  },
  onLoad: function(options) {
    // 页面初始化 options为页面跳转所带来的参数
    // 页面渲染完成
    let that = this;
    wx.login({
      success: function(res) {
        if (!res.code) {
          wx.showModal({
            title: '错误信息',
            content: '注册失败',
            showCancel: false
          });
        }
        that.setData({
          wxCode:res.code
        })
        that.init()
      }
    });

  },
  onReady: function() {

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
  // sendCode: function() {
  //   let that = this;

  //   if (this.data.mobile.length == 0) {
  //     wx.showModal({
  //       title: '错误信息',
  //       content: '手机号不能为空',
  //       showCancel: false
  //     });
  //     return false;
  //   }

  //   if (!check.isValidPhone(this.data.mobile)) {
  //     wx.showModal({
  //       title: '错误信息',
  //       content: '手机号输入不正确',
  //       showCancel: false
  //     });
  //     return false;
  //   }

  //   wx.request({
  //     url: api.AuthRegisterCaptcha,
  //     data: {
  //       mobile: that.data.mobile
  //     },
  //     method: 'POST',
  //     header: {
  //       'content-type': 'application/json'
  //     },
  //     success: function(res) {
  //       if (res.data.errno == 0) {
  //         wx.showModal({
  //           title: '发送成功',
  //           content: '验证码已发送',
  //           showCancel: false
  //         });
  //       } else {
  //         wx.showModal({
  //           title: '错误信息',
  //           content: res.data.errmsg,
  //           showCancel: false
  //         });
  //       }
  //     }
  //   });
  // },
  init: function() {
    let that = this;
    wx.request({
      url: api.AuthGetUserInfo,
      data: {
        wxCode: that.data.wxCode
      },
      method: 'POST',
      header: {
        'content-type': 'application/json'
      },
      success: function(res) {
        if (res.data.errno == 0) {
          that.setData({
            name: res.data.data.name,
            school: res.data.data.school,
            grade: res.data.data.grade,
            birthday: res.data.data.birthday,
            gender: res.data.data.gender,
            mobile: res.data.data.mobile
          })
          console.log(res)
        } else {
          wx.showModal({
            title: '错误信息',
            content: res.data.errmsg,
            showCancel: false
          });
        }
      }
    });
  },
  requestRegister: function() {
    let that = this;
    wx.request({
      url: api.AuthSaveUserInfo,
      data: {
        name: that.data.name,
        school: that.data.school,
        grade: that.data.grade,
        birthday: that.data.birthday,
        gender: that.data.gender,
        mobile: that.data.mobile,
        // code: that.data.code,
        wxCode: that.data.wxCode
      },
      method: 'POST',
      header: {
        'content-type': 'application/json'
      },
      success: function(res) {
        if (res.data.errno == 0) {
          app.globalData.hasLogin = true;
          wx.setStorageSync('userInfo', res.data.data.userInfo);
          wx.setStorage({
            key: "token",
            data: res.data.data.token,
            success: function() {
              wx.switchTab({
                url: '/pages/ucenter/index/index'
              });
            }
          });
        } else {
          wx.showModal({
            title: '错误信息',
            content: res.data.errmsg,
            showCancel: false
          });
        }
      }
    });
  },
  startRegister: function() {
    var that = this;

    // if (this.data.password.length < 6 || this.data.username.length < 6) {
    //   wx.showModal({
    //     title: '错误信息',
    //     content: '用户名和密码不得少于6位',
    //     showCancel: false
    //   });
    //   return false;
    // }

    // if (this.data.password != this.data.confirmPassword) {
    //   wx.showModal({
    //     title: '错误信息',
    //     content: '确认密码不一致',
    //     showCancel: false
    //   });
    //   return false;
    // }

    if (this.data.name == '' ) {
      wx.showModal({
        title: '错误信息',
        content: '学生姓名为空',
        showCancel: false
      });
      return false;
    }
    if (this.data.school == '' ) {
      wx.showModal({
        title: '错误信息',
        content: '学校为空',
        showCancel: false
      });
      return false;
    }
    if (this.data.grade == '' ) {
      wx.showModal({
        title: '错误信息',
        content: '年级为空',
        showCancel: false
      });
      return false;
    }
    if (this.data.birthday == '' ) {
      wx.showModal({
        title: '错误信息',
        content: '生日为空',
        showCancel: false
      });
      return false;
    }
    if (this.data.gender == 0 ) {
      wx.showModal({
        title: '错误信息',
        content: '性别未选择',
        showCancel: false
      });
      return false;
    }

    if (this.data.mobile.length == 0 ) {
      wx.showModal({
        title: '错误信息',
        content: '手机号不能为空',
        showCancel: false
      });
      return false;
    }

    if (!check.isValidPhone(this.data.mobile)) {
      wx.showModal({
        title: '错误信息',
        content: '手机号输入不正确',
        showCancel: false
      });
      return false;
    }

    wx.login({
      success: function(res) {
        if (!res.code) {
          wx.showModal({
            title: '错误信息',
            content: '注册失败',
            showCancel: false
          });
        }
        that.setData({
          wxCode:res.code
        })
        that.requestRegister();
      }
    });
  },
  // bindUsernameInput: function(e) {

  //   this.setData({
  //     username: e.detail.value
  //   });
  // },
  // bindPasswordInput: function(e) {

  //   this.setData({
  //     password: e.detail.value
  //   });
  // },
  // bindConfirmPasswordInput: function(e) {

  //   this.setData({
  //     confirmPassword: e.detail.value
  //   });
  // },
  bindNameInput: function(e) {

    this.setData({
      name: e.detail.value
    });
  },
  bindSchoolInput: function(e) {

    this.setData({
      school: e.detail.value
    });
  },
  bindGradeInput: function(e) {

    this.setData({
      grade: e.detail.value
    });
  },
  bindDateChange: function (e) {
    this.setData({
        birthday: e.detail.value
    })
  },
  bindGenderChange: function(e){
    this.setData({
      gender: e.detail.value
  })
  },
  bindMobileInput: function(e) {

    this.setData({
      mobile: e.detail.value
    });
  },
  bindCodeInput: function(e) {

    this.setData({
      code: e.detail.value
    });
  },
  clearInput: function(e) {
    switch (e.currentTarget.id) {
      case 'clear-name':
        this.setData({
          name: ''
        });
        break;
      case 'clear-school':
        this.setData({
          school: ''
        });
        break;
      case 'clear-grade':
        this.setData({
          grade: ''
        });
        break;
      case 'clear-mobile':
        this.setData({
          mobile: ''
        });
        break;
      case 'clear-code':
        this.setData({
          code: ''
        });
        break;
    }
  }
})