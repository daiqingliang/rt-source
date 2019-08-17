package com.sun.corba.se.spi.monitoring;

public class StatisticsAccumulator {
  protected double max = Double.MIN_VALUE;
  
  protected double min = Double.MAX_VALUE;
  
  private double sampleSum;
  
  private double sampleSquareSum;
  
  private long sampleCount;
  
  protected String unit;
  
  public void sample(double paramDouble) {
    this.sampleCount++;
    if (paramDouble < this.min)
      this.min = paramDouble; 
    if (paramDouble > this.max)
      this.max = paramDouble; 
    this.sampleSum += paramDouble;
    this.sampleSquareSum += paramDouble * paramDouble;
  }
  
  public String getValue() { return toString(); }
  
  public String toString() { return "Minimum Value = " + this.min + " " + this.unit + " Maximum Value = " + this.max + " " + this.unit + " Average Value = " + computeAverage() + " " + this.unit + " Standard Deviation = " + computeStandardDeviation() + " " + this.unit + " Samples Collected = " + this.sampleCount; }
  
  protected double computeAverage() { return this.sampleSum / this.sampleCount; }
  
  protected double computeStandardDeviation() {
    double d = this.sampleSum * this.sampleSum;
    return Math.sqrt((this.sampleSquareSum - d / this.sampleCount) / (this.sampleCount - 1L));
  }
  
  public StatisticsAccumulator(String paramString) {
    this.unit = paramString;
    this.sampleCount = 0L;
    this.sampleSum = 0.0D;
    this.sampleSquareSum = 0.0D;
  }
  
  void clearState() {
    this.min = Double.MAX_VALUE;
    this.max = Double.MIN_VALUE;
    this.sampleCount = 0L;
    this.sampleSum = 0.0D;
    this.sampleSquareSum = 0.0D;
  }
  
  public void unitTestValidate(String paramString, double paramDouble1, double paramDouble2, long paramLong, double paramDouble3, double paramDouble4) {
    if (!paramString.equals(this.unit))
      throw new RuntimeException("Unit is not same as expected Unit\nUnit = " + this.unit + "ExpectedUnit = " + paramString); 
    if (this.min != paramDouble1)
      throw new RuntimeException("Minimum value is not same as expected minimum value\nMin Value = " + this.min + "Expected Min Value = " + paramDouble1); 
    if (this.max != paramDouble2)
      throw new RuntimeException("Maximum value is not same as expected maximum value\nMax Value = " + this.max + "Expected Max Value = " + paramDouble2); 
    if (this.sampleCount != paramLong)
      throw new RuntimeException("Sample count is not same as expected Sample Count\nSampleCount = " + this.sampleCount + "Expected Sample Count = " + paramLong); 
    if (computeAverage() != paramDouble3)
      throw new RuntimeException("Average is not same as expected Average\nAverage = " + computeAverage() + "Expected Average = " + paramDouble3); 
    double d = Math.abs(computeStandardDeviation() - paramDouble4);
    if (d > 1.0D)
      throw new RuntimeException("Standard Deviation is not same as expected Std Deviation\nStandard Dev = " + computeStandardDeviation() + "Expected Standard Dev = " + paramDouble4); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\monitoring\StatisticsAccumulator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */