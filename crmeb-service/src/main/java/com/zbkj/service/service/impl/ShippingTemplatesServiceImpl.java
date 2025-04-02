package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.express.ShippingTemplates;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ShippingTemplatesRegionRequest;
import com.zbkj.common.request.ShippingTemplatesRequest;
import com.zbkj.common.request.ShippingTemplatesSearchRequest;
import com.zbkj.common.response.ShippingTemplatesFreeResponse;
import com.zbkj.common.response.ShippingTemplatesInfoResponse;
import com.zbkj.common.response.ShippingTemplatesRegionResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.ProductResultCode;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.LoginUserVo;
import com.zbkj.service.dao.ShippingTemplatesDao;
import com.zbkj.service.service.ProductService;
import com.zbkj.service.service.ShippingTemplatesFreeService;
import com.zbkj.service.service.ShippingTemplatesRegionService;
import com.zbkj.service.service.ShippingTemplatesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * ShippingTemplatesServiceImpl 接口实现
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
public class ShippingTemplatesServiceImpl extends ServiceImpl<ShippingTemplatesDao, ShippingTemplates> implements ShippingTemplatesService {

    @Resource
    private ShippingTemplatesDao dao;

    @Autowired
    private ShippingTemplatesRegionService shippingTemplatesRegionService;

    @Autowired
    private ShippingTemplatesFreeService shippingTemplatesFreeService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ProductService productService;

    /**
     * 列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return List<ShippingTemplates>
     */
    @Override
    public List<ShippingTemplates> getList(ShippingTemplatesSearchRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<ShippingTemplates> lqw = new LambdaQueryWrapper<>();
        if (admin.getMerId() > 0) {
            lqw.eq(ShippingTemplates::getMerId, admin.getMerId());
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lqw.like(ShippingTemplates::getName, keywords);
        }
        lqw.orderByDesc(ShippingTemplates::getSort).orderByDesc(ShippingTemplates::getId);
        return dao.selectList(lqw);
    }

    /**
     * 新增
     *
     * @param request 新增参数
     * @return Boolean
     */
    @Override
    public Boolean create(ShippingTemplatesRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        // 判断模板名称是否重复
        if (isExistName(request.getName(), admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "模板名称已存在,请更换模板名称!");
        }
        List<ShippingTemplatesRegionRequest> shippingTemplatesRegionRequestList = request.getShippingTemplatesRegionRequestList();
        if (request.getAppoint().equals(2) && CollUtil.isEmpty(shippingTemplatesRegionRequestList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "不包邮，最少需要一条公共区域运费数据");
        }

        ShippingTemplates shippingTemplates = new ShippingTemplates();
        shippingTemplates.setName(request.getName());
        shippingTemplates.setSort(request.getSort());
        shippingTemplates.setType(request.getType());
        shippingTemplates.setAppoint(request.getAppoint());
        shippingTemplates.setMerId(admin.getMerId());
        shippingTemplates.setId(null);

        return transactionTemplate.execute(e -> {
            boolean save = save(shippingTemplates);
            if (!save) {
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            if (!shippingTemplates.getAppoint().equals(0) && CollUtil.isNotEmpty(shippingTemplatesRegionRequestList)) {
                save = shippingTemplatesRegionService.saveAll(shippingTemplatesRegionRequestList, shippingTemplates.getType(), shippingTemplates.getId());
                if (!save) {
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
            }
            if (shippingTemplates.getAppoint().equals(2) && CollUtil.isNotEmpty(request.getShippingTemplatesFreeRequestList())) {
                save = shippingTemplatesFreeService.saveAll(request.getShippingTemplatesFreeRequestList(), shippingTemplates.getType(), shippingTemplates.getId());
                if (!save) {
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 根据模板名称获取模板
     *
     * @param name 模板名称
     * @return ShippingTemplates
     */
    private ShippingTemplates getByNameAndMerId(String name, Integer merId) {
        LambdaQueryWrapper<ShippingTemplates> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShippingTemplates::getName, name);
        lqw.eq(ShippingTemplates::getMerId, merId);
        lqw.last("limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 是否存在模板名称
     */
    private Boolean isExistName(String name, Integer merId) {
        ShippingTemplates templates = getByNameAndMerId(name, merId);
        if (ObjectUtil.isNull(templates)) {
            return Boolean.FALSE;
        }
        if (!templates.getMerId().equals(merId)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 新增
     *
     * @param request ShippingTemplatesRequest 新增参数
     */
    @Override
    public Boolean edit(ShippingTemplatesRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "运费模板ID不能为空");
        }
        ShippingTemplates shippingTemplates = getByIdException(request.getId());
        List<ShippingTemplatesRegionRequest> shippingTemplatesRegionRequestList = request.getShippingTemplatesRegionRequestList();
        if (request.getAppoint().equals(2) && CollUtil.isEmpty(shippingTemplatesRegionRequestList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "不包邮，最少需要一条公共区域运费数据");
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(shippingTemplates.getMerId())) {
            throw new CrmebException(ProductResultCode.SHIPPING_TEMPLATES_NOT_EXIST);
        }
        if (!shippingTemplates.getName().equals(request.getName())) {
            if (isExistName(request.getName(), admin.getMerId())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "模板名称已存在,请更换模板名称!");
            }
        }

        shippingTemplates.setName(request.getName());
        shippingTemplates.setSort(request.getSort());
        shippingTemplates.setType(request.getType());

        return transactionTemplate.execute(e -> {
            if (!shippingTemplates.getAppoint().equals(0)) {
                shippingTemplatesRegionService.deleteByTempId(shippingTemplates.getId());
            }
            if (shippingTemplates.getAppoint().equals(2)) {
                shippingTemplatesFreeService.deleteByTempId(shippingTemplates.getId());
            }
            shippingTemplates.setAppoint(request.getAppoint());
            boolean update = updateById(shippingTemplates);
            if (!update) {
                e.setRollbackOnly();
                return Boolean.FALSE;
            }
            if (CollUtil.isNotEmpty(shippingTemplatesRegionRequestList)) {
                update = shippingTemplatesRegionService.saveAll(shippingTemplatesRegionRequestList, request.getType(), shippingTemplates.getId());
                if (!update) {
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
            }
            if (shippingTemplates.getAppoint().equals(2) && CollUtil.isNotEmpty(request.getShippingTemplatesFreeRequestList())) {
                update = shippingTemplatesFreeService.saveAll(request.getShippingTemplatesFreeRequestList(), request.getType(), shippingTemplates.getId());
                if (!update) {
                    e.setRollbackOnly();
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 删除
     *
     * @param id Integer
     * @return boolean
     */
    @Override
    public Boolean remove(Integer id) {
        ShippingTemplates shippingTemplates = getByIdException(id);
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(shippingTemplates.getMerId())) {
            throw new CrmebException(ProductResultCode.SHIPPING_TEMPLATES_NOT_EXIST);
        }
        if (productService.isUseShippingTemplateId(id)) {
            throw new CrmebException(ProductResultCode.SHIPPING_TEMPLATES_USED);
        }
        return transactionTemplate.execute(e -> {
            shippingTemplatesRegionService.deleteByTempId(id);
            shippingTemplatesFreeService.deleteByTempId(id);
            removeById(id);
            return Boolean.TRUE;
        });
    }

    /**
     * 获取模板信息
     *
     * @param id 模板id
     * @return ShippingTemplates
     */
    @Override
    public ShippingTemplatesInfoResponse getInfo(Integer id) {
        ShippingTemplates shippingTemplates = getByIdException(id);
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(shippingTemplates.getMerId())) {
            throw new CrmebException(ProductResultCode.SHIPPING_TEMPLATES_NOT_EXIST);
        }
        ShippingTemplatesInfoResponse response = new ShippingTemplatesInfoResponse();
        BeanUtils.copyProperties(shippingTemplates, response);
        if (shippingTemplates.getAppoint().equals(0)) {
            return response;
        }
        List<ShippingTemplatesRegionResponse> regionResponseList = shippingTemplatesRegionService.getListGroup(id);
        if (CollUtil.isNotEmpty(regionResponseList)) {
            for (int i = 0; i < regionResponseList.size(); i++) {
                ShippingTemplatesRegionResponse regionResponse = regionResponseList.get(i);
                regionResponse.setTitle("[" + regionResponse.getTitle() + "]");
                if (regionResponse.getTitle().equals("[[0,0]]") && i > 0) {
                    // 保证0,0在列表第一位
                    regionResponseList.remove(i);
                    regionResponseList.add(0, regionResponse);
                }
            }
            response.setRegionList(regionResponseList);
        }
        if (shippingTemplates.getAppoint().equals(2)) {
            List<ShippingTemplatesFreeResponse> freeResponseList = shippingTemplatesFreeService.getListGroup(id);
            freeResponseList.forEach(freeResponse -> {
                freeResponse.setTitle("[" + freeResponse.getTitle() + "]");
            });
            response.setFreeList(freeResponseList);
        }
        return response;
    }

    private ShippingTemplates getByIdException(Integer id) {
        ShippingTemplates shippingTemplates = getById(id);
        if (ObjectUtil.isNull(shippingTemplates)) {
            throw new CrmebException(ProductResultCode.SHIPPING_TEMPLATES_NOT_EXIST);
        }
        return shippingTemplates;
    }


}

