package com.zbkj.common.constants;

/**
 * 社区常量类
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
public class CommunityConstants {

    /**
     * ---------------------------------------
     * --------社区配置------------------
     * ---------------------------------------
     */

    /** 社区图文笔记审核开关 */
    public static final String COMMUNITY_IMAGE_TEXT_AUDIT_SWITCH = "communityImageTextAuditSwitch";
    /** 社区短视频审核开关 */
    public static final String COMMUNITY_SHORT_VIDEO_AUDIT_SWITCH = "communityShortVideoAuditSwitch";
    /** 社区评论审核开关 */
    public static final String COMMUNITY_REPLY_AUDIT_SWITCH = "communityReplyAuditSwitch";
    /** 社区评论开关 */
    public static final String COMMUNITY_REPLY_SWITCH = "communityReplySwitch";

    /** 社区话题推荐数量 */
    public static final Integer COMMUNITY_TOPIC_RECOMMEND_NUM = 5;

    /**
     * ---------------------------------------
     * --------社区笔记常量------------------
     * ---------------------------------------
     */

    /** 社区笔记类型-图文 */
    public static final Integer COMMUNITY_NOTE_TYPE_IMAGE_CONTENT = 1;
    /** 社区笔记类型-视频 */
    public static final Integer COMMUNITY_NOTE_TYPE_SHORT_VIDEO = 2;

    /** 社区笔记关系-点赞 */
    public static final String COMMUNITY_NOTE_RELATION_LIKE = "like";
    /** 社区笔记关系-收藏 */
    public static final String COMMUNITY_NOTE_RELATION_COLLECT = "collect";

    /** 社区笔记审核状态-待审核 */
    public static final Integer COMMUNITY_NOTE_AUDIT_AWAIT = 0;
    /** 社区笔记审核状态-审核通过 */
    public static final Integer COMMUNITY_NOTE_AUDIT_SUCCESS = 1;
    /** 社区笔记审核状态-审核失败 */
    public static final Integer COMMUNITY_NOTE_AUDIT_ERROR = 2;
    /** 社区笔记审核状态-平台关闭 */
    public static final Integer COMMUNITY_NOTE_AUDIT_CLOSE = 3;

    /** 社区笔记评论开关状态-开启 */
    public static final Integer COMMUNITY_NOTE_IS_REPLY_OPEN = 1;
    /** 社区笔记评论开关状态-关闭 */
    public static final Integer COMMUNITY_NOTE_IS_REPLY_CLOSE = 2;
    /** 社区笔记评论开关状态-平台关闭 */
    public static final Integer COMMUNITY_NOTE_IS_REPLY_FORCE_OFF = 3;


    /**
     * ---------------------------------------
     * --------社区评论常量------------------
     * ---------------------------------------
     */
    /** 社区评论审核状态-待审核 */
    public static final Integer COMMUNITY_REPLY_AUDIT_AWAIT = 0;
    /** 社区评论审核状态-审核通过 */
    public static final Integer COMMUNITY_REPLY_AUDIT_SUCCESS = 1;
    /** 社区评论审核状态-审核失败 */
    public static final Integer COMMUNITY_REPLY_AUDIT_ERROR = 2;

    /** 社区评论类型-评论 */
    public static final Integer COMMUNITY_REPLY_TYPE_COMMENT = 1;
    /** 社区评论类型-回复 */
    public static final Integer COMMUNITY_REPLY_TYPE_REPLY = 2;

}
