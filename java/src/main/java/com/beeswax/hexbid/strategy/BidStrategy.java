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
import com.beeswax.openrtb.Openrtb.BidRequest;

/**
 * The interface of bidding strategy.</br>
 * Each bidding strategy needs to implement this interface to define its own bidding logic.</br>
 * Custom bidder only supports {@link CustomStrategy} which is defined using Buzz API. Customers can associate multiple
 * keys, values with a strategy that are made available in the matching adcandidate so that they can implement their own
 * optimization.</br>
 *
 */
public interface BidStrategy {

  /**
   * The method to set bid price with each bidding strategy.
   * 
   * @param adcandidate
   * @param bid request
   * 
   * @return bid price in micros USD
   */
  public long getBidPriceMicrosUSD(Adcandidate adcandidate, BidRequest request);
}
