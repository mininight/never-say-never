package never.say.never.test.web;

import lombok.Data;
import never.say.never.test.idgen.Snowflake;

import java.io.Serializable;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-31
 */
@Data
public class TestVO implements Serializable {
    private static final long serialVersionUID = -8830538433187475912L;
    private Long id = new Snowflake(1, 1).nextId();
}
