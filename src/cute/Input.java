package cute;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 16, 2005
 * Time: 9:15:23 AM
 */
public interface Input {
    public boolean Boolean();
    public short Short();
    public int Integer();
    public long Long();
    public float Float();
    public double Double();
    public char Character();
    public byte Byte();
    public Object Object(String type);
    public Object ObjectShallow(String type);
}
