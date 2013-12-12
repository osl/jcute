package tests;

import cute.Cute;

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
public class If {
    int x;
    int y;

    public int f(int x){
        return x*2+1;

    }

    public int h(int x,int y){
        this.y = y;
        if(this.y==0)
            System.out.println("y==0");
        else
            System.out.println("y==1");
        Cute.Assume(f(x)==11);
        System.out.println("ERROR");
        Cute.Assert(y==0);
        return x+this.x;
    }

    public static void main(String[] args) {
        int x;
        int y;

        x = Cute.input.Integer();
        y = Cute.input.Integer();
        If i = new If();
        i.h(x,y);
    }
}
