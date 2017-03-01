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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.beeswax.bid.AdcandidateOuterClass.Adcandidate;
import com.beeswax.bid.AdcandidateOuterClass.Bidding.CustomStrategy;
import com.beeswax.bid.AdcandidateOuterClass.Bidding.CustomStrategy.Params;
import com.beeswax.openrtb.Openrtb.BidRequest;

/**
 * Flat price strategy sets a flat price for all bid requests.</br>
 * If there are multiple flat prices in {@link Params}, it simply uses the first one
 * when iterating through the param list.</br>
 * 
 */
public class FlatPriceStrategy implements BidStrategy {
  private static final Logger LOGGER = LogManager.getLogger(FlatPriceStrategy.class);
  private static final String FLAT_PRICE_KEY = "flat_price_micros_usd";
  public static final String STRATEGY_NAME = "FLAT_PRICE_STRATEGY";

  /**
   * Return a flat rate from custom strategy parameter.</br>
   * 
   * @param bid agent request
   * @param bid request
   * @return bid price in micros USD
   */
  public long getBidPriceMicrosUSD(Adcandidate candidate, BidRequest request) {
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
                   .equalsIgnoreCase(FLAT_PRICE_KEY)) {
            try {
              return Long.parseLong(param.getValue());
            } catch (NumberFormatException e) {
              LOGGER.error("Invalid flat price : {}", param.getValue(), e);
              // continue because there might be valid flat price in param pairs.
              continue;
            }
          }
        }
      }
    }

    LOGGER.error("No flat price found for FlatPricestrategy. setting price to 0.");
    return 0L;
  }
}
