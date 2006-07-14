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
import org.prevayler.foundation.Cool;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class Listeners {

    private final Map<Class<?>, List<Listener<?>>> _listeners = new HashMap<Class<?>, List<Listener<?>>>();

    public synchronized <E> void addListener(Class<E> eventClass, Listener<? super E> listener) {
        if (eventClass == null || listener == null) {
            return;
        }
        if (!_listeners.containsKey(eventClass)) {
            _listeners.put(eventClass, new LinkedList<Listener<?>>());
        }
        _listeners.get(eventClass).add(listener);
    }

    public synchronized <E> void removeListener(Class<E> eventClass, Listener<? super E> listener) {
        if (eventClass == null || listener == null) {
            return;
        }
        if (_listeners.containsKey(eventClass)) {
            _listeners.get(eventClass).remove(listener);
        }
    }

    public synchronized <E> void dispatch(E event) {
        if (event == null) {
            return;
        }
        for (Class<?> eventClass : _listeners.keySet()) {
            if (eventClass.isInstance(event)) {
                for (Listener<?> listener : _listeners.get(eventClass)) {
                    dispatch(event, listener);
                }
            }
        }
    }

    @SuppressWarnings("unchecked") private <E> void dispatch(E event, Listener<?> listener) {
        try {
            ((Listener<? super E>) listener).handle(event);
        } catch (Exception e) {
            Cool.uncaught(e);
        }
    }

}
