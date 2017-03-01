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
package com.beeswax.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * The interface of HTTP request handlers.</br>
 * Each supported end point in the server needs to implement this handler.</br>
 * NOTE: methods in this interface are executed in I/O thread so it has to be 
 * fully asynchronous or finished very quickly.</br>
 */
public interface RequestHandler {

  /**
   * The method to process HTTP request and return a HTTP response to the client.
   * 
   * @param ChannelHandlerContext
   * @param FullHttpRequest
   * 
   * @return FullHttpResponse
   */
  public FullHttpResponse processRequest(ChannelHandlerContext ctx, FullHttpRequest request);
}
