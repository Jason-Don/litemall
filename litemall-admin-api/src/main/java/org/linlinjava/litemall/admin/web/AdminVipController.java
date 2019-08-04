package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallAd;
import org.linlinjava.litemall.db.domain.LitemallVip;
import org.linlinjava.litemall.db.service.LitemallAdService;
import org.linlinjava.litemall.db.service.LitemallVipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/admin/vip")
@Validated
public class AdminVipController {
    private final Log logger = LogFactory.getLog(AdminVipController.class);

    @Autowired
    private LitemallVipService vipService;

    @RequiresPermissions("admin:vip:list")
    @RequiresPermissionsDesc(menu={"推广管理" , "会员管理"}, button="查询")
    @GetMapping("/list")
    public Object list(String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallVip> litemallVips = vipService.querySelective(name, page, limit, sort, order);
        return ResponseUtil.okList(litemallVips);
    }

    private Object validate(LitemallVip ad) {
        String name = ad.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }
        BigDecimal price = ad.getPrice();
        if (StringUtils.isEmpty(price)) {
            return ResponseUtil.badArgument();
        }
        BigDecimal discount = ad.getDiscount();
        if (StringUtils.isEmpty(discount)) {
            return ResponseUtil.badArgument();
        }
        String discountName = ad.getDiscountName();
        if (StringUtils.isEmpty(discountName)) {
            return ResponseUtil.badArgument();
        }
        Integer validDays = ad.getValidDays();
        if (StringUtils.isEmpty(validDays) || validDays == 0) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    @RequiresPermissions("admin:vip:create")
    @RequiresPermissionsDesc(menu={"推广管理" , "会员管理"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody LitemallVip ad) {
        Object error = validate(ad);
        if (error != null) {
            return error;
        }
        List<LitemallVip> litemallVips = vipService.validIsExistByLevel(ad.getLevel());
        if(litemallVips.size()>0){
            return ResponseUtil.fail(-1,"会员等级'"+ad.getLevel()+"'已存在");
        }
        List<LitemallVip> litemallVips2 = vipService.validIsExisByName(ad.getName());
        if(litemallVips2.size()>0){
            return ResponseUtil.fail(-1,"会员名称\""+ad.getName()+"\"已存在");
        }
        vipService.add(ad);
        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:vip:read")
    @RequiresPermissionsDesc(menu={"推广管理" , "会员管理"}, button="详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        LitemallVip ad = vipService.selectByPrimaryKey(id);
        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:vip:update")
    @RequiresPermissionsDesc(menu={"推广管理" , "会员管理"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody LitemallVip ad) {
        Object error = validate(ad);
        if (error != null) {
            return error;
        }
        if (vipService.updateById(ad) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:vip:delete")
    @RequiresPermissionsDesc(menu={"推广管理" , "会员管理"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallAd ad) {
        Integer id = ad.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        vipService.deleteById(id);
        return ResponseUtil.ok();
    }

}
