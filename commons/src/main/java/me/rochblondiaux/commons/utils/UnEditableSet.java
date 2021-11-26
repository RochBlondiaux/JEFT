package me.rochblondiaux.commons.utils;

import javax.print.attribute.UnmodifiableSetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class UnEditableSet<T> implements Set<T> {

    private final Set<T> set;

    public UnEditableSet(Set<T> set) {
        this.set = set;
    }

    public UnEditableSet() {
        throw new UnmodifiableSetException("Cannot create empty UnEditableList!");
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return set.toArray(a);
    }

    @Override
    public boolean add(T t) {
        throw new UnmodifiableSetException("Cannot add elements from UnEditableList!");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnmodifiableSetException("Cannot remove single element from UnEditableList!");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return set.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnmodifiableSetException("Cannot add elements from UnEditableList!");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnmodifiableSetException("Cannot retain elements from UnEditableList!");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnmodifiableSetException("Cannot remove elements from UnEditableList!");
    }

    @Override
    public void clear() {
        throw new UnmodifiableSetException("Cannot clear elements from UnEditableList!");
    }
}
