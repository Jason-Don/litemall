package org.linlinjava.litemall.db.vo;

import org.linlinjava.litemall.db.domain.LitemallUser;

public class UserVO extends LitemallUser {

    private String vipName;

    public String getVipName() {
        return vipName;
    }

    public void setVipName(String vipName) {
        this.vipName = vipName;
    }
}
