/*****************************************************************************
 * Copyright (ComponentC) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Instantiates components using Constructor-Based Dependency Injection.
 * {@inheritDoc}
 * 
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Jon Tirs&eacute;n
 * @author Zohar Melamed
 * @version $Revision$
 */
public class ConstructorComponentAdapter extends InstantiatingComponentAdapter {

    /**
     * Explicitly specifies parameters, if null uses default parameters.
     * {@inheritDoc}
     */
    public ConstructorComponentAdapter(final Object componentKey,
                                       final Class componentImplementation,
                                       Parameter[] parameters) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        super(componentKey, componentImplementation, parameters);
    }

    /**
     * Use default parameters.
     * {@inheritDoc}
     */
    public ConstructorComponentAdapter(Object componentKey,
                                       Class componentImplementation) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        this(componentKey, componentImplementation, null);
    }

    protected Class[] getMostSatisfiableDependencyTypes(PicoContainer dependencyContainer) throws PicoIntrospectionException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Constructor constructor = getGreediestSatisifableConstructor(dependencyContainer);
        return constructor.getParameterTypes();
    }

    protected Constructor getGreediestSatisifableConstructor(PicoContainer dependencyContainer) throws PicoIntrospectionException, UnsatisfiableDependenciesException, AmbiguousComponentResolutionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        Constructor[] allConstructors = getComponentImplementation().getConstructors();
        List satisfiableConstructors = getAllSatisfiableConstructors(allConstructors, dependencyContainer);

        // now we'll just take the biggest one
        Constructor greediestConstructor = null;
        Set conflicts = new HashSet();
        for (int i = 0; i < satisfiableConstructors.size(); i++) {
            Constructor currentConstructor = (Constructor) satisfiableConstructors.get(i);
            if (greediestConstructor == null) {
                greediestConstructor = currentConstructor;
            } else if (greediestConstructor.getParameterTypes().length < currentConstructor.getParameterTypes().length) {
                conflicts.clear();
                greediestConstructor = currentConstructor;
            } else if (greediestConstructor.getParameterTypes().length == currentConstructor.getParameterTypes().length) {
                conflicts.add(greediestConstructor);
                conflicts.add(currentConstructor);
            }
        }
        if (!conflicts.isEmpty()) {
            throw new TooManySatisfiableConstructorsException(getComponentImplementation(), conflicts);
        }
        return greediestConstructor;
    }

    private List getAllSatisfiableConstructors(Constructor[] constructors, PicoContainer picoContainer) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        List satisfiableConstructors = new ArrayList();
        Set unsatisfiableDependencyTypes = new HashSet();
        for (int i = 0; i < constructors.length; i++) {
            boolean failedDependency = false;
            Constructor constructor = constructors[i];
            Class[] parameterTypes = constructor.getParameterTypes();
            Parameter[] currentParameters = parameters != null ? parameters : createDefaultParameters(parameterTypes, picoContainer);

            for (int j = 0; j < currentParameters.length; j++) {
                ComponentAdapter adapter = currentParameters[j].resolveAdapter(picoContainer, parameterTypes[j]);
                if (adapter == null) {
                    failedDependency = true;
                    unsatisfiableDependencyTypes.add(Arrays.asList(parameterTypes));
                } else {
                    // we can't depend on ourself
                    if (adapter.equals(this)) {
                        failedDependency = true;
                        unsatisfiableDependencyTypes.add(Arrays.asList(parameterTypes));
                    }
                    if (getComponentKey().equals(adapter.getComponentKey())) {
                        failedDependency = true;
                        unsatisfiableDependencyTypes.add(Arrays.asList(parameterTypes));
                    }
                }
            }
            if (!failedDependency) {
                satisfiableConstructors.add(constructor);
            }
        }
        if (satisfiableConstructors.isEmpty()) {
            throw new UnsatisfiableDependenciesException(this, unsatisfiableDependencyTypes);
        }
        return satisfiableConstructors;
    }

    protected Object[] getConstructorArguments(ComponentAdapter[] adapterDependencies) {
        Object[] result = new Object[adapterDependencies.length];
        for (int i = 0; i < adapterDependencies.length; i++) {
            ComponentAdapter adapterDependency = adapterDependencies[i];
            result[i] = adapterDependency.getComponentInstance();
        }
        return result;
    }
}
