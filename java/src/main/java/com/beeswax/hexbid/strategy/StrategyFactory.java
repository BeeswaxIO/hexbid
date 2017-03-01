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

import com.beeswax.bid.AdcandidateOuterClass.Adcandidate;
import com.beeswax.bid.AdcandidateOuterClass.Bidding.CustomStrategy;

/**
 * Strategy Factory is responsible for getting strategy object which sets bid price based on strategy info 
 * from bid agent request.
 *
 */
public class StrategyFactory {
  
  /**
   * Get strategy object for the adcandidate to set its bid price.</br>
   * </br>
   * Customers can associate a bidding strategy with each adcandidate
   * through the Buzz REST API (http://docs.beeswax.com/docs/bidding-strategies).</br>
   * Custom bidding strategy is a powerful framework that can be used to
   * implement sophisticated algorithms. The bidding strategy is identified
   * by a unique name and there can be multiple
   * parameters (up to 5) associated with each strategy.</br>
   * Examples of some very simple strategies are {@link FlatPriceStrategy},
   * {@link RandomPriceStrategy} and {@link RetargetingStrategy}.
   * 
   * @param adcandidate
   * @return Bid Strategy object
   * @throws IllegalArgumentException
   */
  public BidStrategy getStrategy(Adcandidate adcandidate) throws IllegalArgumentException {
    if (adcandidate.hasBidding()) {
      if (adcandidate.getBidding().hasCustomStrategy()) {
        CustomStrategy customStrategy = adcandidate.getBidding().getCustomStrategy();
        if (customStrategy.getName().equalsIgnoreCase(FlatPriceStrategy.STRATEGY_NAME)) {
          return new FlatPriceStrategy();
        } else if (customStrategy.getName().equalsIgnoreCase(RandomPriceStrategy.STRATEGY_NAME)) {
          return new RandomPriceStrategy();
        } else if (customStrategy.getName().equalsIgnoreCase(RetargetingStrategy.STRATEGY_NAME)) {
          return new RetargetingStrategy();
        } else {
          throw new IllegalArgumentException(
              "Unsupported custom strategy : " + customStrategy.getName());
        }
      }
      throw new IllegalArgumentException(
          "No custom strategy found for line item : " + adcandidate.getLineItemId());
    }
    throw new IllegalArgumentException("No Bidding found in Adcandidate message.");
  }
}
