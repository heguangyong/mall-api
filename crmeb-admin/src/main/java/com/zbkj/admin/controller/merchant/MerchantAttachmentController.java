package com.zbkj.admin.controller.merchant;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.system.SystemAttachment;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemAttachmentMoveRequest;
import com.zbkj.common.request.SystemAttachmentRemoveRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.service.service.SystemAttachmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 附件管理表 前端控制器 - 商户端
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 *
 * @author CRMEB Java
 */
@Slf4j
@RestController
@RequestMapping("api/admin/merchant/attachment")
@Api(tags = "商户端附件管理") //配合swagger使用
public class MerchantAttachmentController {

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @PreAuthorize("hasAuthority('merchant:attachment:list')")
    @ApiOperation(value = "附件分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemAttachment>> getList(@RequestParam(value = "pid", required = false) Integer pid,
                                                              @RequestParam(value = "attType",
                                                                      defaultValue = "png,jpeg,jpg,audio/mpeg,text/plain,video/mp4,gif",
                                                                      required = false) String attType,
                                                              @Validated PageParamRequest pageParamRequest) {
        // 资源归属方：-1=平台(查看全部)，商户或者用户id(仅查看自己上传的素材)
        CommonPage<SystemAttachment> systemAttachmentCommonPage =
                CommonPage.restPage(systemAttachmentService.getList(pid, attType, pageParamRequest));
        return CommonResult.success(systemAttachmentCommonPage);
    }


    @PreAuthorize("hasAuthority('merchant:attachment:delete')")
    @LogControllerAnnotation(intoDB = true, methodType = MethodType.DELETE, description = "删除附件")
    @ApiOperation(value = "删除附件")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult<String> delete(@RequestBody @Validated SystemAttachmentRemoveRequest removeRequest) {
        if (systemAttachmentService.deleteByIds(CrmebUtil.stringToArray(removeRequest.getIds()))) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }


    @PreAuthorize("hasAuthority('merchant:attachment:move')")
    @ApiOperation(value = "更改图片目录")
    @RequestMapping(value = "/move", method = RequestMethod.POST)
    public CommonResult<String> updateAttrId(@RequestBody @Validated SystemAttachmentMoveRequest move) {
        if (systemAttachmentService.updateAttrId(move)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }
}



