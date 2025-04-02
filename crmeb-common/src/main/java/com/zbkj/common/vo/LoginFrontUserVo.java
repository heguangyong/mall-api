package com.zbkj.common.vo;

import com.zbkj.common.model.user.User;
import com.zbkj.common.response.employee.FrontMerchantEmployeeResponse;

import java.io.Serializable;
import java.util.List;

/**
 * 登录商户端用户身
 */
public class LoginFrontUserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 登陆时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 当前正在操作的商户
     */
    private Integer activeMerchant;

    /**
     * 权限列表
     */
    private List<FrontMerchantEmployeeResponse> merchantEmployeeList;

    /**
     * 用户信息
     */
    private User user;

    /**
     * 用户ID
     */
    private Integer userId;

    public LoginFrontUserVo() {
    }

    public LoginFrontUserVo(User user, List<FrontMerchantEmployeeResponse> merchantEmployeeList) {
        this.user = user;
        this.merchantEmployeeList = merchantEmployeeList;
    }

    public LoginFrontUserVo(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public String getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getActiveMerchant() {
        return activeMerchant;
    }

    public void setActiveMerchant(Integer activeMerchant) {
        this.activeMerchant = activeMerchant;
    }

    public List<FrontMerchantEmployeeResponse> getMerchantEmployeeList() {
        return merchantEmployeeList;
    }

    public void setMerchantEmployeeList(List<FrontMerchantEmployeeResponse> merchantEmployeeList) {
        this.merchantEmployeeList = merchantEmployeeList;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
