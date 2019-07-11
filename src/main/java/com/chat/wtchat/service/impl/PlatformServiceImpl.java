package com.chat.wtchat.service.impl;

import com.chat.wtchat.dao.PlatformDao;
import com.chat.wtchat.model.Platform;
import com.chat.wtchat.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 平台服务实现类
 *
 * @author Zed
 */
@Service("platformService")
public class PlatformServiceImpl implements PlatformService {

    @Autowired
    private PlatformDao platformDao;


    @Override
    public List<Platform> getAll() {
        return platformDao.findAll();
    }
}
