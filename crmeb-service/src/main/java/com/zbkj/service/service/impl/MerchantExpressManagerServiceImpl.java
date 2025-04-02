package com.zbkj.service.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.OnePassConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.express.Express;
import com.zbkj.common.model.express.MerchantExpress;
import com.zbkj.common.model.merchant.MerchantElect;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.MerchantExpressSearchRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.MerchantResultCode;
import com.zbkj.common.result.SystemConfigResultCode;
import com.zbkj.common.vo.OnePassApplicationInfoVo;
import com.zbkj.service.dao.MerchantExpressDao;
import com.zbkj.service.service.ExpressService;
import com.zbkj.service.service.MerchantElectService;
import com.zbkj.service.service.MerchantExpressManagerService;
import com.zbkj.service.service.OnePassService;
import com.zbkj.service.util.OnePassUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @author HZW
 * @description MerchantExpressServiceImpl 接口实现
 * @date 2024-05-08
 */
@Service
public class MerchantExpressManagerServiceImpl extends ServiceImpl<MerchantExpressDao, MerchantExpress> implements MerchantExpressManagerService {

    @Resource
    private MerchantExpressDao dao;

    @Autowired
    private ExpressService expressService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private MerchantElectService merchantElectService;
    @Autowired
    private OnePassService onePassService;
    @Autowired
    private OnePassUtil onePassUtil;

    /**
     * 关联物流公司
     *
     * @param expressId 物流公司ID
     * @return 关联结果
     */
    @Override
    public Boolean relate(Integer expressId, SystemAdmin admin) {

        Express express = expressService.getInfo(expressId);
        if (!express.getIsShow()) {
            throw new CrmebException(SystemConfigResultCode.EXPRESS_NOT_EXIST);
        }

        if (validateMerAndExpress(admin.getMerId(), expressId)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "已关联此物流公司");
        }

        MerchantExpress merchantExpress = new MerchantExpress();
        merchantExpress.setMerId(admin.getMerId());
        merchantExpress.setExpressId(expressId);
        merchantExpress.setCode(express.getCode());
        merchantExpress.setName(express.getName());
        merchantExpress.setPartnerId(express.getPartnerId());
        return save(merchantExpress);
    }

    /**
     * 检查商户与物流公司是否关联
     */
    private Boolean validateMerAndExpress(Integer merId, Integer expressId) {
        LambdaQueryWrapper<MerchantExpress> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantExpress::getMerId, merId);
        lqw.eq(MerchantExpress::getExpressId, expressId);
        lqw.eq(MerchantExpress::getIsDelete, 0);
        lqw.last(" limit 1");
        MerchantExpress merchantExpress = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(merchantExpress);
    }

    /**
     * 商户物流公司分页列表
     */
    @Override
    public PageInfo<MerchantExpress> searchPage(MerchantExpressSearchRequest request, SystemAdmin admin) {
        LambdaQueryWrapper<MerchantExpress> lqw = Wrappers.lambdaQuery();
        lqw.select(MerchantExpress::getId, MerchantExpress::getCode, MerchantExpress::getName,
                MerchantExpress::getIsDefault, MerchantExpress::getIsOpen, MerchantExpress::getPartnerId,
                MerchantExpress::getPartnerKey, MerchantExpress::getNet, MerchantExpress::getAccount, MerchantExpress::getPassword,
        MerchantExpress::getNetName);
        lqw.eq(MerchantExpress::getMerId, admin.getMerId());
        lqw.eq(MerchantExpress::getIsDelete, 0);
        if (ObjectUtil.isNotNull(request.getOpenStatus())) {
            lqw.eq(MerchantExpress::getIsOpen, request.getOpenStatus());
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lqw.and(i -> i.like(MerchantExpress::getName, keywords).or().like(MerchantExpress::getCode, keywords));
        }
        lqw.orderByDesc(MerchantExpress::getIsDefault, MerchantExpress::getExpressId);
        Page<MerchantExpress> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<MerchantExpress> merchantExpressList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, merchantExpressList);
    }

    /**
     * 查询物流公司面单模板
     *
     * @param com 快递公司编号
     */
    @Override
    public JSONObject template(String com, SystemAdmin admin) {
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
     * 商户物流公司开关
     *
     * @param id 商户物流公司ID
     */
    @Override
    public Boolean openSwitch(Integer id, SystemAdmin admin) {
        MerchantExpress merchantExpress = getByIdException(id);
        if (!admin.getMerId().equals(merchantExpress.getMerId())) {
            throw new CrmebException(MerchantResultCode.MERCHANT_EXPRESS_NOT_EXIST);
        }
        merchantExpress.setIsOpen(!merchantExpress.getIsOpen());
        return updateById(merchantExpress);
    }

    /**
     * 商户物流公司默认开关
     *
     * @param id 商户物流公司ID
     */
    @Override
    public Boolean defaultSwitch(Integer id, SystemAdmin admin) {
        MerchantExpress merchantExpress = getByIdException(id);
        if (!admin.getMerId().equals(merchantExpress.getMerId())) {
            throw new CrmebException(MerchantResultCode.MERCHANT_EXPRESS_NOT_EXIST);
        }
        if (merchantExpress.getIsDefault()) {
            merchantExpress.setIsDefault(false);
            return updateById(merchantExpress);
        }
        merchantExpress.setIsDefault(true);
        return transactionTemplate.execute(e -> {
            cancelDefaultByMerId(merchantExpress.getMerId());
            updateById(merchantExpress);
            return Boolean.TRUE;
        });
    }

    /**
     * 商户物流公司删除
     *
     * @param id 商户物流公司ID
     */
    @Override
    public Boolean delete(Integer id, SystemAdmin admin) {
        MerchantExpress merchantExpress = getByIdException(id);
        if (!admin.getMerId().equals(merchantExpress.getMerId())) {
            throw new CrmebException(MerchantResultCode.MERCHANT_EXPRESS_NOT_EXIST);
        }
        merchantExpress.setIsDelete(true);
        return updateById(merchantExpress);
    }

    private void cancelDefaultByMerId(Integer merId) {
        LambdaUpdateWrapper<MerchantExpress> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MerchantExpress::getIsDefault, 0);
        wrapper.eq(MerchantExpress::getIsDelete, 0);
        wrapper.eq(MerchantExpress::getMerId, merId);
        update(wrapper);
    }

    private MerchantExpress getByIdException(Integer id) {
        MerchantExpress merchantExpress = getById(id);
        if (ObjectUtil.isNull(merchantExpress)) {
            throw new CrmebException(MerchantResultCode.MERCHANT_EXPRESS_NOT_EXIST);
        }
        return merchantExpress;
    }
}

