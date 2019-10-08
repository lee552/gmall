package com.atguigu.gmall.wms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 库存工作单
 * 
 * @author lee552
 * @email lee552@yeah.net
 * @date 2019-09-22 16:08:12
 */
@ApiModel
@Data
@TableName("wms_ware_order_task_detail")
public class WareOrderTaskDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	@ApiModelProperty(name = "id",value = "id")
	private Long id;
	/**
	 * sku_id
	 */
	@ApiModelProperty(name = "skuId",value = "sku_id")
	private Long skuId;
	/**
	 * sku_name
	 */
	@ApiModelProperty(name = "skuName",value = "sku_name")
	private String skuName;
	/**
	 * 购买个数
	 */
	@ApiModelProperty(name = "skuNum",value = "购买个数")
	private Integer skuNum;
	/**
	 * 工作单id
	 */
	@ApiModelProperty(name = "taskId",value = "工作单id")
	private Long taskId;

}
