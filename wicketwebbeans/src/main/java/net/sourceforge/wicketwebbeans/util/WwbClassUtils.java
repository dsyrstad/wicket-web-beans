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
package net.sourceforge.wicketwebbeans.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;

/**
 * Static Class utilities. 
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
        return (Method)findMostSpecificMethodOrConstructor(aClass.getMethods(), aClass, aMethodName, someArgTypes);
    }

    /**
     * Gets the most specific method or constructor to be called for the given class, method name and arguments types.
     * It attempts to find the method with the most "specific" argument type match (in JLS terms). 
     * If successful this method will return the Method that can be called, otherwise null.
     * 
     * @param methodName if non-null the name of the method to find. Otherwise a constructor is assumed.
     */
    private static AccessibleObject findMostSpecificMethodOrConstructor(AccessibleObject[] someAccessibleObjects,
                    Class<?> aClass, String aMethodName, Class<?>... someArgTypes)
    {
        AccessibleObject result = null;
        int resultDistance = 10000;

        for (AccessibleObject accessibleObject : someAccessibleObjects) {
            Class<?>[] paramTypes;
            if (aMethodName == null) {
                paramTypes = ((Constructor<?>)accessibleObject).getParameterTypes();
            }
            else {
                Method method = (Method)accessibleObject;
                if (!method.getName().equals(aMethodName)) {
                    continue;
                }

                paramTypes = method.getParameterTypes();
            }

            if (areArgsCompatible(someArgTypes, paramTypes)) {
                int distance = computeDistance(someArgTypes, paramTypes);
                if (distance < resultDistance) {
                    result = accessibleObject;
                    resultDistance = distance;
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
        return (Constructor<?>)findMostSpecificMethodOrConstructor(aClass.getConstructors(), aClass, null, someArgTypes);
    }

    /**
     * Tells whether the given array of types are compatible with the given array of
     * target types -- that is, whether the given array of objects can be passed as arguments
     * to a method or constructor whose parameter types are the given array of classes.
     */
    private static final boolean areArgsCompatible(Class<?>[] someArgTypes, Class<?>[] targetArgTypes)
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
     * 
     * @param aType the type being sent to aTargetType. May be null.
     * @param aTargetType the desired type. Must not be null.
     */
    private static final boolean isTypeCompatible(Class<?> aType, Class<?> aTargetType)
    {
        // aType -> aTargetType
        // - int -> Integer
        // - long -> long
        // - Integer -> int (because Integer implies that the argument is not null)
        // - Object -> Object
        // - SomeObject -> Object
        // - null -> Object
        // - null -! int

        if (aType == null) {
            // nulls can be set on non-primitive types.
            return !aTargetType.isPrimitive();
        }

        if (aType == aTargetType) {
            return true;
        }

        if (aType.isPrimitive()) {
            return ClassUtils.wrapperToPrimitive(aTargetType) == aType;
        }

        if (aTargetType.isPrimitive()) {
            return ClassUtils.wrapperToPrimitive(aType) == aTargetType;
        }

        return aTargetType.isAssignableFrom(aType);
    }

    /**
     * Determines the "distance" of someArgs from someTargetArgs. The two arrays must be the same length
     * and it is assumed that they are already "compatible".
     * 
     * @param someArgs potential arg types. Some elements may be null.
     * @param someTargetArgs target method arg types. No elements will be null.. 
     */
    private static final int computeDistance(Class<?>[] someArgs, Class<?>[] someTargetArgs)
    {
        assert someArgs.length == someTargetArgs.length;

        int distance = 0;
        for (int i = 0; i < someArgs.length; ++i) {
            Class<?> argType = someArgs[i];
            Class<?> targetType = someTargetArgs[i];

            distance += getTransformationCost(argType, targetType);
        }

        return distance;
    }

    /**
     * Gets the number of steps required needed to turn the source class into the 
     * destination class. This represents the number of steps in the object hierarchy 
     * graph.<p/>
     * Derived from Apache Commons BeanUtils {@link MethodUtils#getObjectTransformationCost}.
     * 
     * @param srcClass The source class
     * @param destClass The destination class
     * @return The cost of transforming an object
     */
    private static int getTransformationCost(Class<?> srcClass, Class<?> destClass)
    {
        int cost = 0;
        while (destClass != null && !destClass.equals(srcClass)) {
            if (destClass.isInterface()) {
                // slight penalty for interface match. 
                // we still want an exact match to override an interface match, but  
                // an interface match should override anything where we have to get a 
                // superclass.
                ++cost;
                break;
            }

            cost += 4;
            destClass = destClass.getSuperclass();
        }

        /*
         * If the destination class is null, we've traveled all the way up to 
         * an Object match. We'll penalize this by adding 6 to the cost.
         */
        if (destClass == null) {
            cost += 6;
        }

        return cost;
    }

    /**
     * Invokes a constructor on aClass that has the same number of arguments as args.
     * However, elements of args do not need to match the property types
     * of the constructor. They will be converted using convertUtilsBean
     *
     * @param aClass
     * @param args
     * @param convertUtilsBean
     * 
     * @return the constructed object.
     * @throws Exception if an error occurs constructor the object. 
     */
    // TODO Test
    public static Object invokeConstructorWithArgConversion(Class<?> aClass, Object[] args,
                    ConvertUtilsBean convertUtilsBean) throws Exception
    {
        Constructor<?> constructor = null;
        for (Constructor<?> testConstructor : aClass.getConstructors()) {
            if (testConstructor.getParameterTypes().length == args.length) {
                constructor = testConstructor;
                break;
            }
        }

        if (constructor == null) {
            throw new RuntimeException("Cannot find constructor matching args: " + ArrayUtils.toString(args));
        }

        Class<?>[] parameterTypes = constructor.getParameterTypes();
        assert parameterTypes.length == args.length;
        Object[] newArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            newArgs[i] = convertUtilsBean.convert(args[i], parameterTypes[i]);
        }

        return constructor.newInstance(newArgs);
    }

    public static Object invokeStaticMethodWithArgConversion(Class<?> aClass, String methodName, Object[] args,
                    ConvertUtilsBean convertUtilsBean) throws Exception
    {
        Method staticMethod = null;
        for (Method testMethod : aClass.getMethods()) {
            if (testMethod.getName().equals(methodName) && testMethod.getParameterTypes().length == args.length
                            && (testMethod.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                staticMethod = testMethod;
                break;
            }
        }

        if (staticMethod == null) {
            throw new RuntimeException("Cannot find static method '" + methodName + "' matching args: "
                            + ArrayUtils.toString(args));
        }

        Class<?>[] parameterTypes = staticMethod.getParameterTypes();
        assert parameterTypes.length == args.length;
        Object[] newArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            newArgs[i] = convertUtilsBean.convert(args[i], parameterTypes[i]);
        }

        return staticMethod.invoke(null, newArgs);
    }
}
