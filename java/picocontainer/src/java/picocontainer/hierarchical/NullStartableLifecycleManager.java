/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.hierarchical;

import picocontainer.StartableLifecycleManager;
import picocontainer.PicoStartException;
import picocontainer.PicoStopException;
import picocontainer.PicoDisposalException;

public class NullStartableLifecycleManager
        implements StartableLifecycleManager {
    public void startComponent(Object component) throws PicoStartException {
    }

    public void stopComponent(Object component) throws PicoStopException {
    }

    public void disposeOfComponent(Object component) throws PicoDisposalException {
    }
}
