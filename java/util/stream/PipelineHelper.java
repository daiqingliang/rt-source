package java.util.stream;

import java.util.Spliterator;
import java.util.function.IntFunction;

abstract class PipelineHelper<P_OUT> extends Object {
  abstract StreamShape getSourceShape();
  
  abstract int getStreamAndOpFlags();
  
  abstract <P_IN> long exactOutputSizeIfKnown(Spliterator<P_IN> paramSpliterator);
  
  abstract <P_IN, S extends Sink<P_OUT>> S wrapAndCopyInto(S paramS, Spliterator<P_IN> paramSpliterator);
  
  abstract <P_IN> void copyInto(Sink<P_IN> paramSink, Spliterator<P_IN> paramSpliterator);
  
  abstract <P_IN> void copyIntoWithCancel(Sink<P_IN> paramSink, Spliterator<P_IN> paramSpliterator);
  
  abstract <P_IN> Sink<P_IN> wrapSink(Sink<P_OUT> paramSink);
  
  abstract <P_IN> Spliterator<P_OUT> wrapSpliterator(Spliterator<P_IN> paramSpliterator);
  
  abstract Node.Builder<P_OUT> makeNodeBuilder(long paramLong, IntFunction<P_OUT[]> paramIntFunction);
  
  abstract <P_IN> Node<P_OUT> evaluate(Spliterator<P_IN> paramSpliterator, boolean paramBoolean, IntFunction<P_OUT[]> paramIntFunction);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\PipelineHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */