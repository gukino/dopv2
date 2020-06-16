package com.clsaa.dop.server.link.model.po;

import com.clsaa.dop.server.link.enums.MonitorState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bind {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bid;

    private String title; // 给这个监控起个名字，做个简单说明

    private long projectId;

    private String projectTitle;

    private long cuser;// 创建者的id

    private String cuserName;//创建者的name

    private String notifiedUid; //被通知者的id,可以有多个人，用逗号分隔

    private String notifiedName; //被通知者的name，同notifiedUid

    private String notifiedEmail; //被通知者的邮箱，同上

    private String service;//这个监控要关注的微服务，不选默认all, 微服务之间用逗号隔开

    private double threshold;// 阈值 单位时间内调用链n次调用>=m次出错通知

    @Enumerated(EnumType.STRING)
    private MonitorState state; //

    private Date ctime;

    private boolean isDelete;
}
