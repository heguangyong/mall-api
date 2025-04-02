package com.zbkj.common.model.wechat;

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
 * 微信URLLink记录表
 * </p>
 *
 * @author HZW
 * @since 2024-01-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_wechat_url_link_record")
@ApiModel(value="WechatUrlLinkRecord对象", description="微信URLLink记录表")
public class WechatUrlLinkRecord implements Serializable {

    private static final long serialVersionUID = -3696625810513086709L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "链接名称")
    private String name;

    @ApiModelProperty(value = "原始小程序页面带参路径")
    private String originalPath;

    @ApiModelProperty(value = "小程序页面路径")
    private String path;

    @ApiModelProperty(value = "进入小程序时的query")
    private String query;

    @ApiModelProperty(value = "失效间隔天数1-30")
    private Integer expireInterval;

    @ApiModelProperty(value = "生成的小程序 URL Link")
    private String urlLink;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "创建人ID")
    private Integer createId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
