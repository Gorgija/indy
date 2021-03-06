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
package org.commonjava.indy.subsys.infinispan.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.codahale.metrics.MetricRegistry.name;

public class IspnCheckRegistrySet
                implements MetricSet
{
    public static final String INDY_METRIC_ISPN = "indy.ispn";

    private static final String SIZE = "size";

    private static final String CURRENT_NUMBER_OF_ENTRIES = "CurrentNumberOfEntries";

    private static final String NUMBER_OF_ENTRIES_ADDED = "NumberOfEntriesAdded";

    private static final String CURRENT_NUMBER_OF_ENTRIES_IN_MEMORY = "CurrentNumberOfEntriesInMemory";

    private static final String TOTAL_NUMBER_OF_ENTRIES = "TotalNumberOfEntries";

    private static final String HITS = "Hits";

    private static final String MISSES = "Misses";

    private static final String RETRIEVALS = "Retrievals";

    private static final String EVICTIONS = "Evictions";

    private static final String REMOVALS = "Removals";

    private static final String TOTAL_HITS = "TotalHits";

    private static final String TOTAL_MISSES = "TotalMisses";

    private static final String TOTAL_RETRIEVALS = "TotalRetrievals";

    private static final String TOTAL_EVICTIONS = "TotalEvictions";

    private static final String TOTAL_REMOVALS = "TotalRemovals";

    private EmbeddedCacheManager cacheManager;

    private List<String> ispnGauges;

    public IspnCheckRegistrySet( EmbeddedCacheManager cacheManager, List<String> ispnGauges )
    {
        this.cacheManager = cacheManager;
        this.ispnGauges = ispnGauges;
    }

    @Override
    public Map<String, Metric> getMetrics()
    {
        final Map<String, Metric> gauges = new HashMap<String, Metric>();
        Set<String> names = cacheManager.getCacheNames();
        names.forEach( n ->
           {
               Cache<Object, Object> cache = cacheManager.getCache( n );
               AdvancedCache<Object, Object> advancedCache = cache.getAdvancedCache();

               gauges.put( name( cache.getName(), SIZE ), (Gauge) () -> advancedCache.size() ); // default

               // These give the current sizes of the cache.
               if ( ispnGauges == null || ispnGauges.contains( CURRENT_NUMBER_OF_ENTRIES ) )
               {
                   gauges.put( name( cache.getName(), CURRENT_NUMBER_OF_ENTRIES ),
                               (Gauge) () -> advancedCache.getStats().getCurrentNumberOfEntries() );
               }
               if ( ispnGauges == null || ispnGauges.contains( TOTAL_NUMBER_OF_ENTRIES ) )
               {
                   gauges.put( name( cache.getName(), TOTAL_NUMBER_OF_ENTRIES ),
                               (Gauge) () -> advancedCache.getStats().getTotalNumberOfEntries() );
               }
               if ( ispnGauges == null || ispnGauges.contains( TOTAL_HITS ) )
               {
                   gauges.put( name( cache.getName(), TOTAL_HITS ),
                               (Gauge) () -> advancedCache.getStats().getHits() );
               }
               if ( ispnGauges == null || ispnGauges.contains( TOTAL_MISSES ) )
               {
                   gauges.put( name( cache.getName(), TOTAL_MISSES ),
                               (Gauge) () -> advancedCache.getStats().getMisses() );
               }
               if ( ispnGauges == null || ispnGauges.contains( TOTAL_RETRIEVALS ) )
               {
                   gauges.put( name( cache.getName(), TOTAL_RETRIEVALS ),
                               (Gauge) () -> advancedCache.getStats().getRetrievals() );
               }
               if ( ispnGauges == null || ispnGauges.contains( TOTAL_EVICTIONS ) )
               {
                   gauges.put( name( cache.getName(), TOTAL_EVICTIONS ),
                               (Gauge) () -> advancedCache.getStats().getEvictions() );
               }
               if ( ispnGauges == null || ispnGauges.contains( TOTAL_REMOVALS ) )
               {
                   gauges.put( name( cache.getName(), TOTAL_REMOVALS ),
                               (Gauge) () -> advancedCache.getStats().getRemoveHits() );
               }

               // The rest of these should show the RATES at which the cache is changing, or is being used.
               if ( ispnGauges == null || ispnGauges.contains( NUMBER_OF_ENTRIES_ADDED ) )
               {
                   gauges.put( name( cache.getName(), NUMBER_OF_ENTRIES_ADDED ),
                               new RecentCountGauge( () -> (float) advancedCache.getStats().getCurrentNumberOfEntries() ) );
               }
               if ( ispnGauges == null || ispnGauges.contains( HITS ) )
               {
                   gauges.put( name( cache.getName(), HITS ),
                               new RecentCountGauge( () -> (float) advancedCache.getStats().getHits() ) );
               }
               if ( ispnGauges == null || ispnGauges.contains( MISSES ) )
               {
                   gauges.put( name( cache.getName(), MISSES ),
                               new RecentCountGauge( () -> (float) advancedCache.getStats().getMisses() ) );
               }
               if ( ispnGauges == null || ispnGauges.contains( RETRIEVALS ) )
               {
                   gauges.put( name( cache.getName(), RETRIEVALS ),
                               new RecentCountGauge( () -> (float) advancedCache.getStats().getRetrievals() ) );
               }
               if ( ispnGauges == null || ispnGauges.contains( EVICTIONS ) )
               {
                   gauges.put( name( cache.getName(), EVICTIONS ),
                               new RecentCountGauge( () -> (float) advancedCache.getStats().getEvictions() ) );
               }
               if ( ispnGauges == null || ispnGauges.contains( REMOVALS ) )
               {
                   gauges.put( name( cache.getName(), REMOVALS ),
                               new RecentCountGauge( () -> (float) advancedCache.getStats().getRemoveHits() ) );
               }
           } );
        return gauges;
    }

    private static final class RecentCountGauge implements Gauge<Float>
    {

        private Supplier<Float> supplier;

        private float lastSize = 0L;
        private long lastTime = System.currentTimeMillis();

        private RecentCountGauge( Supplier<Float> supplier )
        {
            this.supplier = supplier;
        }

        @Override
        public Float getValue()
        {
            float next = supplier.get();
            float ret = next - lastSize;
            lastSize = next;

            long nextTime = System.currentTimeMillis();
            float rate = ret / ((nextTime - lastTime) / 1000);
            lastTime = nextTime;

            return rate;
        }
    }
}
