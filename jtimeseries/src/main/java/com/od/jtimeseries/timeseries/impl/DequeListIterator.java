/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.timeseries.impl;

import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 01-Mar-2009
 * Time: 16:43:19
 * To change this template use File | Settings | File Templates.
 *
 * If you suspected this was a shameless clipboard lift of the iterator logic which has protected access in AbstractList,
 * you'd probably be on the right track.
 */
class DequeListIterator<E> implements ListIterator<E> {

    private ModCountList<E> modCountList;

    public DequeListIterator(ModCountList<E> modCountList) {
        this.modCountList = modCountList;
        this.expectedModCount = modCountList.getModCount();
    }

    public DequeListIterator(ModCountList<E> modCountList, int index) {
        this.modCountList = modCountList;
        this.cursor = index;
        this.expectedModCount = modCountList.getModCount();
    }

    /**
	 * Index of element to be returned by subsequent call to next.
	 */
	int cursor = 0;

	/**
	 * Index of element returned by most recent call to next or
	 * previous.  Reset to -1 if this element is deleted by a call
	 * to remove.
	 */
	int lastRet = -1;

	/**
	 * The modCount value that the iterator believes that the backing
	 * List should have.  If this expectation is violated, the iterator
	 * has detected concurrent modification.
	 */
	int expectedModCount;

	public boolean hasNext() {
            return cursor != modCountList.size();
	}

	public E next() {
            checkForComodification();
	    try {
		E next = modCountList.get(cursor);
		lastRet = cursor++;
		return next;
	    } catch(IndexOutOfBoundsException e) {
		checkForComodification();
		throw new NoSuchElementException();
	    }
	}

	public void remove() {
	    if (lastRet == -1)
		throw new IllegalStateException();
            checkForComodification();

	    try {
		modCountList.remove(lastRet);
		if (lastRet < cursor)
		    cursor--;
		lastRet = -1;
		expectedModCount = modCountList.getModCount();
	    } catch(IndexOutOfBoundsException e) {
		throw new ConcurrentModificationException();
	    }
	}

	final void checkForComodification() {
	    if (modCountList.getModCount() != expectedModCount)
		throw new ConcurrentModificationException();
	}


	public boolean hasPrevious() {
	    return cursor != 0;
	}

    public E previous() {
        checkForComodification();
        try {
            int i = cursor - 1;
            E previous = modCountList.get(i);
            lastRet = cursor = i;
            return previous;
        } catch(IndexOutOfBoundsException e) {
            checkForComodification();
            throw new NoSuchElementException();
        }
    }

	public int nextIndex() {
	    return cursor;
	}

	public int previousIndex() {
	    return cursor-1;
	}

	public void set(E o) {
	    if (lastRet == -1)
		throw new IllegalStateException();
        checkForComodification();
	    try {
		modCountList.set(lastRet, o);
		expectedModCount = modCountList.getModCount();
	    } catch(IndexOutOfBoundsException e) {
		    throw new ConcurrentModificationException();
	    }
	}

	public void add(E o) {
        checkForComodification();
	    try {
		modCountList.add(cursor++, o);
		lastRet = -1;
		expectedModCount = modCountList.getModCount();
	    } catch(IndexOutOfBoundsException e) {
		    throw new ConcurrentModificationException();
	    }
	}
}
