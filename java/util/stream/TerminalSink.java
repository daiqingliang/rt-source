package java.util.stream;

import java.util.function.Supplier;

interface TerminalSink<T, R> extends Sink<T>, Supplier<R> {}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\TerminalSink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */