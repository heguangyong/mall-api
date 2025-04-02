package com.zbkj.service.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.RedisConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.RestTemplateUtil;
import com.zbkj.common.vo.LogisticsResultListVo;
import com.zbkj.common.vo.LogisticsResultVo;
import com.zbkj.common.vo.OnePassLogisticsQueryVo;
import com.zbkj.service.service.LogisticService;
import com.zbkj.service.service.OnePassService;
import com.zbkj.service.service.SystemConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * ExpressServiceImpl 接口实现
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
public class LogisticsServiceImpl implements LogisticService {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private RestTemplateUtil restTemplateUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private OnePassService onePassService;

    /**
     * 快递
     *
     * @param expressNo String 物流单号
     * @param type      String 快递公司字母简写：不知道可不填 95%能自动识别，填写查询速度会更快 https://market.aliyun.com/products/56928004/cmapi021863.html#sku=yuncode15863000015
     * @param com       快递公司编号
     * @param phone     手机号
     * @return LogisticsResultVo
     */
    @Override
    public LogisticsResultVo info(String expressNo, String type, String com, String phone) {
        LogisticsResultVo resultVo = new LogisticsResultVo();
        String redisKey = RedisConstants.LOGISTICS_KEY + expressNo;
        JSONObject result = getCache(redisKey);
        if (ObjectUtil.isNotNull(result)) {
            return JSONObject.toJavaObject(result, LogisticsResultVo.class);
        }
        String logisticsType = systemConfigService.getValueByKeyException(SysConfigConstants.LOGISTICS_QUERY_TYPE);
        if (logisticsType.equals("1")) {// 平台查询
            OnePassLogisticsQueryVo queryVo = onePassService.exprQuery(expressNo, com, phone);
            if (ObjectUtil.isNull(queryVo)) {
                resultVo.setNumber(expressNo);
                resultVo.setExpName(com);
                return resultVo;
            }
            // 一号通vo转公共返回vo
            resultVo = queryToResultVo(queryVo);
            String jsonString = JSONObject.toJSONString(resultVo);
            saveCache(redisKey, JSONObject.parseObject(jsonString));
        }
        if (logisticsType.equals("2")) {// 阿里云查询
            // 顺丰请输入单号 : 收件人或寄件人手机号后四位。例如：123456789:1234
            if (StrUtil.isNotBlank(com) && com.equals("shunfengkuaiyun")) {
                expressNo = expressNo.concat(":").concat(StrUtil.sub(phone, 7, -1));
            }
            String appCode = systemConfigService.getValueByKey(SysConfigConstants.LOGISTICS_QUERY_ALIYUN_CODE);
            String url = StrUtil.format(SysConfigConstants.LOGISTICS_QUERY_ALIYUN_URL, expressNo);
            if (StringUtils.isNotBlank(type)) {
                url = url.concat(StrUtil.format("&type={}", type));
            }
            HashMap<String, String> header = new HashMap<>();
            header.put("Authorization", "APPCODE " + appCode);

            JSONObject data = restTemplateUtil.getData(url, header);
            checkResult(data);
            //把数据解析成对象返回到前端
            result = data.getJSONObject("result");
            saveCache(redisKey, result);
            resultVo = JSONObject.toJavaObject(result, LogisticsResultVo.class);
        }
        return resultVo;
    }

    /**
     * 一号通vo转公共返回vo
     */
    private LogisticsResultVo queryToResultVo(OnePassLogisticsQueryVo queryVo) {
        LogisticsResultVo resultVo = new LogisticsResultVo();
        resultVo.setNumber(queryVo.getNum());
        resultVo.setExpName(queryVo.getCom());
        resultVo.setIsSign(queryVo.getIscheck());
        resultVo.setDeliveryStatus(queryVo.getStatus());

        if (CollUtil.isNotEmpty(queryVo.getContent())) {
            List<LogisticsResultListVo> list = CollUtil.newArrayList();
            queryVo.getContent().forEach(i -> {
                LogisticsResultListVo listVo = new LogisticsResultListVo();
                listVo.setTime(i.getTime());
                listVo.setStatus(i.getStatus());
                list.add(listVo);
            });
            resultVo.setList(list);
        }
        return resultVo;
    }

    /**
     * 获取快递缓存
     *
     * @return JSONObject
     */
    private JSONObject getCache(String redisKey) {
        Object data = redisUtil.get(redisKey);
        if (ObjectUtil.isNotNull(data)) {
            return JSONObject.parseObject(data.toString());
        }
        return null;
    }

    /**
     * 获取快递缓存
     *
     * @param data JSONObject 需要保存的数据
     */
    private void saveCache(String redisKey, JSONObject data) {
        redisUtil.set(redisKey, data.toJSONString(), 1800L, TimeUnit.SECONDS);
    }

    /**
     * 获取快递缓存
     *
     * @param data JSONObject 检测返回的结果
     */
    private void checkResult(JSONObject data) {
        if (!data.getString("status").equals("0")) {
            throw new CrmebException(data.getString("msg"));
        }
    }
}

