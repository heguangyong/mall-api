package com.zbkj.service.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.wechat.video.PayComponentCat;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxvedio.cat.PayComponentCatPageListRequest;
import com.zbkj.common.vo.wxvedioshop.cat_brand.CatItem;

import java.util.List;

/**
 *
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
public interface PayComponentCatService extends IService<PayComponentCat> {

    /**
     * 自动更新自定义交易组件类目
     */
    void autoUpdate();

    /**
     * 获取类目树形结构用于选择
     * @return List<FirstCatVo>
     */
    List<CatItem> getTreeList();

    /**
     * 获取分页类目数据 用于类目申请和整体查看
     * @return List<FirstCatVo>
     */
    List<PayComponentCat> getList(PayComponentCatPageListRequest payComponentCat, PageParamRequest pageParamRequest);

    /**
     * 根据第三级id获取类目
     * @param thirdCatId 第三级id
     * @return PayComponentCat
     */
    PayComponentCat getByThirdCatId(Integer thirdCatId);

    /**
     * 根据提交审核时返回的auditId查询对应类目信息
     * @param audit 类目提审到微信侧时返回的id
     * @return 当前审核的类目信息
     */
    PayComponentCat getByAudit(String audit);

    /**
     * 接收处理类目审核结果查询和审核回调
     * @param jsonObject 微信返回的审核结果
     * @param audit 审核id 如果有数据证明是自己查询的 没有则是回调事件
     */
    void getAuditResultOrAuditCallBack(JSONObject jsonObject, String audit);
}
