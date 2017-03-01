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
package com.beeswax.hexbid.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

/**
 * BidRequestParser parses serialized protocol buffer Bytebuf.
 *
 */
public class BidProtobufParser {

  /**
   * Parse serialized protocol buffer Bytebuf to protobuf object.</br>
   * Preferencing implementation of {@link ProtobufDecoder}
   * 
   * @param bytebuf
   * @return protocol buffer message
   * @throws InvalidProtocolBufferException
   */
  public static <T extends Message.Builder> Message parseProtoBytebuf(ByteBuf bytebuf,
      T messageBuilder)
      throws InvalidProtocolBufferException {
    final byte[] array;
    final int offset;
    final int length = bytebuf.readableBytes();
    if (bytebuf.hasArray()) {
        array = bytebuf.array();
        offset = bytebuf.arrayOffset() + bytebuf.readerIndex();
    } else {
        array = new byte[length];
        bytebuf.getBytes(bytebuf.readerIndex(), array, 0, length);
        offset = 0;
    }
    return messageBuilder.mergeFrom(array, offset, length).buildPartial();
  }
}
