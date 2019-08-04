package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.service.AdminOrderService;
import org.linlinjava.litemall.admin.service.AdminVipOrderService;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/admin/vipOrder")
@Validated
public class AdminVipOrderController {
    private final Log logger = LogFactory.getLog(AdminVipOrderController.class);

    @Autowired
    private AdminVipOrderService adminOrderService;

    /**
     * 查询订单
     *
     * @param userId
     * @param orderSn
     * @param orderStatusArray
     * @param page
     * @param limit
     * @param sort
     * @param order
     * @return
     */
    @RequiresPermissions("admin:vipOrder:list")
    @RequiresPermissionsDesc(menu = {"商场管理", "会员订单管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(Integer userId, String orderSn,
                       @RequestParam(required = false) List<Short> orderStatusArray,
                       String payType,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        return adminOrderService.list(userId, orderSn, orderStatusArray, payType, page, limit, sort, order);
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:vipOrder:read")
    @RequiresPermissionsDesc(menu = {"商场管理", "会员订单管理"}, button = "详情")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id) {
        return adminOrderService.detail(id);
    }

    /**
     * 修改订单状态为 付款
     *
     * @param body 订单信息，{ orderId：xxx, actualPrice：xxx }
     * @return
     */
    @RequiresPermissions("admin:vipOrder:pay")
    @RequiresPermissionsDesc(menu = {"商场管理", "会员订单管理"}, button = "确认付款")
    @PostMapping("/pay")
    public Object pay(@RequestBody String body) {
        return adminOrderService.pay(body);
    }

}
