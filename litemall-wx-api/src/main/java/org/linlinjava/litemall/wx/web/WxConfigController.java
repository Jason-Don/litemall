package org.linlinjava.litemall.wx.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.service.LitemallSystemConfigService;
import org.linlinjava.litemall.wx.service.GetRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 商城信息
 */
@RestController
@RequestMapping("/wx/config")
@Validated
public class WxConfigController extends GetRegionService {
	private final Log logger = LogFactory.getLog(WxConfigController.class);

	@Autowired
	private LitemallSystemConfigService systemConfigService;

	/**
	 * 商城		信息
	 * @return
	 */
	@GetMapping("/mall")
	public Object listMall() {
		Map<String, String> data = systemConfigService.listMail();
		return ResponseUtil.ok(data);
	}


}