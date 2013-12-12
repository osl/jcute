package cute.instrument;

import soot.*;
import soot.jimple.Stmt;
import soot.util.Chain;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 7, 2005
 * Time: 5:51:18 PM
 */
public class AddValue {
    public static void instrument(Body b,Value v,Chain units,Stmt s,String call,boolean before,int lineNo,boolean isCond){
        Type type = v.getType();
        if(type instanceof IntType){
            AddCallWithValue.instrument(b,v,units,s,IntType.v(),"int",lineNo,call, before);
        } else if(type instanceof LongType){
            AddCallWithValue.instrument(b,v,units,s,LongType.v(),"long",lineNo,call, before);
        } else if(type instanceof ByteType){
            AddCallWithValue.instrument(b,v,units,s,ByteType.v(),"byte",lineNo,call, before);
        } else if(type instanceof ShortType){
            AddCallWithValue.instrument(b,v,units,s,ShortType.v(),"short",lineNo,call, before);
        } else if(type instanceof CharType){
            AddCallWithValue.instrument(b,v,units,s,CharType.v(),"char",lineNo,call, before);
        } else if(type instanceof FloatType){
            AddCallWithValue.instrument(b,v,units,s,FloatType.v(),"float",lineNo,call, before);
        } else if(type instanceof DoubleType){
            AddCallWithValue.instrument(b,v,units,s,DoubleType.v(),"double",lineNo,call, before);
        } else if(type instanceof BooleanType){
            AddCallWithValue.instrument(b,v,units,s,BooleanType.v(),"boolean",lineNo,call, before);
        } else if(type instanceof RefType && isCond){
            AddCallWithValue.instrument(b,v,units,s,RefType.v("java.lang.Object"),"java.lang.Object",lineNo,call, before);
        } else if(type instanceof RefType && !isCond){
            AddCallWithDummyValue.instrument(units,s,lineNo);
        } else if(type instanceof ArrayType && isCond){
            AddCallWithValue.instrument(b,v,units,s,RefType.v("java.lang.Object"),"java.lang.Object",lineNo,call, before);
        } else if(type instanceof ArrayType && !isCond){
            AddCallWithDummyValue.instrument(units,s,lineNo);
        } else if(type.toString().equals("null_type")){
            AddCallWithValue.instrument(b,v,units,s,RefType.v("java.lang.Object"),"java.lang.Object",lineNo,call, before);
        }
    }

}
