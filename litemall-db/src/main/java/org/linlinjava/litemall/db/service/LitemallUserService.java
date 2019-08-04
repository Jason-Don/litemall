package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallUserMapper;
import org.linlinjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LitemallUserService {
    @Resource
    private LitemallUserMapper userMapper;
    @Resource
    private LitemallOrderGoodsService litemallOrderGoodsService;
    @Resource
    private LitemallOrderService litemallOrderService;

    public LitemallUser findById(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    public UserVo findUserVoById(Integer userId) {
        LitemallUser user = findById(userId);
        UserVo userVo = new UserVo();
        userVo.setNickname(user.getNickname());
        userVo.setAvatar(user.getAvatar());
        userVo.setName(user.getName());
        return userVo;
    }

    public LitemallUser queryByOid(String openId) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andWeixinOpenidEqualTo(openId).andDeletedEqualTo(false);
        return userMapper.selectOneByExample(example);
    }

    public void add(LitemallUser user) {
        user.setAddTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insertSelective(user);
    }

    public int updateById(LitemallUser user) {
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateByPrimaryKeySelective(user);
    }

    public List<LitemallUser> querySelective(String username, String mobile,Integer goodsId, Integer page, Integer size, String sort, String order) {
        LitemallUserExample example = new LitemallUserExample();
        LitemallUserExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(goodsId)) {
            //goodsId不为空，查找购买此goodsId课程的用户
            List<LitemallOrderGoods> orderGoodsList = litemallOrderGoodsService.findByGid(goodsId);

            if(orderGoodsList.size()>0){
                List<Integer> orderIdList = new ArrayList<>(16);
                orderGoodsList.forEach(orderGoods ->{ orderIdList.add(orderGoods.getOrderId()); });

                List<LitemallOrder> payOrderList = litemallOrderService.findByIds_PayOrder(orderIdList);
                if(payOrderList.size()>0){
                    List<Integer> userIdList = new ArrayList<>(16);
                    payOrderList.forEach(payOrder ->{ userIdList.add(payOrder.getUserId()); });
                    criteria.andIdIn(userIdList);
                }else{
                    //用户下单 但没有付款成功的
                    return new ArrayList<>();
                }
            }else{
                //没有用户下单
                return new ArrayList<>();            }
        }

        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameLike("%" + username + "%");
        }
        if (!StringUtils.isEmpty(mobile)) {
            criteria.andMobileEqualTo(mobile);
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return userMapper.selectByExample(example);
    }

    public int count() {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andDeletedEqualTo(false);

        return (int) userMapper.countByExample(example);
    }

    public List<LitemallUser> queryByUsername(String username) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public boolean checkByUsername(String username) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return userMapper.countByExample(example) != 0;
    }

    public List<LitemallUser> queryByMobile(String mobile) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andMobileEqualTo(mobile).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public List<LitemallUser> queryByOpenid(String openid) {
        LitemallUserExample example = new LitemallUserExample();
        example.or().andWeixinOpenidEqualTo(openid).andDeletedEqualTo(false);
        return userMapper.selectByExample(example);
    }

    public void deleteById(Integer id) {
        userMapper.logicalDeleteByPrimaryKey(id);
    }
}
