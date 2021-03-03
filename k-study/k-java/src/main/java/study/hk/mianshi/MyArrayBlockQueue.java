package study.hk.mianshi;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HK
 * @date 2020-05-26 15:39
 */
public class MyArrayBlockQueue<E> extends AbstractQueue<E> implements BlockingQueue<E> {

    private Object[] items;

    private ReentrantLock reentrantLock = new ReentrantLock();

    private Condition notEmpty = reentrantLock.newCondition();

    private Condition notFull = reentrantLock.newCondition();

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void put(@NotNull E e) throws InterruptedException {

    }

    @Override
    public boolean offer(E e, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return false;
    }

    @NotNull
    @Override
    public E take() throws InterruptedException {
        return null;
    }

    @Nullable
    @Override
    public E poll(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return null;
    }

    @Override
    public int remainingCapacity() {
        return 0;
    }

    @Override
    public int drainTo(@NotNull Collection<? super E> c) {
        return 0;
    }

    @Override
    public int drainTo(@NotNull Collection<? super E> c, int maxElements) {
        return 0;
    }

    @Override
    public boolean offer(E e) {
        return false;
    }

    @Override
    public E poll() {
        return null;
    }

    @Override
    public E peek() {
        return null;
    }
}
