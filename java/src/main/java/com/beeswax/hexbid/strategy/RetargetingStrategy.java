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
import com.beeswax.openrtb.Extension.UserExtensions;
import com.beeswax.openrtb.Openrtb.BidRequest;
import com.google.common.base.Optional;

/**
 * Retargeting Strategy assumes custom bidder maintains a key-value store which has a
 * score indicating the importance of a user. It sets the price by multiplying the score with
 * a base price from strategy parameter list.</br>
 *
 */
public class RetargetingStrategy implements BidStrategy {
  private static final Logger LOGGER = LogManager.getLogger(RetargetingStrategy.class);
  private static final String BASE_PRICE_KEY = "base_price_micros_usd";
  public static final String STRATEGY_NAME = "RETARGETING_STRATEGY";

  /**
   * Get the bid price by multiplying user score that is available from bidder's key-value store with a
   * base price.</br>
   * 
   * @param adcandidate
   * @param bid request
   * @return bid price in micros USD
   */
  public long getBidPriceMicrosUSD(Adcandidate adcandidate, BidRequest request) {
    if (adcandidate.hasBidding() && adcandidate.getBidding()
                                               .hasCustomStrategy()) {
      final CustomStrategy customStrategy = adcandidate.getBidding()
                                                       .getCustomStrategy();
      if (customStrategy.getName()
                        .equalsIgnoreCase(STRATEGY_NAME)) {
        final Optional<String> userId = getUserId(request);
        Optional<Long> basePrice = Optional.absent();

        for (Params param : adcandidate.getBidding()
                                       .getCustomStrategy()
                                       .getCustomParamsList()) {
          if (param.getKey()
                   .equalsIgnoreCase(BASE_PRICE_KEY)) {
            try {
              basePrice = Optional.of(Long.parseLong(param.getValue()));
            } catch (NumberFormatException e) {
              LOGGER.error("Base price is in bad format : {}", param.getValue());
              // continue to find base price in param pairs.
              continue;
            }
          }
        }

        if (userId.isPresent() && basePrice.isPresent()) {
          return getUserScore(userId) * basePrice.get();
        }
      }
    }

    LOGGER.error("Setting price to 0 because user or base price is missing for RetargetingStrategy.");
    return 0;
  }

  /**
   * Get user ID from {@link UserExtensions#getUserId()}.
   *
   * @param request
   * @return Optional user id.
   */
  private Optional<String> getUserId(BidRequest request) {
    if (request.hasUser() && request.getUser().hasExt()) {
      return request.getUser()
                    .getExt()
                    .hasUserId()
                        ? Optional.of(request.getUser()
                                             .getExt()
                                             .getUserId())
                        : Optional.<String>absent();
    }
    return Optional.absent();
  }

  /**
   * Get user score from key-value store based in user id. If user id is absent, score is 0.
   *
   * @param userId
   * @return score
   */
  private Long getUserScore(Optional<String> userId) {
    if (!userId.isPresent()) {
      return 0L;
    }
    // ...
    // user score lookup logic goes here
    // ...
    // for simplicity just set the score to be user id length.
    final Long score = (long) userId.get().length();
    return score;
  }
}

