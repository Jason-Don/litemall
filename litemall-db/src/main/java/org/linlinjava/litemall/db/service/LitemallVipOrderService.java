package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallVipOrderMapper;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class LitemallVipOrderService {
    @Resource
    private LitemallVipOrderMapper litemallVipOrderMapper;

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
        LitemallVipOrderExample example = new LitemallVipOrderExample();
        example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return (int) litemallVipOrderMapper.countByExample(example);
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

    public List<LitemallVipOrder> list(Integer userId) {
        LitemallVipOrderExample example = new LitemallVipOrderExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return litemallVipOrderMapper.selectByExample(example);
    }

    public LitemallVipOrder findById(Integer id) {
        return litemallVipOrderMapper.selectByPrimaryKeyWithLogicalDelete(id,false);
    }

    public List<LitemallVipOrder> queryByOrderStatus(Integer userId, List<Short> orderStatus, Integer page, Integer limit, String sort, String order) {
        LitemallVipOrderExample example = new LitemallVipOrderExample();
        example.setOrderByClause(LitemallVipOrder.Column.addTime.desc());
        LitemallVipOrderExample.Criteria criteria = example.or();
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

        return litemallVipOrderMapper.selectByExample(example);
    }

    public List<LitemallVipOrder> queryByIdStatus(Integer id, List<Short> orderStatus) {
        LitemallVipOrderExample example = new LitemallVipOrderExample();
        example.setOrderByClause(LitemallVipOrder.Column.addTime.desc());
        LitemallVipOrderExample.Criteria criteria = example.or();
        criteria.andIdEqualTo(id);
        if (orderStatus != null) {
            criteria.andOrderStatusIn(orderStatus);
        }
        criteria.andDeletedEqualTo(false);

        return litemallVipOrderMapper.selectByExample(example);
    }

    public int add(LitemallVipOrder vipOrder) {
        return litemallVipOrderMapper.insertSelective(vipOrder);
    }

    public int update(LitemallVipOrder vipOrder){
        return litemallVipOrderMapper.updateByPrimaryKeySelective(vipOrder);
    }

    public void deleteById(Integer id) {
        litemallVipOrderMapper.logicalDeleteByPrimaryKey(id);
    }


    public List<LitemallVipOrder> querySelective(Integer userId, String orderSn, List<Short> orderStatusArray, String payType, Integer page, Integer limit, String sort, String order) {
        LitemallVipOrderExample example = new LitemallVipOrderExample();
        LitemallVipOrderExample.Criteria criteria = example.createCriteria();

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
        return litemallVipOrderMapper.selectByExample(example);
    }

    public List<LitemallVipOrder> queryUnpaid(int minutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusMinutes(minutes);
        LitemallVipOrderExample example = new LitemallVipOrderExample();
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_CREATE).andAddTimeLessThan(expired).andDeletedEqualTo(false);
        return litemallVipOrderMapper.selectByExample(example);
    }
}
