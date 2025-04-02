package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.system.SystemAttachment;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemAttachmentMoveRequest;

import java.util.List;

/**
 * SystemAttachmentService 接口
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface SystemAttachmentService extends IService<SystemAttachment> {

    /**
     * 附件分页
     * @param pid Integer pid
     * @param attType 格式png,jpeg,jpg,audio/mpeg,text/plain,video/mp4,gif
     * @param pageParamRequest PageParamRequest 分页参数
     * @return List<SystemAttachment>
     */
    List<SystemAttachment> getList(Integer pid, String attType, PageParamRequest pageParamRequest);

    /**
     * 给图片加前缀
     * @param path String 路径
     * @return String
     */
    String prefixImage(String path);

    /**
     * 给前端上传的文件加前缀
     * @param path 路径
     * @return 替换后的
     */
    String prefixUploadf(String path);

    /**
     * 给文件加前缀
     * @param path String 路径
     * @return String
     */
    String prefixFile(String path);

    /**
     * 清除 cdn url， 在保存数据的时候使用
     * @param path String 文件路径
     * @return String
     */
    String clearPrefix(String path);

    /**
     * 清除 cdn url， 在保存数据的时候使用
     * @param path String 文件路径
     * @param cdnUrl String cdnUrl
     * @return String
     */
    String clearPrefix(String path, String cdnUrl);

    /**
     * 更改图片目录
     * @param move 参数
     */
    Boolean updateAttrId(SystemAttachmentMoveRequest move);

    /**
     * 获取cdn url
     * @return String
     */
    String getCdnUrl();

    /**
     * 删除附件
     * @param idList 附件ID列表
     */
    Boolean deleteByIds(List<Integer> idList);
}
