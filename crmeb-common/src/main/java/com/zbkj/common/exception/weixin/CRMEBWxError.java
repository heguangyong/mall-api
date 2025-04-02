package com.zbkj.common.exception.weixin;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.common.enums.WxType;
import me.chanjar.weixin.common.util.json.WxGsonBuilder;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.json.GsonBuilderUtils;

import java.io.Serializable;

/**
 * 微信错误码.
 * 请阅读：
 * 公众平台：<a href="https://developers.weixin.qq.com/doc/offiaccount/Getting_Started/Global_Return_Code.html">全局返回码说明</a>
 * 企业微信：<a href="https://work.weixin.qq.com/api/doc#10649">全局错误码</a>
 *
 * edited by 大粽子 form weixin tools for CRMEB Java mer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CRMEBWxError implements Serializable {
  private static final long serialVersionUID = 7869786563361406291L;

  /**
   * 微信错误代码.
   */
  private int errcode;

  /**
   * 微信错误信息.
   * （如果可以翻译为中文，就为中文）
   */
  private String errmsg;

  /**
   * 微信接口返回的错误原始信息（英文）.
   */
  private String errmsgen;

  private String json;

  public CRMEBWxError(int errorCode, String errorMsg) {
    this.errcode = errorCode;
    this.errmsg = errorMsg;
  }

  public static CRMEBWxError fromJson(String json) {
    return fromJson(json, null);
  }

  public static CRMEBWxError fromJson(String json, WxType type) {
    final CRMEBWxError wxError = WxGsonBuilder.create().fromJson(json, CRMEBWxError.class);
    if (wxError.getErrcode() == 0 || type == null) {
      return wxError;
    }

    if (StringUtils.isNotEmpty(wxError.getErrmsg())) {
      wxError.setErrmsgen(wxError.getErrmsg());
    }

    switch (type) {
      case MP: {
        final String msg = CRMEBWxMpErrorMsgEnum.findMsgByCode(wxError.getErrcode());
        if (msg != null) {
          wxError.setErrmsg(msg);
        }
        break;
      }
      case CP: {
        final String msg = CRMEBWxCpErrorMsgEnum.findMsgByCode(wxError.getErrcode());
        if (msg != null) {
          wxError.setErrmsg(msg);
        }
        break;
      }
      case MiniApp: {
        final String msg = CRMEBWxMaErrorMsgEnum.findMsgByCode(wxError.getErrcode());
        if (msg != null) {
          wxError.setErrmsg(msg);
        }
        break;
      }
      case Open: {
        final String msg = CRMEBWxOpenErrorMsgEnum.findMsgByCode(wxError.getErrcode());
        if (msg != null) {
          wxError.setErrmsg(msg);
        }
        break;
      }
      default:
        return wxError;
    }

    return wxError;
  }

  @Override
  public String toString() {
    if (this.json == null) {
      return "错误代码：" + this.errcode + ", 错误信息：" + this.errmsg;
    }

    return "错误代码：" + this.errcode + ", 错误信息：" + this.errmsg + "，微信原始报文：" + this.json;
  }

}
