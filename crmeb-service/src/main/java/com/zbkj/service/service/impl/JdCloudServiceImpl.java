package com.zbkj.service.service.impl;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.service.service.JdCloudService;
import com.zbkj.service.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * JdCloudServiceImpl 接口实现
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
@Slf4j
@Service
public class JdCloudServiceImpl implements JdCloudService {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 文件上传
     * @param fileName 文件名称
     * @param localFilePath 本地文件地址
     * @param bucket 存储桶名称
     */
    @Override
    public void uploadFile(String fileName, String localFilePath, String bucket) {
        File file = new File(localFilePath);
        if(!file.exists()){
            log.error("京东云存储上传文件，不存在，fileName = {}， localFilePath = {}", fileName, localFilePath);
            return;
        }
        AmazonS3 s3 = getAmazonS3Client();
        try {
            if (!s3.doesBucketExistV2(bucket)) {
                s3.createBucket(bucket);
            }

            PutObjectResult putObjectResult = s3.putObject(bucket, fileName, file);
            log.info("京东云存储上传文件结束：{}", putObjectResult.getETag());
        } catch (Exception e) {
            log.error("upload JD OSS file error : ", e);
            throw new CrmebException(e.getMessage());
        } finally {
            s3.shutdown();
        }

    }

    private AmazonS3 getAmazonS3Client() {
        String endpoint = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_JD_CLOUD_ENDPOINT);
        String accessKey = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_JD_ACCESS_KEY);
        String secretKey = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_JD_SECRET_KEY);
        String signingRegion = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_JD_CLOUD_SIGNING_REGION);

        ClientConfiguration config = new ClientConfiguration();

        AwsClientBuilder.EndpointConfiguration endpointConfig =
                new AwsClientBuilder.EndpointConfiguration(endpoint, signingRegion);

        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey,secretKey);
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);

        AmazonS3 s3 = AmazonS3Client.builder()
                .withEndpointConfiguration(endpointConfig)
                .withClientConfiguration(config)
                .withCredentials(awsCredentialsProvider)
                .disableChunkedEncoding()
                .build();

        return s3;
    }
}

