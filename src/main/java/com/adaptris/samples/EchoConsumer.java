package com.adaptris.samples;

import static com.adaptris.core.AdaptrisMessageFactory.defaultIfNull;

import java.util.HashSet;
import java.util.Set;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisPollingConsumer;
import com.adaptris.core.CoreException;
import com.adaptris.core.MetadataElement;
import com.adaptris.core.NullConnection;
import com.adaptris.core.util.LoggingHelper;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import lombok.NoArgsConstructor;

@XStreamAlias("echo-consumer")
@ComponentProfile(summary = "Create a payload `hello-world` when triggered", tag = "echo,example,sample")
@AdapterComponent
@NoArgsConstructor
public class EchoConsumer extends AdaptrisPollingConsumer {

  @Override
  protected int processMessages() {
    int count = 0;
    try {
      NullConnection conn = retrieveConnection(NullConnection.class);
      Set<MetadataElement> metadata = new HashSet<>();
      metadata.add(new MetadataElement("connection-name", LoggingHelper.friendlyName(conn)));
      AdaptrisMessage msg = defaultIfNull(getMessageFactory()).newMessage("hello world", metadata);
      retrieveAdaptrisMessageListener().onAdaptrisMessage(msg);
      count = 1;
    } catch (Exception e) {
      log.warn("Failed to create trigger message; next attempt on next poll");
      log.trace(e.getMessage(), e);
    }
    return count;
  }

  @Override
  protected void prepareConsumer() throws CoreException {
    // Nothing to do.
  }

}
