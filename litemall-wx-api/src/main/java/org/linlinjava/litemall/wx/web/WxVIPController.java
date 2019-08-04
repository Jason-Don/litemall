package org.linlinjava.litemall.wx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallVip;
import org.linlinjava.litemall.wx.service.WxVIPService;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.service.GetRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * 会员
 */
@RestController
@RequestMapping("/wx/vip")
@Validated
public class WxVIPController extends GetRegionService {
	private final Log logger = LogFactory.getLog(WxVIPController.class);

	@Autowired
	private WxVIPService wxVIPService;

	/**
	 * 会员列表
	 * @return
	 */
	@GetMapping("/listAll")
	public Object listAll(@LoginUser Integer userId) {
		if (userId == null) {
			return ResponseUtil.unlogin();
		}
		List<LitemallVip> litemallVips = wxVIPService.listAll();
		return ResponseUtil.ok(litemallVips);
	}

	/**
	 * 获取会员信息
	 * @return
	 */
	@GetMapping("/getVipById")
	public Object getVipById(@LoginUser Integer userId, Integer vipId) {
		if (userId == null) {
			return ResponseUtil.unlogin();
		}
		LitemallVip litemallVip = wxVIPService.getVipById(vipId);
		return ResponseUtil.ok(litemallVip);
	}

	/**
	 * 会员购买下单
	 * @return
	 */
	@PostMapping("/vipOrderSubmit")
	public Object vipOrderSubmit(@LoginUser Integer userId,@RequestBody String body) {
		if (userId == null) {
			return ResponseUtil.unlogin();
		}
		return wxVIPService.vipOrderSubmit(userId,body);

	}
	/**
	 * 会员列表
	 * @return
	 */
	@GetMapping("vipOrderList")
	public Object vipOrderList(@LoginUser Integer userId,
			@RequestParam(defaultValue = "0") Integer showType,
							   @RequestParam(defaultValue = "1") Integer page,
							   @RequestParam(defaultValue = "10") Integer limit,
							   @Sort @RequestParam(defaultValue = "add_time") String sort,
							   @Order @RequestParam(defaultValue = "desc") String order) {
		if (userId == null) {
			return ResponseUtil.unlogin();
		}
		return wxVIPService.vipOrderList(userId, showType, page, limit, sort, order);
	}

    @GetMapping("vipOrderDetail")
    public Object vipOrderDetail(@LoginUser Integer userId, @NotNull Integer orderId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        return wxVIPService.vipOrderDetail(userId, orderId);
    }
    @PostMapping("vipOrderCancel")
    public Object vipOrderCancel(@LoginUser Integer userId, @RequestBody String body) {
        return wxVIPService.vipOrderCancel(userId, body);
    }
    @PostMapping("vipOrderDelete")
    public Object vipOrderDelete(@LoginUser Integer userId, @RequestBody String body) {
        return wxVIPService.vipOrderDelete(userId, body);
    }

}