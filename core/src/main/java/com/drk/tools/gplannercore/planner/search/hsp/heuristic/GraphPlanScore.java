package com.drk.tools.gplannercore.planner.search.hsp.heuristic;

import com.drk.tools.gplannercore.core.Context;
import com.drk.tools.gplannercore.core.state.State;
import com.drk.tools.gplannercore.core.state.Statement;
import com.drk.tools.gplannercore.core.state.Transition;
import com.drk.tools.gplannercore.planner.search.unifier.OperatorUnifierBuilder;
import com.drk.tools.gplannercore.planner.search.unifier.SearchUnifier;
import com.drk.tools.gplannercore.planner.search.unifier.UnifierBuilder;

import java.util.HashSet;
import java.util.Set;

public class GraphPlanScore implements Score {

    @Override
    public int resolve(Context context, State state, State finalState) {
        Layer layer = firstLayer(state, context);
        boolean reachedGoal = reachedGoal(layer, finalState);
        boolean twoConsecutiveEqualLayer = false;
        int level = 0;
        while (!reachedGoal && !twoConsecutiveEqualLayer) {
            Layer nextLayer = expandLayer(layer, context);
            reachedGoal = reachedGoal(nextLayer, finalState);
            twoConsecutiveEqualLayer = layer.equals(nextLayer);
            layer = nextLayer;
            level++;
        }
        return reachedGoal ? level : Integer.MAX_VALUE;
    }

    private boolean reachedGoal(Layer layer, State finalState) {
        Set<Statement> goalStatements = finalState.getStatements();
        return layer.statements.containsAll(goalStatements);
    }

    private Layer firstLayer(State initialState, Context context) {
        UnifierBuilder unifierBuilder = new OperatorUnifierBuilder(context);
        SearchUnifier searchUnifier = unifierBuilder.from(initialState.getStatements(), new HashSet<Statement>());
        Set<Statement> statements = new HashSet<>(initialState.getStatements());
        Set<Transition> transitions = new HashSet<>(searchUnifier.all());
        return new Layer(null, statements, transitions);
    }

    private Layer expandLayer(Layer layer, Context context) {
        Set<Statement> statements = new HashSet<>(layer.statements);
        statements.addAll(transitionEffects(layer.applicableTransitions));
        UnifierBuilder unifierBuilder = new OperatorUnifierBuilder(context);
        SearchUnifier searchUnifier = unifierBuilder.from(statements, new HashSet<Statement>());
        Set<Transition> transitions = new HashSet<>(searchUnifier.all());
        return new Layer(layer, statements, transitions);
    }

    private Set<Statement> transitionEffects(Set<Transition> transitions) {
        Set<Statement> statements = new HashSet<>();
        for (Transition transition : transitions) {
            statements.addAll(transition.stateTransition.getPositiveEffects());
        }
        return statements;
    }


    private static class Layer {

        final Layer lastLayer;
        final Set<Statement> statements;
        final Set<Transition> applicableTransitions;

        Layer(Layer lastLayer, Set<Statement> statements, Set<Transition> applicableTransitions) {
            this.lastLayer = lastLayer;
            this.statements = statements;
            this.applicableTransitions = applicableTransitions;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Layer) {
                Layer otherLayer = (Layer) obj;
                return statements.equals(otherLayer.statements)
                        && applicableTransitions.equals(otherLayer.applicableTransitions);
            }
            return super.equals(obj);
        }

    }
}
