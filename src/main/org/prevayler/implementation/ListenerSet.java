// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.implementation;

import org.prevayler.Listener;

import java.util.ArrayList;
import java.util.List;

class ListenerSet {

    private final List<ListenerGroup<?>> _groups = new ArrayList<ListenerGroup<?>>();

    public synchronized <E> void register(Class<E> eventClass, Listener<? super E> listener) {
        if (eventClass == null || listener == null) {
            return;
        }
        for (ListenerGroup<?> group : _groups) {
            if (group.add(eventClass, listener)) {
                return;
            }
        }
        _groups.add(new ListenerGroup<E>(eventClass, listener));
    }

    public synchronized <E> void unregister(Class<E> eventClass, Listener<? super E> listener) {
        if (eventClass == null || listener == null) {
            return;
        }
        for (ListenerGroup<?> group : _groups) {
            if (group.remove(eventClass, listener)) {
                return;
            }
        }
    }

    public synchronized <E> void dispatch(E event) {
        if (event == null) {
            return;
        }
        for (ListenerGroup<?> group : _groups) {
            group.dispatch(event);
        }
    }

}
