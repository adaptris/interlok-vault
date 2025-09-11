package com.adaptris.samples;

import static com.adaptris.samples.EchoService.DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageFactory;
import com.adaptris.interlok.junit.scaffolding.services.ExampleServiceCase;

public class EchoServiceTest extends ExampleServiceCase {

  private static final String ALTERNATIVE_MESSAGE = "Goodbye World.";

  private AdaptrisMessage message;
  private EchoService service;

  @BeforeEach
  public void setUp() throws Exception {
    message = AdaptrisMessageFactory.getDefaultInstance().newMessage();
    service = new EchoService();
  }

  @Test
  public void testService() throws Exception {
    execute(service, message);
    assertEquals(DEFAULT, message.getContent());
  }

  @Test
  public void testOwnMessage() throws Exception {
    service.setEchoMessage(ALTERNATIVE_MESSAGE);
    execute(service, message);
    assertEquals(ALTERNATIVE_MESSAGE, message.getContent());
  }

  @Override
  protected EchoService retrieveObjectForSampleConfig() {
    return new EchoService();
  }

}