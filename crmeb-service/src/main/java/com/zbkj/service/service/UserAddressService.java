package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.user.UserAddress;
import com.zbkj.common.request.UserAddressRequest;
import com.zbkj.common.request.WechatAddressImportRequest;

import java.util.List;

/**
 * UserAddressService 接口实现
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
public interface UserAddressService extends IService<UserAddress> {

    /**
     * 添加用户地址
     *
     * @param request 地址请求参数
     * @return 地址ID
     */
    Integer create(UserAddressRequest request);

    /**
     * 用户地址编辑
     *
     * @param request 编辑请求参数
     * @return Boolean
     */
    Boolean edit(UserAddressRequest request);

    /**
     * 删除用户地址
     *
     * @param id 地址id
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 获取默认地址
     */
    UserAddress getDefault();

    /**
     * 获取地址详情
     *
     * @param id 地址id
     * @return UserAddress
     */
    UserAddress getDetail(Integer id);

    /**
     * 获取默认地址
     *
     * @return UserAddress
     */
    UserAddress getDefaultByUid(Integer uid);

    /**
     * 获取所有的用户地址
     *
     * @return List
     */
    List<UserAddress> getAllList();

    /**
     * 设置默认地址
     *
     * @param id 地址id
     * @return Boolean
     */
    Boolean setDefault(Integer id);

//    /**
//     * 微信地址导入
//     * @param request 地址参数
//     * @return 是否成功
//     */
//    Boolean wechatImport(WechatAddressImportRequest request);

    /**
     * 获取微信地址信息
     * @param request 微信地址参数
     */
    UserAddress getWechatInfo(WechatAddressImportRequest request);
}
