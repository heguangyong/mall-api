package com.zbkj.common.vo.wxvedioshop.audit;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 上传品牌信息 参数 Request itemData
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
@Data
public class ShopAuditBrandRequestItemDataVo {
    /**
     *  认证审核类型 RegisterType 枚举值	描述
     * 1	国内品牌申请-R标
     * 2	国内品牌申请-TM标
     * 3	海外品牌申请-R标
     * 4	海外品牌申请-TM标
     */
    @Length(max = 20, min = 1,message = "认证审核类型 brandAuditType，字符类型，最长不超过20")
    @TableField(value = "brand_audit_type")
    @ApiModelProperty(value = "认证审核类型 brandAuditType，字符类型，最长不超过20")
    private Integer brandAuditType;

    /** 商标分类:商标共有45个分类，请按商标实际分类上传 例：“1” */
    @NotBlank(message = "商标实际分类不能为空")
    @TableField(value = "trademark_type")
    @Length(max = 20, min = 1, message = "商标分类 TrademarkType，字符类型，最长不超过10")
    @ApiModelProperty(value = "商标分类 TrademarkType，字符类型，最长不超过10")
    private String trademarkType;

    /**
     * 品牌经营类型
     * 枚举值	描述
     * 1	自有品牌
     * 2	代理品牌
     * 3	无品牌
     */
    @NotNull(message = "品牌经营类型不能为空")
    @TableField(value = "brand_management_type")
    @Length(max = 10, min = 1, message = "选择品牌经营类型 BrandManagementType，字符类型，最长不超过10")
    @ApiModelProperty(value = "选择品牌经营类型 BrandManagementType，字符类型，最长不超过10")
    private Integer brandManagementType;

    /**
     * 商品产地是否进口
     * 枚举值	描述
     * 1	是
     * 2	否
     */
    @NotNull(message = "商品产地是否进口不能为空")
    @TableField(value = "commodity_origin_type")
    @ApiModelProperty(value = "商品产地是否进口 CommodityOriginType")
    private Integer commodityOriginType;

    /** 商标/品牌词 */
    @NotBlank(message = "商标/品牌词不能为空")
    @Length(max = 20, min = 1, message = "长度不得超过20")
    @TableField(value = "brand_wording")
    @ApiModelProperty(value = "商标/品牌词不能为空，字符类型，最长不超过20")
    private String brandWording;

    /** 销售授权书（如商持人为自然人，还需提供有其签名的身份证正反面扫描件)，图片url/media_id */
    @TableField(value = "sale_authorization")
    @Size(min=1, max = 10, message = "销售授权书（如商持人为自然人，还需提供有其签名的身份证正反面扫描件)，图片url/media_id，图片类型，最多不超过10张")
    @ApiModelProperty(value = "销售授权书（如商持人为自然人，还需提供有其签名的身份证正反面扫描件)，图片url/media_id，图片类型，最多不超过10张")
    private List<String> saleAuthorization;

    /** 商标注册证书，图片url/media_id */
    @TableField(value = "trademark_registration_certificate")
    @ApiModelProperty(value = "商标注册证书，图片url/media_id，图片类型，最多不超过1张")
    @Size(max = 1, message = "商标注册证书，图片url/media_id，图片类型，最多不超过1张")
    private List<String> trademarkRegistrationCertificate;

    /** 商标变更证明，图片url/media_id */
    @TableField(value = "trademark_change_certificate")
    @ApiModelProperty("商标变更证明，图片url/media_id，图片类型，最多不超过5张")
    @Size(max = 5, message = "商标变更证明，图片url/media_id，图片类型，最多不超过5张")
    private List<String> trademarkChangeCertificate;

    /** 商标注册人姓名 */
    @TableField(value = "trademark_registrant")
    @ApiModelProperty("商标注册人姓名，字符类型，最长不超过100")
    @Length(max = 100, message = "商标注册人姓名，字符类型，最长不超过100")
    private String trademarkRegistrant;

    /** 商标注册号/申请号 */
    @TableField(value = "trademark_registrant_nu")
    @ApiModelProperty("商标注册号/申请号，最长不超过10")
    @Length(max = 10, message = "商标注册号/申请号，最长不超过10")
    private String trademarkRegistrantNu;

    /** 商标有效期，yyyy-MM-dd HH:mm:ss */
    @TableField(value = "trademark_authorization_period")
    @ApiModelProperty(value = "商标有效期，yyyy-MM-dd HH:mm:ss，字符类型，最长不超过30")
    @Length(max = 30, message = "商标有效期，yyyy-MM-dd HH:mm:ss，字符类型，最长不超过30")
    private String trademarkAuthorizationPeriod;

    /** 商标注册申请受理通知书，图片url/media_id */
    @TableField(value = "trademark_registration_application")
    @ApiModelProperty("商标注册申请受理通知书，图片url/media_id，图片类型，最多不超过1张")
    @Length(max = 1, message = "商标注册申请受理通知书，图片url/media_id，图片类型，最多不超过1张")
    private List<String> trademarkRegistrationApplication;

    /** 商标申请人姓名 */
    @TableField(value = "trademark_applicant")
    @ApiModelProperty("商标申请人姓名，字符类型，最长不超过100")
    @Length(max = 100, message = "商标申请人姓名，字符类型，最长不超过100")
    private String trademarkApplicant;

    /** 商标申请时间, yyyy-MM-dd HH:mm:ss */
    @TableField(value = "trademark_application_time")
    @ApiModelProperty(value = "商标申请时间，yyyy-MM-dd HH:mm:ss，字符类型，最长不超过30")
    @Length(max = 30, message = "商标申请时间，yyyy-MM-dd HH:mm:ss，字符类型，最长不超过30")
    private String trademarkApplicationTime;

    /** 中华人民共和国海关进口货物报关单，图片url/media_id */
    @TableField(value = "imported_goods_form")
    @ApiModelProperty(value = "中华人民共和国海关进口货物报关单，图片url/media_id，图片类型，最多不超过5张")
    @Size(max = 5,message = "中华人民共和国海关进口货物报关单，图片url/media_id，图片类型，最多不超过5张")
    private List<String> importedGoodsForm;
}
