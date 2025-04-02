package com.zbkj.service.service.impl;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.service.service.QiNiuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;


/**
 * AsyncServiceImpl 同步到云服务
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class QiNiuServiceImpl implements QiNiuService {
    private static final Logger logger = LoggerFactory.getLogger(QiNiuServiceImpl.class);

    @Override
    public void uploadFile(UploadManager uploadManager, String upToken, String webPth, String localFile, File file) {
        try {
            if(!file.exists()){
                logger.error("上传文件" + localFile + "不存在：");
                return;
            }
            Response put = uploadManager.put(file, webPth, upToken);
            put.close();
        } catch (QiniuException ex) {
            throw new CrmebException(ex.getMessage());
        }
    }
}

