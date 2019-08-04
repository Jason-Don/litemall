package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallOrderMapper;
import org.linlinjava.litemall.db.dao.OrderMapper;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderExample;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class LitemallOrderService {
    @Resource
    private LitemallOrderMapper litemallOrderMapper;
    @Resource
    private OrderMapper orderMapper;

    public int add(LitemallOrder order) {
        order.setAddTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        return litemallOrderMapper.insertSelective(order);
    }

    public int count(Integer userId) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return (int) litemallOrderMapper.countByExample(example);
    }

    public LitemallOrder findById(Integer orderId) {
        return litemallOrderMapper.selectByPrimaryKey(orderId);
    }

    public List<LitemallOrder> findByIds_PayOrder(List<Integer> orderIds) {

        LitemallOrderExample example = new LitemallOrderExample();

        LitemallOrderExample.Criteria criteria = example.or();
        if (orderIds!=null && orderIds.size() >0) {
            criteria.andIdIn(orderIds);
        }
        criteria.andOrderStatusEqualTo(OrderUtil.STATUS_PAY);
        criteria.andDeletedEqualTo(false);

        return litemallOrderMapper.selectByExample(example);
    }


    private String getRandomNum(Integer num) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public int countByOrderSn(Integer userId, String orderSn) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return (int) litemallOrderMapper.countByExample(example);
    }

    // TODO 这里应该产生一个唯一的订单，但是实际上这里仍然存在两个订单相同的可能性
    public String generateOrderSn(Integer userId) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = df.format(LocalDate.now());
        String orderSn = now + getRandomNum(6);
        while (countByOrderSn(userId, orderSn) != 0) {
            orderSn = getRandomNum(6);
        }
        return orderSn;
    }

    public List<LitemallOrder> queryByOrderStatus(Integer userId,boolean isVerifyTime, List<Short> orderStatus, Integer page, Integer limit,
                                                  String sort, String order)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LitemallOrderExample example = new LitemallOrderExample();
        example.setOrderByClause(LitemallOrder.Column.addTime.desc());
        LitemallOrderExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        if (orderStatus != null) {
            criteria.andOrderStatusIn(orderStatus);
        }
        criteria.andDeletedEqualTo(false);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }
        //page == 0 不分页
        if(page > 0){
            PageHelper.startPage(page, limit);
        }

        if(isVerifyTime){
        //加入自定义sql
            String sql = "id in (select og.order_id from litemall_order_goods og " +
                    "join litemall_goods g on g.id = og.goods_id and g.endtime >= CURRENT_DATE() and g.endtime is not null)";
            Class<?> criteriaCls  =  LitemallOrderExample.Criteria.class;
            Class<?> generatedCriteriaCls = criteriaCls.getSuperclass();
            Method addCriterion = generatedCriteriaCls.getDeclaredMethod("addCriterion", String.class);
            addCriterion.setAccessible(true);
            addCriterion.invoke(criteria,sql);
        }

        return litemallOrderMapper.selectByExample(example);
    }

    public List<LitemallOrder> querySelective(Integer userId, String orderSn, List<Short> orderStatusArray, String payType, Integer page, Integer limit, String sort, String order) {
        LitemallOrderExample example = new LitemallOrderExample();
        LitemallOrderExample.Criteria criteria = example.createCriteria();

        if (userId != null) {
            criteria.andUserIdEqualTo(userId);
        }
        if (!StringUtils.isEmpty(orderSn)) {
            criteria.andOrderSnEqualTo(orderSn);
        }
        if (orderStatusArray != null && orderStatusArray.size() != 0) {
            criteria.andOrderStatusIn(orderStatusArray);
        }

        if (!StringUtils.isEmpty(payType)) {
            criteria.andPayTypeEqualTo(payType);
        }

        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return litemallOrderMapper.selectByExample(example);
    }

    public int updateWithOptimisticLocker(LitemallOrder order) {
        LocalDateTime preUpdateTime = order.getUpdateTime();
        order.setUpdateTime(LocalDateTime.now());
        return orderMapper.updateWithOptimisticLocker(preUpdateTime, order);
    }

    public void deleteById(Integer id) {
        litemallOrderMapper.logicalDeleteByPrimaryKey(id);
    }

    public int count() {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andDeletedEqualTo(false);
        return (int) litemallOrderMapper.countByExample(example);
    }

    public List<LitemallOrder> queryUnpaid(int minutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusMinutes(minutes);
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_CREATE).andAddTimeLessThan(expired).andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    public List<LitemallOrder> queryUnconfirm(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusDays(days);
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_SHIP).andShipTimeLessThan(expired).andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    public LitemallOrder findBySn(String orderSn) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return litemallOrderMapper.selectOneByExample(example);
    }

    public Map<Object, Object> orderInfo(Integer userId) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        List<LitemallOrder> orders = litemallOrderMapper.selectByExampleSelective(example, LitemallOrder.Column.orderStatus, LitemallOrder.Column.comments);

        int unpaid = 0;
        int unship = 0;
        int unrecv = 0;
        int uncomment = 0;
        for (LitemallOrder order : orders) {
            if (OrderUtil.isCreateStatus(order)) {
                unpaid++;
            } else if (OrderUtil.isPayStatus(order)) {
                unship++;
            } else if (OrderUtil.isShipStatus(order)) {
                unrecv++;
            } else if (OrderUtil.isConfirmStatus(order) || OrderUtil.isAutoConfirmStatus(order)) {
                uncomment += order.getComments();
            } else {
                // do nothing
            }
        }

        Map<Object, Object> orderInfo = new HashMap<Object, Object>();
        orderInfo.put("unpaid", unpaid);
        orderInfo.put("unship", unship);
        orderInfo.put("unrecv", unrecv);
        orderInfo.put("uncomment", uncomment);
        return orderInfo;

    }

    public List<LitemallOrder> queryComment(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusDays(days);
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andCommentsGreaterThan((short) 0).andConfirmTimeLessThan(expired).andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }
}
