package org.omg.PortableInterceptor;

public interface ClientRequestInterceptorOperations extends InterceptorOperations {
  void send_request(ClientRequestInfo paramClientRequestInfo) throws ForwardRequest;
  
  void send_poll(ClientRequestInfo paramClientRequestInfo) throws ForwardRequest;
  
  void receive_reply(ClientRequestInfo paramClientRequestInfo) throws ForwardRequest;
  
  void receive_exception(ClientRequestInfo paramClientRequestInfo) throws ForwardRequest;
  
  void receive_other(ClientRequestInfo paramClientRequestInfo) throws ForwardRequest;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableInterceptor\ClientRequestInterceptorOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */