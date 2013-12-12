package tests;



//
// Copyright (C) 2005 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

/**
 * This example shows a simple deadlock.
 */
public class Deadlock implements Runnable {
  /**
   * A name for the thread.
   */
  String name;

  /**
   * A fererence to the other Deadlock object running as a seperate thread.
   */
  Deadlock other;

  public Deadlock (String name) {
    this.name = name;
  }

  public static void main (String[] args) {
    Deadlock o1 = new Deadlock("A");
    Deadlock o2 = new Deadlock("B");

    o1.other = o2;
    o2.other = o1;

    Thread t1 = new Thread(o1);
    Thread t2 = new Thread(o2);

    t1.start();
    t2.start();
  }

  public void run () {
    for(int i=0;i<3;i++) {
      System.out.println(name + " cycle start");
      synchronized (this) {
        other.foo();
      }

      System.out.println(name + " cycle end");
    }
  }


  synchronized void foo () {
    System.out.println(name + ".foo() was called");
  }
}