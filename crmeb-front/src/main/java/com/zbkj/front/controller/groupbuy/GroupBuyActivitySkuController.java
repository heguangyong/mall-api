package com.zbkj.front.controller.groupbuy;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.groupbuy.MerchantGroupPageRequest;
import com.zbkj.common.response.groupbuy.GroupBuyActivityFrontOrderViewResponse;
import com.zbkj.common.response.groupbuy.GroupBuyActivityProductListForSale;
import com.zbkj.common.response.groupbuy.GroupBuyActivitySkuForSaleListResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.groupbuy.GroupBuyActivitySkuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 拼团商品表 商城端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/front/groupbuy/sku")
@Api(tags = "拼团商品表") //配合swagger使用

public class GroupBuyActivitySkuController {

    @Autowired
    private GroupBuyActivitySkuService groupBuyActivitySkuService;

    /**
     * 分页显示拼团商品表
     * @param pageParamRequest 分页参数
     * @author dazongzi
     * @since 2024-08-13
     */
//    @ApiOperation(value = "sku分页列表") //配合swagger使用
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    public CommonResult<CommonPage<GroupBuyActivitySkuForSaleListResponse>> getList(@Validated PageParamRequest pageParamRequest) {
//        CommonPage<GroupBuyActivitySkuForSaleListResponse> groupBuyActivitySkuCommonPage = CommonPage.restPage(groupBuyActivitySkuService.getGroupBuySkusForSaleList(pageParamRequest));
//        return CommonResult.success(groupBuyActivitySkuCommonPage);
//    }

    @ApiOperation(value = "拼团商品分页列表") //配合swagger使用
    @RequestMapping(value = "/product/list", method = RequestMethod.GET)
    public CommonResult<List<GroupBuyActivityProductListForSale>> getProductList(
            @Validated PageParamRequest pageParamRequest,
            @RequestParam(value = "showgroup", defaultValue = "-1") Integer showgroup) {
        return CommonResult.success(groupBuyActivitySkuService.getListByGroupProductIdByList(pageParamRequest,showgroup));
    }

    @ApiOperation(value = "拼团订单中查看拼团活动 - 用于分享 (自己购买过)")
    @RequestMapping(value = "/order/share/{orderNo}", method = RequestMethod.GET)
    public CommonResult<GroupBuyActivityFrontOrderViewResponse> orderView(@PathVariable(value = "orderNo") String orderNo) {
        return CommonResult.success(groupBuyActivitySkuService.getGroupBuyActivityFrontForShare(orderNo));
    }

    @ApiOperation(value = "拼团订单中查看拼团活动 - 用于参团 (自己还没购买)")
    @RequestMapping(value = "/order/readshare/{groupActivityId}/{productId}/{recordId}", method = RequestMethod.GET)
    public CommonResult<GroupBuyActivityFrontOrderViewResponse> orderView(@PathVariable(value = "groupActivityId") Integer groupActivityId,
                                                                          @PathVariable(value = "productId") Integer productId,
                                                                          @PathVariable(value = "recordId") Integer recordId) {
        return CommonResult.success(groupBuyActivitySkuService.getGroupBuyActivityFrontForReadShare(groupActivityId, productId, recordId));
    }

//    @ApiOperation(value = "商户拼团商品sku分页列表") //配合swagger使用
//    @RequestMapping(value = "/merchant/{merId}/page", method = RequestMethod.GET)
//    public CommonResult<CommonPage<GroupBuyActivitySkuForSaleListResponse>> findMerchantPage(@PathVariable(value = "merId") Integer merId,
//                                                                                             @Validated PageParamRequest pageParamRequest) {
//        CommonPage<GroupBuyActivitySkuForSaleListResponse> groupBuyActivitySkuCommonPage = CommonPage.restPage(groupBuyActivitySkuService.findMerchantGroupBuySkuForSalePage(merId, pageParamRequest));
//        return CommonResult.success(groupBuyActivitySkuCommonPage);
//    }

    @ApiOperation(value = "商户拼团商品分页列表")
    @RequestMapping(value = "/merchant/product/page", method = RequestMethod.GET)
    public CommonResult<CommonPage<GroupBuyActivityProductListForSale>> findMerchantProductPage(@Validated MerchantGroupPageRequest request) {
        return CommonResult.success(CommonPage.restPage(groupBuyActivitySkuService.findMerchantProductPageByFront(request)));
    }
}



