/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

import java.io.Serializable;
import java.lang.reflect.Modifier;

/**
 * Base class for a ComponentAdapter with general functionality.
 * This implementation provides basic checks for a healthy implementation of a ComponentAdapter.
 * It does not allow to use <code>null</code> for the component key or the implementation, 
 * ensures that the implementation is a concrete class and that the key is assignable from the 
 * implementation if the key represents a type.   
 *  
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 * @since 1.0
 */
public abstract class AbstractComponentAdapter implements ComponentAdapter, Serializable {
    private Object componentKey;
    private Class componentImplementation;
    private PicoContainer container;

    /**
     * Constructs a new ComponentAdapter for the given key and implementation. 
     * @param componentKey the search key for this implementation
     * @param componentImplementation the concrete implementation
     * @throws AssignabilityRegistrationException if the key is a type and the implementation cannot be assigned to.
     * @throws NotConcreteRegistrationException if the implementation is not a concrete class.
     */
    protected AbstractComponentAdapter(Object componentKey, Class componentImplementation) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (componentImplementation == null) {
            throw new NullPointerException("componentImplementation");
        }
        this.componentKey = componentKey;
        this.componentImplementation = componentImplementation;
        checkTypeCompatibility();
        checkConcrete();
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.ComponentAdapter#getComponentKey()
     */
    public Object getComponentKey() {
        if (componentKey == null) {
            throw new NullPointerException("componentKey");
        }
        return componentKey;
    }

    /**
     * {@inheritDoc}
     * @see org.picocontainer.ComponentAdapter#getComponentImplementation()
     */
    public Class getComponentImplementation() {
        return componentImplementation;
    }

    private void checkTypeCompatibility() throws AssignabilityRegistrationException {
        if (componentKey instanceof Class) {
            Class componentType = (Class) componentKey;
            if (!componentType.isAssignableFrom(componentImplementation)) {
                throw new AssignabilityRegistrationException(componentType, componentImplementation);
            }
        }
    }

    private void checkConcrete() throws NotConcreteRegistrationException {
        // Assert that the component class is concrete.
        boolean isAbstract = (componentImplementation.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT;
        if (componentImplementation.isInterface() || isAbstract) {
            throw new NotConcreteRegistrationException(componentImplementation);
        }
    }


    /**
     * @return Returns the ComponentAdapter's class name and the component's key.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getClass().getName() + "[" + getComponentKey() + "]";
    }

    /**
     * @see org.picocontainer.ComponentAdapter#getContainer()
     */
    public PicoContainer getContainer() {
        return container;
    }

    /**
     * @see org.picocontainer.ComponentAdapter#setContainer(org.picocontainer.PicoContainer)
     */
    public void setContainer(PicoContainer picoContainer) {
        this.container = picoContainer;
    }
}
