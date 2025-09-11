package com.adaptris.samples;

import org.junit.jupiter.api.Test;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageFactory;
import com.adaptris.core.StandaloneProducer;
import com.adaptris.interlok.junit.scaffolding.ExampleProducerCase;
import com.adaptris.interlok.junit.scaffolding.services.ExampleServiceCase;

public class EchoProducerTest extends ExampleProducerCase {

  @Test
  public void testProduce() throws Exception {
    StandaloneProducer service = new StandaloneProducer(new EchoProducer());
    AdaptrisMessage message = AdaptrisMessageFactory.getDefaultInstance().newMessage();
    ExampleServiceCase.execute(service, message);
  }

  @Override
  protected StandaloneProducer retrieveObjectForSampleConfig() {
    return new StandaloneProducer(new EchoProducer());
  }

}