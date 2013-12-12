/*
 * Copied from Java I/O by Elliotte
 * Rusty Harold, page 43
 */

package ccl.util;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Copied from Java I/O by Elliotte
 * Rusty Harold, page 43 [1999-07-30 Chr. Clemens Lee].
 * 
 * @author  Elliotte Rusty Harold
 * @version $Id: StreamCopier.java,v 1.1 2005/10/30 22:22:35 ksen Exp $
 */
public class StreamCopier 
{
    public StreamCopier() 
    {
        super();
    }

    /**
     * @return   error
     */
    public static boolean copy( InputStream pInputStream_,
                                OutputStream pOutputStream_ )
    {
        try 
        {
            synchronized( pInputStream_ ) 
                   {
                       synchronized( pOutputStream_ ) 
                              {
                                  byte[] abBuffer = new byte[ 256 ];
                                  while( true ) 
                                  {
                                      int bytesRead = pInputStream_.read( abBuffer );
                                      if ( bytesRead == -1 ) 
                                      {
                                          break;
                                      }
                                      pOutputStream_.write( abBuffer, 0, bytesRead );
                                  }
                              }
                   }
        }
        catch( Exception pException ) 
        {
            Util.printlnErr( "ccl.util.StreamCopier.copy(..).pException: " + pException );
            
            return true;
        }

        return false;
    }
}
