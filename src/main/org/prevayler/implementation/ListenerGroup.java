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

import java.util.ArrayList;
import java.util.List;

class ListenerGroup<E> {

    private final Class<E> _eventClass;

    private final List<Listener<? super E>> _listeners;

    public ListenerGroup(Class<E> eventClass, Listener<? super E> listener) {
        _eventClass = eventClass;
        _listeners = new ArrayList<Listener<? super E>>();
        _listeners.add(listener);
    }

    @SuppressWarnings("unchecked") public <X> boolean add(Class<X> eventClass, Listener<? super X> listener) {
        if (eventClass.equals(_eventClass)) {
            _listeners.add((Listener<? super E>) listener);
            return true;
        } else {
            return false;
        }
    }

    public <X> boolean remove(Class<X> eventClass, Listener<? super X> listener) {
        if (eventClass.equals(_eventClass)) {
            _listeners.remove(listener);
            return true;
        } else {
            return false;
        }
    }

    public <X> void dispatch(X event) {
        if (_eventClass.isInstance(event)) {
            E cast = _eventClass.cast(event);
            for (Listener<? super E> listener : _listeners) {
                try {
                    listener.handle(cast);
                } catch (Exception e) {
                    Cool.uncaught(e);
                }
            }
        }
    }

}
