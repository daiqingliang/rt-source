package java.net;

import java.io.IOException;

class SocketSecrets {
  private static <T> void setOption(Object paramObject, SocketOption<T> paramSocketOption, T paramT) throws IOException {
    SocketImpl socketImpl;
    if (paramObject instanceof Socket) {
      socketImpl = ((Socket)paramObject).getImpl();
    } else if (paramObject instanceof ServerSocket) {
      socketImpl = ((ServerSocket)paramObject).getImpl();
    } else {
      throw new IllegalArgumentException();
    } 
    socketImpl.setOption(paramSocketOption, paramT);
  }
  
  private static <T> T getOption(Object paramObject, SocketOption<T> paramSocketOption) throws IOException {
    SocketImpl socketImpl;
    if (paramObject instanceof Socket) {
      socketImpl = ((Socket)paramObject).getImpl();
    } else if (paramObject instanceof ServerSocket) {
      socketImpl = ((ServerSocket)paramObject).getImpl();
    } else {
      throw new IllegalArgumentException();
    } 
    return (T)socketImpl.getOption(paramSocketOption);
  }
  
  private static <T> void setOption(DatagramSocket paramDatagramSocket, SocketOption<T> paramSocketOption, T paramT) throws IOException { paramDatagramSocket.getImpl().setOption(paramSocketOption, paramT); }
  
  private static <T> T getOption(DatagramSocket paramDatagramSocket, SocketOption<T> paramSocketOption) throws IOException { return (T)paramDatagramSocket.getImpl().getOption(paramSocketOption); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\SocketSecrets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */