package me.qiujun.arbitrage.config.snowflake;

public class Snowflake {
    /**
     * 定义起始时间 2022-01-01 00:00:00
     */
    private final long startTIme = 1640995200000L;

    /**
     * 机器id所占的位数
     */
    private final long workerIdBits = 10L;

    /**
     * 序列在id中占的位数
     */
    private final long sequenceBits = 12L;

    /**
     * 机器ID向左移12位
     */
    private final long workerIdShift = sequenceBits;


    /**
     * 时间截向左移22位(10+12)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = ~(-1L << sequenceBits);


    /**
     * 工作机器ID(0~1024)
     */
    private long workerId = 0L;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    public synchronized long genId() {
        long now = System.currentTimeMillis();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (now < lastTimestamp) {
            throw new RuntimeException(String.format("系统时间错误，%d 毫秒内拒绝生成新 ID", lastTimestamp - now));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (now == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                now = nextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        // 上次生成ID的时间截
        lastTimestamp = now;

        // 移位并通过或运算拼到一起组成64位的ID
        return ((now - startTIme) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    public long nextMillis(long lastTimestamp) {
        long now = System.currentTimeMillis();
        while (now <= lastTimestamp) {
            now = System.currentTimeMillis();
        }
        return now;
    }

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

}