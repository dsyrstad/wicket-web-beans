/*---
   Copyright 2004-2008 Visual Systems Corporation.
   http://www.vscorp.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   
        http://www.apache.org/licenses/LICENSE-2.0
   
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
---*/
// Derived from Ener-J
//
// Portions of this code were derived from OGNL:
//	Copyright (c) 1998-2004, Drew Davidson and Luke Blanshard
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without
//  modification, are permitted provided that the following conditions are
//  met:
//
//  Redistributions of source code must retain the above copyright notice,
//  this list of conditions and the following disclaimer.
//  Redistributions in binary form must reproduce the above copyright
//  notice, this list of conditions and the following disclaimer in the
//  documentation and/or other materials provided with the distribution.
//  Neither the name of the Drew Davidson nor the names of its contributors
//  may be used to endorse or promote products derived from this software
//  without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
//  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
//  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
//  FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
//  COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
//  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
//  OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
//  AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
//  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
//  THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
//  DAMAGE.
package net.sourceforge.wicketwebbeans.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.commons.lang.ClassUtils;

/**
 * Static Class utilities. 
 * 
 * TODO This whole class needs tests. Some is indirectly tested via BeanFactoryTest.
 * 
 * @author Dan Syrstad
 */
public class WwbClassUtils
{
    // Don't allow construction
    private WwbClassUtils()
    {
    }

    /**
     * Gets the most specific method to be called for the given class, method name and arguments.
     * It attempts to find the method with the most "specific" argument type match (in JLS terms). 
     * If successful this method will return the Method that can be called, otherwise null.
     */
    public static Method findMostSpecificMethod(Class<?> aClass, String aMethodName, Object... someArgs)
    {
        return findMostSpecificMethod(aClass, aMethodName, getArgTypes(someArgs));
    }

    /**
     * Gets the most specific method to be called for the given class, method name and arguments types.
     * It attempts to find the method with the most "specific" argument type match (in JLS terms). 
     * If successful this method will return the Method that can be called, otherwise null.
     */
    public static Method findMostSpecificMethod(Class<?> aClass, String aMethodName, Class<?>... someArgTypes)
    {
        Method result = null;
        Class<?>[] resultParameterTypes = null;
        Method[] methods = aClass.getMethods();

        for (Method method : methods) {
            if (method.getName().equals(aMethodName)) {
                Class<?>[] methodParamTypes = method.getParameterTypes();

                if (areArgsCompatible(someArgTypes, methodParamTypes)
                                && ((result == null) || isMoreSpecific(methodParamTypes, resultParameterTypes))) {
                    result = method;
                    resultParameterTypes = methodParamTypes;
                }
            }
        }

        return result;
    }

    /**
     * Invokes the most specific constructor to be called for the given class and arguments.
     * It attempts to find the constructor with the most "specific" argument type match (in JLS terms). 
     * If successful this method will return the Constructor that can be called, otherwise null.
     * 
     * @throws Exception if an error occurs finding the constructor or there is an error invoking the constructor.
     */
    public static Object invokeMostSpecificConstructor(Class<?> aClass, Object... someArgs) throws Exception
    {
        Constructor<?> constructor = findMostSpecificConstructor(aClass, getArgTypes(someArgs));
        if (constructor == null) {
            throw new IllegalArgumentException("No matching constructor found for " + aClass + " and arguments "
                            + Arrays.toString(someArgs));
        }

        return constructor.newInstance(someArgs);
    }

    /**
     * Gets the most specific constructor to be called for the given class and arguments.
     * It attempts to find the constructor with the most "specific" argument type match (in JLS terms). 
     * If successful this method will return the Constructor that can be called, otherwise null.
     */
    public static Constructor<?> findMostSpecificConstructor(Class<?> aClass, Object... someArgs)
    {
        return findMostSpecificConstructor(aClass, getArgTypes(someArgs));
    }

    /**
     * Convert an array of arguments to their types. If an argument is null, the type will be null. 
     */
    private static Class<?>[] getArgTypes(Object... someArgs)
    {
        Class<?>[] someArgTypes = new Class<?>[someArgs.length];
        for (int i = 0; i < someArgTypes.length; i++) {
            someArgTypes[i] = someArgs[i] == null ? null : someArgs[i].getClass();
        }

        return someArgTypes;
    }

    /**
     * Gets the most specific constructor to be called for the given class and arguments types.
     * It attempts to find the constructor with the most "specific" argument type match (in JLS terms). 
     * If successful this method will return the Constructor that can be called, otherwise null.
     */
    public static Constructor<?> findMostSpecificConstructor(Class<?> aClass, Class<?>... someArgTypes)
    {
        Constructor<?> result = null;
        Class<?>[] resultParameterTypes = null;
        Constructor<?>[] xtors = aClass.getConstructors();

        for (Constructor<?> xtor : xtors) {
            Class<?>[] xtorParamTypes = xtor.getParameterTypes();

            if (areArgsCompatible(someArgTypes, xtorParamTypes)
                            && ((result == null) || isMoreSpecific(xtorParamTypes, resultParameterTypes))) {
                result = xtor;
                resultParameterTypes = xtorParamTypes;
            }
        }

        return result;
    }

    /**
     * Tells whether the given array of types are compatible with the given array of
     * target types -- that is, whether the given array of objects can be passed as arguments
     * to a method or constructor whose parameter types are the given array of classes.
     */
    public static final boolean areArgsCompatible(Class<?>[] someArgTypes, Class<?>[] targetArgTypes)
    {
        if (someArgTypes.length != targetArgTypes.length) {
            return false;
        }

        for (int index = 0; index < someArgTypes.length; ++index) {
            if (!isTypeCompatible(someArgTypes[index], targetArgTypes[index])) {
                return false;
            }
        }

        return true;
    }

    /**
     * Tells whether the given type is compatible with the given target type 
     * -- that is, whether the given object can be passed as an argument
     * to a method or constructor whose parameter type is the given class.
     * If object is null this will return true because null is compatible
     * with any type.
     */
    public static final boolean isTypeCompatible(Class<?> aType, Class<?> aTargetType)
    {
        if (aType != null) {
            if (aTargetType.isPrimitive()) {
                if (ClassUtils.wrapperToPrimitive(aType) != aTargetType) {
                    return false;
                }
            }
            else if (!aTargetType.isAssignableFrom(aType)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Tells whether the first array of classes is more specific than the second.
     * Assumes that the two arrays are of the same length.
     */
    public static final boolean isMoreSpecific(Class<?>[] classes1, Class<?>[] classes2)
    {
        for (int index = 0, count = classes1.length; index < count; ++index) {
            Class<?> c1 = classes1[index], c2 = classes2[index];
            if (c1 == c2) {
                continue;
            }
            else if (c1.isPrimitive()) {
                return true;
            }
            else if (c1.isAssignableFrom(c2)) {
                return false;
            }
            else if (c2.isAssignableFrom(c1)) {
                return true;
            }
        }

        // They are the same!  So the first is not more specific than the second.
        return false;
    }
}
