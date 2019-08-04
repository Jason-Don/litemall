package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallVipMapper;
import org.linlinjava.litemall.db.domain.LitemallAd;
import org.linlinjava.litemall.db.domain.LitemallAdExample;
import org.linlinjava.litemall.db.domain.LitemallVip;
import org.linlinjava.litemall.db.domain.LitemallVipExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallVipService {
    @Resource
    private LitemallVipMapper vipMapper;

    /**
     *
     * @return
     */
    public List<LitemallVip> listAll() {
        LitemallVipExample example = new LitemallVipExample();
        example.setOrderByClause("add_time desc");
        return vipMapper.selectByExample(example);
    }

    /**
     *
     * @param id
     * @return
     */
//    public LitemallVip getVipById(Integer id) {
//        return vipMapper.selectByPrimaryKey(id);
//    }


    public LitemallVip selectByPrimaryKey(Integer id) {
        return vipMapper.selectByPrimaryKey(id);
    }

    public List<LitemallVip> validIsExistByLevel(Byte level){
        LitemallVipExample example = new LitemallVipExample();
        LitemallVipExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(level)) {
            criteria.andLevelEqualTo( level);
        }
        return vipMapper.selectByExample(example);
    }

    public List<LitemallVip> validIsExisByName(String name){
        LitemallVipExample example = new LitemallVipExample();
        LitemallVipExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameEqualTo( name);
        }
        return vipMapper.selectByExample(example);
    }

    public List<LitemallVip> querySelective(String name,Integer page, Integer limit, String sort, String order) {
        LitemallVipExample example = new LitemallVipExample();
        LitemallVipExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return vipMapper.selectByExample(example);
    }

    public int updateById(LitemallVip ad) {
        ad.setUpdateTime(LocalDateTime.now());
        return vipMapper.updateByPrimaryKeySelective(ad);
    }

    public void deleteById(Integer id) {
        vipMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallVip ad) {
        ad.setAddTime(LocalDateTime.now());
        ad.setUpdateTime(LocalDateTime.now());
        vipMapper.insertSelective(ad);
    }
}
