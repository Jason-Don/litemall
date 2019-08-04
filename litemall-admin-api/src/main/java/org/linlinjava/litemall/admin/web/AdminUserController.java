package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.domain.LitemallVipOrder;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.db.service.LitemallVipOrderService;
import org.linlinjava.litemall.db.util.VipOrderUtil;
import org.linlinjava.litemall.db.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController {
    private final Log logger = LogFactory.getLog(AdminUserController.class);

    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallVipOrderService vipOrderService;

    @RequiresPermissions("admin:user:list")
    @RequiresPermissionsDesc(menu={"用户管理" , "会员管理"}, button="查询")
    @GetMapping("/list")
    public Object list(String username, String mobile,Integer goodsId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallUser> userList = userService.querySelective(username, mobile,goodsId, page, limit, sort, order);
        List<UserVO> userVOList = new ArrayList<>(userList.size());

        for(LitemallUser userDO : userList){
            UserVO userVO = new UserVO();
            userVO.setId(userDO.getId());
            userVO.setName(userDO.getName());
            userVO.setSchool(userDO.getSchool());
            userVO.setGrade(userDO.getGrade());
            userVO.setMobile(userDO.getMobile());
            userVO.setGender(userDO.getGender());
            userVO.setBirthday(userDO.getBirthday());
            userVO.setBalance(userDO.getBalance());
            userVO.setStatus(userDO.getStatus());

            if(userDO.getVipOrderId()!=null&&userDO.getVipOrderId()!=0){
                LitemallVipOrder vipOrder = vipOrderService.findById(userDO.getVipOrderId());
                Long checkVaild = VipOrderUtil.checkVaild(vipOrder);
                if(checkVaild>=0){
                   userVO.setVipName(vipOrder.getName());
                }
            }
            userVOList.add(userVO);
        }

        return ResponseUtil.okList(userVOList);
    }
}
