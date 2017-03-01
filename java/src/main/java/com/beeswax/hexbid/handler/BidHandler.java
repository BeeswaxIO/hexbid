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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.beeswax.bid.Request.BidAgentRequest;
import com.beeswax.bid.Request.BidAgentResponse;
import com.beeswax.hexbid.bidder.Bidder;
import com.beeswax.hexbid.parser.BidProtobufParser;
import com.beeswax.http.handler.RequestHandler;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;

/**
 * The handler handles all requests to /bid end point</br>
 * Expects a serialized {@link BidAgentRequest} and returns a {@link BidAgentResponse}<br/>
 *
 */
public class BidHandler implements RequestHandler {
  private static final Logger LOGGER = LogManager.getLogger(BidHandler.class);
  public static final String PATH = "/bid";
  private Bidder bidder;

  public BidHandler() {
    this(new Bidder());
  }
  
  @VisibleForTesting
  BidHandler(Bidder bidder) {
    this.bidder = bidder;
  }

  /**
   * 
   * Process full bid request with following error codes:</br>
   * </br>
   * 200 if it sets bid price in {@link BidAgentResponse} successfully.</br>
   * 204 if no bid is made for this request</br>
   * 400 if there is a parsing error {@link BidAgentRequest} or it fails to get bidding strategy.</br>
   * 500 if server experienced an error.</br>
   * 
   * @param ChannelHandlerContext
   * 
   * @return FullHttpResponse
   * 
   */
  public FullHttpResponse processRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
    LOGGER.debug("/bid request");

    try {
      final BidAgentRequest bidRequest =
          (BidAgentRequest) BidProtobufParser.parseProtoBytebuf(request.content(),
              BidAgentRequest.newBuilder());
      final Optional<BidAgentResponse> bidResponse = bidder.SetBid(bidRequest);

      if (!bidResponse.isPresent()) {
        LOGGER.debug("No Bid");
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
      }

      final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
          HttpResponseStatus.OK, Unpooled.wrappedBuffer(bidResponse.get()
                                                                   .toByteArray()));
      response.headers()
              .set(HttpHeaderNames.CONTENT_TYPE, new AsciiString("application/x-protobuf"));
      return response;

    } catch (InvalidProtocolBufferException | IllegalArgumentException e) {
      return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST,
          Unpooled.wrappedBuffer("Bad request".getBytes()));
    } catch (Exception e) {
      LOGGER.error("Unexpected error when setting bid", e);
      return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR,
          Unpooled.wrappedBuffer("Internal error when setting bid".getBytes()));
    }
  }
}
