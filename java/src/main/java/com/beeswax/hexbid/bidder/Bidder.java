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
package com.beeswax.hexbid.bidder;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.beeswax.bid.AdcandidateOuterClass.Adcandidate;
import com.beeswax.bid.Request.BidAgentRequest;
import com.beeswax.bid.Request.BidAgentResponse;
import com.beeswax.bid.Request.BidAgentResponse.AgentData;
import com.beeswax.bid.Request.BidAgentResponse.AgentParams;
import com.beeswax.bid.Request.BidAgentResponse.Bid;
import com.beeswax.bid.Request.BidAgentResponse.Creative;
import com.beeswax.hexbid.strategy.RetargetingStrategy;
import com.beeswax.hexbid.strategy.BidStrategy;
import com.beeswax.hexbid.strategy.FlatPriceStrategy;
import com.beeswax.hexbid.strategy.RandomPriceStrategy;
import com.beeswax.hexbid.strategy.StrategyFactory;
import com.beeswax.openrtb.Openrtb.BidRequest;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

/**
 * Bidder sets bid in {@link BidAgentResponse} based on custom strategy name from {@link BidAgentRequest}.</br>
 * </br>
 * Supported custom strategies are the following:</br>
 * Flat Price Strategy - {@link FlatPriceStrategy}</br>
 * Random Price Strategy - {@link RandomPriceStrategy}</br>
 * Retargeting Strategy - {@link RetargetingStrategy}</br>
 *
 */
public class Bidder {
  private static final Logger LOGGER = LogManager.getLogger(Bidder.class);
  private final StrategyFactory strategyFactory;

  public Bidder() {
    this(new StrategyFactory());
  }
  
  @VisibleForTesting
  Bidder(StrategyFactory strategyFactory) {
    this.strategyFactory = strategyFactory;
  }

  /**
   * Set bid in BidAgentResponse including line item id, creative, bid price and bid agent data.
   *  
   * @param request
   * @return BidAgentResponse
   */
  public Optional<BidAgentResponse> SetBid(BidAgentRequest request) {
    final BidAgentResponse.Builder bidResponseBuild = BidAgentResponse.newBuilder();
    
    if (request.getAdcandidatesCount() == 0) {
      return Optional.absent();
    }

    // Iterate through adcandidate list and set bid price in bid response based on its strategy
    for (Adcandidate adcandidate : request.getAdcandidatesList()) {
      final Bid.Builder bidBuilder = Bid.newBuilder();

      // set line item id for the bid
      bidBuilder.setLineItemId(adcandidate.getLineItemId());

      // select creative for this Adcandidate
      final Creative.Builder creativeBuilder = Creative.newBuilder();
      creativeBuilder.setId(getCreativeId(adcandidate));
      bidBuilder.setCreative(creativeBuilder);

      try {
        // Set bid price
        // The currency of bid price set here is assumed to be the value set by the customer via the Buzz REST API.
        // That value is available in real time via the adcandidate.bidding.currency field.
        bidBuilder.setBidPriceMicros(getBidPrice(adcandidate, request.getBidRequest()));
      } catch (IllegalArgumentException e) {
        LOGGER.error("Error getting bidding strategy", e);
        // ignore this one with invalid strategy and continue processing other Adcandidates 
        continue;
      }

      // set agent data which is used for logging custom fields
      bidBuilder.setAgentData(getAgentData(adcandidate));

      // add bid to bid agent response
      bidResponseBuild.addBids(bidBuilder.buildPartial());
    }
    return Optional.of(bidResponseBuild.build());
  }

  /**
   * Get Creative ID selected from an Adcandidate</br>
   * for simplicity we just randomly select one here but your bidder can have more complicated logic for selection.
   * 
   * @return creative id
   */
  private long getCreativeId(Adcandidate adcandidate) {
    final Random rand = new Random();
    return adcandidate.getCreativeIds(rand.nextInt(adcandidate.getCreativeIdsList()
                                                              .size()));
  }

  /**
   * Get bid price in USD micros for an Adcandidate based on its bidding strategy.
   * 
   * @param adcandidate
   * @param bid request
   * @return
   * @throws IllegalArgumentException
   */
  private long getBidPrice(Adcandidate adcandidate, BidRequest request) throws IllegalArgumentException {
    final BidStrategy strategy = strategyFactory.getStrategy(adcandidate);
    return strategy.getBidPriceMicrosUSD(adcandidate, request);
  }

  /**
   * Agent data are some custom attributes of the bid agent which are logged and available
   *  via bid & win logs.</br>
   *  e.g.</br>
   *  If the bidding agent uses a machine learning model to set bid price, some of its attributes
   *  can be passed in via AgentData, like model id.
   *  
   * @param adcandidate
   * @return
   */
  private AgentData getAgentData(Adcandidate adcandidate) {
    final AgentData.Builder agentDataBuilder = AgentData.newBuilder();
    // ...
    // custom logic goes here
    // ...
    // for example, set hexbid version in agent data
    agentDataBuilder.setAgentId("beeswax-hexbid");
    final AgentParams.Builder agentParamsBuilder = AgentParams.newBuilder();
    agentParamsBuilder.setKey("version");
    agentParamsBuilder.setStringValue("1.0.0");
    agentDataBuilder.addAgentParams(agentParamsBuilder.buildPartial());
    return agentDataBuilder.buildPartial();
  }
}
