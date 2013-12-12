package cute.concolic.concurrency;

import cute.Cute;
import cute.concolic.Globals;
import cute.concolic.Information;
import cute.concolic.generateinputandschedule.GenerateInputAndSchedule;
import cute.concolic.logging.RaceLog;
import cute.concolic.pathconstraint.PathConstraint;
import cute.concolic.pathconstraint.ScheduleConstraint;
import cute.concolic.symbolicexecution.BranchHistory;

import java.util.*;

/**
 * Author: Koushik Sen <ksen@cs.uiuc.edu>
 */
public class Scheduler extends Thread {
    public Semaphore semaphore = new Semaphore();
    public Semaphore blocker = new Semaphore(1);
    public Semaphore waitNotifySemaphore = new Semaphore(1);


    public IdentityHashMap numbersNotified = new IdentityHashMap();

    // maps thread to thread to thread id (Thread -> Integer)
    public IdentityHashMap threadToLogicalId = new IdentityHashMap();
    // reverse map of previous map (only ids of active threads are kept)
    public TreeMap logicalIdToThread = new TreeMap();
    // maps thread id to Thread (Integer -> ThreadBase)
    public TreeMap logicalIdToThreadBase = new TreeMap();

    // maps Lock object to Thread (Object -> Thread)
    public LockBase locks = new LockBase();

    private HashMap read = new HashMap();
    private HashMap write = new HashMap();
    private HashMap lock = new HashMap();

    private LinkedList postponed = new LinkedList();
    private long lastAccessed;
    private int lastAccessestype = Scheduler.NONE;
    private WaitList waits = new WaitList();
    private WaitList joins = new WaitList();
    private Integer chosenThread = null;

    private Information information;
    private PathConstraint path;
    private cute.concolic.symbolicstate.State state;
    private BranchHistory history;
    private Random rand;
    private GenerateInputAndSchedule solver;

    public Scheduler(Information information, PathConstraint path, cute.concolic.symbolicstate.State state,
                     BranchHistory history, Random rand, GenerateInputAndSchedule solver) {
        this.information = information;
        this.path = path;
        this.state = state;
        this.history = history;
        this.rand = rand;
        this.solver = solver;
    }

    public Thread getThread(Integer i){
        return getThreadBaseFromId(i).getThread();
    }

    public Integer getLogicalId(Thread t){
        if(t==null) return null;
        Integer id = (Integer)threadToLogicalId.get(t);
        if(id==null) return null;
        else return id;
    }

    private ThreadBase getThreadBaseFromId(Integer thread) {
        return (ThreadBase)logicalIdToThreadBase.get(thread);
    }

    public ThreadBase getThreadBase(Thread t){
        if(t==null) throw  new NullPointerException();
        Integer tmp = (Integer)threadToLogicalId.get(t);
        if(tmp==null){
            tmp = new Integer(threadToLogicalId.size());
            threadToLogicalId.put(t,tmp);
            synchronized(logicalIdToThread){
                logicalIdToThread.put(tmp,t);
            }
        }
        ThreadBase tb = getThreadBaseFromId(tmp);
        if (tb==null){
            tb = new ThreadBase(t);
            logicalIdToThreadBase.put(tmp,tb);
        }
        return tb;
    }

    public ThreadBase getThreadBase(){
        Thread t = Thread.currentThread();
        return getThreadBase(t);
    }

    public static final int READ = 1;
    public static final int WRITE = 2;
    public static final int LOCK = 3;
    public static final int NONE = 4;

    private RaceLog checkAndSetRaceTmp(HashMap threadToVarInfo,Integer thisTid,ThreadBase tb,boolean reportRace){
        RaceLog rl = null;
        if(threadToVarInfo!=null){
            VarInfo vinfoa;
            for (Iterator iterator = threadToVarInfo.keySet().iterator(); iterator.hasNext();) {
                Integer tid = (Integer) iterator.next();
                if(!tid.equals(thisTid)){
                    vinfoa = (VarInfo)threadToVarInfo.get(tid);
                    if(tb.isIndependent(vinfoa.getVc()) && !tb.intersects(vinfoa.getLocks())){
                        if(tb.getLastRaceAt(tid)<vinfoa.getIndex()){
                            tb.setLastRaceAt(tid,vinfoa.getIndex());
                            ScheduleConstraint pe2 = path.getSchedule(vinfoa.getIndex());
                            if(!pe2.getPostponed().contains(thisTid))
                                pe2.setRace(true);
                            if(reportRace){
                                information.returnVal = Cute.EXIT_RACE;
                                rl = new RaceLog(vinfoa.getIndex(),path.size()-1);
//                                ExecutionLog.printRace(vinfoa.getIndex(),path.size()-1);
//                                System.err.println("Possible data race between "
//                                        +vinfoa.getIndex()+" and "
//                                        +(path.size()-1));
                            }
                        }
                    }
                }
            }
        }
        return rl;
    }

    public RacePair checkAndSetRace(long addr, int accessType, IndexInfo ii){
        ThreadBase tb = getThreadBase();
        VarInfo vinfoa;

        Integer thisTid = (Integer)threadToLogicalId.get(Thread.currentThread());
        HashMap threadToVarInfo=null;
        Long var = new Long(addr);
        RacePair rp = new RacePair();
        if(accessType == WRITE || accessType == READ){
            threadToVarInfo = (HashMap)write.get(var);
            rp.rl1 = checkAndSetRaceTmp(threadToVarInfo,thisTid,tb,true);
        }
        if(accessType==WRITE){
            threadToVarInfo = (HashMap)read.get(var);
            rp.rl2 = checkAndSetRaceTmp(threadToVarInfo,thisTid,tb,true);
        }
        if(accessType==LOCK){
            threadToVarInfo = (HashMap)lock.get(var);
            checkAndSetRaceTmp(threadToVarInfo,thisTid,tb,false);
        }

        vinfoa = new VarInfo(tb.getVc(),tb.getLocks(),path.size()-1);
        ii.index = vinfoa.getIndex();
        HashMap tmp;
        if(accessType == WRITE){
            tmp = write;
        } else if(accessType==READ){
            tmp = read;
        } else {
            tmp = lock;
        }
        threadToVarInfo = (HashMap)tmp.get(var);
        if(threadToVarInfo==null){
            threadToVarInfo = new HashMap();
            tmp.put(var,threadToVarInfo);
        }
        threadToVarInfo.put(thisTid,vinfoa);
        return rp;
    }


    public void startBefore(Thread child){
        blocker.acquire();
        ThreadBase tb2 = getThreadBase();
        ThreadBase tb = getThreadBase(child);
        tb.setVc(tb2.getVc());
        tb.addOneToVc(child);
        tb2.addOneToVc();
        information.nThreads++;
        blocker.release();
    }

    public void startAfter(){
        blocker.acquire();
        ThreadBase tb = getThreadBase();
        tb.enableAll();
        blocker.release();
        tb.acquire();
    }

    public RacePair access(long addr,boolean read,IndexInfo ii){
        blocker.acquire();
        ThreadBase tb = getThreadBase();
        tb.enableAll();

        tb.toAccess = addr;
        tb.toAccessType = read?READ:WRITE;
        semaphore.release();
        blocker.release();
        tb.acquire();
        blocker.acquire();
        tb.toAccessType = NONE;

        RacePair rp = checkAndSetRace(addr,read?READ:WRITE,ii);
        blocker.release();
        return rp;
    }

    public void lock(Object l){
        blocker.acquire();
        long addr = System.identityHashCode(l);
        if(locks.isHeldByCurrentThread(l)){
            locks.incrementCount(l);
            blocker.release();
            return;
        }
        ThreadBase tb = getThreadBase();
        tb.enableAll();
        tb.waitingOn = l;

        tb.toAccess = addr;
        tb.toAccessType = LOCK;

        semaphore.release();
        blocker.release();

        tb.acquire();

        blocker.acquire();
        tb.toAccessType = NONE;
        checkAndSetRace(addr,LOCK, new IndexInfo());
        locks.acquire(l);
        tb.addLock(l);
        blocker.release();
    }

    public void unlock(Object l){
        blocker.acquire();
        locks.decrementCount(l);
        if(!locks.isHeldByCurrentThread(l)){
            getThreadBase().removeLock();
        }
        blocker.release();
    }

    public void waitBefore(Object l){
        blocker.acquire();
        ThreadBase tb = getThreadBase();
        locks.release(l);
        tb.removeLock();
        tb.enableAll();
        tb.enabled = false;
        waits.add(l,Thread.currentThread());
        semaphore.release();
        blocker.release();
    }

    public void waitAfter(Object l){
        blocker.acquire();
        waits.remove(l,Thread.currentThread());
        long addr = System.identityHashCode(l);
        ThreadBase tb = getThreadBase();
        tb.enableAll();
        tb.waitingOn = l;

        tb.toAccess = addr;
        tb.toAccessType = LOCK;

        waitNotifySemaphore.release();
        blocker.release();

        tb.acquire();

        blocker.acquire();
        tb.toAccessType = NONE;
        checkAndSetRace(addr,LOCK, new IndexInfo());
        locks.acquire(l);
        tb.addLock(l);
        blocker.release();
    }

    public void notifyBefore(Object l){
        blocker.acquire();
        if(waits.contains(l) && !numbersNotified.containsKey(l)){
            numbersNotified.put(l,new Integer(1));
        }
        blocker.release();
    }

    public void notifyAllBefore(Object l){
        blocker.acquire();
        if(waits.size(l)>0 && !numbersNotified.containsKey(l)){
            numbersNotified.put(l,new Integer(waits.size(l)));
        }
        blocker.release();
    }

    public void joinBefore(Thread child){
        blocker.acquire();
        ThreadBase tb = getThreadBase();
        tb.enableAll();
        tb.enabled = false;
        if(logicalIdToThread.containsKey(getLogicalId(child))){
            joins.add(child,Thread.currentThread());
            semaphore.release();
        }
        blocker.release();
    }

    public void joinAfter(Thread child){
        blocker.acquire();
        ThreadBase tb = getThreadBase();
        if(!child.isAlive()){
            tb.maxVc(getThreadBase(child).getVc());
        }
        tb.enableAll();
        if(joins.contains(child)){
            joins.remove(child,Thread.currentThread());
        }
        semaphore.release();
        blocker.release();
        tb.acquire();
    }


    public void endBefore(){
        blocker.acquire();
        ThreadBase tb = getThreadBase();
        tb.enabled=false;
        logicalIdToThread.remove(getLogicalId(Thread.currentThread()));
        state.popLocals();
        if(joins.contains(Thread.currentThread())){
            semaphore.decrement(joins.size(Thread.currentThread()));
        }
        for (Iterator iterator = tb.getLocks().iterator(); iterator.hasNext();) {
            Object l = iterator.next();
            locks.release(l);
        }
        //tb.removeAllLocks();
        semaphore.release();
        blocker.release();
    }

    public int getLockDepth(Object l){
        return locks.getCount(l);
    }

    public void run() {
        while(true){
            semaphore.acquire();

            Thread.yield();

            blocker.acquire();
            assert numbersNotified.size()<=1;

            if(numbersNotified.size()==1){
                Object l = numbersNotified.keySet().toArray()[0];
                Integer N = (Integer)numbersNotified.get(l);
                if(N!=null && !locks.isHeldByAnyThread(l)){
                    int n = N.intValue();
                    if(n>0){
                        waitNotifySemaphore.decrement(n);
                        numbersNotified.remove(l);
                        blocker.release();
                        waitNotifySemaphore.acquire();
                        waitNotifySemaphore.release();
                        blocker.acquire();
                    }
                }
            }
//
//
//            blocker.acquire();
            scheduleNext();
            blocker.release();
        }
    }

    public boolean isEnabled(Thread t){
        ThreadBase tb = getThreadBase(t);
        return (t.isAlive() && tb.enabled && (tb.waitingOn==null || !locks.isHeldByAnyThread(tb.waitingOn)));
    }

    public void scheduleNext(){
        if(information.searchMode==Globals.SEARCH_RANDOM){
            scheduleNextRandom();
        } else {
            scheduleNextDirected();
        }
    }

    private void scheduleNextRandom() {
        int k = path.size();
        int l = history.size();
        boolean scheduled = false;
        ThreadBase toSchedule = null;
        int count = 0;
        ScheduleConstraint pe;
        Integer threadId = null;
        Integer lastThreadId = null;

        synchronized(logicalIdToThread){
            if (k<l-1) {
                pe = (ScheduleConstraint)history.get(k);
                path.add(pe,true);
                threadId = pe.getThreadId();
                toSchedule = getThreadBaseFromId(threadId);
                scheduled = true;
            } else {
                if(k==l-1){
                    pe = (ScheduleConstraint)history.get(k);
                    path.add(pe,true);
                    lastThreadId = pe.getThreadId();

                    TreeSet enabled = new TreeSet();
                    for (Iterator iterator = logicalIdToThread.keySet().iterator(); iterator.hasNext();) {
                        Integer integer = (Integer) iterator.next();
                        Thread t = getThread(integer);
                        if(isEnabled(t)){
                           enabled.add(integer);
                        }
                    }
                    if(enabled.size()==1){
                        chosenThread = threadId = (Integer)enabled.first();
                        toSchedule = getThreadBaseFromId(threadId);
                        scheduled = true;
                    } else if(enabled.size()==2){
                        enabled.remove(lastThreadId);
                        chosenThread = threadId = (Integer)enabled.first();
                        toSchedule = getThreadBaseFromId(threadId);
                        scheduled = true;
                    } else {
                        enabled.remove(lastThreadId);
                        int sz = enabled.size();
                        int nexti = rand.nextInt();
                        if(nexti<0) nexti = -nexti;
                        nexti = nexti  % sz;
                        Integer integer = (Integer) enabled.toArray()[nexti];
                        toSchedule = getThreadBaseFromId(integer);
                        chosenThread = threadId = integer;
                        scheduled = true;
                    }
                } else {
                    pe = new ScheduleConstraint();
                    history.add(pe);
                    path.add(pe,true);
                    if(chosenThread!=null && logicalIdToThread.containsKey(chosenThread)
                            && isEnabled(getThread(chosenThread))){
                        toSchedule = getThreadBaseFromId(chosenThread);
                        threadId = chosenThread;
                        scheduled = true;
                    } else {
                        count=0;
                        for (Iterator iterator = logicalIdToThread.keySet().iterator(); iterator.hasNext();) {
                            Integer integer = (Integer) iterator.next();
                            Thread t = getThread(integer);
                            if(t.isAlive()) {
                                count++;
                            }
                        }
                        if(count==0){
                            pe.setThreadId(threadId);
                            solver.predict();
                        }
                        int sz = logicalIdToThread.keySet().size();
                        while(!scheduled) {
                            int nexti = rand.nextInt();
                            if(nexti<0) nexti = -nexti;
                            nexti = nexti  % sz;
                            Integer integer = (Integer) logicalIdToThread.keySet().toArray()[nexti];
                            Thread t = getThread(integer);
                            if(isEnabled(t)){
                                toSchedule = getThreadBase(t);
                                chosenThread = threadId = integer;
                                scheduled = true;
                            }
                        }
                    }
                }
            }
            if(!scheduled){
                pe.setThreadId(threadId);
                solver.predict();
            } else {
                pe.setThreadId(threadId);
                toSchedule.release();
            }
        }

    }

    public void scheduleNextDirected(){
        int k = path.size();
        int l = history.size();
        boolean scheduled = false;
        ThreadBase toSchedule = null;
        int count = 0;
        int enabledThreadCount = 0;
        ScheduleConstraint pe;
        Integer threadId = null;

        synchronized(logicalIdToThread){
            if (k<l-1) {
                pe = (ScheduleConstraint)history.get(k);
                path.add(pe,true);
                threadId = pe.getThreadId();
                toSchedule = getThreadBaseFromId(threadId);
                scheduled = true;
            } else {
                if(k==l-1){
                    pe = (ScheduleConstraint)history.get(k);
                    path.add(pe,true);
                    pe.setRace(false);
                    postponed = pe.getPostponed();
                    assert(!postponed.contains(pe.getThreadId()));
                    postponed.addLast(pe.getThreadId());
                    pe.postponeCurrentThread();
                    lastAccessed = getThreadBaseFromId(pe.getThreadId()).toAccess;
                    lastAccessestype = getThreadBaseFromId(pe.getThreadId()).toAccessType;
                } else {
                    pe = new ScheduleConstraint();
                    history.add(pe);
                    path.add(pe,true);
                }
                for (Iterator iterator = logicalIdToThread.keySet().iterator(); iterator.hasNext();) {
                    Integer integer = (Integer) iterator.next();
                    Thread t = getThread(integer);
                    ThreadBase tb = getThreadBase(t);
                    if(isEnabled(t)){
                        if(!scheduled && !postponed.contains(integer)){
                            toSchedule = tb;
                            threadId = integer;
                            scheduled = true;
                        }
                        enabledThreadCount++;
                    }
                    if(t.isAlive()) {
                        count++;
                    }
                }
                pe.setEnabledThreadCount(enabledThreadCount);

                if(count==0){
                    pe.setThreadId(threadId);
                    solver.predict();
                }

                for (Iterator iterator = postponed.listIterator(); !scheduled && iterator.hasNext();) {
                    Integer integer = (Integer) iterator.next();
                    Thread t = getThread(integer);
                    if(t!=null){
                        ThreadBase tb = getThreadBase(t);
                        if(isEnabled(t)){
                            iterator.remove();
                            toSchedule = tb;
                            threadId = integer;
                            scheduled = true;
                        }
                    }
                }
            }

            if(!scheduled){
                System.err.println("************************ Deadlock found ****************************");
                pe.setThreadId(threadId);
                information.returnVal += Cute.EXIT_DEADLOCK;
                solver.predict();
            } else {
                if(lastAccessed == toSchedule.toAccess &&
                        ((toSchedule.toAccessType==LOCK && lastAccessestype==LOCK)
                        || (toSchedule.toAccessType == WRITE && lastAccessestype==READ)
                        || (toSchedule.toAccessType == WRITE && lastAccessestype==WRITE)
                        || (toSchedule.toAccessType == READ && lastAccessestype==READ))){
                    lastAccessestype = NONE;
                    pe.setPostponed(postponed);
                }

                pe.setThreadId(threadId);
                toSchedule.release();
            }
        }
    }

}


