/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Michael Ward                                             *
 *****************************************************************************/

package org.nanocontainer.jmx;

import javax.management.DynamicMBean;
import javax.management.MBeanInfo;

import org.nanocontainer.jmx.testmodel.Person;
import org.nanocontainer.jmx.testmodel.PersonMBean;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import junit.framework.TestCase;


/**
 * @author Michael Ward
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class StandardMBeanFactoryTestCase extends TestCase {

    public void testMBeanCreationWithMBeanInfo() {
        final DynamicMBeanFactory factory = new StandardMBeanFactory();
        final MBeanInfo mBeanInfo = Person.createMBeanInfo();
        final DynamicMBean mBean = factory.create(new Person(), mBeanInfo);
        assertNotNull(mBean);
        assertEquals(mBeanInfo, mBean.getMBeanInfo());
    }

    public void testMBeanCreationWithMBeanInfoAndArbitraryInterfaceName() {
        final DynamicMBeanFactory factory = new StandardMBeanFactory();
        final MBeanInfo mBeanInfo = Person.createMBeanInfo();
        final DynamicMBean mBean = factory.create(new SimpleTouchable(), Touchable.class, mBeanInfo);
        assertNotNull(mBean);
    }

    public void testStandardMBeanCeation() {
        final DynamicMBeanFactory factory = new StandardMBeanFactory();
        final DynamicMBean mBean = factory.create(new Person(), PersonMBean.class, (String)null);
        assertNotNull(mBean);
    }

    public void testStandardMBeanCeationWithDescription() {
        final DynamicMBeanFactory factory = new StandardMBeanFactory();
        final DynamicMBean mBean = factory.create(new Person(), PersonMBean.class, "An individual description");
        assertNotNull(mBean);
        assertEquals("An individual description", mBean.getMBeanInfo().getDescription());
    }

    public void testMBeanCreationFailsWithoutManagementInterface() {
        final DynamicMBeanFactory factory = new StandardMBeanFactory();
        final MBeanInfo mBeanInfo = Person.createMBeanInfo();
        try {
            factory.create(new SimpleTouchable(), mBeanInfo);
            fail("JMXRegistrationException expected");
        } catch(final JMXRegistrationException e) {
            // fine
        }
    }
}
