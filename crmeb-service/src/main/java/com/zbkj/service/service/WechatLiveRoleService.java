package com.zbkj.service.service;

import com.google.gson.JsonArray;
import com.zbkj.common.request.PageParamRequest;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/21 15:56
 * @Description: 描述对应的业务场景
 */
public interface WechatLiveRoleService {
    /**
     * 设置成员角色
     *
     * @param username 用户的微信号
     * @param role     设置用户的角色 取值[1-管理员，2-主播，3-运营者]，设置超级管理员将无效
     * @return 设置结果
     */
    String addRole(String username, int role);

    /**
     * 移除成员角色
     *
     * @param username 用户的微信号
     * @param role     删除用户的角色 取值[1-管理员，2-主播，3-运营者]，删除超级管理员将无效
     * @return 移除结果
     */
    String deleteRole(String username, int role);

    /**
     * 查询成员列表
     *
     * @param role             查询的用户角色，取值 [-1-所有成员， 0-超级管理员，1-管理员，2-主播，3-运营者]，默认-1
     * @param pageParamRequest 分页查询对象
     * @param keyword          搜索的微信号或昵称，不传则返回全部
     * @return
     */
    JsonArray listByRole(PageParamRequest pageParamRequest, Integer role, String keyword);
}
