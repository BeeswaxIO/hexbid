/*******************************************************************************
 * Copyright 2014-2017 BeeswaxIO Corporation.
 * Portions may be licensed to BeeswaxIO Corporation under one or more contributor license agreements.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.beeswax.hexbid.strategy;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.beeswax.bid.AdcandidateOuterClass.Adcandidate;
import com.beeswax.bid.AdcandidateOuterClass.Bidding.CustomStrategy;
import com.beeswax.bid.AdcandidateOuterClass.Bidding.CustomStrategy.Params;
import com.beeswax.openrtb.Openrtb.BidRequest;

/**
 * Random Price Strategy sets a random price no exceeding max price micros.</br>
 * If there are multiple max price in {@link Params}, it simply uses the first one when iterating
 * through param list.</br>
 *
 */
public class RandomPriceStrategy implements BidStrategy {
  private static final Logger LOGGER = LogManager.getLogger(RandomPriceStrategy.class);
  private static final String MAX_PRICE_KEY = "max_price_micros_usd";
  public static final String STRATEGY_NAME = "RANDOM_PRICE_STRATEGY";

  /**
   * Get a random bid price no exceeding a max price micros from custom strategy parameter.</br>
   * 
   * @param Adcandidate
   * @param bid request
   * @return bid price in micros USD
   */
  public long getBidPriceMicrosUSD(Adcandidate candidate, BidRequest request) {
    final Random rand = new Random();
    if (candidate.hasBidding() && candidate.getBidding()
                                           .hasCustomStrategy()) {
      final CustomStrategy customStrategy = candidate.getBidding()
                                                     .getCustomStrategy();
      if (customStrategy.getName()
                        .equalsIgnoreCase(STRATEGY_NAME)) {
        for (Params param : candidate.getBidding()
                                     .getCustomStrategy()
                                     .getCustomParamsList()) {
          if (param.getKey()
                   .equalsIgnoreCase(MAX_PRICE_KEY)) {
            try {
              final int maxPrice = Integer.parseInt(param.getValue());
              return (1 + rand.nextInt(maxPrice));
            } catch (NumberFormatException e) {
              LOGGER.error("Invalid max price : {}", param.getValue(), e);
              // continue to find other valid max price in param pairs.
              continue;
            }
          }
        }
      }
    }

    LOGGER.error("No max price found for RandomPriceStrategy. setting price to 0.");
    return 0L;
  }

}
