package com.drk.tools.gplannercore.planner.search.hsp.heuristic;

import com.drk.tools.gplannercore.core.Context;
import com.drk.tools.gplannercore.core.state.State;

public interface Score {

    int resolve(Context context, State state, State finalState);
}
