/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.nanocontainer.concurrent;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DecoratingComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;


/**
 * A {@link ComponentAdapterFactory} for {@link ThreadLocalComponentAdapter} instances.
 *
 * @author J&ouml;rg Schaible
 */
public class ThreadLocalComponentAdapterFactory
        extends DecoratingComponentAdapterFactory {

    /**
     * Constructs a wrapping
     * {@link org.picocontainer.defaults.ComponentAdapterFactory}.
     *
     * @param delegate The delegated ComponentAdapterFactory.
     */
    public ThreadLocalComponentAdapterFactory(ComponentAdapterFactory delegate) {
        super(delegate);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.picocontainer.defaults.DecoratingComponentAdapterFactory#createComponentAdapter(java.lang.Object, java.lang.Class, org.picocontainer.Parameter[])
     */
    public ComponentAdapter createComponentAdapter(Object componentKey, Class componentImplementation, Parameter[] parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException,
            NotConcreteRegistrationException {
        return new ThreadLocalComponentAdapter(super.createComponentAdapter(componentKey, componentImplementation, parameters));
    }

}
