/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Chris Stevenson*
 *****************************************************************************/

package org.nanocontainer.multicast;

import org.nanocontainer.proxy.ProxyFactory;
import org.nanocontainer.proxy.StandardProxyFactory;
import org.picocontainer.defaults.InterfaceFinder;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Chris Stevenson
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MulticasterFactory {
    private final ProxyFactory proxyFactory;

    public MulticasterFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public MulticasterFactory() {
        this(new StandardProxyFactory());
    }

    public Object createComponentMulticaster(Class classOrInterface,
                                             Class[] interfaces,
                                             List objectsToAggregateCallFor,
                                             boolean callInReverseOrder,
                                             Invoker invoker) {
        List copy = new ArrayList(objectsToAggregateCallFor);

        if (!callInReverseOrder) {
            // reverse the list
            Collections.reverse(copy);
        }
        Object[] targets = copy.toArray();

        if(classOrInterface == null) {
            classOrInterface = new InterfaceFinder().getClass(objectsToAggregateCallFor.toArray());
        }
        if(interfaces == null) {
            interfaces = new InterfaceFinder().getAllInterfaces(objectsToAggregateCallFor);
        }

        return proxyFactory.createProxy(classOrInterface, interfaces, new AggregatingInvocationInterceptor(this, targets, invoker, proxyFactory));
    }
}
