class JCuteGuiExecLog2 {
    private static void addResults(cute.gui.JCuteTextUI tui,int sz){
        for(int i=0;i<sz-1;i++){
            tui.addExitValue(new Integer(0));
        }
        tui.addExitValue(new Integer(2));
    }

    private static void addResults2(cute.gui.JCuteTextUI tui,int sz){
        for(int i=0;i<sz;i++){
            tui.addExitValue(new Integer(0));
        }
    }

	public static void main(String args[]){
		cute.gui.JCuteTextUI tui = new cute.gui.JCuteTextUI(false);
		tui.updateSoftCompleted();
		tui.setSrcDirName(System.getProperty("user.dir")+"/src");
		tui.setSrcFileName(System.getProperty("user.dir")+"/src/tests/Regression.java");
		tui.setCompilableExt();
		tui.setOptionExtraOptions("");
		tui.setOptionSequential(true);
		tui.setOptionPrintOutput(true);
		tui.setOptionGenerateJUnit(false);
		tui.setOptionLogPath(false);
		tui.setOptionLogTraceAndInput(false);
		tui.setOptionLogLevel(2);
		tui.setOptionNumberOfPaths(10000);
		tui.setOptionDepthForDFS(0);
		tui.setOptionQuickSearchThreshold(100);
		tui.setOptionSearchStrategy(0);
		tui.setOptionJUnitOutputFolderName(".");
		tui.setOptionJUnitPkgName("");
		tui.updateSoftCompleted();
		tui.setMainClassNamePlusFun("tests.Regression.test1");
		tui.compileAction();
		tui.deleteAction();
		addResults2(tui,5);
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test2");
		addResults(tui,2);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test3");
		addResults(tui,2);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test4");
		addResults(tui,3);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test5");
		addResults(tui,3);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test6");
		addResults(tui,2);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test7");
		addResults(tui,2);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test8");
		addResults(tui,2);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test9");
		addResults(tui,2);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test10");
		addResults(tui,3);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test11");
		addResults(tui,3);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test12");
		addResults(tui,2);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test13");
		addResults(tui,3);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test14");
		addResults(tui,2);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test15");
		addResults(tui,3);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test16");
		addResults(tui,2);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test17");
		addResults(tui,3);
		tui.deleteAction();
		tui.continueAction("");

		tui.setMainClassNamePlusFun("tests.Regression.test18");
		addResults(tui,6);
		tui.deleteAction();
		tui.continueAction("");

		tui.quitAction();
	}
}
