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

import org.junit.Assert;
import org.junit.Test;

import com.beeswax.bid.AdcandidateOuterClass.Adcandidate;
import com.beeswax.bid.AdcandidateOuterClass.Bidding;
import com.beeswax.bid.AdcandidateOuterClass.Bidding.CustomStrategy;
import com.beeswax.bid.AdcandidateOuterClass.Bidding.CustomStrategy.Params;
import com.beeswax.openrtb.Openrtb.BidRequest;
import com.beeswax.openrtb.Openrtb.BidRequest.User;

public class RandomPriceStrategyTest {

  @Test
  public void getBidPriceTest() {
    final Adcandidate.Builder adcandidateBuilder = Adcandidate.newBuilder();
    final Bidding.Builder biddingBuilder = Bidding.newBuilder();
    final CustomStrategy.Builder strategyBuilder = CustomStrategy.newBuilder();
    strategyBuilder.setName(RandomPriceStrategy.STRATEGY_NAME);

    final Params.Builder params = Params.newBuilder();
    params.setKey("max_price_micros_usd");
    params.setValue("123");
    strategyBuilder.addCustomParams(params);

    params.setKey("max_price_micros_usd");
    params.setValue("399");
    strategyBuilder.addCustomParams(params);

    biddingBuilder.setCustomStrategy(strategyBuilder.buildPartial());
    final Adcandidate adcandidate = adcandidateBuilder.setBidding(biddingBuilder.buildPartial())
                                                      .buildPartial();
    final User user = User.newBuilder()
                          .setId("BITO.123456")
                          .buildPartial();
    final BidRequest bidRequest = BidRequest.newBuilder()
                                            .setUser(user)
                                            .buildPartial();

    final RandomPriceStrategy customStrategy = new RandomPriceStrategy();
    for (int attempt = 0; attempt < 10; attempt++) {
      Long bidPrice = customStrategy.getBidPriceMicrosUSD(adcandidate, bidRequest);
      Assert.assertTrue(bidPrice <= 123);
      Assert.assertTrue(bidPrice > 0);
    }
  }
}
