package sun.misc;

public class RequestProcessor implements Runnable {
  private static Queue<Request> requestQueue;
  
  private static Thread dispatcher;
  
  public static void postRequest(Request paramRequest) {
    lazyInitialize();
    requestQueue.enqueue(paramRequest);
  }
  
  public void run() {
    lazyInitialize();
    while (true) {
      try {
        Request request = (Request)requestQueue.dequeue();
        try {
          request.execute();
        } catch (Throwable throwable) {}
      } catch (InterruptedException interruptedException) {}
    } 
  }
  
  public static void startProcessing() {
    if (dispatcher == null) {
      dispatcher = new Thread(new RequestProcessor(), "Request Processor");
      dispatcher.setPriority(7);
      dispatcher.start();
    } 
  }
  
  private static void lazyInitialize() {
    if (requestQueue == null)
      requestQueue = new Queue(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\RequestProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */