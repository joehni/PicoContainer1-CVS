/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan and Mauro Talevi                          *
 *****************************************************************************/
package org.nanocontainer.jmx;

import org.picocontainer.PicoRegistrationException;

/**
 * A registration exception caused trying to register the component with JMX
 * 
 * @author James Strachan
 * @author Mauro Talevi
 * @version $Revision: 1.1 $
 */
public class NanoMXRegistrationException extends PicoRegistrationException {
    public NanoMXRegistrationException(Throwable cause) {
        super(cause);
    }
}
