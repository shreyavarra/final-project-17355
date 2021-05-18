package edu.cmu.se355.recitation;

import soot.PackManager;
import soot.Scene;
import soot.Transform;

import java.util.Set;
import java.util.HashSet;

import edu.cmu.se355.common.Utils;

import org.junit.Test;
import org.junit.Assert;


public class PrintLiveAnalysisMain {
    /** The abbreviated name of the analysis */
    public static final String ANALYSIS_NAME = "jap.defsanalysis";

    /** Runs Soot with PrintGuaranteedDefs available */
    public static void main(String[] args) {
        // Inject the analysis tagger into Soot
        PackManager.v().getPack("jap")
            .add(new Transform(ANALYSIS_NAME, PrintLiveAnalysis.instance()));

        // run Soot with the arguments given
        // Utils.runSoot() keeps Soot from calling System.exit() if we are
        // invoked from JUnit The analysis should record which fields are read
        System.out.println("Args: " + args.toString());
        Utils.runSoot(args);

        System.out.println("total warnings: " + Utils.getErrors().size());
    }

    /** A JUnit test for Guaranteed Defs Analysis */
	@Test
    public void testGuaranteedDefsAnalysis() {
        PrintLiveAnalysisMain.main(Utils.getSootArgs(PrintLiveAnalysisMain.ANALYSIS_NAME, "edu.cmu.se355.recitation.TestLiveAnalysis"));
    }
}
