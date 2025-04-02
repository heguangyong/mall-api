package com.zbkj.common.dto;

/**
 * @ClassName IpLocation
 * @Description IP位置对象
 * @Author HZW
 * @Date 2023/6/29 11:01
 * @Version 1.0
 */
public class IpLocation {

//    @ApiModelProperty("ip地址")
    private String ip;

//    @ApiModelProperty("国家")
    private String country;

//    @ApiModelProperty("省")
    private String province;

//    @ApiModelProperty("省")
    private String city;

//    @ApiModelProperty("服务商")
    private String isp;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    @Override
    public String toString() {
        return "IpLocation{" +
                "ip='" + ip + '\'' +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", isp='" + isp + '\'' +
                '}';
    }
}
