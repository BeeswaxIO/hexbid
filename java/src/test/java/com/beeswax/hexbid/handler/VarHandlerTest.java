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

import com.beeswax.hexbid.handler.VarHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class VarHandlerTest {

  @Test
  public void processRequestTest() {
    final FullHttpRequest request = Mockito.mock(FullHttpRequest.class);
    Mockito.when(request.uri())
           .thenReturn("/var?");
    final ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);

    final VarHandler handler = new VarHandler();
    final FullHttpResponse response = handler.processRequest(ctx, request);
    Assert.assertEquals(HttpVersion.HTTP_1_1, response.protocolVersion());
    Assert.assertEquals(HttpResponseStatus.OK, response.status());
    Assert.assertEquals("var", response.content()
                                       .toString(Charset.forName("UTF-8")));
  }
}
