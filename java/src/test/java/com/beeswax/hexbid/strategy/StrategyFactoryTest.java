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
import org.mockito.Mockito;

import com.beeswax.bid.AdcandidateOuterClass.Adcandidate;
import com.beeswax.bid.AdcandidateOuterClass.Bidding;
import com.beeswax.bid.AdcandidateOuterClass.Bidding.CustomStrategy;

public class StrategyFactoryTest {

  @Test
  public void getStrategyTest_RandomPriceStrategy() {
    final Adcandidate.Builder adcandidateBuilder = Adcandidate.newBuilder();
    final Bidding.Builder biddingBuilder = Bidding.newBuilder();
    final CustomStrategy.Builder strategyBuilder = CustomStrategy.newBuilder();
    strategyBuilder.setName(RandomPriceStrategy.STRATEGY_NAME);
    biddingBuilder.setCustomStrategy(strategyBuilder.buildPartial());
    final Adcandidate adcandidate = adcandidateBuilder.setBidding(biddingBuilder.buildPartial())
                                                      .buildPartial();

    final StrategyFactory factory = new StrategyFactory();
    Assert.assertTrue(factory.getStrategy(adcandidate) instanceof RandomPriceStrategy);
  }
  
  @Test
  public void getStrategyTest_FlatPriceStrategy() {
    final Adcandidate.Builder adcandidateBuilder = Adcandidate.newBuilder();
    final Bidding.Builder biddingBuilder = Bidding.newBuilder();
    final CustomStrategy.Builder strategyBuilder = CustomStrategy.newBuilder();
    strategyBuilder.setName(FlatPriceStrategy.STRATEGY_NAME);
    biddingBuilder.setCustomStrategy(strategyBuilder.buildPartial());
    final Adcandidate adcandidate = adcandidateBuilder.setBidding(biddingBuilder.buildPartial())
                                                      .buildPartial();

    final StrategyFactory factory = new StrategyFactory();
    Assert.assertTrue(factory.getStrategy(adcandidate) instanceof FlatPriceStrategy);
  }
  
  @Test
  public void getStrategyTest_RetargetingStrategy() {
    final Adcandidate.Builder adcandidateBuilder = Adcandidate.newBuilder();
    final Bidding.Builder biddingBuilder = Bidding.newBuilder();
    final CustomStrategy.Builder strategyBuilder = CustomStrategy.newBuilder();
    strategyBuilder.setName(RetargetingStrategy.STRATEGY_NAME);
    biddingBuilder.setCustomStrategy(strategyBuilder.buildPartial());
    final Adcandidate adcandidate = adcandidateBuilder.setBidding(biddingBuilder.buildPartial())
                                                      .buildPartial();

    final StrategyFactory factory = new StrategyFactory();
    Assert.assertTrue(factory.getStrategy(adcandidate) instanceof RetargetingStrategy);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void getStrategyTest_Invalid() {
    final Adcandidate.Builder adcandidateBuilder = Adcandidate.newBuilder();
    final Bidding bidding = Bidding.newBuilder()
                                   .buildPartial();
    adcandidateBuilder.setBidding(bidding);

    final StrategyFactory factory = new StrategyFactory();
    Mockito.verify(factory.getStrategy(adcandidateBuilder.buildPartial()));
  }
}
