package com.clsaa.dop.server.api.module.response.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "API简要信息")
public class ApiSimpleInfo {
    @ApiModelProperty(value = "apiId",example = "123",dataType = "String")
    private String apiId;

    @ApiModelProperty(value = "apiPath",example = "/v2/test",dataType = "String")
    private String apiPath;

    @ApiModelProperty(value = "frequency",example = "1233",dataType = "int")
    private int frequency;

    @ApiModelProperty(value = "responseTime",example = "1300",dataType = "int")
    private int responseTime;

    @ApiModelProperty(value = "failTimes",example = "120",dataType = "int")
    private int failTimes;

    public ApiSimpleInfo() {
    }

    public ApiSimpleInfo(String apiId, String apiPath, int frequency, int responseTime, int failTimes) {
        this.apiId = apiId;
        this.apiPath = apiPath;
        this.frequency = frequency;
        this.responseTime = responseTime;
        this.failTimes = failTimes;
    }

    public String getApiId() {
        return apiId;
    }

    public String getApiPath() {
        return apiPath;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public int getFailTimes() {
        return failTimes;
    }
}