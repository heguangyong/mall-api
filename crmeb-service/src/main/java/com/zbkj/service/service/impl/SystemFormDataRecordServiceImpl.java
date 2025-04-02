package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.system.SystemFormDataRecord;
import com.zbkj.service.dao.SystemFormDataRecordDao;
import com.zbkj.service.service.SystemFormDataRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author HZW
 * @description SystemFormDataRecordServiceImpl 接口实现
 * @date 2024-08-01
 */
@Service
public class SystemFormDataRecordServiceImpl extends ServiceImpl<SystemFormDataRecordDao, SystemFormDataRecord> implements SystemFormDataRecordService {

    @Resource
    private SystemFormDataRecordDao dao;


}

