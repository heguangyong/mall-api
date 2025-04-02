package com.zbkj.service.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.live.*;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.enums.WechatMPLiveRoomReviewStatusEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.exception.weixin.CRMEBWxError;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.wechat.live.WechatLiveAssistant;
import com.zbkj.common.model.wechat.live.WechatLiveGoods;
import com.zbkj.common.model.wechat.live.WechatLiveRoom;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxmplive.goods.WechatGoodsJSON;
import com.zbkj.common.request.wxmplive.goods.WechatLiveGoodsInRoomResponse;
import com.zbkj.common.request.wxmplive.goods.WechatLiveRoomSearchRequest;
import com.zbkj.common.request.wxmplive.room.WechatLiveRoomSharCode;
import com.zbkj.common.request.wxmplive.room.WechatMpLiveRoomInfoRequest;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.LoginUserVo;
import com.zbkj.service.dao.wechat.live.WechatLiveRoomDao;
import com.zbkj.service.service.*;
import me.chanjar.weixin.common.enums.WxType;
import me.chanjar.weixin.common.error.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/9 10:40
 * @Description: 小程序直播服务
 */
@Service
public class WechatLiveRoomServiceImpl extends ServiceImpl<WechatLiveRoomDao, WechatLiveRoom> implements WechatLiveRoomService {

    private static final Logger logger = LoggerFactory.getLogger(WechatLiveRoomServiceImpl.class);

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private WechatLiveAssistantService wechatLiveAssistantService;

    @Resource
    private WechatLiveRoomDao wechatLiveRoomDao;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private WechatLiveGoodsService weChatLiveGoodsService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 创建直播室
     *
     * @param roomInfo 直播间对象
     * @return 创建结果
     */
    @Override
    public Boolean creteRoom(WechatMpLiveRoomInfoRequest roomInfo) {
        // 获取当前登录人信息和商户信息
        SystemAdmin currentUser = SecurityUtil.getLoginUserVo().getUser();
        Merchant currentMerchantInfo = merchantService.getByIdException(currentUser.getMerId());
        WechatLiveRoom wechatLiveRoom = new WechatLiveRoom();
        BeanUtils.copyProperties(roomInfo, wechatLiveRoom);
        wechatLiveRoom.setMerName(currentMerchantInfo.getName());
        wechatLiveRoom.setMerType(currentMerchantInfo.getTypeId());
        wechatLiveRoom.setMerId(currentMerchantInfo.getId());
        wechatLiveRoom.setCoverImgLocal(systemAttachmentService.clearPrefix(wechatLiveRoom.getCoverImgLocal()));
        wechatLiveRoom.setShareImgLocal(systemAttachmentService.clearPrefix(wechatLiveRoom.getShareImgLocal()));
        wechatLiveRoom.setFeedsImgLocal(systemAttachmentService.clearPrefix(wechatLiveRoom.getFeedsImgLocal()));
        wechatLiveRoom.setStartTime(CrmebDateUtil.strToDate(roomInfo.getStartTime(), DateConstants.DATE_FORMAT));
        wechatLiveRoom.setEndTime(CrmebDateUtil.strToDate(roomInfo.getEndTime(), DateConstants.DATE_FORMAT));
        wechatLiveRoom.setReviewStatus(WechatMPLiveRoomReviewStatusEnum.BEFORE_REVIEW.getCode());
        return wechatLiveRoomDao.insert(wechatLiveRoom) > 0;
    }

    /**
     * 平台审核:
     *
     * @param id           当前本地直播间id
     * @param reviewStatus 审核状态 0=商户创建成功平台待审核，1=平台审核失败，2=平台审核成功并提交给微信审核失败，3=平台审核成功并提交给微信审核成功
     * @param reviewReason 审核失败原因
     * @return 审核结果
     */
    @Override
    public Boolean reviewRoom(Integer id, Integer reviewStatus, String reviewReason) {
        if (reviewStatus.equals(WechatMPLiveRoomReviewStatusEnum.PLAT_REVIEW_ERROR.getCode()) &&
                ObjectUtil.isEmpty(reviewReason)) {
            throw new CrmebException("平台审核失败时，审核原因不能为空！");
        }
        WechatLiveRoom wechatLiveRoom = wechatLiveRoomDao.selectById(id);

        // 审核失败
        if (reviewStatus.equals(WechatMPLiveRoomReviewStatusEnum.PLAT_REVIEW_ERROR.getCode())) {
            wechatLiveRoom.setReviewStatus(WechatMPLiveRoomReviewStatusEnum.PLAT_REVIEW_ERROR.getCode());
            return wechatLiveRoomDao.updateById(wechatLiveRoom) > 0;
        }
        // 执行审核成功逻辑 回写微信审核结果
        try {
            wechatLiveRoom.setReviewStatus(reviewStatus);
            wechatLiveRoom.setReviewReason(reviewReason);
            // 判断时候提交给微信审核
            if (wechatLiveRoom.getReviewStatus().equals(WechatMPLiveRoomReviewStatusEnum.WECHAT_REVIEW_SUCCESS.getCode())) {
                WxMaLiveRoomInfo wxMaLiveRoomInfo = new WxMaLiveRoomInfo();
                BeanUtils.copyProperties(wechatLiveRoom, wxMaLiveRoomInfo);
                wxMaLiveRoomInfo.setStartTime(wechatLiveRoom.getStartTime().getTime() / 1000);
                wxMaLiveRoomInfo.setEndTime(wechatLiveRoom.getEndTime().getTime() / 1000);
                wxMaLiveRoomInfo.setName(wechatLiveRoom.getRoomName());
                // 审核成功后回写小程序直播码和直播间id
                WxMaCreateRoomResult roomResult = wxMaService.getLiveService().createRoom(wxMaLiveRoomInfo);
                if (ObjectUtil.isNotNull(roomResult.getRoomId())) {
                    wechatLiveRoom.setRoomId(roomResult.getRoomId())
                            .setQrcodeUrl(roomResult.getQrcodeUrl())
                            .setReviewStatus(WechatMPLiveRoomReviewStatusEnum.WECHAT_REVIEW_SUCCESS.getCode());
                } else {
                    wechatLiveRoom.setReviewStatus(WechatMPLiveRoomReviewStatusEnum.WECHAT_REVIEW_ERROR.getCode())
                            .setReviewReason("微信审核直播间 失败:" + JSON.toJSONString(roomResult));
                }
            } else {
                throw new CrmebException("微信小程序直播 审核有漏网参数:id:" + id + "|reviewStatus:" + reviewStatus + "|reviewReason:" + reviewReason + "!{}" + JSON.toJSONString(wechatLiveRoom));
            }
            return wechatLiveRoomDao.updateById(wechatLiveRoom) > 0;
        } catch (WxErrorException e) {
            wechatLiveRoom.setReviewStatus(WechatMPLiveRoomReviewStatusEnum.WECHAT_REVIEW_ERROR.getCode())
                    .setReviewReason(e.getMessage());
            wechatLiveRoomDao.updateById(wechatLiveRoom);
            logger.error("微信小程序直播 - 创建直播室失败 {}", e.getMessage());
            throw new CrmebException("微信小程序直播 - 创建直播室失败！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }


    /**
     * 编辑直播间
     *
     * @param roomInfo 待编辑直播间对象
     * @return 编辑结果
     */
    @Override
    public Boolean editRoom(WechatMpLiveRoomInfoRequest roomInfo) {
        // 获取当前直播间真实状态 判断时候需要更新
        WechatLiveRoom wechatLiveRoom = getRoomInfoById(roomInfo.getId());
        try {

            if (wechatLiveRoom.getReviewStatus().equals(WechatMPLiveRoomReviewStatusEnum.WECHAT_REVIEW_SUCCESS.getCode())) {
                WxMaLiveRoomInfo wxMaLiveRoomInfo = new WxMaLiveRoomInfo();
                BeanUtils.copyProperties(roomInfo, wxMaLiveRoomInfo);
                wxMaLiveRoomInfo.setId(wechatLiveRoom.getRoomId());
                wxMaLiveRoomInfo.setStartTime(wechatLiveRoom.getStartTime().getTime() / 1000);
                wxMaLiveRoomInfo.setEndTime(wechatLiveRoom.getEndTime().getTime() / 1000);
                wxMaLiveRoomInfo.setName(wechatLiveRoom.getRoomName());
                wxMaService.getLiveService().editRoom(wxMaLiveRoomInfo);
            } else {
                wechatLiveRoom.setReviewStatus(WechatMPLiveRoomReviewStatusEnum.BEFORE_REVIEW.getCode());
            }
            BeanUtils.copyProperties(roomInfo, wechatLiveRoom);
            if (ObjectUtil.isNotEmpty(wechatLiveRoom.getCoverImgLocal())) {
                wechatLiveRoom.setCoverImgLocal(systemAttachmentService.clearPrefix(wechatLiveRoom.getCoverImgLocal()));
            }
            if (ObjectUtil.isNotEmpty(wechatLiveRoom.getShareImgLocal())) {
                wechatLiveRoom.setShareImgLocal(systemAttachmentService.clearPrefix(wechatLiveRoom.getShareImgLocal()));
            }
            if (ObjectUtil.isNotEmpty(wechatLiveRoom.getFeedsImgLocal())) {
                wechatLiveRoom.setFeedsImgLocal(systemAttachmentService.clearPrefix(wechatLiveRoom.getFeedsImgLocal()));
            }
            if (ObjectUtil.isNotEmpty(roomInfo.getStartTime())) {
                wechatLiveRoom.setStartTime(CrmebDateUtil.strToDate(roomInfo.getStartTime(), DateConstants.DATE_FORMAT));
            }
            if (ObjectUtil.isNotEmpty(roomInfo.getEndTime())) {
                wechatLiveRoom.setEndTime(CrmebDateUtil.strToDate(roomInfo.getEndTime(), DateConstants.DATE_FORMAT));
            }

//            if(ObjectUtil.isNotEmpty(roomInfo.getRoomName())) {
//                wechatLiveRoom.setRoomName(roomInfo.getRoomName());
//            }
            return wechatLiveRoomDao.updateById(wechatLiveRoom) > 0;
        } catch (WxErrorException e) {
            //wechatLiveRoom.setReviewStatus(WechatMPLiveRoomReviewStatusEnum.BEFORE_REVIEW.getCode()).setReviewReason(e.toString());
            wechatLiveRoomDao.updateById(wechatLiveRoom);
            logger.error("微信小程序直播 - 编辑直播室失败 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 编辑直播室失败！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 删除直播间
     *
     * @param id 直播间id
     * @return 删除结果
     */
    @Override
    public Boolean deleteRoom(Integer id) {
        WechatLiveRoom wechatLiveRoom = getRoomInfoById(id);
        try {
            if (wechatLiveRoom.getReviewStatus().equals(WechatMPLiveRoomReviewStatusEnum.WECHAT_REVIEW_SUCCESS.getCode())) {
                wxMaService.getLiveService().deleteRoom(wechatLiveRoom.getRoomId());
            }
            return wechatLiveRoomDao.deleteById(id) > 0;
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 删除直播间失败 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 删除直播间失败！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 直播间详情
     *
     * @param id 直播间id
     * @return 详情
     */
    @Override
    public WechatLiveRoom getRoomInfoById(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        WechatLiveRoom liveRoom = getById(id);
        if (ObjectUtil.isNull(liveRoom)) {
            throw new CrmebException("直播间不存在");
        }
        if (admin.getMerId() > 0 && !admin.getMerId().equals(liveRoom.getMerId())) {
            throw new CrmebException("直播间不存在");
        }
        return liveRoom;
    }

    /**
     * 获取直播列表和回放
     *
     * @param pageParamRequest 分页参数
     * @return 查询结果
     */
    @Override
    public List<WechatLiveRoom> getLiveList(WechatLiveRoomSearchRequest searchRequest, PageParamRequest pageParamRequest, Boolean isMer) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LoginUserVo currentUser = SecurityUtil.getLoginUserVo();
        //带 WechatLiveGoods 类的多条件查询
        LambdaQueryWrapper<WechatLiveRoom> lambdaQueryWrapper = Wrappers.lambdaQuery();
        // 搜索关键字:id,直播间id,直播间名称,主播昵称,主播微信号,主播副号微信号,主播手机号
        if (ObjectUtil.isNotEmpty(searchRequest.getKeywords())) {
            String decodeKeyWords = URLUtil.decode(searchRequest.getKeywords());
            lambdaQueryWrapper.eq(WechatLiveRoom::getId, decodeKeyWords)
                    .or().eq(WechatLiveRoom::getRoomId, decodeKeyWords)
                    .or().like(WechatLiveRoom::getRoomName, decodeKeyWords)
                    .or().like(WechatLiveRoom::getAnchorName, decodeKeyWords)
                    .or().like(WechatLiveRoom::getAnchorWechat, decodeKeyWords)
                    .or().like(WechatLiveRoom::getSubAnchorWechat, decodeKeyWords);
        }
        // 直播间状态。101：直播中，102：未开始，103已结束，104禁播，105：暂停，106：异常，107：已过期
        if (ObjectUtil.isNotEmpty(searchRequest.getLiveStatus())) {
            lambdaQueryWrapper.eq(WechatLiveRoom::getLiveStatus, searchRequest.getLiveStatus());
        }
        if (ObjectUtil.isNotEmpty(searchRequest.getStoreShow())) {
            lambdaQueryWrapper.eq(WechatLiveRoom::getStoreShow, searchRequest.getStoreShow());
        }
        // 审核:0=商户创建成功平台待审核，1=平台审核失败，2=平台审核成功并提交给微信审核失败，3=平台审核成功并提交给微信审核成功
        if (ObjectUtil.isNotEmpty(searchRequest.getReviewStatus())) {
            lambdaQueryWrapper.eq(WechatLiveRoom::getReviewStatus, searchRequest.getReviewStatus());
        }
        // 商户名称
        if (ObjectUtil.isNotEmpty(searchRequest.getMerName())) {
            lambdaQueryWrapper.like(WechatLiveRoom::getMerName, searchRequest.getMerName());
        }
        // 商户类型
        if (ObjectUtil.isNotEmpty(searchRequest.getMerType())) {
            lambdaQueryWrapper.like(WechatLiveRoom::getMerType, searchRequest.getMerType());
        }
        // 推荐星级
        if (ObjectUtil.isNotEmpty(searchRequest.getStar())) {
            lambdaQueryWrapper.like(WechatLiveRoom::getStar, searchRequest.getStar());
        }
        // 排序
        if (ObjectUtil.isNotEmpty(searchRequest.getSort())) {
            lambdaQueryWrapper.like(WechatLiveRoom::getSort, searchRequest.getSort());
        }
        if (isMer) {
            lambdaQueryWrapper.eq(WechatLiveRoom::getMerId, currentUser.getUser().getMerId());
        }

        lambdaQueryWrapper.orderByDesc(WechatLiveRoom::getStar).orderByDesc(WechatLiveRoom::getSort).orderByDesc(WechatLiveRoom::getId);
        return wechatLiveRoomDao.selectList(lambdaQueryWrapper);
    }

    /**
     * 移动端商城 直播列表数据 直播间状态。101：直播中，102：未开始，103已结束，104禁播，105：暂停，106：异常，107：已过期
     * 查找有为开始和
     *
     * @param pageParamRequest 分页数据
     * @return 直播列表
     */
    @Override
    public List<WechatLiveRoom> getLiveListForFront(PageParamRequest pageParamRequest) {
        // 查询本地数据
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<WechatLiveRoom> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.le(WechatLiveRoom::getLiveStatus, 103)
                .eq(WechatLiveRoom::getStoreShow, 1) // 0=隐藏 1=显示
                .orderByDesc(WechatLiveRoom::getSort)
                .orderByDesc(WechatLiveRoom::getStartTime);
        return wechatLiveRoomDao.selectList(lambdaQueryWrapper);

    }

    //    /**
//     * 根据直播间id获取推流地址
//     * @param roomId 直播间id
//     * @return 推流地址
//     */
//    @Override
//    public String getPushUrl(Integer roomId){
//        try {
//            return wxMaService.getLiveService().getPushUrl(roomId);
//        }catch (WxErrorException e){
//            logger.error("微信小程序直播 - 获取直播间推流地址失败 {}", e.getError());
//            throw new CrmebException("微信小程序直播 - 获取直播间推流地址失败！");
//        }
//    }

    /**
     * 获取直播间分享二维码
     *
     * @param id     直播间id
     * @param params 自定义参数
     * @return 获取分享二维码结果
     */
    @Override
    public WechatLiveRoomSharCode getShareQRCodeByRoomId(Integer id, String params) {
        WechatLiveRoom wechatLiveRoom = getRoomInfoById(id);
        WechatLiveRoomSharCode sharCode = new WechatLiveRoomSharCode();
        try {
            WxMaLiveSharedCode sharedCode = wxMaService.getLiveService().getSharedCode(wechatLiveRoom.getRoomId(), params);
            String json = new Gson().toJson(sharedCode);
            sharCode = JSON.parseObject(json, WechatLiveRoomSharCode.class);

            wechatLiveRoom.setQrcodeUrl(json);
            wechatLiveRoomDao.updateById(wechatLiveRoom);
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 获取直播间分享二维码失败 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 获取直播间分享二维码失败！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
        return sharCode;
    }

    /**
     * 添加商品到直播间
     *
     * @param id  本地直播间id
     * @param ids 商品id集合
     * @return 添加商品到直播间结果
     */
    @Override
    public Boolean addGoodsToRoom(Integer id, List<Integer> ids) {
        WechatLiveRoom wechatLiveRoom = getRoomInfoById(id);
        try {
            boolean addGoodsToRoomResult = wxMaService.getLiveService().addGoodsToRoom(wechatLiveRoom.getRoomId(), ids);
            wechatLiveRoom.setGoods(Joiner.on(",").join(ids));
            return wechatLiveRoomDao.updateById(wechatLiveRoom) > 0;
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 添加商品到直播间 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 添加商品到直播间！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 加载当前直播间商品列表
     *
     * @param id 直播间id
     * @return 直播间对应的商品列表信息
     */
    @Override
    public List<WechatLiveGoodsInRoomResponse> getRoomGoodsList(Integer id) {
        List<WechatLiveGoodsInRoomResponse> responseList = new ArrayList<>();
        WechatLiveRoom wechatLiveRoom = getRoomInfoById(id);
        if (ObjectUtil.isEmpty(wechatLiveRoom.getGoods()) || wechatLiveRoom.getGoods().length() == 0)
            return new ArrayList<>();

        // 本地导入的记录
        List<Integer> goodsIdList = Stream.of(wechatLiveRoom.getGoods().split(",")).map(Integer::valueOf).collect(Collectors.toList());

        // 同步的微信记录 对比做出上下架状态差异
        List<WechatGoodsJSON> wechatGoodsJSONS = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(wechatLiveRoom.getGoodsJson())) {
            wechatGoodsJSONS = JSONUtil.toList(JSONUtil.parseArray(wechatLiveRoom.getGoodsJson(), Boolean.FALSE), WechatGoodsJSON.class);
        }

        List<WechatLiveGoods> currentRoomGoods = weChatLiveGoodsService.getByGoodsId(goodsIdList);
        for (WechatLiveGoods currentRoomGood : currentRoomGoods) {
            WechatLiveGoodsInRoomResponse roomResponse = new WechatLiveGoodsInRoomResponse();
            BeanUtils.copyProperties(currentRoomGood, roomResponse);
            boolean isExit = false;
            if (ObjectUtil.isNotEmpty(wechatGoodsJSONS)) {
                isExit = wechatGoodsJSONS.stream().filter(good -> good.getGoodsId().equals(currentRoomGood.getGoodsId())).collect(Collectors.toList()).size() > 0;
            }
            roomResponse.setOnSale(isExit ? 1 : 0);
            responseList.add(roomResponse);
        }

        return responseList;
    }

    /**
     * 删除直播间商品
     *
     * @param id      直播间id
     * @param goodsId 待删除商品id
     * @return 删除商品结果
     */
    @Override
    public Boolean deleteGoodsInRoom(Integer id, Integer goodsId) {
        try {
            WechatLiveRoom wechatLiveRoom = getRoomInfoById(id);

            boolean deleteInRoomResult = wxMaService.getLiveService().deleteInRoom(wechatLiveRoom.getRoomId(), goodsId);

            List<Integer> beforeDeleteRoomGoodsList = Stream.of(wechatLiveRoom.getGoods().split(",")).map(Integer::valueOf).collect(Collectors.toList());
            List<Integer> afterDeleteRoomGoodsList = beforeDeleteRoomGoodsList.stream().filter(i -> !i.equals(goodsId)).collect(Collectors.toList());
            wechatLiveRoom.setGoods(Joiner.on(",").join(afterDeleteRoomGoodsList));
            int deleteRows = wechatLiveRoomDao.updateById(wechatLiveRoom);
            syncLiveRoom();
            return deleteRows > 0;
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 删除直播间商品 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 删除直播间商品！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 获取主播副号
     *
     * @param roomId 直播间id
     * @return 主播副号
     */
    @Override
    public String getSubanchorByRoomId(Integer roomId) {
        try {
            String subanchor = wxMaService.getLiveService().getSubanchor(roomId);
            return subanchor;
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 获取主播副号 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 获取主播副号！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 新增主播副号
     *
     * @param roomId   直播间id
     * @param userName 主播副号
     * @return 添加结果
     */
    @Override
    public Boolean addSubanchorNameByRoomId(Integer roomId, String userName) {
        try {
            return wxMaService.getLiveService().addSubanchor(roomId, userName);
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 添加主播副号 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 添加主播副号！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 修改主播副号
     *
     * @param roomId   直播间id
     * @param userName 主播副号
     * @return 修改结果
     */
    @Override
    public Boolean modifySubanchorNameByRoomId(Integer roomId, String userName) {
        try {
            return wxMaService.getLiveService().modifySubanchor(roomId, userName);
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 修改主播副号 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 修改主播副号！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 删除主播副号
     *
     * @param roomId 直播间id
     * @return 删除结果
     */
    @Override
    public Boolean deleteSubanchorNameByRoomId(Integer roomId) {
        try {
            return wxMaService.getLiveService().deleteSubanchor(roomId);
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 删除主播副号 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 删除主播副号！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 推送商品
     *
     * @param roomId  直播间id
     * @param goodsId 商品id
     * @return 推送结果
     */
    @Override
    public Boolean goodsPush(Integer roomId, Integer goodsId) {
        try {
            return wxMaService.getLiveService().push(roomId, goodsId);
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 推送商品失败 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 推送商品失败！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 上下架商品
     *
     * @param roomId  直播间id
     * @param goodsId 商品id
     * @param onSale  上下架 【0：下架，1：上架】
     * @return 上下架结果
     */
    @Override
    public Boolean goodsOnSale(Integer roomId, Integer goodsId, Integer onSale) {
        getRoomInfoById(roomId);
        try {
            boolean onsaleResult = wxMaService.getLiveService().onsale(roomId, goodsId, onSale);
//            LambdaQueryWrapper<WechatLiveRoom> lambdaQueryWrapper = Wrappers.lambdaQuery();
//            lambdaQueryWrapper.eq(WechatLiveRoom::getRoomId, roomId);
//            // 只会有一条数据
//            List<WechatLiveRoom> wechatLiveRooms = wechatLiveRoomDao.selectList(lambdaQueryWrapper);
//            WechatLiveRoom wechatLiveRoom = null;
//            if(wechatLiveRooms.size() > 0){
//                wechatLiveRoom = wechatLiveRooms.get(0);
//                List<Integer> currentExistGoodsIds = Stream.of(wechatLiveRoom.getGoods().split(",")).map(Integer::valueOf).collect(Collectors.toList());
//                List<Integer> afterOnSaleGoodsIds = new ArrayList<>();
//                if(onSale.equals(0)){
//                    afterOnSaleGoodsIds = currentExistGoodsIds.stream().filter(item -> !item.equals(goodsId)).collect(Collectors.toList());
//                }else{
//                    afterOnSaleGoodsIds.add(onSale);
//                }
//                wechatLiveRoom.setGoods(Joiner.on(",").join(afterOnSaleGoodsIds));
//            }
//            if(ObjectUtil.isNull(wechatLiveRoom)) throw new CrmebException("直播商品上下架操作数据不正确");
//            wechatLiveRoomDao.updateById(wechatLiveRoom);
            syncLiveRoom();
            return onsaleResult;
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 推送商品失败 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 推送商品失败！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }


    /**
     * 商品排序
     *
     * @param roomId 直播间id
     * @param goods  排序商品
     * @return 排序结果
     */
    @Override
    public Boolean goodsSort(Integer roomId, List<Map<String, String>> goods) {
        getRoomInfoById(roomId);
        try {
            return wxMaService.getLiveService().sort(roomId, goods);
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 商品排序 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 商品排序！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * # 编辑直播间小助手
     * ## 一个接口针对前端传递的数据判断新增，编辑和删除
     * 1. 新增
     * 1. 单本地没有数据，并且参数传递进来了，那么参数就是要新增的小助手
     * 2. 一次性可以添加多个小助手到直播间
     * 2. 编辑
     * 1. 本地有数据，并且也传递参数进来了
     * 2. 去掉本地和参数同样的id，
     * 3. 本地没有的为新增数据
     * 4. 本地有参数没有的为删除数据
     * 5. 编辑小助手只能一条一条调用
     * 3. 删除
     * 1. 本地有数据，但参数是空，删除全部小助手。
     * 2. 本地有数据，参数少于当前已经存在的小助手id, 删除参数中不存在的id
     *
     * @param id     本地直播间id
     * @param assids 小助手id集合
     * @return 添加结果
     */
    @Override
    public Boolean mangerAssistant(Integer id, List<Integer> assids) {
        WechatLiveRoom wechatLiveRoom = getRoomInfoById(id);

        // 已经选在的小助手
        List<Integer> existAss = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(wechatLiveRoom.getAssistant())) {
            existAss = Stream.of(wechatLiveRoom.getAssistant().split(",")).map(Integer::valueOf).collect(Collectors.toList());
        }
        //为了对比参数方便将参数排序
        List<Integer> assidss = assids.stream().sorted().collect(Collectors.toList());
        List<Integer> existAsss = existAss.stream().sorted().collect(Collectors.toList());

        // 根据已有数据对比出新增和删除的小助手，在调用微信对应接口之后再更新本地小助手记录Ï
        List<Integer> forAddAss = new ArrayList<>();
        List<Integer> forDelAss = new ArrayList<>();
        if (assids.equals(existAss)) { // 本地和参数一样不做任何处理
            return true;
        }
        if (existAss.size() == 0) { // 初次新增小助手
            forAddAss.addAll(assids);
        } else if (existAss.size() > 0 && assids.size() == 0) { // 删除本地所有小助手
            forDelAss.addAll(existAss);
        } else {
            // 编辑小助手 分别产出新增和删除 原来有的在此计算是新增还是删除
            // 参数中没找到本地的 需要删除
            List<Integer> localNotExist = existAss.stream().filter(item -> !assidss.contains(item)).collect(Collectors.toList());
            forDelAss.addAll(localNotExist);
            // 参数中有本地没找到的 需要新增
            List<Integer> pramNotExist = assidss.stream().filter(item -> !existAsss.contains(item)).collect(Collectors.toList());
            forAddAss.addAll(pramNotExist);

        }

        try {
            List<WechatLiveAssistant> wechatLiveAssistants_forAdd = new ArrayList<>();
            if (forAddAss.size() > 0) {
                wechatLiveAssistants_forAdd = wechatLiveAssistantService.listByIds(forAddAss);
            }
            List<WechatLiveAssistant> wechatLiveAssistants_forDel = new ArrayList<>();
            if (forDelAss.size() > 0) {
                wechatLiveAssistants_forDel = wechatLiveAssistantService.listByIds(forDelAss);
            }
            List<WxMaLiveAssistantInfo> wxMaLiveAssistantInfosAdd = new ArrayList<>();
            List<WxMaLiveAssistantInfo> wxMaLiveAssistantInfosDel = new ArrayList<>();
            for (WechatLiveAssistant user : wechatLiveAssistants_forAdd) {
                WxMaLiveAssistantInfo wxMaLiveAssistantInfo = new WxMaLiveAssistantInfo();
                wxMaLiveAssistantInfo.setAlias(user.getAssDesc());
                wxMaLiveAssistantInfo.setNickname(user.getWechatNickname());
                wxMaLiveAssistantInfo.setUsername(user.getWechat());
                wxMaLiveAssistantInfosAdd.add(wxMaLiveAssistantInfo);
            }
            for (WechatLiveAssistant user : wechatLiveAssistants_forDel) {
                WxMaLiveAssistantInfo wxMaLiveAssistantInfo = new WxMaLiveAssistantInfo();
                wxMaLiveAssistantInfo.setAlias(user.getAssDesc());
                wxMaLiveAssistantInfo.setNickname(user.getWechatNickname());
                wxMaLiveAssistantInfo.setUsername(user.getWechat());
                wxMaLiveAssistantInfosDel.add(wxMaLiveAssistantInfo);
            }
            // 新增 小助手
            if (wxMaLiveAssistantInfosAdd.size() > 0) {
                boolean addOnRoomResult = wxMaService.getLiveService().addAssistant(wechatLiveRoom.getRoomId(), wxMaLiveAssistantInfosAdd);
                if (!addOnRoomResult) throw new CrmebException("新增小助手到直播间失败");
                wechatLiveRoom.setAssistant(Joiner.on(",").join(assids));
                return wechatLiveRoomDao.updateById(wechatLiveRoom) > 0;
            }
//            // 编辑 小助手
//            wxMaLiveAssistantInfosAdd.forEach(item -> {
//                try {
//                    wxMaService.getLiveService().modifyAssistant(wechatLiveRoom.getRoomId(), item.getUsername(), item.getNickname());
//                } catch (WxErrorException e) {
//                    logger.error("微信小程序直播 - 编辑助手出错:{}", e);
//                }
//            });

            // 删除 小助手
            wxMaLiveAssistantInfosDel.forEach(item -> {
                try {
                    wxMaService.getLiveService().removeAssistant(wechatLiveRoom.getRoomId(), item.getUsername());
                } catch (WxErrorException e) {
                    logger.error("微信小程序直播 - 删除助手出错:{}", e);
                }
            });
            wechatLiveRoom.setAssistant(Joiner.on(",").join(assids));
            return wechatLiveRoomDao.updateById(wechatLiveRoom) > 0;
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 新增直播间小助手 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 新增直播间小助手！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 修改直播间小助手
     *
     * @param id    直播间id
     * @param assid 小助手id
     * @return 修改结果
     */
    @Override
    public Boolean modifyAssistant(Integer id, Integer assid) {
        try {
            WechatLiveRoom wechatLiveRoom = wechatLiveRoomDao.selectById(id);
            if (ObjectUtil.isNull(wechatLiveRoom)) throw new CrmebException("当前直播间不存在");

            WechatLiveAssistant currentAss = wechatLiveAssistantService.getById(assid);
            boolean resultAssistant = wxMaService.getLiveService().modifyAssistant(wechatLiveRoom.getRoomId(), currentAss.getWechat(), currentAss.getWechatNickname());

            if (!resultAssistant)
                throw new CrmebException("修改直播间小助手 操作失败:" + JSON.toJSONString(wechatLiveRoom));

            List<Integer> assisList = new ArrayList<>();
            if (ObjectUtil.isNotNull(wechatLiveRoom.getAssistant()) && wechatLiveRoom.getAssistant().length() > 0) {
                assisList = Stream.of(wechatLiveRoom.getAssistant().split(",")).map(Integer::valueOf).collect(Collectors.toList());
                assisList.add(assid);
            }
            wechatLiveRoom.setAssistant(Joiner.on(",").join(assisList));
            return wechatLiveRoomDao.updateById(wechatLiveRoom) > 0;
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 修改直播间小助手 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 修改直播间小助手！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 删除直播间小助手
     *
     * @param id    直播间id
     * @param assid 用户微信号
     * @return 删除结果
     */
    @Override
    public Boolean removeAssistant(Integer id, Integer assid) {
        try {
            WechatLiveRoom wechatLiveRoom = wechatLiveRoomDao.selectById(id);
            WechatLiveAssistant assistant = wechatLiveAssistantService.getById(assid);
            boolean removeAssistant = wxMaService.getLiveService().removeAssistant(wechatLiveRoom.getRoomId(), assistant.getWechat());
            if (!removeAssistant) throw new CrmebException("删除直播间小助手 微信端异常");
            List<Integer> assisList = new ArrayList<>();
            if (ObjectUtil.isNotEmpty(wechatLiveRoom.getAssistant())) {
                assisList = Stream.of(wechatLiveRoom.getAssistant().split(",")).map(Integer::valueOf).collect(Collectors.toList());
                assisList.remove(assid);
            }
            wechatLiveRoom.setAssistant(Joiner.on(",").join(assisList));
            return wechatLiveAssistantService.removeById(assid);
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 删除直播间小助手 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 删除直播间小助手！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 获取直播间小助手
     *
     * @param id 本地直播间id
     * @return 获取结果
     */
    @Override
    public List<WechatLiveAssistant> getAssistantByRoom(Integer id) {

        // 调用本地直播间绑定的小助手
        WechatLiveRoom wechatLiveRoom = getRoomInfoById(id);
        List<WechatLiveAssistant> wechatLiveAssistantList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(wechatLiveRoom.getAssistant())) {
            List<Integer> assIds = Stream.of(wechatLiveRoom.getAssistant().split(",")).map(Integer::valueOf).collect(Collectors.toList());
            wechatLiveAssistantList = wechatLiveAssistantService.listByIds(assIds);
        }
        return wechatLiveAssistantList;
    }

    /**
     * 操作禁言功能
     *
     * @param id         本地直播间id
     * @param banComment 1-禁言，0-取消禁言
     * @return 禁言结果
     */
    @Override
    public Boolean updateComment(Integer id, Integer banComment) {
        try {
            WechatLiveRoom wechatLiveRoom = getRoomInfoById(id);
            boolean updateComment = wxMaService.getLiveService().updatecomment(wechatLiveRoom.getRoomId(), banComment);
            if (!updateComment) throw new CrmebException("禁言操作失败:" + JSON.toJSONString(wechatLiveRoom));
            wechatLiveRoom.setCloseComment(banComment);
            return wechatLiveRoomDao.updateById(wechatLiveRoom) > 0;
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 禁言功能 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 禁言功能！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 官方收录管理
     *
     * @param id            直播间id
     * @param isFeedsPublic 是否开启官方收录 【1: 开启，0：关闭】
     * @return 操作结果
     */
    @Override
    public Boolean updateFeedPublic(Integer id, Integer isFeedsPublic) {
        try {
            WechatLiveRoom wechatLiveRoom = getRoomInfoById(id);
            boolean updateFeedPublic = wxMaService.getLiveService().updatefeedpublic(wechatLiveRoom.getRoomId(), isFeedsPublic);
            if (!updateFeedPublic) throw new CrmebException("官方收录操作失败:" + JSON.toJSONString(wechatLiveRoom));

            wechatLiveRoom.setIsFeedsPublic(isFeedsPublic);
            return wechatLiveRoomDao.updateById(wechatLiveRoom) > 0;
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 官方收录管理 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 官方收录管理！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 客服功能管理
     *
     * @param id      直播间id
     * @param closeKf 是否关闭客服 【0：开启，1：关闭】
     * @return 结果
     */
    @Override
    public Boolean updateKF(Integer id, Integer closeKf) {
        try {
            WechatLiveRoom wechatLiveRoom = getRoomInfoById(id);
            if (wechatLiveRoom.getReviewStatus().equals(WechatMPLiveRoomReviewStatusEnum.WECHAT_REVIEW_SUCCESS.getCode())) {
                boolean updateKf = wxMaService.getLiveService().updatekf(wechatLiveRoom.getRoomId(), closeKf);
                if (!updateKf) throw new CrmebException("客户开关操作失败:" + JSON.toJSONString(wechatLiveRoom));
            }
            wechatLiveRoom.setCloseKf(closeKf);
            return wechatLiveRoomDao.updateById(wechatLiveRoom) > 0;
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 客服功能管理 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 客服功能管理！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 回放功能管理
     *
     * @param id          直播间id
     * @param closeReplay 是否关闭回放 【0：开启，1：关闭】
     * @return 结果
     */
    @Override
    public Boolean updateReplay(Integer id, Integer closeReplay) {
        try {
            WechatLiveRoom wechatLiveRoom = getRoomInfoById(id);
            boolean updateReplay = wxMaService.getLiveService().updatereplay(wechatLiveRoom.getRoomId(), closeReplay);
            if (!updateReplay) throw new CrmebException("回放功能操作失败:" + JSON.toJSONString(wechatLiveRoom));
            wechatLiveRoom.setLiveReplay(closeReplay + "");
            return wechatLiveRoomDao.updateById(wechatLiveRoom) > 0;
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 客服功能管理 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 客服功能管理！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 下载商品讲解视频
     *
     * @param id      直播间id
     * @param goodsId 商品id
     * @return 视频下载链接
     */
    @Override
    public String getVideo(Integer id, Integer goodsId) {
        try {
            WechatLiveRoom wechatLiveRoom = wechatLiveRoomDao.selectById(id);
            return wxMaService.getLiveService().getVideo(wechatLiveRoom.getRoomId(), goodsId);
        } catch (WxErrorException e) {
            logger.error("微信小程序直播 - 客服功能管理 {}", e.getError());
            throw new CrmebException("微信小程序直播 - 客服功能管理！" + CRMEBWxError.fromJson(e.getError().getJson(), WxType.Open));
        }
    }

    /**
     * 同步直播间信息
     *
     * @return 微信端直播间信息
     */
    @Override
    public List<WxMaLiveResult.RoomInfo> syncLiveRoom() {
        // 根据roomId 获取本地直播间列表
        LambdaQueryWrapper<WechatLiveRoom> lambdaQueryWrapper = Wrappers.lambdaQuery();
        List<WechatLiveRoom> wechatLiveRoomsList = new ArrayList<>();
        List<WxMaLiveResult.RoomInfo> liveInfos = new ArrayList<>();
        try {
            liveInfos = wxMaService.getLiveService().getLiveInfos();
            for (WxMaLiveResult.RoomInfo liveInfo : liveInfos) {
                // 装载直播间
                WechatLiveRoom wechatLiveRoom = new WechatLiveRoom();
                wechatLiveRoom.setRoomName(liveInfo.getName());
                wechatLiveRoom.setCoverImgWx(liveInfo.getCoverImg());
                wechatLiveRoom.setStartTime(new Date(Long.parseLong(liveInfo.getStartTime() + "000")));
                wechatLiveRoom.setEndTime(new Date(Long.parseLong(liveInfo.getEndTime() + "000")));
                wechatLiveRoom.setAnchorName(liveInfo.getAnchorName());
                wechatLiveRoom.setRoomId(liveInfo.getRoomId());
                wechatLiveRoom.setLiveStatus(liveInfo.getLiveStatus());
                wechatLiveRoom.setType(liveInfo.getType());
                wechatLiveRoom.setCloseLike(liveInfo.getCloseLike());
                wechatLiveRoom.setCloseGoods(liveInfo.getCloseGoods());
                wechatLiveRoom.setCloseComment(liveInfo.getCloseComment());
                wechatLiveRoom.setCloseKf(liveInfo.getCloseKf());
                wechatLiveRoom.setCloseReplay(liveInfo.getCloseReplay());
                wechatLiveRoom.setIsFeedsPublic(liveInfo.getIsFeedsPublic());
                wechatLiveRoom.setCreaterOpenid(liveInfo.getCreaterOpenid());
                wechatLiveRoom.setFeedsImgWx(liveInfo.getFeedsImg());
                wechatLiveRoom.setShareImgWx(liveInfo.getShareImg());
                if (liveInfo.getGoods().size() > 0) {
                    wechatLiveRoom.setGoodsJson(JSON.toJSONString(liveInfo.getGoods()));
                }


                // 同步回放
                if (wechatLiveRoom.getLiveStatus().equals(103)) { // 103=直播已结束
                    WxMaLiveResult liveReplay = wxMaService.getLiveService().getLiveReplay(wechatLiveRoom.getRoomId(), 1, 99);
                    if (liveReplay.getLiveReplay().size() > 0) {
                        wechatLiveRoom.setLiveReplay(JSON.toJSONString(liveReplay.getLiveReplay()));
                    }
                }

                wechatLiveRoomsList.add(wechatLiveRoom);
            }
            List<Integer> idsForgetLocalLiveRoom = wechatLiveRoomsList.stream().map(WechatLiveRoom::getRoomId).collect(Collectors.toList());
            lambdaQueryWrapper.in(WechatLiveRoom::getRoomId, idsForgetLocalLiveRoom);
            List<WechatLiveRoom> currentWechatLiveRoomsForUpdate = wechatLiveRoomDao.selectList(lambdaQueryWrapper);
            if (currentWechatLiveRoomsForUpdate.size() == 0) throw new CrmebException("直播间 - 本地待更新数据 为空");
            for (WechatLiveRoom wechatLiveRoom : wechatLiveRoomsList) {
                List<WechatLiveRoom> currentLiveRoom = currentWechatLiveRoomsForUpdate.stream().filter(liveRoom -> liveRoom.getRoomId().equals(wechatLiveRoom.getRoomId())).collect(Collectors.toList());
                if (currentLiveRoom.size() > 0) {
                    WechatLiveRoom cuRoom = currentLiveRoom.get(0);
                    // 设置当前直播商品的id
                    wechatLiveRoom.setId(cuRoom.getId());
                    // 设置直播商品原本导入的商品id
                    wechatLiveRoom.setGoods(cuRoom.getGoods());
                } else {
                    logger.error("直播间 - 本地未找到roomId为:{}的数据", wechatLiveRoom.getRoomId());
                }
            }
            boolean batchUpdateWechatRoomResult = updateBatchById(wechatLiveRoomsList);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return liveInfos;
    }

    /**
     * 在商城中显示
     *
     * @param id   直播间id
     * @param show 0=隐藏 1=显示
     * @return 操作结果
     */
    @Override
    public Boolean showStore(Integer id, Integer show) {
        WechatLiveRoom wechatLiveRoom = new WechatLiveRoom();
        wechatLiveRoom.setId(id).setStoreShow(show);
        return wechatLiveRoomDao.updateById(wechatLiveRoom) > 0;
    }
}
