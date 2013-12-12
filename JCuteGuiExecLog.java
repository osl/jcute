class JCuteGuiExecLog {
	public static void main(String args[]){
		cute.gui.JCuteTextUI tui = new cute.gui.JCuteTextUI(false);
		tui.updateSoftCompleted();
		tui.updateSoftCompleted();
		tui.setOptionExtraOptions("");
		tui.setOptionSequential(true);
		tui.setOptionPrintOutput(true);
		tui.setOptionGenerateJUnit(true);
		tui.setOptionLogPath(true);
		tui.setOptionLogTraceAndInput(true);
		tui.setOptionLogLevel(3);
		tui.setOptionLogAssertion(true);
		tui.setOptionLogException(true);
		tui.setOptionLogDeadlock(true);
		tui.setOptionLogRace(true);
		tui.setOptionNumberOfPaths(10000);
		tui.setOptionDepthForDFS(0);
		tui.setOptionQuickSearchThreshold(100);
		tui.setOptionSearchStrategy(0);
		tui.setOptionJUnitOutputFolderName("d:\\sync\\work\\cute\\java");
		tui.setOptionJUnitPkgName("");
		tui.updateSoftCompleted();
		tui.quitAction();
	}
}
