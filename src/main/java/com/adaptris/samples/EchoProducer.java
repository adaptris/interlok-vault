package com.adaptris.samples;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.CoreException;
import com.adaptris.core.NullConnection;
import com.adaptris.core.ProduceException;
import com.adaptris.core.ProduceOnlyProducerImp;
import com.adaptris.core.util.LoggingHelper;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.NoArgsConstructor;

@XStreamAlias("echo-producer")
@ComponentProfile(summary = "Simply echo 'hello world' during `produce`",
    tag = "echo,example,sample")
@AdapterComponent
@NoArgsConstructor
public class EchoProducer extends ProduceOnlyProducerImp {

  @Override
  public void prepare() throws CoreException {
    // Nothing to do
  }

  @Override
  protected void doProduce(AdaptrisMessage msg, String endpoint) throws ProduceException {
    // We could do something with the connection here if needs be if we have
    // resources that require a connection.
    NullConnection conn = retrieveConnection(NullConnection.class);
    System.err.println(endpoint + " says Hello World via " + LoggingHelper.friendlyName(conn));
  }

  @Override
  public String endpoint(AdaptrisMessage msg) throws ProduceException {
    return "static-echo-endpoint";
  }



}
