/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.script.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.picocontainer.PicoContainer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * {@inheritDoc}
 * The groovy script has to return an instance of {@link NanoContainer}.
 * There is an implicit variable named "parent" that may contain a reference to a parent
 * container. It is recommended to use this as a constructor argument to the instantiated
 * NanoPicoContainer.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 * @version $Revision$
 */
public class GroovyContainerBuilder extends ScriptedContainerBuilder {
    private Script groovyScript;

    public GroovyContainerBuilder(final Reader script, ClassLoader classLoader) {
        super(script, classLoader);
    }

    // TODO: This should really return NanoContainer using a nano variable in the script. --Aslak
    protected PicoContainer createContainerFromScript(PicoContainer parentContainer, Object assemblyScope) {
        if (groovyScript == null) {
            createGroovyScript();
        }
        Binding binding = new Binding();
        binding.setVariable("parent", parentContainer);
        binding.setVariable("assemblyScope", assemblyScope);
        groovyScript.setBinding(binding);

        // both returning something or defining the variable is ok.
        Object result = groovyScript.run();
        Object picoVariable = binding.getVariable("pico");
        if (picoVariable == null) {
            picoVariable = result;
        }
        if (picoVariable instanceof PicoContainer) {
            return (PicoContainer) picoVariable;
        } else if (picoVariable instanceof NanoContainer) {
            return ((NanoContainer) picoVariable).getPico();
        } else {
            throw new NanoContainerMarkupException("Bad type for pico:" + picoVariable.getClass().getName());
        }
    }

    private void createGroovyScript() {
        try {
            GroovyClassLoader loader = new GroovyClassLoader(classLoader);
            InputStream scriptIs = new InputStream() {
                public int read() throws IOException {
                    return script.read();
                }
            };
            Class scriptClass = loader.parseClass(scriptIs, "nanocontainer.groovy");
            groovyScript = InvokerHelper.createScript(scriptClass, null);
        } catch (CompilationFailedException e) {
            throw new NanoContainerGroovyCompilationException("Compilation Failed '" + e.getMessage() + "'", e);
        } catch (IOException e) {
            throw new NanoContainerMarkupException(e);
        }

    }
}