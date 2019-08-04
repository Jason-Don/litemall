package org.linlinjava.litemall.wx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.domain.LitemallVip;
import org.linlinjava.litemall.db.domain.LitemallVipOrder;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.db.service.LitemallVipOrderService;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.db.util.VipOrderUtil;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务
 */
@RestController
@RequestMapping("/wx/user")
@Validated
public class WxUserController {
    private final Log logger = LogFactory.getLog(WxUserController.class);

    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallVipOrderService vipOrderService;

    /**
     * 用户个人页面数据
     * <p>
     * 目前是用户订单统计信息
     *
     * @param userId 用户ID
     * @return 用户个人页面数据
     */
    @GetMapping("index")
    public Object list(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        Map<Object, Object> data = new HashMap<Object, Object>();
        data.put("order", orderService.orderInfo(userId));

        Map<Object, Object> vipInfo = new HashMap<Object, Object>();
        vipInfo.put("name",null);

        LitemallUser user = userService.findById(userId);
        if( !"null".equals(user.getVipOrderId()) ){
            List<Short> status = new ArrayList<Short>(2);
            status.add(OrderUtil.STATUS_PAY);
            //其实可以不加status添件，因为当vipOrder支付成功时，vipOrder的id才会更新到user上
            List<LitemallVipOrder> vipOrders = vipOrderService.queryByIdStatus(user.getVipOrderId(), status);

            if(vipOrders.size()>0){
                for(LitemallVipOrder vipOrder : vipOrders){
                    long between = VipOrderUtil.checkVaild(vipOrder);
                    if(between>=0){//有效
                        vipInfo.put("name",vipOrder.getName());
                    }else{
                        break;
                    }
                }
            }
        }
        vipInfo.put("balance",user.getBalance());
        data.put("vipInfo",vipInfo);
        return ResponseUtil.ok(data);
    }

}