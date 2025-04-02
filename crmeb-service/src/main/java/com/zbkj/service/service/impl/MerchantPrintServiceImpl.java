package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.MerchantPrint;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.MerchantResultCode;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.PrintContentVo;
import com.zbkj.service.dao.MerchantPrintDao;
import com.zbkj.service.service.MerchantPrintService;
import com.zbkj.service.util.PrintUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dazongzi
 * @description MerchantPrintServiceImpl 接口实现
 * @date 2023-09-20
 */
@Service
public class MerchantPrintServiceImpl extends ServiceImpl<MerchantPrintDao, MerchantPrint> implements MerchantPrintService {
    private final Logger logger = LoggerFactory.getLogger(MerchantPrintServiceImpl.class);
    @Resource
    private MerchantPrintDao dao;
    @Autowired
    private PrintUtil printUtil;

    /**
     * 列表
     *
     * @param pageParamRequest 分页类参数
     * @return List<MerchantPrint>
     * @author dazongzi
     * @since 2023-09-20
     */
    @Override
    public List<MerchantPrint> getList(PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        //带 MerchantPrint 类的多条件查询
        LambdaQueryWrapper<MerchantPrint> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MerchantPrint::getMerId, systemAdmin.getMerId());
        lambdaQueryWrapper.orderByDesc(MerchantPrint::getUpdateTime);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 获取商户打印配置
     *
     * @param merId 商户id
     * @return 开启的打印配置
     */
    @Override
    public List<MerchantPrint> getByMerIdAndStatusOn(Integer merId) {
        LambdaQueryWrapper<MerchantPrint> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MerchantPrint::getMerId, merId);
        lambdaQueryWrapper.eq(MerchantPrint::getStatus, 1);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 新增小票打印机配置
     *
     * @param merchantPrint 小票打印机对象
     * @return 新增结果
     */
    @Override
    public Boolean savePrintConfig(MerchantPrint merchantPrint) {
        SystemAdmin currentUser = SecurityUtil.getLoginUserVo().getUser();
        if (isExistNameByMerchant(merchantPrint.getPrintName(), currentUser.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, merchantPrint.getPrintName() + "|当前打印机配置已存在");
        }
        // 获取当前登录人信息和商户信息
        // 设置当前打印机归属商户
        merchantPrint.setMerId(currentUser.getMerId());
        int insertCount = dao.insert(merchantPrint);
        return insertCount > 0;
    }

    @Override
    public Boolean updatePrintConfig(MerchantPrint merchantPrint) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        MerchantPrint oldMerchantPrint = getById(merchantPrint.getId());
        if (ObjectUtil.isNull(oldMerchantPrint) || !admin.getMerId().equals(oldMerchantPrint.getMerId())) {
            throw new CrmebException(MerchantResultCode.MERCHANT_PRINT_NOT_EXIST);
        }
        merchantPrint.setMerId(admin.getMerId());
        if (isExistNameByMerchant(merchantPrint.getPrintName(), admin.getMerId(), merchantPrint.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, merchantPrint.getPrintName() + "|当前打印机配置已存在");
        }
        return updateById(merchantPrint);
    }

    /**
     * 更新打印机状态
     *
     * @param id     更改id
     * @param status 具体状态值 0关闭，1开启
     * @return 结果
     */
    @Override
    public Boolean updateStatus(Integer id, Integer status) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        LambdaUpdateWrapper<MerchantPrint> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(MerchantPrint::getStatus, status);
        updateWrapper.eq(MerchantPrint::getId, id);
        updateWrapper.eq(MerchantPrint::getMerId, admin.getMerId());
        return update(updateWrapper);
    }

//    /**
//     * 执行具体打印
//     *
//     * @param merchantOrderList 当前待打印的订单
//     * @param op                小票打印开关：0关闭，1=手动打印，2=自动打印，3=自动和手动
//     */
//    @Override
//    public void printReceipt(List<MerchantOrder> merchantOrderList, Integer op) {
//        printUtil.printTicket(merchantOrderList, op);
//
//    }

    /**
     * 执行具体打印
     *
     * @param merchantOrder 当前待打印的订单
     */
    @Override
    public void printReceipt(MerchantOrder merchantOrder) {
        printUtil.printTicket(merchantOrder);

    }

    /**
     * 执行具体打印
     *
     * @param merchantOrderList 当前待打印的订单
     */
    @Override
    public void batchPrintTicket(List<MerchantOrder> merchantOrderList) {
        printUtil.batchPrintTicket(merchantOrderList);

    }

    /**
     * 删除打印
     * @param id 打印ID
     */
    @Override
    public Boolean deleteById(Integer id) {
        MerchantPrint merchantPrint = getByIdException(id);
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(merchantPrint.getMerId())) {
            throw new CrmebException(MerchantResultCode.MERCHANT_PRINT_NOT_EXIST);
        }
        return removeById(id);
    }

    /**
     * 获取打印机配置详情
     * @param id 打印机ID
     */
    @Override
    public MerchantPrint getPrintInfo(Integer id) {
        MerchantPrint merchantPrint = getByIdException(id);
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(merchantPrint.getMerId())) {
            throw new CrmebException(MerchantResultCode.MERCHANT_PRINT_NOT_EXIST);
        }
        return merchantPrint;
    }

    /**
     * 获取打印内容配置
     * @param id 小票ID
     */
    @Override
    public PrintContentVo getPrintContentConfig(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        MerchantPrint merchantPrint = getByIdException(id);
        if (!admin.getMerId().equals(merchantPrint.getMerId())) {
            throw new CrmebException(MerchantResultCode.MERCHANT_PRINT_NOT_EXIST);
        }
        if (StrUtil.isBlank(merchantPrint.getContent())) {
            return null;
        }
        return JSON.parseObject(merchantPrint.getContent(), PrintContentVo.class);
    }

    /**
     * 保存打印内容配置
     */
    @Override
    public Boolean savePrintContentConfig(Integer id, PrintContentVo voRequest) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        MerchantPrint merchantPrint = getByIdException(id);
        if (!admin.getMerId().equals(merchantPrint.getMerId())) {
            throw new CrmebException(MerchantResultCode.MERCHANT_PRINT_NOT_EXIST);
        }
        String content = JSONObject.toJSONString(voRequest);
        merchantPrint.setContent(content);
        return updateById(merchantPrint);
    }

    private MerchantPrint getByIdException(Integer id) {
        MerchantPrint merchantPrint = getById(id);
        if (ObjectUtil.isNull(merchantPrint)) {
            throw new CrmebException(MerchantResultCode.MERCHANT_PRINT_NOT_EXIST);
        }
        return merchantPrint;
    }

    private Boolean isExistNameByMerchant(String printName, Integer merId) {
        LambdaQueryWrapper<MerchantPrint> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MerchantPrint::getPrintName, printName);
        lambdaQueryWrapper.eq(MerchantPrint::getMerId, merId);
        lambdaQueryWrapper.last("limit 1");
        MerchantPrint merchantPrint = dao.selectOne(lambdaQueryWrapper);
        return ObjectUtil.isNotNull(merchantPrint);
    }

    private Boolean isExistNameByMerchant(String printName, Integer merId, Integer printId) {
        LambdaQueryWrapper<MerchantPrint> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MerchantPrint::getPrintName, printName);
        lambdaQueryWrapper.eq(MerchantPrint::getMerId, merId);
        lambdaQueryWrapper.ne(MerchantPrint::getId, printId);
        lambdaQueryWrapper.last("limit 1");
        MerchantPrint merchantPrint = dao.selectOne(lambdaQueryWrapper);
        return ObjectUtil.isNotNull(merchantPrint);
    }
}

