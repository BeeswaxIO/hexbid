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
package com.beeswax.hexbid.handler;

import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.beeswax.bid.AdcandidateOuterClass.Adcandidate;
import com.beeswax.bid.AdcandidateOuterClass.Bidding;
import com.beeswax.bid.AdcandidateOuterClass.Bidding.CustomStrategy;
import com.beeswax.bid.AdcandidateOuterClass.Bidding.CustomStrategy.Params;
import com.beeswax.bid.Request.BidAgentRequest;
import com.beeswax.bid.Request.BidAgentResponse;
import com.beeswax.hexbid.handler.BidHandler;
import com.beeswax.hexbid.parser.BidProtobufParser;
import com.beeswax.hexbid.strategy.RetargetingStrategy;
import com.beeswax.openrtb.Extension.UserExtensions;
import com.beeswax.openrtb.Openrtb.BidRequest;
import com.beeswax.openrtb.Openrtb.BidRequest.User;
import com.beeswax.hexbid.strategy.FlatPriceStrategy;
import com.beeswax.hexbid.strategy.RandomPriceStrategy;
import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class BidHandlerTest {

  @Test
  public void processRequestTest_NoAdcandidate() {
    final BidAgentRequest.Builder requestBuilder = BidAgentRequest.newBuilder();
    final ByteBuf requestByteBuf = Unpooled.wrappedBuffer(requestBuilder.build()
                                                                        .toByteArray());
    final FullHttpRequest request = Mockito.mock(FullHttpRequest.class);
    Mockito.when(request.content())
           .thenReturn(requestByteBuf);

    final ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
    final BidHandler handler = new BidHandler();
    final FullHttpResponse response = handler.processRequest(ctx, request);
    Assert.assertEquals(HttpResponseStatus.NO_CONTENT, response.status());
    Assert.assertEquals(0, response.content()
                                   .readableBytes());
  }

  @Test
  public void processRequestTest_InvalidProto() {
    final FullHttpRequest request = Mockito.mock(FullHttpRequest.class);
    Mockito.when(request.content())
           .thenReturn(Unpooled.wrappedBuffer("Invalid proto".getBytes()));

    final ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
    final BidHandler handler = new BidHandler();
    final FullHttpResponse response = handler.processRequest(ctx, request);
    Assert.assertEquals(HttpResponseStatus.BAD_REQUEST, response.status());
    Assert.assertEquals("Bad request", response.content()
                                               .toString(Charset.forName("UTF-8")));
  }

  @Test
  public void processRequestTest_FlatPriceStrategy() {
    final BidAgentRequest.Builder requestBuilder = BidAgentRequest.newBuilder();
    final Adcandidate.Builder adcandidateBuilder = Adcandidate.newBuilder();
    final Bidding.Builder biddingBuilder = Bidding.newBuilder();
    final CustomStrategy.Builder strategyBuilder = CustomStrategy.newBuilder();
    strategyBuilder.setName(FlatPriceStrategy.STRATEGY_NAME);

    final Params.Builder params = Params.newBuilder();
    params.setKey("flat_price_micros_usd");
    params.setValue("866");
    strategyBuilder.addCustomParams(params);

    biddingBuilder.setCustomStrategy(strategyBuilder.buildPartial());
    adcandidateBuilder.setBidding(biddingBuilder.buildPartial());
    adcandidateBuilder.setLineItemId(111);
    adcandidateBuilder.addCreativeIds(33);
    requestBuilder.addAdcandidates(adcandidateBuilder.buildPartial());

    final ByteBuf requestByteBuf = Unpooled.wrappedBuffer(requestBuilder.build()
                                                                        .toByteArray());

    final FullHttpRequest request = Mockito.mock(FullHttpRequest.class);
    Mockito.when(request.content())
           .thenReturn(requestByteBuf);

    final ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
    final BidHandler handler = new BidHandler();
    final FullHttpResponse bytebufRsponse = handler.processRequest(ctx, request);
    try {
      final BidAgentResponse protoResponse =
          (BidAgentResponse) BidProtobufParser.parseProtoBytebuf(bytebufRsponse.content(),
              BidAgentResponse.newBuilder());
      Assert.assertEquals(1, protoResponse.getBidsCount());
      Assert.assertEquals(33, protoResponse.getBids(0)
                                           .getCreative()
                                           .getId());
      Assert.assertEquals(111, protoResponse.getBids(0)
                                            .getLineItemId());
      Assert.assertEquals(866, protoResponse.getBids(0)
                                            .getBidPriceMicros());

    } catch (InvalidProtocolBufferException e) {
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void processRequestTest_RandomPriceStrategy() {
    final BidAgentRequest.Builder requestBuilder = BidAgentRequest.newBuilder();
    final Adcandidate.Builder adcandidateBuilder = Adcandidate.newBuilder();
    final Bidding.Builder biddingBuilder = Bidding.newBuilder();
    final CustomStrategy.Builder strategyBuilder = CustomStrategy.newBuilder();
    strategyBuilder.setName(RandomPriceStrategy.STRATEGY_NAME);

    final Params.Builder params = Params.newBuilder();
    params.setKey("max_price_micros_usd");
    params.setValue("1200");
    strategyBuilder.addCustomParams(params);

    biddingBuilder.setCustomStrategy(strategyBuilder.buildPartial());
    adcandidateBuilder.setBidding(biddingBuilder.buildPartial());
    adcandidateBuilder.setLineItemId(34);
    adcandidateBuilder.addCreativeIds(13814);

    requestBuilder.addAdcandidates(adcandidateBuilder.buildPartial());

    final ByteBuf requestByteBuf = Unpooled.wrappedBuffer(requestBuilder.build()
                                                                        .toByteArray());

    final FullHttpRequest request = Mockito.mock(FullHttpRequest.class);
    Mockito.when(request.content())
           .thenReturn(requestByteBuf);

    final ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
    final BidHandler handler = new BidHandler();
    final FullHttpResponse bytebufRsponse = handler.processRequest(ctx, request);
    try {
      final BidAgentResponse protoResponse =
          (BidAgentResponse) BidProtobufParser.parseProtoBytebuf(bytebufRsponse.content(),
              BidAgentResponse.newBuilder());
      Assert.assertEquals(1, protoResponse.getBidsCount());
      Assert.assertEquals(13814, protoResponse.getBids(0)
                                              .getCreative()
                                              .getId());
      Assert.assertEquals(34, protoResponse.getBids(0)
                                           .getLineItemId());
      Assert.assertTrue(protoResponse.getBids(0)
                                     .getBidPriceMicros() <= protoResponse.getBids(0)
                                                                          .getBidPriceMicros());
      Assert.assertTrue(protoResponse.getBids(0)
                                     .getBidPriceMicros() > 0);
    } catch (InvalidProtocolBufferException e) {
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void processRequestTest_RetargetingStrategy() {
    final BidAgentRequest.Builder requestBuilder = BidAgentRequest.newBuilder();
    final Adcandidate.Builder adcandidateBuilder = Adcandidate.newBuilder();
    final Bidding.Builder biddingBuilder = Bidding.newBuilder();
    final CustomStrategy.Builder strategyBuilder = CustomStrategy.newBuilder();
    strategyBuilder.setName(RetargetingStrategy.STRATEGY_NAME);

    final Params.Builder params = Params.newBuilder();
    params.setKey("base_price_micros_usd");
    params.setValue("100");
    strategyBuilder.addCustomParams(params);

    params.setKey("param_2");
    params.setValue("999");
    strategyBuilder.addCustomParams(params);

    biddingBuilder.setCustomStrategy(strategyBuilder.buildPartial());
    adcandidateBuilder.setBidding(biddingBuilder.buildPartial());
    adcandidateBuilder.setLineItemId(999);
    adcandidateBuilder.addCreativeIds(888);

    requestBuilder.addAdcandidates(adcandidateBuilder.buildPartial());

    final UserExtensions userExt = UserExtensions.newBuilder()
                                                 .setUserId("BITO.123456")
                                                 .buildPartial();
    final User user = User.newBuilder()
                          .setExt(userExt)
                          .buildPartial();
    requestBuilder.setBidRequest(BidRequest.newBuilder()
                                           .setUser(user)
                                           .buildPartial());
    final ByteBuf requestByteBuf = Unpooled.wrappedBuffer(requestBuilder.buildPartial()
                                                                        .toByteArray());

    final FullHttpRequest request = Mockito.mock(FullHttpRequest.class);
    Mockito.when(request.content())
           .thenReturn(requestByteBuf);

    final ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);
    final BidHandler handler = new BidHandler();
    final FullHttpResponse bytebufRsponse = handler.processRequest(ctx, request);
    try {
      final BidAgentResponse protoResponse =
          (BidAgentResponse) BidProtobufParser.parseProtoBytebuf(bytebufRsponse.content(),
              BidAgentResponse.newBuilder());
      Assert.assertEquals(1, protoResponse.getBidsCount());
      Assert.assertEquals(888, protoResponse.getBids(0)
                                            .getCreative()
                                            .getId());
      Assert.assertEquals(999, protoResponse.getBids(0)
                                            .getLineItemId());
      Assert.assertEquals(1100, protoResponse.getBids(0)
                                             .getBidPriceMicros());
    } catch (InvalidProtocolBufferException e) {
      Assert.fail(e.getMessage());
    }
  }
}
