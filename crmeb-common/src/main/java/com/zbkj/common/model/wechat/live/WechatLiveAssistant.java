package com.zbkj.common.model.wechat.live;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author dazongzi
 * @since 2023-03-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_wechat_live_assistant")
@ApiModel(value="WechatLiveAssistant对象", description="")
public class WechatLiveAssistant implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "微信号")
    private String wechat;

    @ApiModelProperty(value = "用户微信昵称")
    private String wechatNickname;

    @ApiModelProperty(value = "助手微信头像 微信返回")
    private String headimg;

    @ApiModelProperty(value = "openid 微信返回")
    private String openid;

    @ApiModelProperty(value = "备注")
    private String assDesc;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
