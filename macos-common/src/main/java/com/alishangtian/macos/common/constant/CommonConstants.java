package com.alishangtian.macos.common.constant;

import java.util.Arrays;
import java.util.List;

/**
 * @Description CommonConstants
 * @Date 2020/6/23 下午3:48
 * @Author maoxiaobing
 **/
public class CommonConstants {
    /**
     * broker leader选举key前缀
     **/
    public static final String LEADER_KEY_PREFIX = "leader";
    /**
     * 默认分片key集合
     *
     * @Description TODO
     * @Date 2020/7/3 下午3:28
     * @Author maoxiaobing
     **/
    public static final List<String> DEFAULT_KEYSETS = Arrays.asList(new String[]{"zset:{0}", "zset:{1}", "zset:{2}", "zset:{3}", "zset:{4}", "zset:{5}", "zset:{6}", "zset:{7}", "zset:{8}", "zset:{9}", "zset:{10}", "zset:{11}", "zset:{12}", "zset:{13}", "zset:{14}", "zset:{15}"});
}
