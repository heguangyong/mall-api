package com.zbkj.service.service;


import cn.binarywang.wx.miniapp.bean.live.WxMaLiveResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.wechat.live.WechatLiveAssistant;
import com.zbkj.common.model.wechat.live.WechatLiveRoom;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxmplive.goods.WechatLiveGoodsInRoomResponse;
import com.zbkj.common.request.wxmplive.goods.WechatLiveRoomSearchRequest;
import com.zbkj.common.request.wxmplive.room.WechatMpLiveRoomInfoRequest;
import com.zbkj.common.request.wxmplive.room.WechatLiveRoomSharCode;

import java.util.List;
import java.util.Map;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/9 10:25
 * @Description: 小程序直播服务
 */
public interface WechatLiveRoomService extends IService<WechatLiveRoom> {

    /**
     * 创建直播间
     * @param roomInfo 直播间对象
     * @return 创建结果
     */
    Boolean creteRoom(WechatMpLiveRoomInfoRequest roomInfo);

    /**
     * 平台审核:
     * @param id 当前本地直播间id
     * @param reviewStatus 审核状态 0=商户创建成功平台待审核，1=平台审核失败，2=平台审核成功并提交给微信审核失败，3=平台审核成功并提交给微信审核成功
     * @param reviewReason 审核失败原因
     * @return 审核结果
     */
    Boolean reviewRoom(Integer id, Integer reviewStatus, String reviewReason);

    /**
     * 编辑直播间
     * @param roomInfo 待编辑直播间对象
     * @return 编辑结果
     */
    Boolean editRoom(WechatMpLiveRoomInfoRequest roomInfo);

    /**
     * 删除直播间
     * @param id 直播间id
     * @return 删除结果
     */
    Boolean deleteRoom(Integer id);

    /**
     * 直播间详情
     * @param id 直播间id
     * @return 详情
     */
    WechatLiveRoom getRoomInfoById(Integer id);

    /**
     * 获取直播列表和回放
     * @param searchRequest 直播间搜索
     * @param pageParamRequest 分页参数
     * @param isMer 时候商户
     * @return 查询结果
     */
    List<WechatLiveRoom> getLiveList(WechatLiveRoomSearchRequest searchRequest, PageParamRequest pageParamRequest, Boolean isMer);

    /**
     * 移动端商城 直播列表数据
     * @param pageParamRequest 分页数据
     * @return 直播列表
     */
    List<WechatLiveRoom> getLiveListForFront(PageParamRequest pageParamRequest);

//    /**
//     * 根据直播间id获取推流地址
//     * @param roomId 直播间id
//     * @return 推流地址
//     */
//    String getPushUrl(Integer roomId);

    /**
     * 获取直播间分享二维码
     * @param id 直播间id
     * @param params 自定义参数
     * @return 获取分享二维码结果
     */
    WechatLiveRoomSharCode getShareQRCodeByRoomId(Integer id, String params);

    /**
     * 添加商品到直播间
     * @param id 直播间id
     * @param ids 商品id集合
     * @return 添加商品到直播间结果
     */
    Boolean addGoodsToRoom(Integer id, List<Integer> ids);

    /**
     * 加载当前直播间商品列表
     * @param id 直播间id
     * @return 直播间对应的商品列表信息
     */
    List<WechatLiveGoodsInRoomResponse> getRoomGoodsList(Integer id);

    /**
     * 删除直播间商品
     * @param id 直播间id
     * @param goodsId 待删除商品id
     * @return 删除商品结果
     */
    Boolean deleteGoodsInRoom(Integer id, Integer goodsId);

    /**
     * 获取主播副号
     * @param roomId 房间id
     * @return 主播副号
     */
    String getSubanchorByRoomId(Integer roomId);

    /**
     * 新增主播副号
     * @param roomId 直播间id
     * @param userName 主播副号
     * @return 添加结果
     */
    Boolean addSubanchorNameByRoomId(Integer roomId, String userName);

    /**
     * 修改主播副号
     * @param roomId 直播间id
     * @param userName 主播副号
     * @return 修改结果
     */
    Boolean modifySubanchorNameByRoomId(Integer roomId, String userName);

    /**
     * 删除主播副号
     * @param roomId 直播间id
     * @return 删除结果
     */
    Boolean deleteSubanchorNameByRoomId(Integer roomId);

    /**
     *  推送商品
     * @param roomId 直播间id
     * @param goodsId 商品id
     * @return 推送结果
     */
    Boolean goodsPush(Integer roomId, Integer goodsId);

    /**
     * 上下架商品
     * @param roomId 直播间id
     * @param goodsId 商品id
     * @param onSale 上下架 【0：下架，1：上架】
     * @return 上下架结果
     */
    Boolean goodsOnSale(Integer roomId, Integer goodsId, Integer onSale);

    /**
     * 商品排序
     * @param roomId 直播间id
     * @param goods 排序商品
     * @return 排序结果
     */
    Boolean goodsSort(Integer roomId, List<Map<String, String>> goods);

    /**
     * 管理直播间小助手到直播间
     * @param id 本地直播间id
     * @param assids 小助手信息集合
     * @return 添加结果
     */
    Boolean mangerAssistant(Integer id, List<Integer> assids);

    /**
     * 修改直播间小助手
     * @param roomId 直播间id
     * @param assId 小助手id
     * @return 修改结果
     */
    Boolean modifyAssistant(Integer roomId, Integer assId);

    /**
     * 删除直播间小助手
     * @param id 直播间id
     * @param assid 小助手本地id
     * @return 删除结果
     */
    Boolean removeAssistant(Integer id, Integer assid);

    /**
     * 获取直播间小助手
     * @param id 本地直播间id
     * @return 获取结果
     */
    List<WechatLiveAssistant> getAssistantByRoom(Integer id);

    /**
     * 操作禁言功能
     * @param id 本地直播间id
     * @param banComment 1-禁言，0-取消禁言
     * @return 禁言结果
     */
    Boolean updateComment(Integer id, Integer banComment);

    /**
     * 官方收录管理
     * @param id 直播间id
     * @param isFeedsPublic 是否开启官方收录 【1: 开启，0：关闭】
     * @return 操作结果
     */
    Boolean updateFeedPublic(Integer id, Integer isFeedsPublic);

    /** 客服功能管理
     * @param id 直播间id
     * @param closeKf 是否关闭客服 【0：开启，1：关闭】
     * @return 结果
     */
    Boolean updateKF(Integer id, Integer closeKf);

    /**
     * 回放功能管理
     * @param id 直播间id
     * @param closeReplay  是否关闭回放 【0：开启，1：关闭】
     * @return 结果
     */
    Boolean updateReplay(Integer id, Integer closeReplay);

    /**
     * 下载商品讲解视频
     * @param id 直播间id
     * @param goodsId 商品id
     * @return 视频下载链接
     */
    String getVideo(Integer id, Integer goodsId);

    /**
     * 同步直播间信息
     * @return 微信端直播间信息
     */
    List<WxMaLiveResult.RoomInfo> syncLiveRoom();

    /**
     * 在商城中显示
     * @param id 直播间id
     * @param show 0=隐藏 1=显示
     * @return 操作结果
     */
    Boolean showStore(Integer id, Integer show);
}
