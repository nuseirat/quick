package com.quick.loop;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

public final class QuickLoop {

    private final int start;
    private final int end;
    private int step;

    public QuickLoop(int end) {
        this(0, end);
    }

    public QuickLoop(int start, int end) {
        this.start = start;
        this.end = end;
        this.step = start <= end ? 1 : -1;
    }

    public QuickLoop step(int step) {
        if (step == 0) {
            throw new QuickLoopException("QuickLoop error: step cannot be 0");
        }

        if (start < end && step < 0) {
            throw new QuickLoopException("QuickLoop error: step must be positive when start < end");
        }

        if (start > end && step > 0) {
            throw new QuickLoopException("QuickLoop error: step must be negative when start > end");
        }

        this.step = step;
        return this;
    }

    public QuickLoop reverse() {
        QuickLoop reversed = new QuickLoop(end, start);
        reversed.step = -this.step;
        return reversed;
    }

    public void run(IntConsumer action) {
        Objects.requireNonNull(action, "QuickLoop run error: action cannot be null");

        try {
            if (step > 0) {
                for (int i = start; i < end; i += step) {
                    try {
                        action.accept(i);
                    } catch (SkipException e) {
                        continue;
                    }
                }
            } else {
                for (int i = start; i > end; i += step) {
                    try {
                        action.accept(i);
                    } catch (SkipException e) {
                        continue;
                    }
                }
            }
        } catch (StopException e) {
        }
    }

    public void forEach(IntConsumer action) {
        run(action);
    }

    public void print() {
        run(System.out::println);
    }

    public List<Integer> toList() {
        List<Integer> result = new ArrayList<>();
        run(result::add);
        return result;
    }

    public long count() {
        long total = 0;

        if (step > 0) {
            for (int i = start; i < end; i += step) {
                total++;
            }
        } else {
            for (int i = start; i > end; i += step) {
                total++;
            }
        }

        return total;
    }

    public int sum() {
        int total = 0;

        if (step > 0) {
            for (int i = start; i < end; i += step) {
                total += i;
            }
        } else {
            for (int i = start; i > end; i += step) {
                total += i;
            }
        }

        return total;
    }

    public Integer first() {
        if (step > 0) {
            return start < end ? start : null;
        }

        return start > end ? start : null;
    }

    public Integer last() {
        Integer last = null;

        if (step > 0) {
            for (int i = start; i < end; i += step) {
                last = i;
            }
        } else {
            for (int i = start; i > end; i += step) {
                last = i;
            }
        }

        return last;
    }

    public List<Integer> filter(IntPredicate condition) {
        Objects.requireNonNull(condition, "QuickLoop filter error: condition cannot be null");

        List<Integer> result = new ArrayList<>();
        run(i -> {
            if (condition.test(i)) {
                result.add(i);
            }
        });
        return result;
    }

    public List<Integer> where(IntPredicate condition) {
        return filter(condition);
    }

    public List<Integer> map(IntUnaryOperator mapper) {
        Objects.requireNonNull(mapper, "QuickLoop map error: mapper cannot be null");

        List<Integer> result = new ArrayList<>();
        run(i -> result.add(mapper.applyAsInt(i)));
        return result;
    }

    public static void skip() {
        throw new SkipException();
    }

    public static void stop() {
        throw new StopException();
    }

    @Override
    public String toString() {
        return "QuickLoop{" +
                "start=" + start +
                ", end=" + end +
                ", step=" + step +
                '}';
    }

    public static final class QuickLoopException extends RuntimeException {
        public QuickLoopException(String message) {
            super(message);
        }

        public QuickLoopException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static final class SkipException extends RuntimeException {
    }

    private static final class StopException extends RuntimeException {
    }
}