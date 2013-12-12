package tests;

/**
 * Copyright (c) 2004
 * <p/>
 * Koushik Sen <ksen@cs.uiuc.edu>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class LocksHeld {
    public static void visit(ThreadGroup group, int level) {
        int numThreads = group.activeCount();
        Thread[] threads = new Thread[numThreads];
        numThreads = group.enumerate(threads, false);

        for (int i=0; i<numThreads; i++) {
            Thread thread = threads[i];
            if(!(thread.getName().equals("Reference Handler") || thread.getName().equals("Finalizer")
                    || thread.getName().equals("Signal Dispatcher")))
                System.out.println("thread is "+thread+" "+level);
        }
        int numGroups = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[numGroups];
        numGroups = group.enumerate(groups, false);

        for (int i=0; i<numGroups; i++) {
            visit(groups[i], level+1);
        }
    }

    public static void main(String[] args) {
        ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
        while (root.getParent() != null) {
            root = root.getParent();
        }
        visit(root, 0);
    }
}
