package com.alishangtian.mubbo.service;

import com.alishangtian.macos.common.entity.MubboBody;
import com.alishangtian.macos.common.util.JSONUtils;
import com.alishangtian.mubbo.provider.annotation.MubboService;
import com.alishangtian.mubbo.provider.annotation.ServiceProvider;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author shangtian
 * @description
 * @date 2021/1/8:15
 */
@Service
@Log4j2
@MubboService("mubboService")
public class MubboServerService {

    private ConcurrentMap<Integer, MubboBody> database = Maps.newConcurrentMap();
    private AtomicInteger idSequence = new AtomicInteger();

    @ServiceProvider("/insert")
    public Integer insert(MubboBody mubboBody) {
        mubboBody.setId(idSequence.getAndIncrement());
        log.info("mubboService/insert mubboBody:{}", JSONUtils.toJSONString(mubboBody));
        database.put(mubboBody.getId(), mubboBody);
        return mubboBody.getId();
    }

    @ServiceProvider("/update")
    public MubboBody update(MubboBody mubboBody) {
        log.info("mubboConsumerService/update mubboBody:{}", JSONUtils.toJSONString(mubboBody));
        if (null == mubboBody.getId()) {
            mubboBody.setId(-1);
            return mubboBody;
        }
        MubboBody mubboBody1 = database.get(mubboBody.getId());
        if (null == mubboBody1) {
            mubboBody.setId(-1);
            return mubboBody;
        }
        BeanUtils.copyProperties(mubboBody, mubboBody1);
        database.put(mubboBody1.getId(), mubboBody1);
        return mubboBody1;
    }

    @ServiceProvider("/delete")
    public Integer delete(Integer id) {
        log.info("mubboConsumerService/update delete id:{}", id);
        if (null != database.remove(id)) {
            return id;
        }
        return -1;
    }
}
