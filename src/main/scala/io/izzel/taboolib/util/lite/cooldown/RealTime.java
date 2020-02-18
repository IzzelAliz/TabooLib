package io.izzel.taboolib.util.lite.cooldown;

import java.util.Calendar;
import java.util.function.Function;

/**
 * @Author sky
 * @Since 2020-02-18 14:13
 */
public enum RealTime {

    /**
     * 周日开始，周六结束
     */
    START_IN_SUNDAY(r -> {
        Calendar time = Calendar.getInstance();
        switch (r.unit) {
            case HOUR: {
                // 重置日期
                time.set(Calendar.MILLISECOND, 0);
                time.set(Calendar.SECOND, 0);
                time.set(Calendar.MINUTE, 0);
                // 推进日期
                time.add(Calendar.HOUR, r.value);
                return time.getTimeInMillis();
            }
            case DAY: {
                // 重置日期
                time.set(Calendar.MILLISECOND, 0);
                time.set(Calendar.SECOND, 0);
                time.set(Calendar.MINUTE, 0);
                time.set(Calendar.HOUR_OF_DAY, 0);
                // 推进日期
                time.add(Calendar.DAY_OF_YEAR, r.value);
                return time.getTimeInMillis();
            }
            case WEEK: {
                // 重置日期
                time.set(Calendar.MILLISECOND, 0);
                time.set(Calendar.SECOND, 0);
                time.set(Calendar.MINUTE, 0);
                time.set(Calendar.HOUR_OF_DAY, 0);
                time.set(Calendar.DAY_OF_WEEK, 1);
                // 推进日期
                time.add(Calendar.WEEK_OF_YEAR, r.value);
                return time.getTimeInMillis();
            }
        }
        return 0L;
    }),

    /**
     * 周一开始，周日结束
     */
    START_IN_MONDAY(r -> {
        Calendar time = Calendar.getInstance();
        switch (r.unit) {
            case HOUR: {
                // 同上处理
                return START_IN_SUNDAY.next.apply(r);
            }
            case DAY: {
                // 同上处理
                return START_IN_SUNDAY.next.apply(r);
            }
            case WEEK: {
                // 判断周日
                if (time.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    // 推进日
                    time.add(Calendar.DAY_OF_YEAR, 1 + ((r.value - 1) * 7));
                } else {
                    // 推进周
                    time.add(Calendar.WEEK_OF_YEAR, r.value);
                    // 重置日
                    time.set(Calendar.DAY_OF_WEEK, 2);
                }
                // 重置时
                time.set(Calendar.HOUR_OF_DAY, 0);
                // 重置分
                time.set(Calendar.MINUTE, 0);
                // 重置秒
                time.set(Calendar.SECOND, 0);
                return time.getTimeInMillis();
            }
        }
        return 0L;
    });

    Function<In, Long> next;

    RealTime(Function<In, Long> next) {
        this.next = next;
    }

    /**
     * 获取下一周期的起始时间
     */
    public long nextTime(RealTimeUnit unit, int value) {
        return next.apply(new In(unit, value));
    }

    class In {

        private RealTimeUnit unit;
        private int value;

        public In(RealTimeUnit unit, int value) {
            this.unit = unit;
            this.value = value;
        }
    }
}

