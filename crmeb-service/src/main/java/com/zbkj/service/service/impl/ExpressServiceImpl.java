package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.constants.OnePassConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.express.Express;
import com.zbkj.common.model.express.MerchantExpress;
import com.zbkj.common.model.merchant.MerchantElect;
import com.zbkj.common.request.ExpressSearchRequest;
import com.zbkj.common.request.ExpressUpdateRequest;
import com.zbkj.common.request.ExpressUpdateShowRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.SystemConfigResultCode;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.OnePassApplicationInfoVo;
import com.zbkj.service.dao.ExpressDao;
import com.zbkj.service.service.ExpressService;
import com.zbkj.service.service.MerchantElectService;
import com.zbkj.service.service.MerchantExpressService;
import com.zbkj.service.service.OnePassService;
import com.zbkj.service.util.OnePassUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
public class ExpressServiceImpl extends ServiceImpl<ExpressDao, Express> implements ExpressService {

    @Resource
    private ExpressDao dao;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private OnePassService onePassService;
    @Autowired
    private OnePassUtil onePassUtil;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private MerchantExpressService merchantExpressService;
    @Autowired
    private MerchantElectService merchantElectService;

    /**
     * 分页显示快递公司表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @Override
    public List<Express> getList(ExpressSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Express> lqw = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lqw.like(Express::getCode, keywords).or().like(Express::getName, keywords);
        }
        // 排序：sort字段倒序，正常id正序，方便展示常用物流公司
        lqw.orderByDesc(Express::getSort);
        lqw.orderByAsc(Express::getId);
        return dao.selectList(lqw);
    }

    /**
     * 编辑
     */
    @Override
    public Boolean updateExpress(ExpressUpdateRequest expressRequest) {
        Express temp = getById(expressRequest.getId());
        if (ObjectUtil.isNull(temp)) throw new CrmebException(SystemConfigResultCode.EXPRESS_NOT_EXIST);

        if (StrUtil.isBlank(expressRequest.getAccount()) && temp.getPartnerId().equals(true)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请输入月结账号");
        }
        if (StrUtil.isBlank(expressRequest.getPassword()) && temp.getPartnerKey().equals(true)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请输入月结密码");
        }
        if (StrUtil.isBlank(expressRequest.getNetName()) && temp.getNet().equals(true)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请输入取件网点");
        }
        boolean status = true;
        if (!expressRequest.getStatus() && temp.getStatus()) {
            status = false;
        }
        Express express = new Express();
        BeanUtils.copyProperties(expressRequest, express);
        if (status) {
            return updateById(express);
        }
        return transactionTemplate.execute(e -> {
            updateById(express);
            if (!express.getStatus()) {
                merchantExpressService.deleteByExpressId(express.getId());
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 修改显示状态
     */
    @Override
    public Boolean updateExpressShow(ExpressUpdateShowRequest expressRequest) {
        Express temp = getById(expressRequest.getId());
        if (ObjectUtil.isNull(temp))
            throw new CrmebException(SystemConfigResultCode.EXPRESS_NOT_EXIST);
        if (temp.getIsShow().equals(expressRequest.getIsShow())) {
            return Boolean.TRUE;
        }
        Express express = new Express();
        BeanUtils.copyProperties(expressRequest, express);
        return transactionTemplate.execute(e -> {
            updateById(express);
            if (!express.getIsShow()) {
                merchantExpressService.deleteByExpressId(express.getId());
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 同步物流公司
     */
    @Override
    public Boolean syncExpress() {
        if (redisUtil.exists(OnePassConstants.ONE_PASS_EXPRESS_CACHE_KEY)) {
            return Boolean.TRUE;
        }
        getExpressList();
        redisUtil.set(OnePassConstants.ONE_PASS_EXPRESS_CACHE_KEY, 1, 3600L, TimeUnit.SECONDS);
        return Boolean.TRUE;
    }

    /**
     * 查询全部物流公司
     *
     * @param type 类型：normal-普通，elec-电子面单
     */
    @Override
    public List<Express> findAll(String type) {
        LambdaQueryWrapper<Express> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Express::getIsShow, true);
        if (type.equals("elec")) {
            lqw.eq(Express::getStatus, true);
        }
        lqw.orderByDesc(Express::getSort);
        lqw.orderByAsc(Express::getId);
        return dao.selectList(lqw);
    }

    /**
     * 查询物流公司面单模板
     *
     * @param com 快递公司编号
     */
    @Override
    public JSONObject template(String com) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        // 根据当前登录商户 获取电子面单打印配置
        MerchantElect merchantElect = merchantElectService.getMerchantElect(admin);
        if(ObjectUtil.isNull(merchantElect) || merchantElect.getId() <= 0) throw new CrmebException("请先配置电子面单打印配置");

        if(merchantElect.getOp().equals(1) && ObjectUtil.isNull(merchantElect.getCloudPrintNo())){
            throw new CrmebException("云打印机编号不能为空");
        }
        Integer isShipment = merchantElect.getCloudPrintNo().isEmpty() ? 1 : 0;
        OnePassApplicationInfoVo infoVo = onePassService.getApplicationInfoException();
        String token = onePassUtil.getToken(infoVo);
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("com", com);
        param.add("is_shipment", isShipment);
        return onePassUtil.getFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_EXPRESS_TEMP_URI_V2, param, header);
    }

    /**
     * 查询快递公司
     *
     * @param code 快递公司编号
     */
    @Override
    public Express getByCode(String code) {
        LambdaQueryWrapper<Express> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Express::getCode, code);
        return dao.selectOne(lqw);
    }

    /**
     * 获取快递公司详情
     *
     * @param id 快递公司id
     */
    @Override
    public Express getInfo(Integer id) {
        Express express = getById(id);
        if (ObjectUtil.isNull(express)) {
            throw new CrmebException(SystemConfigResultCode.EXPRESS_NOT_EXIST);
        }
        return express;
    }

    /**
     * 从平台获取物流公司
     * 并存入数据库
     */
    private void getExpressList() {
        OnePassApplicationInfoVo infoVo = onePassService.getApplicationInfoException();
        String token = onePassUtil.getToken(infoVo);
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        //        param.add("type", 1);// 快递类型：1，国内运输商；2，国际运输商；3，国际邮政 不传获取全部
        param.add("page", 0);
        param.add("limit", 9999);

        JSONObject post = onePassUtil.getFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_EXPRESS_URI_V2, param, header);
        System.out.println("OnePass Express ALL post = " + post);
        JSONObject jsonObject = post.getJSONObject("data");
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if (CollUtil.isEmpty(jsonArray)) return;

        List<Express> expressList = CollUtil.newArrayList();
        List<String> codeList = getAllCode();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            if (StrUtil.isNotBlank(object.getString("code")) && !codeList.contains(object.getString("code"))) {
                Express express = new Express();
                express.setName(Optional.ofNullable(object.getString("name")).orElse(""));
                express.setCode(Optional.ofNullable(object.getString("code")).orElse(""));
                express.setPartnerId(false);
                express.setPartnerKey(false);
                express.setNet(false);
                if (ObjectUtil.isNotNull(object.getInteger("partner_id"))) {
                    express.setPartnerId(object.getInteger("partner_id") == 1);
                }
                if (ObjectUtil.isNotNull(object.getInteger("partner_key"))) {
                    express.setPartnerKey(object.getInteger("partner_key") == 1);
                }
                if (ObjectUtil.isNotNull(object.getInteger("net"))) {
                    express.setNet(object.getInteger("net") == 1);
                }
                express.setIsShow(true);
                express.setStatus(false);
                if (!express.getPartnerId() && !express.getPartnerKey() && !express.getNet()) {
                    express.setStatus(true);
                }
                expressList.add(express);
            }
        }
        if (CollUtil.isNotEmpty(expressList)) {
            boolean saveBatch = saveBatch(expressList);
            if (!saveBatch) throw new CrmebException("同步物流公司失败");
        }
    }

    /**
     * 获取所有物流公司code
     */
    private List<String> getAllCode() {
        LambdaQueryWrapper<Express> lqw = new LambdaQueryWrapper<>();
        lqw.select(Express::getCode);
        List<Express> expressList = dao.selectList(lqw);
        if (CollUtil.isEmpty(expressList)) {
            return CollUtil.newArrayList();
        }
        return expressList.stream().map(Express::getCode).collect(Collectors.toList());
    }
}

