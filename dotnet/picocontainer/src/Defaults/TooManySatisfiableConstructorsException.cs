/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 * C# port by Maarten Grootendorst                                           *
 *****************************************************************************/

using System;
using System.Collections;
namespace PicoContainer.Defaults
{
  [Serializable]
  public class TooManySatisfiableConstructorsException : PicoIntrospectionException
	{
    private Type forClass;
    private ICollection constructors;

    public TooManySatisfiableConstructorsException(Type forClass, ICollection constructors) 
    {
      this.forClass = forClass;
      this.constructors = constructors;
    }

    public Type ForImplementationClass
    {
      get {
        return forClass;
      }
    }

    public override String Message
    {
      get 
      {
        return "Too many satisfiable constructors:" + constructors.ToString();
      }
    }

    public ICollection Constructors
    {
      get {
        return constructors;
      }
    }
  }
}

