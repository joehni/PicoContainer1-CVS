package org.picocontainer.tck;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.picocontainer.*;
import org.picocontainer.defaults.*;
import org.picocontainer.testmodel.*;

import java.io.*;
import java.util.*;

/**
 * This test tests (at least it should) all the methods in MutablePicoContainer.
 */
public abstract class AbstractPicoContainerTestCase extends TestCase {

    protected abstract MutablePicoContainer createPicoContainer();

    protected final MutablePicoContainer createPicoContainerWithDependsOnTouchableOnly() throws
            PicoRegistrationException, PicoIntrospectionException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(DependsOnTouchable.class);
        return pico;

    }

    protected final MutablePicoContainer createPicoContainerWithTouchableAndDependsOnTouchable() throws
            PicoRegistrationException, PicoIntrospectionException {
        MutablePicoContainer pico = createPicoContainerWithDependsOnTouchableOnly();
        pico.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        return pico;
    }

    // TODO: remove? redundant test.
    public void testNewContainerIsNotNull() throws PicoRegistrationException, PicoIntrospectionException {
        assertNotNull(createPicoContainerWithTouchableAndDependsOnTouchable());
    }

    public void testRegisteredComponentsExistAndAreTheCorrectTypes() throws PicoException, PicoRegistrationException {
        PicoContainer pico = createPicoContainerWithTouchableAndDependsOnTouchable();

        assertTrue("Container should have Touchable component",
                pico.hasComponent(Touchable.class));
        assertTrue("Container should have DependsOnTouchable component",
                pico.hasComponent(DependsOnTouchable.class));
        assertTrue("Component should be instance of Touchable",
                pico.getComponentInstance(Touchable.class) instanceof Touchable);
        assertTrue("Component should be instance of DependsOnTouchable",
                pico.getComponentInstance(DependsOnTouchable.class) instanceof DependsOnTouchable);
        assertTrue("should not have non existent component", !pico.hasComponent(Map.class));
    }

    public void testRegistersSingleInstance() throws PicoException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer();
        StringBuffer sb = new StringBuffer();
        pico.registerComponentInstance(sb);
        assertSame(sb, pico.getComponentInstance(StringBuffer.class));
    }

    public void testContainerIsSerializable() throws PicoException, PicoInitializationException,
            IOException, ClassNotFoundException {

        PicoContainer pico = createPicoContainerWithTouchableAndDependsOnTouchable();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(pico);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

        pico = (PicoContainer) ois.readObject();

        DependsOnTouchable dependsOnTouchable = (DependsOnTouchable) pico.getComponentInstance(DependsOnTouchable.class);
        assertNotNull(dependsOnTouchable);
        SimpleTouchable touchable = (SimpleTouchable) pico.getComponentInstance(Touchable.class);

        assertTrue(touchable.wasTouched);
    }

    public void testGettingComponentWithMissingDependencyFails() throws PicoException, PicoRegistrationException {
        try {
            PicoContainer picoContainer = createPicoContainerWithDependsOnTouchableOnly();
            picoContainer.getComponentInstance(DependsOnTouchable.class);
            fail("should need a Touchable");
        } catch (UnsatisfiableDependenciesException e) {
            assertEquals(DependsOnTouchable.class, e.getUnsatisfiableComponentImplementation());
            final Set unsatisfiableDependencies = e.getUnsatisfiableDependencies();
            assertEquals(1, unsatisfiableDependencies.size());
            assertEquals(Touchable.class, unsatisfiableDependencies.iterator().next());
        }
    }

    public void testDuplicateRegistration() throws Exception {
        try {
            MutablePicoContainer pico = createPicoContainer();
            pico.registerComponentImplementation(Object.class);
            pico.registerComponentImplementation(Object.class);
            fail("Should have failed with duplicate registration");
        } catch (DuplicateComponentKeyRegistrationException e) {
            assertTrue("Wrong key", e.getDuplicateKey() == Object.class);
        }
    }

    public void testExternallyInstantiatedObjectsCanBeRegistgeredAndLookeUp() throws PicoException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer();
        final HashMap map = new HashMap();
        pico.registerComponentInstance(Map.class, map);
        assertSame(map, pico.getComponentInstance(Map.class));
    }

    public void testAmbiguousResolution() throws PicoRegistrationException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation("ping", String.class);
        pico.registerComponentInstance("pong", "pang");
        try {
            pico.getComponentInstance(String.class);
        } catch (AmbiguousComponentResolutionException e) {
            assertTrue(e.getMessage().indexOf("java.lang.String") != -1);
        }
    }

    public void testLookupWithUnregisteredKeyReturnsNull() throws PicoIntrospectionException, PicoInitializationException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        MutablePicoContainer pico = createPicoContainer();
        assertNull(pico.getComponentInstance(String.class));
    }

    public static class ListAdder {
        public ListAdder(Collection list) {
            list.add("something");
        }
    }

    public void TODOtestMulticasterResolution() throws PicoRegistrationException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer();

        pico.registerComponentImplementation(ListAdder.class);
        pico.registerComponentImplementation("a", ArrayList.class);
        pico.registerComponentImplementation("l", LinkedList.class);

        pico.getComponentInstance(ListAdder.class);

        List a = (List) pico.getComponentInstance("a");
        assertTrue(a.contains("something"));

        List l = (List) pico.getComponentInstance("l");
        assertTrue(l.contains("something"));
    }

    public void testUnsatisfiedComponentsExceptionGivesVerboseEnoughErrorMessage() {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(ComponentD.class);

        try {
            pico.getComponentInstance(ComponentD.class);
        } catch (UnsatisfiableDependenciesException e) {
            Set unsatisfiableDependencies = e.getUnsatisfiableDependencies();
            assertEquals(2, unsatisfiableDependencies.size());
            assertTrue(unsatisfiableDependencies.contains(ComponentE.class));
            assertTrue(unsatisfiableDependencies.contains(ComponentB.class));

            assertTrue(e.getMessage().indexOf("class " + ComponentE.class.getName()) != -1);
            assertTrue(e.getMessage().indexOf("class " + ComponentB.class.getName()) != -1);
        }
    }

    public void testCyclicDependencyThrowsCyclicDependencyException() {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(ComponentB.class);
        pico.registerComponentImplementation(ComponentD.class);
        pico.registerComponentImplementation(ComponentE.class);

        try {
            pico.getComponentInstance(ComponentD.class);
            fail();
        } catch (CyclicDependencyException e) {
            final List dDependencies = Arrays.asList(ComponentD.class.getConstructors()[0].getParameterTypes());
            final List reportedDependencies = Arrays.asList(e.getDependencies());
            assertEquals(dDependencies, reportedDependencies);
        } catch (StackOverflowError e) {
            fail();
        }
    }

    public void testRemovalNonRegisteredComponentAdapterWorksAndReturnsNull() {
        final MutablePicoContainer picoContainer = createPicoContainer();
        assertNull(picoContainer.unregisterComponent("COMPONENT DOES NOT EXIST"));
    }

    /**
     * Important! Nanning really, really depends on this!
     */
    public void testComponentAdapterRegistrationOrderIsMaintained() {
        ConstructorComponentAdapter c1 = new ConstructorComponentAdapter("1", Object.class);
        ConstructorComponentAdapter c2 = new ConstructorComponentAdapter("2", String.class);

        MutablePicoContainer picoContainer = createPicoContainer();
        picoContainer.registerComponent(c1);
        picoContainer.registerComponent(c2);
        assertEquals("registration order should be maintained",
                Arrays.asList(new Object[] {c1, c2}), picoContainer.getComponentAdapters());

        picoContainer.getComponentInstances(); // create all the instances at once
        assertFalse("instances should be created in same order as adapters are created",
                picoContainer.getComponentInstances().get(0) instanceof String);
        assertTrue("instances should be created in same order as adapters are created",
                picoContainer.getComponentInstances().get(1) instanceof String);

        MutablePicoContainer reversedPicoContainer = createPicoContainer();
        reversedPicoContainer.registerComponent(c2);
        reversedPicoContainer.registerComponent(c1);
        assertEquals("registration order should be maintained",
                Arrays.asList(new Object[] {c2, c1}), reversedPicoContainer.getComponentAdapters());

        reversedPicoContainer.getComponentInstances(); // create all the instances at once
        assertTrue("instances should be created in same order as adapters are created",
                reversedPicoContainer.getComponentInstances().get(0) instanceof String);
        assertFalse("instances should be created in same order as adapters are created",
                reversedPicoContainer.getComponentInstances().get(1) instanceof String);
    }

    public static class NeedsTouchable {
        public Touchable touchable;

        public NeedsTouchable(Touchable touchable) {
            this.touchable = touchable;
        }
    }

    public static class NeedsWashable {
        public Washable washable;

        public NeedsWashable(Washable washable) {
            this.washable = washable;
        }
    }

    public void testSameInstanceCanBeUsedAsDifferentType() {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation("wt", WashableTouchable.class);
        pico.registerComponentImplementation("nw", NeedsWashable.class);
        pico.registerComponentImplementation("nt", NeedsTouchable.class);

        NeedsWashable nw = (NeedsWashable) pico.getComponentInstance("nw");
        NeedsTouchable nt = (NeedsTouchable) pico.getComponentInstance("nt");
        assertSame(nw.washable, nt.touchable);
    }

    public void testRegisterComponentWithObjectBadType() throws PicoIntrospectionException {
        MutablePicoContainer pico = createPicoContainer();

        try {
            pico.registerComponentInstance(Serializable.class, new Object());
            fail("Shouldn't be able to register an Object.class as Serializable because it is not, " +
                    "it does not implement it, Object.class does not implement much.");
        } catch (AssignabilityRegistrationException e) {
        }

    }

    public static class JMSService {
        public final String serverid;
        public final String path;

        public JMSService(String serverid, String path) {
            this.serverid = serverid;
            this.path = path;
        }
    }

    // http://jira.codehaus.org/secure/ViewIssue.jspa?key=PICO-52
    public void testPico52() {
        MutablePicoContainer pico = createPicoContainer();

        pico.registerComponentImplementation("foo", JMSService.class, new Parameter[]{
            new ConstantParameter("0"),
            new ConstantParameter("something"),
        });
        JMSService jms = (JMSService) pico.getComponentInstance("foo");
        assertEquals("0", jms.serverid);
        assertEquals("something", jms.path);
    }

    public static class ComponentA {
        public ComponentA(ComponentB b, ComponentC c) {
            Assert.assertNotNull(b);
            Assert.assertNotNull(c);
        }
    }

    public static class ComponentB {
    }

    public static class ComponentC {
    }

    public static class ComponentD {
        public ComponentD(ComponentE e, ComponentB b) {
            Assert.assertNotNull(e);
            Assert.assertNotNull(b);
        }
    }

    public static class ComponentE {
        public ComponentE(ComponentD d) {
            Assert.assertNotNull(d);
        }
    }

    public static class ComponentF {
        public ComponentF(ComponentA a) {
            Assert.assertNotNull(a);
        }
    }

    public void testAggregatedVerificationException() {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(ComponentA.class);
        pico.registerComponentImplementation(ComponentE.class);
        try {
            pico.verify();
            fail("we expect a PicoVerificationException");
        } catch (PicoVerificationException e) {
            List nested = e.getNestedExceptions();
            assertEquals(2, nested.size());

            Set bc = new HashSet(Arrays.asList(new Class[]{ComponentB.class, ComponentC.class}));
            assertTrue(nested.contains(new UnsatisfiableDependenciesException(ComponentA.class, bc)));

            Set d = new HashSet(Arrays.asList(new Class[]{ComponentD.class}));
            assertTrue(nested.contains(new UnsatisfiableDependenciesException(ComponentE.class, d)));
        }
    }

    public void testRegistrationOfAdapterSetsHostingContainerAsSelf() {
        final InstanceComponentAdapter componentAdapter = new InstanceComponentAdapter("", new Object());
        final MutablePicoContainer picoContainer = createPicoContainer();
        picoContainer.registerComponent(componentAdapter);
        assertSame(picoContainer, componentAdapter.getContainer());
    }

}
