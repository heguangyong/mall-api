package com.zbkj.common.exception.weixin;

/**
 * edited by 大粽子 form weixin tools for CRMEB Java mer
 */
public class CRMEBWxErrorException extends Exception {
  private static final long serialVersionUID = -6357149550353160810L;

  private final CRMEBWxError error;

  private static final int DEFAULT_ERROR_CODE = -99;

  public CRMEBWxErrorException(String message) {
    this(CRMEBWxError.builder().errcode(DEFAULT_ERROR_CODE).errmsg(message).build());
  }

  public CRMEBWxErrorException(CRMEBWxError error) {
    super(error.toString());
    this.error = error;
  }

  public CRMEBWxErrorException(CRMEBWxError error, Throwable cause) {
    super(error.toString(), cause);
    this.error = error;
  }

  public CRMEBWxErrorException(Throwable cause) {
    super(cause.getMessage(), cause);
    this.error = CRMEBWxError.builder().errcode(DEFAULT_ERROR_CODE).errmsg(cause.getMessage()).build();
  }

  public CRMEBWxError getError() {
    return this.error;
  }
}
