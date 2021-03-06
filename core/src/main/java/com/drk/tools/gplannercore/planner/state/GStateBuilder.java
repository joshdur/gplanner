package com.drk.tools.gplannercore.planner.state;

import com.drk.tools.gplannercore.core.variables.Variable;
import com.drk.tools.gplannercore.planner.state.atoms.Atom;
import com.drk.tools.gplannercore.planner.state.atoms.BinaryAtom;
import com.drk.tools.gplannercore.planner.state.atoms.TernaryAtom;

public class GStateBuilder {

    private final GState state;
    private final boolean debug;

    public GStateBuilder() {
        this(false);
    }

    public GStateBuilder(boolean debug) {
        this.state = new GState();
        this.debug = debug;
    }

    public <V extends Variable> GStateBuilder set(Atom<V> a, V v) {
        state.set(StatementBuilder.build(debug, a, v));
        return this;
    }

    public <V1 extends Variable, V2 extends Variable> GStateBuilder from(BinaryAtom<V1, V2> a, V1 v1, V2 v2) {
        state.set(StatementBuilder.build(debug, a, v1, v2));
        return this;
    }

    public <V1 extends Variable, V2 extends Variable, V3 extends Variable> GStateBuilder from(TernaryAtom<V1, V2, V3> a, V1 v1, V2 v2, V3 v3) {
        state.set(StatementBuilder.build(debug, a, v1, v2, v3));
        return this;
    }

    public GState build() {
        return state;
    }
}
