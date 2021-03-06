/**
 * Copyright (C) 2011-2018 Red Hat, Inc. (https://github.com/Commonjava/indy)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.indy.metrics;

import org.apache.commons.lang3.ClassUtils;
import org.commonjava.indy.measure.annotation.MetricNamed;

import javax.interceptor.InvocationContext;

import static com.codahale.metrics.MetricRegistry.name;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.commonjava.indy.measure.annotation.MetricNamed.DEFAULT;

public class IndyMetricsConstants
{
    public static final String EXCEPTION = "exception";

    public static final String METER = "meter";

    public static final String TIMER = "timer";

    /**
     * Get default metric name. Use abbreviated package name, e.g., foo.bar.ClassA.methodB -> f.b.ClassA.methodB
     */
    public static String getDefaultName( Class<?> declaringClass, String method )
    {
        // minimum len 1 shortens the package name and keeps class name
        String cls = ClassUtils.getAbbreviatedName( declaringClass.getName(), 1 );
        return name( cls, method );
    }

    /**
     * Get the metric fullname.
     * @param name user specified name
     * @param defaultName 'class name + method name', not null.
     */
    public static String getName( String nodePrefix, String name, String defaultName, String suffix )
    {
        if ( isBlank( name ) || name.equals( DEFAULT ) )
        {
            name = defaultName;
        }
        return name( nodePrefix, name, suffix );
    }


}
