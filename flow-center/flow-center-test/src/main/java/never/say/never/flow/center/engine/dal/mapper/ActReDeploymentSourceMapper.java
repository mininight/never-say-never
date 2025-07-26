package never.say.never.flow.center.engine.dal.mapper;

import never.say.never.flow.center.engine.dal.entity.ActReDeploymentSource;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Ivan
 */
@Mapper
public interface ActReDeploymentSourceMapper {

    int deleteByPrimaryKey(String id);

    int insert(ActReDeploymentSource record);

    ActReDeploymentSource selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ActReDeploymentSource record);

}