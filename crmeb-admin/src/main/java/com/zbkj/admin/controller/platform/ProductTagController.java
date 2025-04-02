package com.zbkj.admin.controller.platform;


import com.zbkj.common.model.product.ProductTag;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.producttag.ProductTagRequest;
import com.zbkj.common.request.producttag.ProductTagSearchRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.ProductTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 *  前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/producttag")
@Api(tags = "商品标签") //配合swagger使用
public class ProductTagController {

    @Autowired
    private ProductTagService productTagService;

    /**
     * 分页显示
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author dazongzi
     * @since 2023-10-11
     */
    @PreAuthorize("hasAuthority('platform:product:tag:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ProductTag>>  getList(@Validated ProductTagSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<ProductTag> productTagCommonPage = CommonPage.restPage(productTagService.getList(request, pageParamRequest));
        return CommonResult.success(productTagCommonPage);
    }

    /**
     * 新增
     * @param productTagRequest 新增参数
     * @author dazongzi
     * @since 2023-10-11
     */
    @PreAuthorize("hasAuthority('platform:product:tag:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated ProductTagRequest productTagRequest){
        ProductTag productTag = new ProductTag();
        BeanUtils.copyProperties(productTagRequest, productTag);
        if(productTagService.saveProductTag(productTag)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除
     * @param id Integer
     * @author dazongzi
     * @since 2023-10-11
     */
    @PreAuthorize("hasAuthority('platform:product:tag:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@PathVariable(value = "id") Integer id){
        if(productTagService.deleteProductTag(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改
     * @param productTagRequest 修改参数
     * @author dazongzi
     * @since 2023-10-11
     */
    @PreAuthorize("hasAuthority('platform:product:tag:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated ProductTagRequest productTagRequest){
        ProductTag productTag = new ProductTag();
        BeanUtils.copyProperties(productTagRequest, productTag);
        if(productTagService.editProductTag(productTag)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 更新显示状态
     * @param id 标签id
     * @param status 更改的状态
     * @return 更改结果
     */
    @PreAuthorize("hasAuthority('platform:product:tag:status')")
    @ApiOperation(value = "更新状态")
    @RequestMapping(value = "/status/{id}/{status}", method = RequestMethod.GET)
    public CommonResult<String> status(@PathVariable Integer id, @PathVariable Integer status){
        if(productTagService.updateStatus(id, status)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询信息
     * @param id Integer
     * @author dazongzi
     * @since 2023-10-11
     */
    @PreAuthorize("hasAuthority('platform:product:tag:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<ProductTag> info(@PathVariable(value = "id") Integer id){
        ProductTag productTag = productTagService.getById(id);
        return CommonResult.success(productTag);
   }
}



