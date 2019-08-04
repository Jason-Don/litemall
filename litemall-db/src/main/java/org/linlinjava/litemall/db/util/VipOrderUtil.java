package org.linlinjava.litemall.db.util;

import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallVipOrder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public class VipOrderUtil {



    public static Long checkVaild(LitemallVipOrder vipOrder){
        long between = ChronoUnit.DAYS.between(LocalDate.now(), vipOrder.getEndTime());
        return between;
    }

}
