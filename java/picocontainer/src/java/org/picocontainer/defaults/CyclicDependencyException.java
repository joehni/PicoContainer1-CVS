/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.PicoInitializationException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class CyclicDependencyException extends PicoInitializationException {
    private final List stack;

    /**
     * @since 1.1
     */
    public CyclicDependencyException(Class element) {
        this.stack = new LinkedList();
        push(element);
    }
    
    /**
     * @since 1.1
     */
    public void push(Class element) {
        stack.add(element);
    }

    public Class[] getDependencies() {
        return (Class[]) stack.toArray(new Class[stack.size()]);
    }

    public String getMessage() {
        return "Cyclic dependency: " + stack.toString();
    }
}
