package com.drk.tools.gplannercore.core.main;

import com.drk.tools.gplannercore.core.state.StateTransition;
import com.drk.tools.gplannercore.core.state.Transition;
import com.drk.tools.gplannercore.core.variables.Variable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseUnifier {

    private final String operatorName;
    private final Integer[] counts;
    private final int code;
    private final StateCounter stateCounter;

    public BaseUnifier(String operatorName, Integer... counts) {
        this.operatorName = operatorName;
        this.counts = counts;
        this.code = operatorName.hashCode();
        this.stateCounter = new StateCounter(counts);
    }

    public int getCode() {
        return this.code;
    }

    public boolean hasNext() {
        return stateCounter.hasNext();
    }

    public Transition next() {
        List<Integer> variablePositions = stateCounter.currentValues();
        StateTransition stateTransition = build(recoverVariables(variablePositions));
        int counterPosition = stateCounter.getPosition();
        stateCounter.next();
        return new Transition(code, counterPosition, stateTransition);
    }

    public String asString(Transition transition) {
        StateCounter stateCounter = new StateCounter(transition.variableStateCode, counts);
        List<Variable> variables = recoverVariables(stateCounter.currentValues());
        return String.format("%s %s", operatorName, variables.toString());
    }

    private List<Variable> recoverVariables(List<Integer> variablePositions) {
        List<Variable> variables = new ArrayList<>();
        for (int i = 0; i < variablePositions.size(); i++) {
            variables.add(variableAt(i, variablePositions.get(i)));
        }
        return variables;
    }

    protected abstract Variable variableAt(int index, int variablePosition);

    public Transition execute(Transition transition) throws Throwable {
        StateCounter stateCounter = new StateCounter(transition.variableStateCode, counts);
        List<Integer> variablePositions = stateCounter.currentValues();
        StateTransition stateTransition = execute(recoverVariables(variablePositions));
        return new Transition(code, transition.variableStateCode, stateTransition);
    }


    protected abstract StateTransition build(List<Variable> variables);

    protected abstract StateTransition execute(List<Variable> variables) throws Throwable;

    private static class StateCounter {

        private List<Element> elements;
        private boolean hasFinished;
        private int position;

        StateCounter(Integer... variableCounts) {
            this.elements = buildElements(variableCounts);
            this.hasFinished = false;
            this.position = 0;
        }

        StateCounter(int position, Integer... variableCounts) {
            this(variableCounts);
            while (this.position != position && !hasFinished) {
                next();
            }
        }

        private List<Element> buildElements(Integer... variableCounts) {
            List<Element> elements = new ArrayList<>();
            for (int count : variableCounts) {
                elements.add(new Element(count));
            }
            return elements;
        }

        boolean hasNext() {
            return !hasFinished;
        }

        int getPosition() {
            return position;
        }

        void next() {
            int elementIndex = 0;
            boolean isOver = true;
            while (isOver && elementIndex < elements.size()) {
                Element element = elements.get(elementIndex);
                element.next();
                isOver = element.isOver();
                if (isOver) {
                    element.reset();
                    elementIndex++;
                }
            }

            if (isOver) {
                hasFinished = true;
            }
            position++;
        }

        List<Integer> currentValues() {
            List<Integer> values = new ArrayList<>();
            for (Element element : elements) {
                values.add(element.value());
            }
            return values;
        }
    }

    private static class Element {

        private final int count;
        private int index;

        Element(int count) {
            this.count = count;
            this.index = 0;
        }

        void reset() {
            index = 0;
        }

        boolean isOver() {
            return index >= count;
        }

        void next() {
            index++;
        }

        int value() {
            return index;
        }
    }
}
