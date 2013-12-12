package bugs;

class Node {
    public int value;

    public Node left, right;

    public Node(int x) {
        value = x;
        left = null;
        right = null;
    }

}

public class JCuteBinTreeBare {

    static JCuteBinTreeBare act = new JCuteBinTreeBare();

    private Node root;

    public JCuteBinTreeBare() {
        root = null;
    }

    public void add(int x) {
        Node current = root;

        if (root == null) {

            root = new Node(x);
            return;
        }

        while (current.value != x) {
            if (x < current.value) {
                if (current.left == null) {

                    current.left = new Node(x);
                } else {

                    current = current.left;
                }
            } else {
                if (current.right == null) {

                    current.right = new Node(x);
                } else {

                    current = current.right;
                }
            }
        }
    }

    private boolean find(int x) {
        Node current = root;

        while (current != null) {

            if (current.value == x) {

                return true;
            }

            if (x < current.value) {

                current = current.left;
            } else {

                current = current.right;
            }
        }


        return false;
    }

    public boolean remove(int x) {
        Node current = root;
        Node parent = null;
        boolean branch = true; //true =left, false =right

        while (current != null) {

            if (current.value == x) {
                Node bigson = current;
                while (bigson.left != null || bigson.right != null) {
                    parent = bigson;
                    if (bigson.right != null) {

                        bigson = bigson.right;
                        branch = false;
                    } else {

                        bigson = bigson.left;
                        branch = true;
                    }
                }

                //		System.out.println("Remove: current "+current.value+" parent "+parent.value+" bigson "+bigson.value);
                if (parent != null) {
                    if (branch) {

                        parent.left = null;
                    } else {

                        parent.right = null;
                    }
                }

                if (bigson != current) {

                    current.value = bigson.value;
                } else {

                }

                return true;
            }

            parent = current;
            //	    if (current.value <x ) { // THERE WAS ERROR
            if (current.value > x) {

                current = current.left;
                branch = true;
            } else {

                current = current.right;
                branch = false;
            }
        }


        return false;
    }

    static void step() {
        int i = cute.Cute.input.Integer();
        int x = cute.Cute.input.Integer();
        switch (i) {
            case 0:
                act.add(x);
                break;
            case 1:
                act.remove(x);
                break;
        }
    }

    static void unbounded() {
        for (int i = 0 ; i < 10 ; i++) {
            step();
        }
    }


    public static void main(String[] Argv) {
        unbounded();
    }
}
//@The following comments are auto-generated to save options for testing the current file
//@jcute.optionPrintOutput=true
//@jcute.optionLogPath=false
//@jcute.optionLogTraceAndInput=false
//@jcute.optionGenerateJUnit=false
//@jcute.optionExtraOptions=
//@jcute.optionJUnitOutputFolderName=d:\sync\work\cute\java
//@jcute.optionJUnitPkgName=
//@jcute.optionNumberOfPaths=100
//@jcute.optionLogLevel=2
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=1
//@jcute.optionSequential=true
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=true
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=true
//@jcute.optionUseRandomInputs=false
