/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package edu.cmu.se355.recitation;

import java.util.*;

import soot.*;
import soot.jimple.DefinitionStmt;
import soot.shimple.*;
import soot.options.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.toolkits.scalar.BackwardFlowAnalysis;

/**
 * Find all locals guaranteed to be defined at (just before) a given
 * program point.
 *
 * @author Navindra Umanee; modified by Jenna Wise, Jeremy Lacomis, and Priya Varra
 **/

public class LiveAnalysis {
    protected Map<Unit, List> unitToGuaranteedDefs;

    public LiveAnalysis(UnitGraph graph) {
        G.v().out.println("[" + graph.getBody().getMethod().getName() +
                          "]     Constructing Live Analysis...");

        GuaranteedDefsAnalysis analysis = new GuaranteedDefsAnalysis(graph);

        // build map of program statement to locals guaranteed to be defined
        // just before it
        unitToGuaranteedDefs = new HashMap<Unit, List>(graph.size() * 2 + 1, 0.7f);

        Iterator unitIt = graph.iterator();
        while(unitIt.hasNext()) {
            Unit s = (Unit) unitIt.next();
            FlowSet<Unit> set = (FlowSet) analysis.getFlowBefore(s);
         // CHANGE this to say unit to Live Variable
            unitToGuaranteedDefs.put(s, Collections.unmodifiableList(set.toList()));
        }
    }
    /**
     * Returns a list of locals guaranteed to be defined at (just
     * before) program point <tt>s</tt>.
     **/
    public List getGuaranteedDefs(Unit s) {
        return unitToGuaranteedDefs.get(s);
    }
}

/**
 * Flow analysis to determine all locals guaranteed to be defined at a
 * given program point.
 **/
class GuaranteedDefsAnalysis extends BackwardFlowAnalysis<Unit, FlowSet<Local>> {
    private FlowSet<Local> emptySet;
    // maps Units (program stmts) to FlowSets (Soot's set lattice) representing
    // the GEN set
    Map<Unit, FlowSet<Local>> setsAt;

    /**
     * All INs are initialized to emptySet
     **/
    protected FlowSet<Local> newInitialFlow() {
        // TODO: return All INs
        return emptySet.clone();
    }

    /**
     * IN (Start) is emptySet
     **/
    protected FlowSet<Local> entryInitialFlow() {
        // TODO: return IN (Start)
        return emptySet.clone();
    }

    protected void copy(FlowSet<Local> source, FlowSet<Local> dest) {
        source.copy(dest);
    }

    /**
     * All paths joined by intersection
     **/
    protected void merge(FlowSet<Local> in1, FlowSet<Local> in2, FlowSet<Local> out) {

        // TODO: implement the join operator on data flow information
        in1.union(in2, out);

    }

    /**
     * OUT is union with variables defined by the statement
     **/
    protected void flowThrough(FlowSet<Local> inSet, Unit node, FlowSet<Local> outSet) {
        FlowSet<Local> writes = (FlowSet)emptySet.clone();

        System.out.println("Statement: ");
        System.out.println(node.toString());

        for (ValueBox def : node.getUseAndDefBoxes()) {
            if (def.getValue() instanceof Local) {
                Local def_local = (Local) def.getValue();
                System.out.println("Adding var: ");
                System.out.println(def_local);
                writes.add(def_local);
            }
        }
        inSet.difference(writes, outSet);

        for (ValueBox use : node.getUseBoxes()) {
            if (use.getValue() instanceof Local) {
                outSet.add((Local) use.getValue());
            }
        }

        System.out.println("Printing live in");
        System.out.println(outSet.toString());
        setsAt.put(node, outSet);
    }

    GuaranteedDefsAnalysis(DirectedGraph graph) {
        super(graph);

        emptySet = new ArraySparseSet<Local>();
        setsAt = new HashMap<>(graph.size() * 2 + 1);

        // Run the analysis
        doAnalysis();
        System.out.println("Printing results: ");
        System.out.println(setsAt.toString());
        System.out.println("Finished printing results");
    }
}
