package com.adaptris.samples;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.adaptris.core.FixedIntervalPoller;
import com.adaptris.core.StandaloneConsumer;
import com.adaptris.core.stubs.DefectiveMessageFactory;
import com.adaptris.core.stubs.DefectiveMessageFactory.WhenToBreak;
import com.adaptris.core.stubs.MockMessageListener;
import com.adaptris.interlok.junit.scaffolding.ExampleConsumerCase;
import com.adaptris.util.TimeInterval;

public class EchoConsumerTest extends ExampleConsumerCase {

  @Override
  protected StandaloneConsumer retrieveObjectForSampleConfig() {
    return new StandaloneConsumer(new EchoConsumer());
  }

  @Test
  public void testConsume() throws Exception {
    MockMessageListener listener = new MockMessageListener(0);
    EchoConsumer ec = new EchoConsumer();
    ec.setPoller(new FixedIntervalPoller(new TimeInterval(300L, TimeUnit.MILLISECONDS)));
    StandaloneConsumer sc = new StandaloneConsumer(ec);
    sc.registerAdaptrisMessageListener(listener);
    try {
      start(sc);
      await().atMost(Duration.ofSeconds(1)).with().pollInterval(Duration.ofMillis(100)).until(listener::messageCount,
          greaterThanOrEqualTo(1));
      assertEquals("hello world", listener.getMessages().get(0).getContent());
    } finally {
      stop(sc);
    }
  }

  @Test
  public void testConsume_WithFailure() throws Exception {
    MockMessageListener listener = new MockMessageListener(0);
    EchoConsumer ec = new EchoConsumer();
    ec.setPoller(new FixedIntervalPoller(new TimeInterval(300L, TimeUnit.MILLISECONDS)));
    ec.setMessageFactory(new DefectiveMessageFactory(WhenToBreak.METADATA_SET));
    StandaloneConsumer sc = new StandaloneConsumer(ec);
    sc.registerAdaptrisMessageListener(listener);
    try {
      start(sc);
      Thread.sleep(1000);
    } finally {
      stop(sc);
    }
  }

}