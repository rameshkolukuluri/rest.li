/*
   Copyright (c) 2016 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.linkedin.d2.balancer.strategies.degrader;

import com.linkedin.d2.balancer.util.hashing.Ring;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Ang Xu
 */
public class DegraderRingFactory<T> implements RingFactory<T>
{
  public static final String POINT_BASED_CONSISTENT_HASH = "pointBased";
  public static final String MULTI_PROBE_CONSISTENT_HASH = "multiProbe";
  private static final Logger _log = LoggerFactory.getLogger(DegraderRingFactory.class);

  private final RingFactory<T> _ringFactory;

  public DegraderRingFactory(DegraderLoadBalancerStrategyConfig config)
  {
    String consistentHashAlgorithm = config.getConsistentHashAlgorithm();
    if (consistentHashAlgorithm == null || consistentHashAlgorithm.equalsIgnoreCase(POINT_BASED_CONSISTENT_HASH))
    {
      _ringFactory = new PointBasedConsistentHashRingFactory<>(config);
    }
    else if (MULTI_PROBE_CONSISTENT_HASH.equalsIgnoreCase(consistentHashAlgorithm))
    {
      _ringFactory = new MPConsistentHashRingFactory<>(config.getNumProbes());
    }
    else
    {
      _log.warn("Unknown consistent hash algorithm {}, falling back to point-based hash ring", consistentHashAlgorithm);
      _ringFactory = new PointBasedConsistentHashRingFactory<>(config);
    }
  }

  @Override
  public Ring<T> createRing(Map<T, Integer> points) {
    return _ringFactory.createRing(points);
  }
}
