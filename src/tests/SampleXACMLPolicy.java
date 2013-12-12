package tests;


import cute.Cute;

public class SampleXACMLPolicy
{
   private static final int DENY = 0;
   private static final int PERMIT = 1;

   public int getDecision(boolean isTeacher,
                          boolean isStudent,
                          boolean receive,
                          boolean assign,
                          boolean grades)
   {
      if (isTeacher)
      {
         if (receive)
         {
            if (grades)
            {
               Cute.Assert(true);
               return PERMIT;
            }
         }
         if (assign)
         {
            if (grades)
            {
               Cute.Assert(true);
               return PERMIT;
            }
         }
      }
      else if (isStudent)
      {
         if (receive)
         {
            if (grades)
            {
               Cute.Assert(true);
               return PERMIT;
            }
         }
         if (assign)
         {
            if (grades)
            {
               Cute.Assert(true);
               return DENY;
            }
         }
      }
      Cute.Assert(true);
      return DENY;
   }

   public static void main(String[] args)
   {
      boolean isTeacher = Cute.input.Boolean();
      boolean isStudent = Cute.input.Boolean();
      boolean receive = Cute.input.Boolean();
      boolean assign = Cute.input.Boolean();
      boolean grades = Cute.input.Boolean();
      SampleXACMLPolicy policy = new SampleXACMLPolicy();
      int decision = policy.getDecision(isTeacher, isStudent, receive, assign, grades);
      Cute.Assert(decision == PERMIT || decision == DENY);
   }
}
//@The following comments are auto-generated to save options for testing the current file
//@jcute.optionPrintOutput=true
//@jcute.optionLogPath=true
//@jcute.optionLogTraceAndInput=true
//@jcute.optionGenerateJUnit=true
//@jcute.optionExtraOptions=
//@jcute.optionJUnitOutputFolderName=d:\sync\work\cute\java
//@jcute.optionJUnitPkgName=
//@jcute.optionNumberOfPaths=100
//@jcute.optionLogLevel=2
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=true
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=true
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=true
//@jcute.optionUseRandomInputs=false
