/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.sample.nanowar.service;

import java.util.Collection;

import org.nanocontainer.sample.nanowar.model.Cheese;

/**
 * This is a service which is independent of any MVC framework.
 * 
 * @author Mauro Talevi
 */
public interface CheeseService {

    public Collection getCheeses();

    public Cheese find(Cheese example);

    public void save(Cheese cheese);

}