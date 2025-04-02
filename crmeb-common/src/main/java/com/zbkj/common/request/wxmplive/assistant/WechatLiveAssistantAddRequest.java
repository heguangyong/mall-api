package com.zbkj.common.request.wxmplive.assistant;

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
 * 小助手新增和编辑对象
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
public class WechatLiveAssistantAddRequest implements Serializable {

    private static final long serialVersionUID=1L;


    private Integer id;

    @ApiModelProperty(value = "微信号", required = true)
    private String wechat;

    @ApiModelProperty(value = "用户微信昵称", required = true)
    private String wechatNickname;

    @ApiModelProperty(value = "备注")
    private String assDesc;


}
