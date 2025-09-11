
package com.adaptris.samples;

import org.apache.commons.lang3.StringUtils;
import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ServiceException;
import com.adaptris.core.ServiceImp;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XStreamAlias("echo-service")
@AdapterComponent
@ComponentProfile(summary = "Simply put some given string into the message payload", tag = "echo,example,sample")
@DisplayOrder(order = { "echoMessage" })
@NoArgsConstructor
public class EchoService extends ServiceImp {

  static final String DEFAULT = "Hello World.";

  /**
   * The text to use as the payload.
   *
   */
  @InputFieldDefault(value = DEFAULT)
  @Getter
  @Setter
  private String echoMessage;

  @Override
  public void doService(AdaptrisMessage message) throws ServiceException {
    message.setContent(echoMessage(), message.getContentEncoding());
  }

  @Override
  public final void prepare() {
  }

  @Override
  public final void initService() {
  }

  @Override
  public final void closeService() {
  }

  private String echoMessage() {
    return StringUtils.defaultIfEmpty(echoMessage, DEFAULT);
  }
}
