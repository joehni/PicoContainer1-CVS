/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picoextras.webwork;

import webwork.action.Action;
import webwork.action.factory.*;
import org.picoextras.webwork.PicoActionFactory;

/**
 * Custom webwork action lifecycle that ensures actions are treated as pico components.
 *
 * <p>To use, add to webwork.properties:
 * <pre>
 * webwork.action.factory=org.picoextras.webwork.WebWorkActionFactory
 * </pre></p>
 */
public class WebWorkActionFactory extends ActionFactory {

    private ActionFactory factory;

    public WebWorkActionFactory() {
        // replace standard JavaActionFactory with PicoActionFactory
        factory = new PicoActionFactory();
        // the rest are the standard webwork ActionFactoryProxies
        factory = new PrefixActionFactoryProxy(factory);
        factory = new CommandActionFactoryProxy(factory);
        factory = new AliasingActionFactoryProxy(factory);
        factory = new CommandActionFactoryProxy(factory);
        factory = new ContextActionFactoryProxy(factory);
        factory = new PrepareActionFactoryProxy(factory);
        factory = new ParametersActionFactoryProxy(factory);
        factory = new ChainingActionFactoryProxy(factory);
    }

    public Action getActionImpl(String actionName) throws Exception {
        return factory.getActionImpl(actionName);
    }

}
