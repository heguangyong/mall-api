package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.PageList;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.enums.PayComponentCatStatusEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.wechat.video.PayComponentCat;
import com.zbkj.common.model.wechat.video.PayComponentShopBrand;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxvedio.cat.PayComponentCatPageListRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.vo.wxvedioshop.cat_brand.*;
import com.zbkj.service.dao.PayComponentCatDao;
import com.zbkj.service.service.PayComponentCatService;
import com.zbkj.service.service.PayComponentShopBrandService;
import com.zbkj.service.service.WechatVideoSpuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *  自定义交易组件 类目service
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Service
public class PayComponentCatServiceImpl extends ServiceImpl<PayComponentCatDao, PayComponentCat> implements PayComponentCatService {

    private final Logger logger = LoggerFactory.getLogger(PayComponentCatServiceImpl.class);

    @Resource
    private PayComponentCatDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private WechatVideoSpuService wechatVideoSpuService;

    @Autowired
    private PayComponentShopBrandService payComponentShopBrandService;

    @Autowired
    private RedisUtil redisUtil;

    private final String PAY_COMPONENT_CAT_LIST = "pay_component_cat_list";

    /**
     * 获取类目
     * @return List<FirstCatVo>
     */
    @Override
    public List<CatItem> getTreeList() {
        if (!redisUtil.exists(PAY_COMPONENT_CAT_LIST)) {
            autoUpdate();
        }
        String catJsonStr = redisUtil.get(PAY_COMPONENT_CAT_LIST);
        List<CatItem> firstCatVoList = JSONArray.parseArray(catJsonStr, CatItem.class);
        return firstCatVoList;
    }

    /**
     * 获取分页类目数据 用于类目申请和整体查看
     *
     * @param payComponentCat 类目查询对象
     * @param pageParamRequest 分页对象
     * @return List<PayComponentCat>
     */
    @Override
    public List<PayComponentCat> getList(PayComponentCatPageListRequest payComponentCat, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<PayComponentCat> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if(ObjectUtil.isNotEmpty(payComponentCat.getCatName())){
            lambdaQueryWrapper.like(PayComponentCat::getFirstCatName, URLUtil.decode(payComponentCat.getCatName()))
                    .or().like(PayComponentCat::getSecondCatName, URLUtil.decode(payComponentCat.getCatName()))
                    .or().like(PayComponentCat::getThirdCatName, URLUtil.decode(payComponentCat.getCatName()));
        }
        // 类目名称查询
//        if(ObjectUtil.isNotEmpty(payComponentCat.getFirstCatName())){
//            lambdaQueryWrapper.eq(PayComponentCat::getFirstCatName, payComponentCat.getFirstCatName());
//        }
//        if(ObjectUtil.isNotEmpty(payComponentCat.getSecondCatName())){
//            lambdaQueryWrapper.eq(PayComponentCat::getSecondCatName, payComponentCat.getSecondCatName());
//        }
//        if(ObjectUtil.isNotEmpty(payComponentCat.getThirdCatName())){
//            lambdaQueryWrapper.eq(PayComponentCat::getThirdCatName, payComponentCat.getThirdCatName());
//        }
        // 资质
//        if(ObjectUtil.isNotEmpty(payComponentCat.getQualificationType())){
//            lambdaQueryWrapper.eq(PayComponentCat::getQualificationType, payComponentCat.getQualificationType());
//        }
//        if(ObjectUtil.isNotEmpty(payComponentCat.getProductQualificationType())){
//            lambdaQueryWrapper.eq(PayComponentCat::getProductQualificationType, payComponentCat.getProductQualificationType());
//        }
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据第三级id获取类目
     * @param thirdCatId 第三级id
     * @return PayComponentCat
     */
    @Override
    public PayComponentCat getByThirdCatId(Integer thirdCatId) {
        LambdaQueryWrapper<PayComponentCat> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayComponentCat::getThirdCatId, thirdCatId);
        return dao.selectOne(lqw);
    }

    /**
     * 根据提交审核时返回的auditId查询对应类目信息
     *
     * @param audit 类目提审到微信侧时返回的id
     * @return 当前审核的类目信息
     */
    @Override
    public PayComponentCat getByAudit(String audit) {
        LambdaQueryWrapper<PayComponentCat> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayComponentCat::getAuditId, audit);
        return dao.selectOne(lqw);
    }

    /**
     * 接收处理类目审核结果查询和审核回调 审核状态, 0：审核中，1：审核成功，9：审核拒绝
     *
     * @param jsonObject 微信返回的审核结果
     * @param auditId 审核id 如果有数据证明是自己查询的 没有则是回调事件
     */
    @Override
    public void getAuditResultOrAuditCallBack(JSONObject jsonObject, String auditId) {

        // 审核id
        if(ObjectUtil.isNull(auditId)){
            auditId = jsonObject.getString("audit_id");
        }
        // 审核状态,1:已通过,9:拒绝
        Integer status = jsonObject.getInteger("status");
        // 审核类型，本接口固定为2 暂时无用
        // String auditType = jsonObject.getString("audit_type");
        // 审核相关信息 其实就是拒绝原因
        String rejectReason = jsonObject.getString("reject_reason");

        // 查询结果 不是品牌就是类目  如果是品牌审核，返回brand_id 比较难受的是如果状态是拒接 只能根据auditId 去查询是类目还是品牌
        PayComponentCat byAudit = getByAudit(auditId);

        if(ObjectUtil.isNotNull(byAudit)){
            // 处理类目 审核结果
            LambdaUpdateWrapper<PayComponentCat> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
            // 通过
            if(status == 1){
                lambdaUpdateWrapper.set(PayComponentCat::getStatus,PayComponentCatStatusEnum.WECHAT_REVIEW_SUCCESS.getCode());
                // 拒绝
            }else{
                lambdaUpdateWrapper.set(PayComponentCat::getStatus,PayComponentCatStatusEnum.WECHAT_REVIEW_FAILED.getCode());
                lambdaUpdateWrapper.set(PayComponentCat::getRejectReason, rejectReason);
            }
            lambdaUpdateWrapper.eq(PayComponentCat::getAuditId, auditId);
            update(lambdaUpdateWrapper);

        }else{
            // 处理品牌 审核结果
            LambdaUpdateWrapper<PayComponentShopBrand> lambdaUpdateWrapperShopBrand = Wrappers.lambdaUpdate();
            if(status == 9){
                lambdaUpdateWrapperShopBrand.set(PayComponentShopBrand::getStatus, 9);
                lambdaUpdateWrapperShopBrand.set(PayComponentShopBrand::getRejectReason, rejectReason);
            }else{
                lambdaUpdateWrapperShopBrand.set(PayComponentShopBrand::getStatus, status);
            }
            lambdaUpdateWrapperShopBrand.eq(PayComponentShopBrand::getAuditId, auditId);
            payComponentShopBrandService.update(lambdaUpdateWrapperShopBrand);
        }
    }

    /**
     * 自动更新自定义交易组件类目
     * 1.获取最新的类目信息
     * 2.更新数据库
     * 3.更新缓存
     */
    @Override
    public void autoUpdate() {
        List<ShopCatDetailVo> catDetailVoList = wechatVideoSpuService.getShopCat();
        if (CollUtil.isEmpty(catDetailVoList)) {
            return ;
        }

        // 这里只能根据微信的变更而变更，获取用油权限的数据记录后再新获取之后的数据中修改对应状态再进数据库
        LambdaQueryWrapper<PayComponentCat> lqwCanUse = Wrappers.lambdaQuery();
        lqwCanUse.eq(PayComponentCat::getStatus, PayComponentCatStatusEnum.WECHAT_REVIEW_SUCCESS.getCode());
        List<PayComponentCat> payComponentCats = dao.selectList(lqwCanUse);

        // 转换对象 并 设置默认不需要申请的状态
        List<PayComponentCat> catList = catDetailVoList.stream().map(e -> {
            PayComponentCat payComponentCat = new PayComponentCat();
            BeanUtils.copyProperties(e, payComponentCat);
            return payComponentCat;
        }).collect(Collectors.toList());

        Boolean execute = transactionTemplate.execute(e -> {
            // 清空原有数据
            dao.deleteAll();
            // 插入现在的数据
            saveBatch(catList);
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("自动更新自定义交易组件类目,操作数据库时出错");
        }
        // 清空Redis缓存，重新添加
        Boolean updateCatRedis = updateCatRedis(catList);
        if (!updateCatRedis) {
            throw new CrmebException("自动更新自定义交易组件类目,操作Redis时出错");
        }
    }

    /**
     * 更新类目缓存
     * @param catList 类目列表
     */
    private Boolean updateCatRedis(List<PayComponentCat> catList) {
        // 组装缓存数据
//        List<FirstCatVo> catVoList = assembleRedisData(catList);
        List<CatItem> catVoList = assembleRedisDataForFront(catList);

        if (redisUtil.exists(PAY_COMPONENT_CAT_LIST)) {
            redisUtil.delete(PAY_COMPONENT_CAT_LIST);
        }
        return redisUtil.set(PAY_COMPONENT_CAT_LIST, JSONArray.toJSONString(catVoList));
    }

    /**
     * 组装redis数据
     * @param catList 类目列表
     * @return List<FirstCatVo>
     */
    private List<FirstCatVo> assembleRedisData(List<PayComponentCat> catList) {
        // 第一级
        HashMap<Integer, String> firstMap = CollUtil.newHashMap();
        catList.forEach(e -> {
            if (!firstMap.containsKey(e.getFirstCatId())) {
                firstMap.put(e.getFirstCatId(), e.getFirstCatName());
            }
        });
        List<FirstCatVo> voList = CollUtil.newArrayList();
        firstMap.forEach((k, v) -> {
            FirstCatVo firstCatVo = new FirstCatVo();
            firstCatVo.setFirstCatId(k);
            firstCatVo.setFirstCatName(v);
            voList.add(firstCatVo);
        });
        // 第二级
        voList.forEach(e -> {
            HashMap<Integer, String> secondMap = CollUtil.newHashMap();
            catList.stream().filter(a -> a.getFirstCatId().equals(e.getFirstCatId())).forEach(cat -> {
                if (!secondMap.containsKey(cat.getSecondCatId())) {
                    secondMap.put(cat.getSecondCatId(), cat.getSecondCatName());
                }
            });
            List<SecondCatVo> secondCatVoList = CollUtil.newArrayList();
            secondMap.forEach((k, v) -> {
                SecondCatVo secondCatVo = new SecondCatVo();
                secondCatVo.setSecondCatId(k);
                secondCatVo.setSecondCatName(v);
                secondCatVoList.add(secondCatVo);
            });
            // 第三级
            secondCatVoList.forEach(b -> {
                List<ThirdCatVo> thirdCatVoList = catList.stream().filter(i -> i.getSecondCatId().equals(b.getSecondCatId())).map(o -> {
                    ThirdCatVo thirdCatVo = new ThirdCatVo();
                    thirdCatVo.setThirdCatId(o.getThirdCatId());
                    thirdCatVo.setThirdCatName(o.getThirdCatName());
                    thirdCatVo.setQualification(o.getQualification());
                    thirdCatVo.setQualificationType(o.getQualificationType());
                    thirdCatVo.setProductQualification(o.getProductQualification());
                    thirdCatVo.setProductQualificationType(o.getProductQualificationType());
                    return thirdCatVo;
                }).collect(Collectors.toList());
                b.setThirdCatList(thirdCatVoList);
            });
            e.setSecondCatList(secondCatVoList);
        });
        return voList;
    }

    /**
     * 组装redis数据 数据对应前端组件
     * @param catList 类目列表
     * @return List<FirstCatVo>
     */
    private List<CatItem> assembleRedisDataForFront(List<PayComponentCat> catList) {
        // 第一级
        HashMap<Integer, String> firstMap = CollUtil.newHashMap();
        catList.forEach(e -> {
            if (!firstMap.containsKey(e.getFirstCatId())) {
                firstMap.put(e.getFirstCatId(), e.getFirstCatName());
            }
        });
        List<CatItem> catItem1s = CollUtil.newArrayList();
        firstMap.forEach((k, v) -> {
            catItem1s.add(new CatItem(k,v,new ArrayList<>()));
        });
        // 第二级
        catItem1s.forEach(e -> {
            HashMap<Integer, String> secondMap = CollUtil.newHashMap();
            catList.stream().filter(a -> a.getFirstCatId().equals(e.getValue())).forEach(cat -> {
                if (!secondMap.containsKey(cat.getSecondCatId())) {
                    secondMap.put(cat.getSecondCatId(), cat.getSecondCatName());
                }
            });
            List<CatItem> catItem2s = CollUtil.newArrayList();
            secondMap.forEach((k, v) -> {
                CatItem item = new CatItem();
                item.setLabel(v);
                item.setValue(k);
                catItem2s.add(item);
            });
            // 第三级
            catItem2s.forEach(b -> {
                List<CatItem> catItem3s = CollUtil.newArrayList();
                List<ThirdCatVo> thirdCatVoList = catList.stream().filter(i -> i.getSecondCatId().equals(b.getValue())).map(o -> {
                    ThirdCatVo thirdCatVo = new ThirdCatVo();
                    CatItem item = new CatItem();
                    item.setLabel(o.getThirdCatName());
                    item.setValue(o.getThirdCatId());
                    item.setQualification(o.getQualification());
                    item.setQualificationType(o.getQualificationType());
                    item.setProductQualification(o.getProductQualification());
                    item.setProductQualificationType(o.getProductQualificationType());
                    item.setStatus(o.getStatus());
                    catItem3s.add(item);
                    return thirdCatVo;
                }).collect(Collectors.toList());
                b.setChildren(catItem3s);
            });
            e.setChildren(catItem2s);
        });
        return catItem1s;
    }
}

