package org.omg.PortableInterceptor;

public interface ServerRequestInterceptorOperations extends InterceptorOperations {
  void receive_request_service_contexts(ServerRequestInfo paramServerRequestInfo) throws ForwardRequest;
  
  void receive_request(ServerRequestInfo paramServerRequestInfo) throws ForwardRequest;
  
  void send_reply(ServerRequestInfo paramServerRequestInfo) throws ForwardRequest;
  
  void send_exception(ServerRequestInfo paramServerRequestInfo) throws ForwardRequest;
  
  void send_other(ServerRequestInfo paramServerRequestInfo) throws ForwardRequest;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableInterceptor\ServerRequestInterceptorOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */