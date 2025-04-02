package com.zbkj.common.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.dto.IpLocation;
import com.zbkj.common.vo.MyRecord;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.lionsoul.ip2region.xdb.Searcher;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IP工具类
 * @Author 指缝de阳光
 * @Date 2022/8/30 16:21
 * @Version 1.0
 */
public class IPUtil {

    /**
     * 字符常量0
     */
    private static final String ZERO = "0";
    /**
     * 本级ip
     */
    private static final String LOCALHOST = "127.0.0.1";

    public static MyRecord getAddressByIp(String ip) {
        Map map = new HashMap();
        map.put("ip", ip);
        map.put("accessKey", "alibaba-inc");
        String result = post("http://ip.taobao.com/outGetIpInfo", map);
        Map valueMap = JSONObject.parseObject(result, Map.class);
        MyRecord myRecord = new MyRecord();
        // 请求成功，解析响应数据
        if ("query success".equals(valueMap.get("msg"))) {
            Map<String, String> dataMap = (Map<String, String>) valueMap.get("data");
            String country = dataMap.get("country");
            String region = dataMap.get("region");
            String city = dataMap.get("city");
            myRecord.set("country", country);
            myRecord.set("region", region);
            myRecord.set("city", city);
        }
        return myRecord;
    }

    public static String post(String url, Map<String, String> mapParameter) {
        // 创建httpClient的默认实例
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建POST请求
            HttpPost httpPost = new HttpPost(url);
            // 设置参数
            List<NameValuePair> nameValuePairList = new ArrayList<>();
            // 迭代参数
            if (mapParameter != null && mapParameter.size() > 0) {
                mapParameter.forEach((k, v) -> nameValuePairList.add(new BasicNameValuePair(k, v)));
            }
            // 编码
            if (nameValuePairList.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairList);
                httpPost.setEntity(entity);
            }
            // 执行post请求
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                if (response != null) {
                    org.apache.http.HttpEntity httpEntity = response.getEntity();
                    // 如果返回的内容不为空
                    if (httpEntity != null) {
                        return EntityUtils.toString(httpEntity);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取客户端的IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (LOCALHOST.equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对于通过多个代理转发的情况，取第一个非unknown的IP地址。
        // 这里假设第一个IP为真实IP，后面的为代理IP。
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * 根据iP获取归属地信息
     */
    public static IpLocation getLocation(String ip) {
        IpLocation location = new IpLocation();
        location.setIp(ip);
        try (InputStream inputStream = IPUtil.class.getResourceAsStream("/ip2region/ip2region.xdb");) {
            byte[] bytes = IoUtil.readBytes(inputStream);
            Searcher searcher = Searcher.newWithBuffer(bytes);
            String region = searcher.search(ip);
            if (StrUtil.isNotBlank(region)) {
                // xdb返回格式 国家|区域|省份|城市|ISP，
                // 只有中国的数据绝大部分精确到了城市，其他国家部分数据只能定位到国家，后前的选项全部是0
                String[] result = region.split("\\|");
                location.setCountry(ZERO.equals(result[0]) ? StrUtil.EMPTY : result[0]);
                location.setProvince(ZERO.equals(result[2]) ? StrUtil.EMPTY : result[2]);
                location.setCity(ZERO.equals(result[3]) ? StrUtil.EMPTY : result[3]);
                location.setIsp(ZERO.equals(result[4]) ? StrUtil.EMPTY : result[4]);
            }
            searcher.close();
        } catch (Exception e) {
            e.printStackTrace();
            return location;
        }
        return location;
    }
}
