package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.wechat.live.WechatLiveGoods;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxmplive.goods.WechatLiveGoodsPlatReviewRequest;
import com.zbkj.common.request.wxmplive.goods.WechatLiveGoodsSearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.WechatLiveGoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 *  前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/mp/live/goods")
@Api(tags = "平台端 - 微信小程序 - 直播 - 商品") //配合swagger使用
public class PlatformWechatLiveGoodsController {

    @Autowired
    private WechatLiveGoodsService wechatLiveGoodsService;

    /**
     * 分页显示
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author dazongzi
     * @since 2023-03-27
     */
    @PreAuthorize("hasAuthority('platform:mp:live:goods:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<WechatLiveGoods>> getList(@Validated WechatLiveGoodsSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<WechatLiveGoods> wechatLiveGoodsCommonPage = CommonPage.restPage(wechatLiveGoodsService.getList(request, pageParamRequest, Boolean.FALSE));
        return CommonResult.success(wechatLiveGoodsCommonPage);
    }

    /**
     * 平台审核
     * @param reviewRequest 平台审核对象
     * @author dazongzi
     * @since 2023-03-27
     */
    @PreAuthorize("hasAuthority('platform:mp:live:goods:review')")
    @ApiOperation(value = "平台审核")
    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated WechatLiveGoodsPlatReviewRequest reviewRequest){
        if(wechatLiveGoodsService.platReview(reviewRequest.getId(), reviewRequest.getReviewStatus(), reviewRequest.getReviewReason())){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除
     * @param id Integer
     * @author dazongzi
     * @since 2023-03-27
     */
    @PreAuthorize("hasAuthority('platform:mp:live:goods:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@PathVariable(name = "id") Integer id){
        if(wechatLiveGoodsService.deleteGoods(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改排序
     * @author dazongzi
     * @since 2023-03-27
     */
    @PreAuthorize("hasAuthority('platform:mp:live:goods:sort')")
    @ApiOperation(value = "修改排序")
    @RequestMapping(value = "/sort/{id}/{sort}", method = RequestMethod.GET)
    public CommonResult<String> sort(@PathVariable(name = "id") Integer id, @PathVariable(name = "sort") Integer sort){
        if(wechatLiveGoodsService.updateSort(id, sort)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询信息
     * @author dazongzi
     * @since 2023-03-27
     */
    @PreAuthorize("hasAuthority('platform:mp:live:goods:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<WechatLiveGoods> info(@PathVariable(value = "id") Integer id){
        WechatLiveGoods wechatLiveGoods = wechatLiveGoodsService.getById(id);
        return CommonResult.success(wechatLiveGoods);
   }

}



