/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.defaults;

import org.nanocontainer.nanoaop.ComponentPointcut;

/**
 * @author Stephen Molitor
 */
public class DefaultComponentPointcut implements ComponentPointcut {
    
    private final Object componentKey;
    
    public DefaultComponentPointcut(Object componentKey) {
        this.componentKey = componentKey;
    }
    
    public Object getComponentKey() {
        return componentKey;
    }

}
