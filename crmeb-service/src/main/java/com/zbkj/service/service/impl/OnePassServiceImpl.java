package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.OnePassConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.result.OnePassResultCode;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.OnePassApplicationInfoVo;
import com.zbkj.common.vo.OnePassLogisticsQueryVo;
import com.zbkj.common.vo.OnePassUserInfoVo;
import com.zbkj.service.service.OnePassService;
import com.zbkj.service.service.SystemConfigService;
import com.zbkj.service.util.OnePassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OnePassService 接口实现
 * 一号通
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
public class OnePassServiceImpl implements OnePassService {

    private static final Logger logger = LoggerFactory.getLogger(OnePassSmsServiceImpl.class);

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private OnePassUtil onePassUtil;

    /**
     * 一号通用户信息
     * @return OnePassUserInfoVo
     */
    @Override
    public OnePassUserInfoVo info() {
        return getInfo();
    }

    private OnePassUserInfoVo getInfo() {
        OnePassApplicationInfoVo infoVo = getApplicationInfoException();
        String accessToken = onePassUtil.getToken(infoVo);
        HashMap<String, String> header = onePassUtil.getCommonHeader(accessToken);
        JSONObject jsonObject = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.USER_INFO_URI_V2, null, header);
        OnePassUserInfoVo userInfoVo = jsonObject.getObject("data", OnePassUserInfoVo.class);
        userInfoVo.setAccount(userInfoVo.getAccount());
        return userInfoVo;
    }

    /**
     * 复制平台商品
     * @param url 商品链接
     * 平台复制商品示例
     * {"msg":"ok","data":{"image":"http://img.alicdn.com/imgextra/i2/2201504973406/O1CN01C7wCJe1b1zdtDz50j_!!2201504973406.jpg","store_info":"豹纹衣身,潮酷有型","give_integral":0,"cost":0,"temp_id":"","description_images":["http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01tGSA611b1zaDd3sOk_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01zrSHcP1b1zaPZB9IR_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i2/2201504973406/O1CN01X8KO5g1b1zaJcCOaN_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i2/2201504973406/O1CN01cS03ht1b1zaNO9kKJ_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01cCfVkZ1b1zaLXTr3v_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01zqsHfb1b1zaONUmg7_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01pD7WGr1b1zaLujJVA_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01hB0cth1b1zaONVO6K_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01aAWvO51b1zaLNWxzL_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01DweEqg1b1zaLXSBBL_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01bk0dqy1b1zaMlYKrE_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01PwOQL41b1zaJFfOVL_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01Npc2Nx1b1zaLXSmZ2_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01CYZLU51b1zaDd2fZG_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01tXnKqb1b1zaJcAqxs_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i3/2201504973406/O1CN0145DNqP1b1zaLuhZRf_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01QBjDyr1b1zaPZ9wTi_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01120POL1b1zaNOAHdT_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01C0YYiq1b1zaLNXdZZ_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01txpuLk1b1zaNO9LQZ_!!2201504973406.jpg"],"description":"<img src=\"http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01tGSA611b1zaDd3sOk_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01zrSHcP1b1zaPZB9IR_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i2/2201504973406/O1CN01X8KO5g1b1zaJcCOaN_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i2/2201504973406/O1CN01cS03ht1b1zaNO9kKJ_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01cCfVkZ1b1zaLXTr3v_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01zqsHfb1b1zaONUmg7_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01pD7WGr1b1zaLujJVA_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01hB0cth1b1zaONVO6K_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01aAWvO51b1zaLNWxzL_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01DweEqg1b1zaLXSBBL_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01bk0dqy1b1zaMlYKrE_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01PwOQL41b1zaJFfOVL_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01Npc2Nx1b1zaLXSmZ2_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01CYZLU51b1zaDd2fZG_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01tXnKqb1b1zaJcAqxs_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i3/2201504973406/O1CN0145DNqP1b1zaLuhZRf_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01QBjDyr1b1zaPZ9wTi_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01120POL1b1zaNOAHdT_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i1/2201504973406/O1CN01C0YYiq1b1zaLNXdZZ_!!2201504973406.jpg\"><img src=\"http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01txpuLk1b1zaNO9LQZ_!!2201504973406.jpg\">","cate_id":"","slider_image":["http://img.alicdn.com/imgextra/i2/2201504973406/O1CN01C7wCJe1b1zdtDz50j_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i4/2201504973406/O1CN01y07EFV1b1zaLru5es_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01dvxZXQ1b1zaJFgGWj_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i2/2201504973406/O1CN0178H0r51b1zaLrvZ56_!!2201504973406.jpg","http://img.alicdn.com/imgextra/i3/2201504973406/O1CN01Rw9wYQ1b1zaPznzFQ_!!2201504973406.jpg"],"soure_link":"","attrs":[],"unit_name":"件","postage":0,"video_link":"","price":0,"store_name":"CacheCache短袖t恤女装2019新款潮豹纹牛油果绿抹茶绿古着感少女","ficti":0,"keyword":"","stock":0,"add_time":0,"items":[{"detail":["160/84A/S","165/88A/M","170/92A/L","175/96A/XL"],"value":"尺码"},{"detail":["蜡黄色/757"],"value":"主要颜色"}],"ot_price":0,"info":{"header":[{"minWidth":120,"title":"尺码","align":"center","key":"value1"},{"minWidth":120,"title":"主要颜色","align":"center","key":"value2"},{"minWidth":80,"slot":"pic","title":"图片","align":"center"},{"minWidth":95,"slot":"price","title":"售价","align":"center"},{"minWidth":95,"slot":"cost","title":"成本价","align":"center"},{"minWidth":95,"slot":"ot_price","title":"原价","align":"center"},{"minWidth":95,"slot":"stock","title":"库存","align":"center"},{"minWidth":120,"slot":"bar_code","title":"商品编号","align":"center"},{"minWidth":95,"slot":"weight","title":"重量(KG)","align":"center"},{"minWidth":95,"slot":"volume","title":"体积(m³)","align":"center"},{"minWidth":70,"slot":"action","title":"操作","align":"center"}],"attr":[{"detail":["160/84A/S","165/88A/M","170/92A/L","175/96A/XL"],"value":"尺码"},{"detail":["蜡黄色/757"],"value":"主要颜色"}],"value":[{"brokerage":0,"cost":0,"value2":"蜡黄色/757","value1":"160/84A/S","weight":0,"pic":"","volume":0,"brokerage_two":0,"price":0,"bar_code":"","detail":{"主要颜色":"蜡黄色/757","尺码":"160/84A/S"},"stock":0,"ot_price":0},{"brokerage":0,"cost":0,"value2":"蜡黄色/757","value1":"165/88A/M","weight":0,"pic":"","volume":0,"brokerage_two":0,"price":0,"bar_code":"","detail":{"主要颜色":"蜡黄色/757","尺码":"165/88A/M"},"stock":0,"ot_price":0},{"brokerage":0,"cost":0,"value2":"蜡黄色/757","value1":"170/92A/L","weight":0,"pic":"","volume":0,"brokerage_two":0,"price":0,"bar_code":"","detail":{"主要颜色":"蜡黄色/757","尺码":"170/92A/L"},"stock":0,"ot_price":0},{"brokerage":0,"cost":0,"value2":"蜡黄色/757","value1":"175/96A/XL","weight":0,"pic":"","volume":0,"brokerage_two":0,"price":0,"bar_code":"","detail":{"主要颜色":"蜡黄色/757","尺码":"175/96A/XL"},"stock":0,"ot_price":0}]}},"status":200}
     */
    @Override
    public JSONObject copyGoods(String url) {
        OnePassApplicationInfoVo infoVo = getApplicationInfoException();
        HashMap<String, String> header = onePassUtil.getCommonHeader(onePassUtil.getToken(infoVo));
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("url", url);
        JSONObject jsonObject = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_COPY_GOODS_URI_V2, params, header);
        return jsonObject.getJSONObject("data");
    }

    /**
     * 电子面单
     * 兼容老设备 siid参数不存在的时候必须填写，print_type=IMAGE 返回面单图
     * 并且Header中再新增一个version=v1.1的参数
     * @param record 电子面单参数
     * @return
     */
    @Override
    public MyRecord expressDump(MyRecord record) {
        OnePassApplicationInfoVo infoVo = getApplicationInfoException();
        HashMap<String, String> header = onePassUtil.getCommonHeader(onePassUtil.getToken(infoVo));
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        Map<String, Object> columns = record.getColumns();
        Object siid = columns.get("siid");
        if(ObjectUtil.isEmpty(siid)){
            columns.put("print_type", "IMAGE");
            header.put("version", "v1.1");
        }
        columns.forEach(params::add);
        JSONObject post = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_EXPRESS_DUMP_URI_V2, params, header);
        MyRecord myRecord = new MyRecord();
        JSONObject jsonObject = post.getJSONObject("data");
        return myRecord.setColums(jsonObject);
    }

    /**
     * 物流追踪
     * @param expressNo 快递单号
     * @param com   快递公司简写
     * @param phone  手机号,顺丰使用
     * @return OnePassLogisticsQueryVo
     */
    @Override
    public OnePassLogisticsQueryVo exprQuery(String expressNo, String com, String phone) {
        OnePassApplicationInfoVo infoVo = getApplicationInfoException();
        HashMap<String, String> header = onePassUtil.getCommonHeader(onePassUtil.getToken(infoVo));
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("com", com);
        params.add("num", expressNo);
        params.add("phone", phone);
        JSONObject post = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_EXPRESS_QUEARY_URI_V2, params, header);
        String dataStr = post.getString("data");
        if (StrUtil.isBlank(dataStr) || dataStr.equals("[]")) {
            return null;
        }
        JSONObject jsonObject = post.getJSONObject("data");
        return JSONObject.toJavaObject(jsonObject, OnePassLogisticsQueryVo.class);
    }

    /**
     * 更新sms配置信息
     *
     * @param accessKey  access_key一号通后台应用管理获得APPID
     * @param secretKey secret_key一号通后台应用管理获得AppSecret
     */
    private void setApplicationInfo(String accessKey, String secretKey) {
        boolean accountResult = systemConfigService.updateOrSaveValueByName(OnePassConstants.ONE_PASS_ACCESS_KEY, accessKey);
        boolean tokenResult = systemConfigService.updateOrSaveValueByName(OnePassConstants.ONE_PASS_SECRET_KEY, secretKey);
        if (!accountResult || !tokenResult) {
            throw new CrmebException("数据更新失败！");
        }
    }

    /**
     * 保存一号通应用信息
     *
     * @param request 一号通服务中申请的应用信息
     * @return 保存结果
     */
    @Override
    public Boolean saveApplicationInfo(OnePassApplicationInfoVo request) {
        setApplicationInfo(request.getAccessKey(), request.getSecretKey());
        return Boolean.TRUE;
    }

    /**
     * 获取一号通应用信息
     * @return 一号通应用信息
     */
    @Override
    public OnePassApplicationInfoVo getApplicationInfo() {
        List<String> list = new ArrayList<>();
        list.add(OnePassConstants.ONE_PASS_ACCESS_KEY);// 获取配置账号
        list.add(OnePassConstants.ONE_PASS_SECRET_KEY);//获取配置密码
        MyRecord myRecord = systemConfigService.getValuesByKeyList(list);
        OnePassApplicationInfoVo onePassApplicationInfoVo = new OnePassApplicationInfoVo();
        onePassApplicationInfoVo.setAccessKey(myRecord.getStr(OnePassConstants.ONE_PASS_ACCESS_KEY));
        onePassApplicationInfoVo.setSecretKey(myRecord.getStr(OnePassConstants.ONE_PASS_SECRET_KEY));
        return onePassApplicationInfoVo;
    }

    /**
     * 获取一号通应用信息会抛出异常
     * @return 一号通应用信息
     */
    @Override
    public OnePassApplicationInfoVo getApplicationInfoException() {
        List<String> list = new ArrayList<>();
        list.add(OnePassConstants.ONE_PASS_ACCESS_KEY);// 获取配置账号
        list.add(OnePassConstants.ONE_PASS_SECRET_KEY);//获取配置密码
        MyRecord myRecord = systemConfigService.getValuesByKeyList(list);

        String accessKey = myRecord.getStr(OnePassConstants.ONE_PASS_ACCESS_KEY);
        if (StrUtil.isBlank(accessKey)) {
            throw new CrmebException(OnePassResultCode.ACCESS_KEY_NOT_CONFIG);
        }
        String secretKey = myRecord.getStr(OnePassConstants.ONE_PASS_SECRET_KEY);
        if (StrUtil.isBlank(secretKey)) {
            throw new CrmebException(OnePassResultCode.SECRET_KEY_NOT_CONFIG);
        }
        return new OnePassApplicationInfoVo(accessKey, secretKey);
    }

}
