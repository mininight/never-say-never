package never.say.never.testtest;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-30
 */
public class TestAny {
    private Double value;

    public Double getValue() {
        return value;
    }

    public static void main(String[] args) {
//        TestAny testAny = new TestAny();
//        Double t = testAny == null ? 0d : testAny.getValue();
//        System.out.println(t);
        String ulid = UlidCreator.getMonotonicUlid().toString();
        System.out.println(ulid);
        System.out.println(Ulid.getInstant(ulid));
    }
}
