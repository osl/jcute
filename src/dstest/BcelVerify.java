package dstest;

import org.apache.bcel.verifier.Verifier;
import org.apache.bcel.verifier.VerifierFactory;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 26, 2005
 * Time: 10:59:31 AM
 */
public class BcelVerify {
    public static void main(String[] args) {
        JavaClass java_class1 = null;
        try {
            java_class1 = new ClassParser(args[0]).parse();
            Verifier v;
            v= VerifierFactory.getVerifier(java_class1.getClassName());
            System.out.println(v.doPass1());
            System.out.println(v.doPass2());
            System.out.println(v.doPass3a(1));
            System.out.println(v.doPass3b(1));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
