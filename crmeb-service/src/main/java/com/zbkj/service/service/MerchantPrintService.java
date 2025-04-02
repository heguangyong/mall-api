package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.MerchantPrint;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.PrintContentVo;

import java.util.List;

/**
* @author dazongzi
* @description MerchantPrintService 接口
* @date 2023-09-20
*/
public interface MerchantPrintService extends IService<MerchantPrint> {

    /**
     * 小票打印机列表
     * @param pageParamRequest 分页参数
     * @return 小票打印机配置列表
     */
    List<MerchantPrint> getList(PageParamRequest pageParamRequest);

    /**
     * 获取商户打印配置
     * @param merId 商户id
     * @return 开启的打印配置
     */
    List<MerchantPrint> getByMerIdAndStatusOn(Integer merId);

    /**
     * 新增小票打印机配置
     *
     * @param merchantPrint 小票打印机对象
     * @return 新增结果
     */
    Boolean savePrintConfig(MerchantPrint merchantPrint);

    /**
     * 编辑小票打印机配置
     */
    Boolean updatePrintConfig(MerchantPrint merchantPrint);

    /**
     * 更新打印机状态
     * @param id 更改id
     * @param status 具体状态值 0关闭，1开启
     * @return 结果
     */
    Boolean updateStatus(Integer id, Integer status);

    /**
     *  执行具体打印
     * @param merchantOrder 当前待打印的订单
     */
    void printReceipt(MerchantOrder merchantOrder);

    /**
     *  批量执行具体打印
     * @param merchantOrderList 当前待打印的订单
     */
    void batchPrintTicket(List<MerchantOrder> merchantOrderList);

    /**
     * 删除打印
     * @param id 打印ID
     */
    Boolean deleteById(Integer id);

    /**
     * 获取打印机配置详情
     * @param id 打印机ID
     */
    MerchantPrint getPrintInfo(Integer id);

    /**
     * 获取打印内容配置
     * @param id 小票ID
     */
    PrintContentVo getPrintContentConfig(Integer id);

    /**
     * 保存打印内容配置
     */
    Boolean savePrintContentConfig(Integer id, PrintContentVo voRequest);
}
