package com.zbkj.admin.controller.merchant;

import cn.hutool.core.util.ObjectUtil;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.MerchantElect;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.merchant.MerchantElectRequest;
import com.zbkj.common.request.merchant.MerchantElectSearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.MerchantElectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 商户电子面单配置表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/elect")
@Api(tags = "商户端 - 电子面单主信息配置") //配合swagger使用

public class MerchantElectController {

    @Autowired
    private MerchantElectService merchantElectService;

    /**
     * 修改商户电子面单配置表
     * @param merchantElectRequest 修改参数
     * @author dazongzi
     * @since 2025-02-20
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated MerchantElectRequest merchantElectRequest) {
        MerchantElect merchantElect = new MerchantElect();
        BeanUtils.copyProperties(merchantElectRequest, merchantElect);
        if(merchantElect.getOp().equals(1) && ObjectUtil.isEmpty(merchantElect.getCloudPrintNo())){
            throw new CrmebException("云打印机编号不能为空");
        }
        if(merchantElectService.updateMerchantElect(merchantElect)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 查询商户电子面单配置表信息
     * @author dazongzi
     * @since 2025-02-20
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<MerchantElect> info() {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        MerchantElect merchantElect = merchantElectService.getMerchantElect(admin);
        return CommonResult.success(merchantElect);
    }
}



