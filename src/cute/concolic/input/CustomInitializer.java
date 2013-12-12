package cute.concolic.input;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jul 23, 2006
 * Time: 1:35:17 AM
 */
public class CustomInitializer {
    public static Object getNewInstance(Class c, boolean random, Random rand) {
        String name = c.getName();
        if(name.equals("java.lang.Integer"))
        { if(random) return new Integer(rand.nextInt()); else return new Integer(0);}
        if(name.equals("java.lang.Long"))
        { if(random) return new Long(rand.nextLong()); else return new Long(0);}
        if(name.equals("java.lang.Short"))
        { if(random) return new Short((short)rand.nextInt()); else return new Short((short)0);}
        if(name.equals("java.lang.Character"))
        { if(random) return new Character((char)rand.nextInt()); else return new Character((char)0);}
        if(name.equals("java.lang.Byte"))
        { if(random) return new Byte((byte)rand.nextInt()); else return new Byte((byte)0);}
        if(name.equals("java.lang.Boolean"))
        { if(random) return new Boolean(rand.nextBoolean()); else return new Boolean(false);}
        if(name.equals("java.lang.Float"))
        { if(random) return new Float(rand.nextFloat()); else return new Float(0);}
        if(name.equals("java.lang.Double"))
        { if(random) return new Double(rand.nextDouble()); else return new Double(0);}

        return null;
    }
}
