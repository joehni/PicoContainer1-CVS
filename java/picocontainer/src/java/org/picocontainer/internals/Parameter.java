/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Jon Tirsen                        *
 *****************************************************************************/

package org.picocontainer.internals;

import org.picocontainer.PicoInitializationException;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision$
 */
public interface Parameter {
    Object resolve(ComponentRegistry componentRegistry, ComponentSpecification compSpec, Class targetType)
            throws PicoInitializationException;
}
