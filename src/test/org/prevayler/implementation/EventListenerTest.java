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

import static org.prevayler.Safety.Level.LEVEL_4_JOURNALING;

import org.prevayler.GenericTransaction;
import org.prevayler.Listener;
import org.prevayler.PrevalenceContext;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Safety;

import java.io.Serializable;

import junit.framework.TestCase;

public class EventListenerTest extends TestCase {

    private Prevayler<AppendingSystem> _prevayler;

    @Override public void setUp() {
        _prevayler = PrevaylerFactory.createTransientPrevayler(new AppendingSystem());
    }

    public void testListening() {
        MyListener listener = new MyListener();
        assertNull(listener._value);

        _prevayler.execute(new MyTransaction());
        assertNull(listener._value);

        _prevayler.register(MyEvent.class, listener);

        _prevayler.execute(new MyTransaction());
        assertEquals("xx", listener._value);

        _prevayler.execute(new MyTransaction());
        assertEquals("xxx", listener._value);

        listener._value = null;
        _prevayler.unregister(MyEvent.class, listener);
        _prevayler.execute(new MyTransaction());
        assertNull(listener._value);
        assertEquals("xxxx", _prevayler.execute(new ValueQuery()));
    }

    public static final class MyEvent {

        public final String _value;

        public MyEvent(String value) {
            _value = value;
        }

    }

    public static final class MyListener implements Listener<MyEvent> {

        public String _value = null;

        public void handle(MyEvent event) {
            _value = event._value;
        }

    }

    @Safety(LEVEL_4_JOURNALING) public static final class MyTransaction implements GenericTransaction<AppendingSystem, Void, RuntimeException>, Serializable {

        private static final long serialVersionUID = 1L;

        public Void executeOn(AppendingSystem prevalentSystem, PrevalenceContext prevalenceContext) {
            prevalentSystem.append("x");
            prevalenceContext.trigger(new MyEvent(prevalentSystem.value()));
            return null;
        }

    }

}
