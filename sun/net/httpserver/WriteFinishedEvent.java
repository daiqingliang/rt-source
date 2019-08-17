package sun.net.httpserver;

class WriteFinishedEvent extends Event {
  WriteFinishedEvent(ExchangeImpl paramExchangeImpl) {
    super(paramExchangeImpl);
    assert !paramExchangeImpl.writefinished;
    paramExchangeImpl.writefinished = true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\WriteFinishedEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */