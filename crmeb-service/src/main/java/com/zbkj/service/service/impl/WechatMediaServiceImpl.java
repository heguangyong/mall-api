package com.zbkj.service.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.WeChatConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.RestTemplateUtil;
import com.zbkj.common.utils.UploadUtil;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.WechatMediaService;
import com.zbkj.service.service.WechatService;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * @Auther: 大粽子
 * @Date: 2023/3/9 18:29
 * @Description: 微信素材上传和获取路径
 */
@Service
public class WechatMediaServiceImpl implements WechatMediaService {
    private static final Logger logger = LoggerFactory.getLogger(WechatMediaService.class);

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private CrmebConfig crmebConfig;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private WechatService wechatService;

    /**
     * 上传素材到微信端
     * @param type type	是	媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
     * @param multipart media	是	form-data中媒体文件标识，有filename、filelength、content-type等信息
     * @return 微信素材上传结果
     * @throws WxErrorException 微信素材上传异常
     */
    @Override
    public WxMediaUploadResult uploadMedia(String type, MultipartFile multipart) {
        WxMediaUploadResult wxMediaUploadResult;
        // 创建转存文件
        String rootPath = crmebConfig.getImagePath().trim();
        // 模块
        String modelPath = "public/wechat/";
        // 变更文件名
        String newFileName = UploadUtil.fileName(FilenameUtils.getExtension(multipart.getOriginalFilename()).toLowerCase());
        // 创建目标文件的名称，规则：  子目录/年/月/日.后缀名
        String webPath = modelPath + CrmebDateUtil.nowDate("yyyy/MM/dd") + "/";
        // 文件分隔符转化为当前系统的格式
        String destPath = FilenameUtils.separatorsToSystem(rootPath + webPath) + newFileName;

        try {
            // 创建文件
            File file = UploadUtil.createFile(destPath);
            multipart.transferTo(file);

            wxMediaUploadResult = wxMaService.getMediaService().uploadMedia(type, file);
        }catch (Exception e){
            logger.error("上传微信素材出错:{}", e.getMessage());
            throw new CrmebException(StrUtil.format("上传微信素材出错:{}", e.getMessage()));
        }
        return wxMediaUploadResult;
    }

    /**
     * 上传素材到微信端 用本地图片换mediaId
     *
     * @param type      type	是	媒体文件类型，分别有图片（image）
     * @param imagePath 本地图片路径
     * @return 微信素材上传结果
     */
    @Override
    public WxMediaUploadResult uploadMediaByLocal(String type, String imagePath) {
        WxMediaUploadResult wxMediaUploadResult;
        // 替换路径找到本地素材
//        String localImage = crmebConfig.getImagePath().trim() + systemAttachmentService.clearPrefix(imagePath);
        // 创建转存文件
        String rootPath = crmebConfig.getImagePath().trim();
        // 模块
        String modelPath = "public/wechat/";
        // 变更文件名
        String newFileName = UploadUtil.fileName(FilenameUtils.getExtension(imagePath).toLowerCase());
        // 创建目标文件的名称，规则：  子目录/年/月/日.后缀名
        String webPath = modelPath + CrmebDateUtil.nowDate("yyyy/MM/dd") + "/";
        // 文件分隔符转化为当前系统的格式
        String destPath = FilenameUtils.separatorsToSystem(rootPath + webPath) + newFileName ;
        // 创建文件
        File file = new File(destPath);
        HttpUtil.downloadFile(imagePath, file);
        try {
            wxMediaUploadResult = wxMaService.getMediaService().uploadMedia(type, file);
            return wxMediaUploadResult;
        }catch (Exception e){
            logger.error("上传微信素材出错:{}", e.getMessage());
            throw new CrmebException(StrUtil.format("上传微信素材出错:{}", e.getMessage()));
        }
    }

    /**
     * 根据素材id 获取已经上传的微信端素材
     * @param mediaId 媒体id
     * @return 当前id对应的文件资源
     * @throws WxErrorException 获取资源时的异常
     */
    @Override
    public File getFileByMediaId(String mediaId) throws WxErrorException {
        return wxMaService.getMediaService().getMedia(mediaId);
    }

    /**
     * 新增微信永久图片
     *
     * @param multipart 图片对象
     * @return 微信侧素材id
     */
    @Override
    public String preserveUploadImgNotMediaId(MultipartFile multipart) {

        // 创建转存文件
        String rootPath = crmebConfig.getImagePath().trim();
        // 模块
        String modelPath = "public/wechat/";
        // 变更文件名
        String newFileName = UploadUtil.fileName(FilenameUtils.getExtension(multipart.getOriginalFilename()).toLowerCase());
        // 创建目标文件的名称，规则：  子目录/年/月/日.后缀名
        String webPath = modelPath + CrmebDateUtil.nowDate("yyyy/MM/dd") + "/";
        // 文件分隔符转化为当前系统的格式
        String destPath = FilenameUtils.separatorsToSystem(rootPath + webPath) + newFileName;
        // 创建文件
        File file = null;
        try {
            file = UploadUtil.createFile(destPath);
            multipart.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 获取accessToken
        String miniAccessToken = wechatService.getMiniAccessToken();
        // 请求微信接口
        String url = StrUtil.format(WeChatConstants.WECHAT_MEDIA_UPLOADIMG, miniAccessToken);

        FileSystemResource fileSystemResource = new FileSystemResource(destPath);

        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("media", fileSystemResource);
        String uploadResult = restTemplateUtil.postFormData(url, params);
        JSONObject jsonObject = JSONObject.parseObject(uploadResult);
//        WxUtil.checkResult(jsonObject);
        return jsonObject.getString("url");
    }

    /**
     * 新增微信永久图片 没有mediaID的结果
     *
     * @param imgUrl 图片对象
     * @return mediaID
     */
    @Override
    public JSONObject preserveUploadImgHasMediaId(String imgUrl) {
        Assert.notNull(imgUrl, "图片地址不能为空");

//将文件下载后保存在E盘，返回结果为下载文件大小

        // 创建转存文件
        String rootPath = crmebConfig.getImagePath().trim();
        // 模块
        String modelPath = "public/wechat/";
        // 变更文件名
        String newFileName = UploadUtil.fileName(FilenameUtils.getExtension(imgUrl).toLowerCase());
        // 创建目标文件的名称，规则：  子目录/年/月/日.后缀名
        String webPath = modelPath + CrmebDateUtil.nowDate("yyyy/MM/dd") + "/";
        // 文件分隔符转化为当前系统的格式
        String destPath = FilenameUtils.separatorsToSystem(rootPath + webPath) + newFileName ;
        // 创建文件
        File file = new File(destPath);
        HttpUtil.downloadFile(imgUrl, file);

        // 获取accessToken
        String miniAccessToken = wechatService.getPublicAccessToken();
        // 请求微信接口
        String url = StrUtil.format(WeChatConstants.WECHAT_MEDIA_UPLOADIMG_HASMEDIAID, miniAccessToken);

        FileSystemResource fileSystemResource = new FileSystemResource(destPath);

        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("media", fileSystemResource);
        params.add("type", "image");
        String uploadResult = restTemplateUtil.postFormData(url, params);
        return JSONObject.parseObject(uploadResult);
    }

    /**
     * 获取微信永久图片
     *
     * @param mediaId 微信侧素材id
     * @return 微信侧图片地址
     */
    @Override
    public File preserveImgGet(String mediaId) {
        // 获取accessToken
        String miniAccessToken = wechatService.getPublicAccessToken();
        // 请求微信接口
        String url = StrUtil.format(WeChatConstants.WECHAT_MEDIA_GET, miniAccessToken);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("media_id", mediaId);
        String uploadResult = restTemplateUtil.postJsonData(url, jsonObject);
        File file = new File("Tempalte","xx");
        try {
            FileUtils.writeByteArrayToFile(file, uploadResult.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            logger.error("获取永久素材出错:{}", e.getMessage());
            throw new CrmebException("获取永久素材出错:"+e.getMessage());
        }
        return new File(uploadResult);
    }
}
