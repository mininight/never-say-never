package never.say.never.test.idgen;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class Snowflake {

    private final long twepoch;
    private final long workerIdBits = 10L;
    private final long maxWorkerId = ~(-1L << workerIdBits);
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits;
    private final long sequenceMask = ~(-1L << sequenceBits);
    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private static final Random RANDOM = new Random();

    /**
     * @param workerId
     * @param twepoch  起始的时间戳
     */
    public Snowflake(long workerId, long twepoch) {
        Preconditions.checkArgument(workerId < maxWorkerId, "worker id overflow");
        Preconditions.checkArgument(timeGen() > twepoch, "Snowflake not support twepoch gt currentTime");
        this.twepoch = twepoch;
        this.workerId = workerId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = timeGen();
                    if (timestamp < lastTimestamp) {
                        throw new RuntimeException(String.format("服务器出现时钟回拨问题，请检查。当前时间戳：%d，上一次使用的时间戳：%d",
                                timestamp, lastTimestamp));
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException("wait interrupted", e);
                }
            } else {
                throw new RuntimeException(String.format("服务器出现时钟回拨问题，请检查。当前时间戳：%d，上一次使用的时间戳：%d",
                        timestamp, lastTimestamp));
            }
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                //seq 为0的时候表示是下一毫秒时间开始对seq做随机
                sequence = RANDOM.nextInt(100);
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //如果是新的ms开始
            sequence = RANDOM.nextInt(100);
        }
        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) | (workerId << workerIdShift) | sequence;

    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public long getWorkerId() {
        return workerId;
    }

    public static void main(String[] args) {
        long id = new Snowflake(1, 1678669200000L).nextId();
        System.out.println((id + "").length());
    }
}


