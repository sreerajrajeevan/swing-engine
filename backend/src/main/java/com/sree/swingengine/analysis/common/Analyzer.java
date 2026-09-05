package com.sree.swingengine.analysis.common;

public interface Analyzer<T> {

    T analyze(AnalysisContext context);

}