package org.picocontainer.extras;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;

import java.beans.IntrospectionException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class InvokingComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {

    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return new InvokingComponentAdapterFactory(new DefaultComponentAdapterFactory(), "setMessage", new Class[]{String.class}, new String[]{"hello"});
    }

    private ComponentAdapter createAdapterCallingSetMessage(Class impl) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        InvokingComponentAdapterFactory.Adapter adapter =
                (InvokingComponentAdapterFactory.Adapter) createComponentAdapterFactory().createComponentAdapter("whatever", impl, null);
        return adapter;
    }

    public static class Foo {
        public String message;

        public String setMessage(String message) {
            this.message = message;
            return message + " world";
        }
    }

    public static class Failing {
        public void setMessage(String message) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public static class NoSetMessage {
    }

    public void testSuccessfulMethod() throws PicoInitializationException, NoSuchMethodException, IntrospectionException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter adapter = createAdapterCallingSetMessage(Foo.class);
        Foo foo = (Foo) adapter.getComponentInstance();
        assertNotNull(foo);
        assertEquals("hello", foo.message);

        assertEquals("hello world", ((InvokingComponentAdapterFactory.Adapter) adapter).getInvocationResult());
    }

    public void testFailingInvocation() throws NoSuchMethodException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter adapter = createAdapterCallingSetMessage(Failing.class);
        try {
            adapter.getComponentInstance();
            fail();
        } catch (PicoInitializationException e) {
        }
    }

    public void testNoInvocation() throws NoSuchMethodException, PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        ComponentAdapter adapter = createAdapterCallingSetMessage(NoSetMessage.class);
        NoSetMessage noSetMessage = (NoSetMessage) adapter.getComponentInstance();
        assertNotNull(noSetMessage);
    }
}
