package org.linlinjava.litemall.wx.service;

import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallGoods.Column;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallVip;
import org.linlinjava.litemall.db.domain.LitemallVipOrder;
import org.linlinjava.litemall.db.service.LitemallVipOrderService;
import org.linlinjava.litemall.db.service.LitemallVipService;
import org.linlinjava.litemall.db.util.OrderHandleOption;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.linlinjava.litemall.wx.util.WxResponseCode.ORDER_INVALID_OPERATION;

@Service
public class WxVIPService {
    @Resource
    private LitemallVipService litemallVipService;
//    @Resource
//    private LitemallVipOrderMapper vipOrderMapper;
    @Resource
    private LitemallVipOrderService litemallVipOrderService;

    /**
     *
     * @return
     */
    public List<LitemallVip> listAll() {
        return litemallVipService.listAll();
    }

    /**
     *
     * @param id
     * @return
     */
    public LitemallVip getVipById(Integer id) {
        return litemallVipService.selectByPrimaryKey(id);
    }

    @Transactional
    public Object vipOrderSubmit(Integer userId, String body){
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (body == null) {
            return ResponseUtil.badArgument();
        }
        Integer vipId = JacksonUtil.parseInteger(body, "vipId");
        String payType = JacksonUtil.parseString(body, "payType");
        LitemallVip litemallVip = litemallVipService.selectByPrimaryKey(vipId);
        if("offline".equals(payType)){
            LitemallVipOrder vipOrder = new LitemallVipOrder();
            vipOrder.setOrderSn(litemallVipOrderService.generateOrderSn(userId));
            vipOrder.setUserId(userId);
            vipOrder.setVipId(vipId);
            vipOrder.setName(litemallVip.getName());
            vipOrder.setOrderPrice(litemallVip.getPrice());
            vipOrder.setActualPrice(litemallVip.getPrice());
            vipOrder.setDiscount(litemallVip.getDiscount());
            vipOrder.setDiscountName(litemallVip.getDiscountName());
            vipOrder.setValidDays(litemallVip.getValidDays());
            vipOrder.setAddTime(LocalDateTime.now());
            vipOrder.setStartTime(LocalDate.now());
            vipOrder.setEndTime(LocalDate.now().plusDays(litemallVip.getValidDays()));
            vipOrder.setOrderStatus(OrderUtil.STATUS_CREATE);
            vipOrder.setPayType(payType);
            litemallVipOrderService.add(vipOrder);
            return ResponseUtil.ok(vipOrder);
        }else {
            // TODO 线上支付
        }
        return ResponseUtil.fail();

    }

    /**
     *
     * @return
     */
    public Object vipOrderList(Integer userId, Integer showType,
                                               Integer page, Integer limit, String sort, String order) {

        List<Short> orderStatus = OrderUtil.orderStatus(showType);

        List<LitemallVipOrder> vipOrderList = litemallVipOrderService.queryByOrderStatus(userId, orderStatus, page, limit, sort, order);

        List<Map<String, Object>> vipOrderVoList = new ArrayList<>(vipOrderList.size());

        vipOrderList.forEach(vipOrder -> {
            LitemallOrder litemallOrder =  new LitemallOrder();
            litemallOrder.setOrderStatus(vipOrder.getOrderStatus());
            litemallOrder.setPayType(vipOrder.getPayType());

            Map<String, Object> vipOrderVo = new HashMap<>();
            vipOrderVo.put("id",vipOrder.getId());
            vipOrderVo.put("orderSn",vipOrder.getOrderSn());
            vipOrderVo.put("orderStatusText", OrderUtil.orderStatusText(litemallOrder));
            vipOrderVo.put("handleOption", OrderUtil.build(litemallOrder));
            vipOrderVo.put("payTypeText",OrderUtil.payTypeText(litemallOrder));
            vipOrderVo.put("addTime",vipOrder.getAddTime());

//            LitemallVip litemallVip = litemallVipService.selectByPrimaryKey(vipOrder.getVipId());

            vipOrderVo.put("vipName",vipOrder.getName());
            vipOrderVo.put("actualPrice", vipOrder.getActualPrice());
            vipOrderVo.put("discount",vipOrder.getDiscount());
            vipOrderVo.put("discountName",vipOrder.getDiscountName());
            vipOrderVo.put("addTime",vipOrder.getAddTime());
            vipOrderVo.put("startTime",vipOrder.getStartTime());
            vipOrderVo.put("validDays",vipOrder.getValidDays());
            vipOrderVo.put("endTime",vipOrder.getEndTime());


            vipOrderVoList.add(vipOrderVo);
        });
        return ResponseUtil.okList(vipOrderVoList, vipOrderList);

    }

    public Object vipOrderDetail(Integer userId, Integer id) {


        LitemallVipOrder vipOrder = litemallVipOrderService.findById(id);


            LitemallOrder litemallOrder =  new LitemallOrder();
            litemallOrder.setOrderStatus(vipOrder.getOrderStatus());
            litemallOrder.setPayType(vipOrder.getPayType());

            Map<String, Object> vipOrderVo = new HashMap<>();
            vipOrderVo.put("id",vipOrder.getId());
            vipOrderVo.put("orderSn",vipOrder.getOrderSn());
            vipOrderVo.put("orderStatusText", OrderUtil.orderStatusText(litemallOrder));
            vipOrderVo.put("handleOption", OrderUtil.build(litemallOrder));
            vipOrderVo.put("payTypeText",OrderUtil.payTypeText(litemallOrder));
            vipOrderVo.put("addTime",vipOrder.getAddTime());

//            LitemallVip litemallVip = litemallVipService.selectByPrimaryKey(vipOrder.getVipId());

            vipOrderVo.put("vipName",vipOrder.getName());
        vipOrderVo.put("orderPrice", vipOrder.getOrderPrice());
        vipOrderVo.put("actualPrice", vipOrder.getActualPrice());
            vipOrderVo.put("discount",vipOrder.getDiscount());
            vipOrderVo.put("discountName",vipOrder.getDiscountName());
            vipOrderVo.put("addTime",vipOrder.getAddTime());
            vipOrderVo.put("startTime",vipOrder.getStartTime());
            vipOrderVo.put("validDays",vipOrder.getValidDays());
            vipOrderVo.put("endTime",vipOrder.getEndTime());


        return ResponseUtil.ok(vipOrderVo);

    }

    @Transactional
    public Object vipOrderCancel(Integer userId, String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallVipOrder litemallVipOrder = litemallVipOrderService.findById(orderId);
        if (litemallVipOrder == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (!litemallVipOrder.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        LitemallOrder order = new LitemallOrder();
        order.setOrderStatus(litemallVipOrder.getOrderStatus());
        order.setPayType(litemallVipOrder.getPayType());

        // 检测是否能够取消
        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isCancel()) {
            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能取消");
        }

        LitemallVipOrder updateVipOrder = new LitemallVipOrder();
        // 设置订单已取消状态
        updateVipOrder.setId(litemallVipOrder.getId());
        updateVipOrder.setOrderStatus(OrderUtil.STATUS_CANCEL);
//        order.setEndTime(LocalDateTime.now());
        if (litemallVipOrderService.update(updateVipOrder) == 0) {
            throw new RuntimeException("更新数据已失效");
        }

        return ResponseUtil.ok();
    }

    public Object vipOrderDelete(Integer userId, String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallVipOrder litemallVipOrder = litemallVipOrderService.findById(orderId);
        if (litemallVipOrder == null) {
            return ResponseUtil.badArgument();
        }
        if (!litemallVipOrder.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        LitemallOrder order = new LitemallOrder();
        order.setOrderStatus(litemallVipOrder.getOrderStatus());
        order.setPayType(litemallVipOrder.getPayType());

        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isDelete()) {
            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能删除");
        }

        // 订单order_status没有字段用于标识删除
        // 而是存在专门的delete字段表示是否删除
        litemallVipOrderService.deleteById(orderId);

        return ResponseUtil.ok();
    }

}
