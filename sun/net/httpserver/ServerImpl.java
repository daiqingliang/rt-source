package sun.net.httpserver;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

class ServerImpl implements TimeSource {
  private String protocol;
  
  private boolean https;
  
  private Executor executor;
  
  private HttpsConfigurator httpsConfig;
  
  private SSLContext sslContext;
  
  private ContextList contexts;
  
  private InetSocketAddress address;
  
  private ServerSocketChannel schan;
  
  private Selector selector;
  
  private SelectionKey listenerKey;
  
  private Set<HttpConnection> idleConnections;
  
  private Set<HttpConnection> allConnections;
  
  private Set<HttpConnection> reqConnections;
  
  private Set<HttpConnection> rspConnections;
  
  private List<Event> events;
  
  private Object lolock = new Object();
  
  private boolean bound = false;
  
  private boolean started = false;
  
  private HttpServer wrapper;
  
  static final int CLOCK_TICK = ServerConfig.getClockTick();
  
  static final long IDLE_INTERVAL = ServerConfig.getIdleInterval();
  
  static final int MAX_IDLE_CONNECTIONS = ServerConfig.getMaxIdleConnections();
  
  static final long TIMER_MILLIS = ServerConfig.getTimerMillis();
  
  static final long MAX_REQ_TIME = getTimeMillis(ServerConfig.getMaxReqTime());
  
  static final long MAX_RSP_TIME = getTimeMillis(ServerConfig.getMaxRspTime());
  
  static final boolean timer1Enabled = (MAX_REQ_TIME != -1L || MAX_RSP_TIME != -1L);
  
  private Timer timer;
  
  private Timer timer1;
  
  private Logger logger;
  
  Dispatcher dispatcher;
  
  static boolean debug = ServerConfig.debugEnabled();
  
  private int exchangeCount = 0;
  
  ServerImpl(HttpServer paramHttpServer, String paramString, InetSocketAddress paramInetSocketAddress, int paramInt) throws IOException {
    this.protocol = paramString;
    this.wrapper = paramHttpServer;
    this.logger = Logger.getLogger("com.sun.net.httpserver");
    ServerConfig.checkLegacyProperties(this.logger);
    this.https = paramString.equalsIgnoreCase("https");
    this.address = paramInetSocketAddress;
    this.contexts = new ContextList();
    this.schan = ServerSocketChannel.open();
    if (paramInetSocketAddress != null) {
      ServerSocket serverSocket = this.schan.socket();
      serverSocket.bind(paramInetSocketAddress, paramInt);
      this.bound = true;
    } 
    this.selector = Selector.open();
    this.schan.configureBlocking(false);
    this.listenerKey = this.schan.register(this.selector, 16);
    this.dispatcher = new Dispatcher();
    this.idleConnections = Collections.synchronizedSet(new HashSet());
    this.allConnections = Collections.synchronizedSet(new HashSet());
    this.reqConnections = Collections.synchronizedSet(new HashSet());
    this.rspConnections = Collections.synchronizedSet(new HashSet());
    this.time = System.currentTimeMillis();
    this.timer = new Timer("server-timer", true);
    this.timer.schedule(new ServerTimerTask(), CLOCK_TICK, CLOCK_TICK);
    if (timer1Enabled) {
      this.timer1 = new Timer("server-timer1", true);
      this.timer1.schedule(new ServerTimerTask1(), TIMER_MILLIS, TIMER_MILLIS);
      this.logger.config("HttpServer timer1 enabled period in ms:  " + TIMER_MILLIS);
      this.logger.config("MAX_REQ_TIME:  " + MAX_REQ_TIME);
      this.logger.config("MAX_RSP_TIME:  " + MAX_RSP_TIME);
    } 
    this.events = new LinkedList();
    this.logger.config("HttpServer created " + paramString + " " + paramInetSocketAddress);
  }
  
  public void bind(InetSocketAddress paramInetSocketAddress, int paramInt) throws IOException {
    if (this.bound)
      throw new BindException("HttpServer already bound"); 
    if (paramInetSocketAddress == null)
      throw new NullPointerException("null address"); 
    ServerSocket serverSocket = this.schan.socket();
    serverSocket.bind(paramInetSocketAddress, paramInt);
    this.bound = true;
  }
  
  public void start() {
    if (!this.bound || this.started || this.finished)
      throw new IllegalStateException("server in wrong state"); 
    if (this.executor == null)
      this.executor = new DefaultExecutor(null); 
    Thread thread = new Thread(this.dispatcher);
    this.started = true;
    thread.start();
  }
  
  public void setExecutor(Executor paramExecutor) {
    if (this.started)
      throw new IllegalStateException("server already started"); 
    this.executor = paramExecutor;
  }
  
  public Executor getExecutor() { return this.executor; }
  
  public void setHttpsConfigurator(HttpsConfigurator paramHttpsConfigurator) {
    if (paramHttpsConfigurator == null)
      throw new NullPointerException("null HttpsConfigurator"); 
    if (this.started)
      throw new IllegalStateException("server already started"); 
    this.httpsConfig = paramHttpsConfigurator;
    this.sslContext = paramHttpsConfigurator.getSSLContext();
  }
  
  public HttpsConfigurator getHttpsConfigurator() { return this.httpsConfig; }
  
  public void stop(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("negative delay parameter"); 
    this.terminating = true;
    try {
      this.schan.close();
    } catch (IOException iOException) {}
    this.selector.wakeup();
    long l = System.currentTimeMillis() + (paramInt * 1000);
    while (System.currentTimeMillis() < l) {
      delay();
      if (this.finished)
        break; 
    } 
    this.finished = true;
    this.selector.wakeup();
    synchronized (this.allConnections) {
      for (HttpConnection httpConnection : this.allConnections)
        httpConnection.close(); 
    } 
    this.allConnections.clear();
    this.idleConnections.clear();
    this.timer.cancel();
    if (timer1Enabled)
      this.timer1.cancel(); 
  }
  
  public HttpContextImpl createContext(String paramString, HttpHandler paramHttpHandler) {
    if (paramHttpHandler == null || paramString == null)
      throw new NullPointerException("null handler, or path parameter"); 
    HttpContextImpl httpContextImpl = new HttpContextImpl(this.protocol, paramString, paramHttpHandler, this);
    this.contexts.add(httpContextImpl);
    this.logger.config("context created: " + paramString);
    return httpContextImpl;
  }
  
  public HttpContextImpl createContext(String paramString) {
    if (paramString == null)
      throw new NullPointerException("null path parameter"); 
    HttpContextImpl httpContextImpl = new HttpContextImpl(this.protocol, paramString, null, this);
    this.contexts.add(httpContextImpl);
    this.logger.config("context created: " + paramString);
    return httpContextImpl;
  }
  
  public void removeContext(String paramString) throws IllegalArgumentException {
    if (paramString == null)
      throw new NullPointerException("null path parameter"); 
    this.contexts.remove(this.protocol, paramString);
    this.logger.config("context removed: " + paramString);
  }
  
  public void removeContext(HttpContext paramHttpContext) throws IllegalArgumentException {
    if (!(paramHttpContext instanceof HttpContextImpl))
      throw new IllegalArgumentException("wrong HttpContext type"); 
    this.contexts.remove((HttpContextImpl)paramHttpContext);
    this.logger.config("context removed: " + paramHttpContext.getPath());
  }
  
  public InetSocketAddress getAddress() { return (InetSocketAddress)AccessController.doPrivileged(new PrivilegedAction<InetSocketAddress>() {
          public InetSocketAddress run() { return (InetSocketAddress)ServerImpl.this.schan.socket().getLocalSocketAddress(); }
        }); }
  
  Selector getSelector() { return this.selector; }
  
  void addEvent(Event paramEvent) {
    synchronized (this.lolock) {
      this.events.add(paramEvent);
      this.selector.wakeup();
    } 
  }
  
  static void dprint(String paramString) throws IllegalArgumentException {
    if (debug)
      System.out.println(paramString); 
  }
  
  static void dprint(Exception paramException) {
    if (debug) {
      System.out.println(paramException);
      paramException.printStackTrace();
    } 
  }
  
  Logger getLogger() { return this.logger; }
  
  private void closeConnection(HttpConnection paramHttpConnection) {
    paramHttpConnection.close();
    this.allConnections.remove(paramHttpConnection);
    switch (paramHttpConnection.getState()) {
      case REQUEST:
        this.reqConnections.remove(paramHttpConnection);
        break;
      case RESPONSE:
        this.rspConnections.remove(paramHttpConnection);
        break;
      case IDLE:
        this.idleConnections.remove(paramHttpConnection);
        break;
    } 
    assert !this.reqConnections.remove(paramHttpConnection);
    assert !this.rspConnections.remove(paramHttpConnection);
    assert !this.idleConnections.remove(paramHttpConnection);
  }
  
  void logReply(int paramInt, String paramString1, String paramString2) {
    String str1;
    if (!this.logger.isLoggable(Level.FINE))
      return; 
    if (paramString2 == null)
      paramString2 = ""; 
    if (paramString1.length() > 80) {
      str1 = paramString1.substring(0, 80) + "<TRUNCATED>";
    } else {
      str1 = paramString1;
    } 
    String str2 = str1 + " [" + paramInt + " " + Code.msg(paramInt) + "] (" + paramString2 + ")";
    this.logger.fine(str2);
  }
  
  long getTicks() { return this.ticks; }
  
  public long getTime() { return this.time; }
  
  void delay() {
    Thread.yield();
    try {
      Thread.sleep(200L);
    } catch (InterruptedException interruptedException) {}
  }
  
  void startExchange() { this.exchangeCount++; }
  
  int endExchange() {
    this.exchangeCount--;
    assert this.exchangeCount >= 0;
    return this.exchangeCount;
  }
  
  HttpServer getWrapper() { return this.wrapper; }
  
  void requestStarted(HttpConnection paramHttpConnection) {
    paramHttpConnection.creationTime = getTime();
    paramHttpConnection.setState(HttpConnection.State.REQUEST);
    this.reqConnections.add(paramHttpConnection);
  }
  
  void requestCompleted(HttpConnection paramHttpConnection) {
    assert paramHttpConnection.getState() == HttpConnection.State.REQUEST;
    this.reqConnections.remove(paramHttpConnection);
    paramHttpConnection.rspStartedTime = getTime();
    this.rspConnections.add(paramHttpConnection);
    paramHttpConnection.setState(HttpConnection.State.RESPONSE);
  }
  
  void responseCompleted(HttpConnection paramHttpConnection) {
    assert paramHttpConnection.getState() == HttpConnection.State.RESPONSE;
    this.rspConnections.remove(paramHttpConnection);
    paramHttpConnection.setState(HttpConnection.State.IDLE);
  }
  
  void logStackTrace(String paramString) throws IllegalArgumentException {
    this.logger.finest(paramString);
    StringBuilder stringBuilder = new StringBuilder();
    StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
    for (byte b = 0; b < arrayOfStackTraceElement.length; b++)
      stringBuilder.append(arrayOfStackTraceElement[b].toString()).append("\n"); 
    this.logger.finest(stringBuilder.toString());
  }
  
  static long getTimeMillis(long paramLong) { return (paramLong == -1L) ? -1L : (paramLong * 1000L); }
  
  private static class DefaultExecutor implements Executor {
    private DefaultExecutor() {}
    
    public void execute(Runnable param1Runnable) { param1Runnable.run(); }
  }
  
  class Dispatcher implements Runnable {
    final LinkedList<HttpConnection> connsToRegister = new LinkedList();
    
    private void handleEvent(Event param1Event) {
      ExchangeImpl exchangeImpl = param1Event.exchange;
      HttpConnection httpConnection = exchangeImpl.getConnection();
      try {
        if (param1Event instanceof WriteFinishedEvent) {
          int i = ServerImpl.this.endExchange();
          if (ServerImpl.this.terminating && i == 0)
            ServerImpl.this.finished = true; 
          ServerImpl.this.responseCompleted(httpConnection);
          LeftOverInputStream leftOverInputStream = exchangeImpl.getOriginalInputStream();
          if (!leftOverInputStream.isEOF())
            exchangeImpl.close = true; 
          if (exchangeImpl.close || ServerImpl.this.idleConnections.size() >= ServerImpl.MAX_IDLE_CONNECTIONS) {
            httpConnection.close();
            ServerImpl.this.allConnections.remove(httpConnection);
          } else if (leftOverInputStream.isDataBuffered()) {
            ServerImpl.this.requestStarted(httpConnection);
            handle(httpConnection.getChannel(), httpConnection);
          } else {
            this.connsToRegister.add(httpConnection);
          } 
        } 
      } catch (IOException iOException) {
        ServerImpl.this.logger.log(Level.FINER, "Dispatcher (1)", iOException);
        httpConnection.close();
      } 
    }
    
    void reRegister(HttpConnection param1HttpConnection) {
      try {
        SocketChannel socketChannel = param1HttpConnection.getChannel();
        socketChannel.configureBlocking(false);
        SelectionKey selectionKey = socketChannel.register(ServerImpl.this.selector, 1);
        selectionKey.attach(param1HttpConnection);
        param1HttpConnection.selectionKey = selectionKey;
        param1HttpConnection.time = ServerImpl.this.getTime() + ServerImpl.IDLE_INTERVAL;
        ServerImpl.this.idleConnections.add(param1HttpConnection);
      } catch (IOException iOException) {
        ServerImpl.dprint(iOException);
        ServerImpl.this.logger.log(Level.FINER, "Dispatcher(8)", iOException);
        param1HttpConnection.close();
      } 
    }
    
    public void run() {
      while (!ServerImpl.this.finished) {
        try {
          List list = null;
          synchronized (ServerImpl.this.lolock) {
            if (ServerImpl.this.events.size() > 0) {
              list = ServerImpl.this.events;
              ServerImpl.this.events = new LinkedList();
            } 
          } 
          if (list != null)
            for (Event event : list)
              handleEvent(event);  
          for (HttpConnection httpConnection : this.connsToRegister)
            reRegister(httpConnection); 
          this.connsToRegister.clear();
          ServerImpl.this.selector.select(1000L);
          Set set = ServerImpl.this.selector.selectedKeys();
          Iterator iterator = set.iterator();
          while (iterator.hasNext()) {
            SelectionKey selectionKey = (SelectionKey)iterator.next();
            iterator.remove();
            if (selectionKey.equals(ServerImpl.this.listenerKey)) {
              if (ServerImpl.this.terminating)
                continue; 
              SocketChannel socketChannel = ServerImpl.this.schan.accept();
              if (ServerConfig.noDelay())
                socketChannel.socket().setTcpNoDelay(true); 
              if (socketChannel == null)
                continue; 
              socketChannel.configureBlocking(false);
              SelectionKey selectionKey1 = socketChannel.register(ServerImpl.this.selector, 1);
              HttpConnection httpConnection = new HttpConnection();
              httpConnection.selectionKey = selectionKey1;
              httpConnection.setChannel(socketChannel);
              selectionKey1.attach(httpConnection);
              ServerImpl.this.requestStarted(httpConnection);
              ServerImpl.this.allConnections.add(httpConnection);
              continue;
            } 
            try {
              if (selectionKey.isReadable()) {
                SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                HttpConnection httpConnection = (HttpConnection)selectionKey.attachment();
                selectionKey.cancel();
                socketChannel.configureBlocking(true);
                if (ServerImpl.this.idleConnections.remove(httpConnection))
                  ServerImpl.this.requestStarted(httpConnection); 
                handle(socketChannel, httpConnection);
                continue;
              } 
              assert false;
            } catch (CancelledKeyException cancelledKeyException) {
              handleException(selectionKey, null);
            } catch (IOException iOException) {
              handleException(selectionKey, iOException);
            } 
          } 
          ServerImpl.this.selector.selectNow();
        } catch (IOException iOException) {
          ServerImpl.this.logger.log(Level.FINER, "Dispatcher (4)", iOException);
        } catch (Exception exception) {
          ServerImpl.this.logger.log(Level.FINER, "Dispatcher (7)", exception);
        } 
      } 
      try {
        ServerImpl.this.selector.close();
      } catch (Exception exception) {}
    }
    
    private void handleException(SelectionKey param1SelectionKey, Exception param1Exception) {
      HttpConnection httpConnection = (HttpConnection)param1SelectionKey.attachment();
      if (param1Exception != null)
        ServerImpl.this.logger.log(Level.FINER, "Dispatcher (2)", param1Exception); 
      ServerImpl.this.closeConnection(httpConnection);
    }
    
    public void handle(SocketChannel param1SocketChannel, HttpConnection param1HttpConnection) throws IOException {
      try {
        ServerImpl.Exchange exchange = new ServerImpl.Exchange(ServerImpl.this, param1SocketChannel, ServerImpl.this.protocol, param1HttpConnection);
        ServerImpl.this.executor.execute(exchange);
      } catch (HttpError httpError) {
        ServerImpl.this.logger.log(Level.FINER, "Dispatcher (4)", httpError);
        ServerImpl.this.closeConnection(param1HttpConnection);
      } catch (IOException iOException) {
        ServerImpl.this.logger.log(Level.FINER, "Dispatcher (5)", iOException);
        ServerImpl.this.closeConnection(param1HttpConnection);
      } 
    }
  }
  
  class Exchange implements Runnable {
    SocketChannel chan;
    
    HttpConnection connection;
    
    HttpContextImpl context;
    
    InputStream rawin;
    
    OutputStream rawout;
    
    String protocol;
    
    ExchangeImpl tx;
    
    HttpContextImpl ctx;
    
    boolean rejected = false;
    
    Exchange(SocketChannel param1SocketChannel, String param1String, HttpConnection param1HttpConnection) throws IOException {
      this.chan = param1SocketChannel;
      this.connection = param1HttpConnection;
      this.protocol = param1String;
    }
    
    public void run() {
      this.context = this.connection.getHttpContext();
      SSLEngine sSLEngine = null;
      String str = null;
      SSLStreams sSLStreams = null;
      try {
        boolean bool;
        if (this.context != null) {
          this.rawin = this.connection.getInputStream();
          this.rawout = this.connection.getRawOutputStream();
          bool = false;
        } else {
          bool = true;
          if (ServerImpl.this.https) {
            if (ServerImpl.this.sslContext == null) {
              ServerImpl.this.logger.warning("SSL connection received. No https contxt created");
              throw new HttpError("No SSL context established");
            } 
            sSLStreams = new SSLStreams(ServerImpl.this, ServerImpl.this.sslContext, this.chan);
            this.rawin = sSLStreams.getInputStream();
            this.rawout = sSLStreams.getOutputStream();
            sSLEngine = sSLStreams.getSSLEngine();
            this.connection.sslStreams = sSLStreams;
          } else {
            this.rawin = new BufferedInputStream(new Request.ReadStream(ServerImpl.this, this.chan));
            this.rawout = new Request.WriteStream(ServerImpl.this, this.chan);
          } 
          this.connection.raw = this.rawin;
          this.connection.rawout = this.rawout;
        } 
        Request request = new Request(this.rawin, this.rawout);
        str = request.requestLine();
        if (str == null) {
          ServerImpl.this.closeConnection(this.connection);
          return;
        } 
        int i = str.indexOf(' ');
        if (i == -1) {
          reject(400, str, "Bad request line");
          return;
        } 
        String str1 = str.substring(0, i);
        int j = i + 1;
        i = str.indexOf(' ', j);
        if (i == -1) {
          reject(400, str, "Bad request line");
          return;
        } 
        String str2 = str.substring(j, i);
        URI uRI = new URI(str2);
        j = i + 1;
        String str3 = str.substring(j);
        Headers headers1 = request.headers();
        String str4 = headers1.getFirst("Transfer-encoding");
        long l = 0L;
        if (str4 != null && str4.equalsIgnoreCase("chunked")) {
          l = -1L;
        } else {
          str4 = headers1.getFirst("Content-Length");
          if (str4 != null)
            l = Long.parseLong(str4); 
          if (l == 0L)
            ServerImpl.this.requestCompleted(this.connection); 
        } 
        this.ctx = ServerImpl.this.contexts.findContext(this.protocol, uRI.getPath());
        if (this.ctx == null) {
          reject(404, str, "No context found for request");
          return;
        } 
        this.connection.setContext(this.ctx);
        if (this.ctx.getHandler() == null) {
          reject(500, str, "No handler for context");
          return;
        } 
        this.tx = new ExchangeImpl(str1, uRI, request, l, this.connection);
        String str5 = headers1.getFirst("Connection");
        Headers headers2 = this.tx.getResponseHeaders();
        if (str5 != null && str5.equalsIgnoreCase("close"))
          this.tx.close = true; 
        if (str3.equalsIgnoreCase("http/1.0")) {
          this.tx.http10 = true;
          if (str5 == null) {
            this.tx.close = true;
            headers2.set("Connection", "close");
          } else if (str5.equalsIgnoreCase("keep-alive")) {
            headers2.set("Connection", "keep-alive");
            int k = (int)(ServerConfig.getIdleInterval() / 1000L);
            int m = ServerConfig.getMaxIdleConnections();
            String str7 = "timeout=" + k + ", max=" + m;
            headers2.set("Keep-Alive", str7);
          } 
        } 
        if (bool)
          this.connection.setParameters(this.rawin, this.rawout, this.chan, sSLEngine, sSLStreams, ServerImpl.this.sslContext, this.protocol, this.ctx, this.rawin); 
        String str6 = headers1.getFirst("Expect");
        if (str6 != null && str6.equalsIgnoreCase("100-continue")) {
          ServerImpl.this.logReply(100, str, null);
          sendReply(100, false, null);
        } 
        List list1 = this.ctx.getSystemFilters();
        List list2 = this.ctx.getFilters();
        Filter.Chain chain1 = new Filter.Chain(list1, this.ctx.getHandler());
        Filter.Chain chain2 = new Filter.Chain(list2, new LinkHandler(this, chain1));
        this.tx.getRequestBody();
        this.tx.getResponseBody();
        if (ServerImpl.this.https) {
          chain2.doFilter(new HttpsExchangeImpl(this.tx));
        } else {
          chain2.doFilter(new HttpExchangeImpl(this.tx));
        } 
      } catch (IOException iOException) {
        ServerImpl.this.logger.log(Level.FINER, "ServerImpl.Exchange (1)", iOException);
        ServerImpl.this.closeConnection(this.connection);
      } catch (NumberFormatException numberFormatException) {
        reject(400, str, "NumberFormatException thrown");
      } catch (URISyntaxException uRISyntaxException) {
        reject(400, str, "URISyntaxException thrown");
      } catch (Exception exception) {
        ServerImpl.this.logger.log(Level.FINER, "ServerImpl.Exchange (2)", exception);
        ServerImpl.this.closeConnection(this.connection);
      } 
    }
    
    void reject(int param1Int, String param1String1, String param1String2) {
      this.rejected = true;
      ServerImpl.this.logReply(param1Int, param1String1, param1String2);
      sendReply(param1Int, false, "<h1>" + param1Int + Code.msg(param1Int) + "</h1>" + param1String2);
      ServerImpl.this.closeConnection(this.connection);
    }
    
    void sendReply(int param1Int, boolean param1Boolean, String param1String) {
      try {
        StringBuilder stringBuilder = new StringBuilder(512);
        stringBuilder.append("HTTP/1.1 ").append(param1Int).append(Code.msg(param1Int)).append("\r\n");
        if (param1String != null && param1String.length() != 0) {
          stringBuilder.append("Content-Length: ").append(param1String.length()).append("\r\n").append("Content-Type: text/html\r\n");
        } else {
          stringBuilder.append("Content-Length: 0\r\n");
          param1String = "";
        } 
        if (param1Boolean)
          stringBuilder.append("Connection: close\r\n"); 
        stringBuilder.append("\r\n").append(param1String);
        String str = stringBuilder.toString();
        byte[] arrayOfByte = str.getBytes("ISO8859_1");
        this.rawout.write(arrayOfByte);
        this.rawout.flush();
        if (param1Boolean)
          ServerImpl.this.closeConnection(this.connection); 
      } catch (IOException iOException) {
        ServerImpl.this.logger.log(Level.FINER, "ServerImpl.sendReply", iOException);
        ServerImpl.this.closeConnection(this.connection);
      } 
    }
    
    class LinkHandler implements HttpHandler {
      Filter.Chain nextChain;
      
      LinkHandler(Filter.Chain param2Chain) { this.nextChain = param2Chain; }
      
      public void handle(HttpExchange param2HttpExchange) throws IOException { this.nextChain.doFilter(param2HttpExchange); }
    }
  }
  
  class ServerTimerTask extends TimerTask {
    public void run() {
      LinkedList linkedList = new LinkedList();
      ServerImpl.this.time = System.currentTimeMillis();
      ServerImpl.this.ticks++;
      synchronized (ServerImpl.this.idleConnections) {
        for (HttpConnection httpConnection : ServerImpl.this.idleConnections) {
          if (httpConnection.time <= ServerImpl.this.time)
            linkedList.add(httpConnection); 
        } 
        for (HttpConnection httpConnection : linkedList) {
          ServerImpl.this.idleConnections.remove(httpConnection);
          ServerImpl.this.allConnections.remove(httpConnection);
          httpConnection.close();
        } 
      } 
    }
  }
  
  class ServerTimerTask1 extends TimerTask {
    public void run() {
      LinkedList linkedList = new LinkedList();
      ServerImpl.this.time = System.currentTimeMillis();
      synchronized (ServerImpl.this.reqConnections) {
        if (ServerImpl.MAX_REQ_TIME != -1L) {
          for (HttpConnection httpConnection : ServerImpl.this.reqConnections) {
            if (httpConnection.creationTime + ServerImpl.TIMER_MILLIS + ServerImpl.MAX_REQ_TIME <= ServerImpl.this.time)
              linkedList.add(httpConnection); 
          } 
          for (HttpConnection httpConnection : linkedList) {
            ServerImpl.this.logger.log(Level.FINE, "closing: no request: " + httpConnection);
            ServerImpl.this.reqConnections.remove(httpConnection);
            ServerImpl.this.allConnections.remove(httpConnection);
            httpConnection.close();
          } 
        } 
      } 
      linkedList = new LinkedList();
      synchronized (ServerImpl.this.rspConnections) {
        if (ServerImpl.MAX_RSP_TIME != -1L) {
          for (HttpConnection httpConnection : ServerImpl.this.rspConnections) {
            if (httpConnection.rspStartedTime + ServerImpl.TIMER_MILLIS + ServerImpl.MAX_RSP_TIME <= ServerImpl.this.time)
              linkedList.add(httpConnection); 
          } 
          for (HttpConnection httpConnection : linkedList) {
            ServerImpl.this.logger.log(Level.FINE, "closing: no response: " + httpConnection);
            ServerImpl.this.rspConnections.remove(httpConnection);
            ServerImpl.this.allConnections.remove(httpConnection);
            httpConnection.close();
          } 
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\ServerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */