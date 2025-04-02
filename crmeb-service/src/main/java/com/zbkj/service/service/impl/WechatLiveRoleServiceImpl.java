package com.zbkj.service.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.google.gson.JsonArray;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.service.service.WechatLiveRoleService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/21 15:46
 * @Description: 小程序直播成员管理
 */
@Service
public class WechatLiveRoleServiceImpl implements WechatLiveRoleService {

    private static final Logger logger = LoggerFactory.getLogger(WechatLiveRoleServiceImpl.class);

    @Autowired
    private WxMaService wxMaService;

    /**
     * 设置成员角色
     * @param username 用户的微信号
     * @param role 设置用户的角色 取值[1-管理员，2-主播，3-运营者]，设置超级管理员将无效
     * @return 设置结果
     */
    @Override
    public String addRole(String username, int role){
        try {
            return wxMaService.getLiveMemberService().addRole(username, role);
        }catch (WxErrorException e){
            logger.error("微信小程序直播 - 设置成员角色 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 设置成员角色！");
        }
    }

    /**
     * 移除成员角色
     * @param username 用户的微信号
     * @param role 删除用户的角色 取值[1-管理员，2-主播，3-运营者]，删除超级管理员将无效
     * @return 移除结果
     */
    @Override
    public String deleteRole(String username, int role){
        try {
            return wxMaService.getLiveMemberService().deleteRole(username, role);
        }catch (WxErrorException e){
            logger.error("微信小程序直播 - 移除成员角色 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 移除成员角色！");
        }
    }


    /**
     * 查询成员列表
     * @param role 查询的用户角色，取值 [-1-所有成员， 0-超级管理员，1-管理员，2-主播，3-运营者]，默认-1
     * @param pageParamRequest 分页查询对象
     * @param keyword 搜索的微信号或昵称，不传则返回全部
     * @return
     */
    @Override
    public JsonArray listByRole(PageParamRequest pageParamRequest, Integer role, String keyword){
        try {
            return wxMaService.getLiveMemberService().listByRole(role, pageParamRequest.getPage(), pageParamRequest.getLimit(), keyword);
        }catch (WxErrorException e){
            logger.error("微信小程序直播 - 查询成员列表 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 查询成员列表！");
        }
    }
}
