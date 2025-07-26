package never.say.never.flow.center.engine.dal.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author Ivan
 */
@Data
public class ActReDeploymentSource {

    private String id;

    private String name;

    private String description;

    private Short category;

    private Short type;

    private Short status;

    private String tenantId;

    private Date deployTime;

    private Date createTime;

    private Date modifyTime;
}