package org.example.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel(description = "通用返回结果")
@Data
public class CommonResponse<T> {
    @ApiModelProperty(value = "状态码", example = "200")
    private int code;

    @ApiModelProperty(value = "返回消息", example = "成功")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private T data;
}
