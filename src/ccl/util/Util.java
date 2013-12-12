/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is the reusable ccl java library
 * (http://www.kclee.com/clemens/java/ccl/).
 *
 * The Initial Developer of the Original Code is
 * Chr. Clemens Lee.
 * Portions created by Chr. Clemens Lee are Copyright (C) 2002
 * Chr. Clemens Lee. All Rights Reserved.
 *
 * Contributor(s): Chr. Clemens Lee <clemens@kclee.com>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package ccl.util;

import java.io.*;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

interface Comparable {
    public int compare(Object o1,Object o2);
}

/**
 * A general purpose class with a variety of support and convenience methods.
 *
 * <p> There are different groups of methods in this class:
 * <br>
 * <br><a href="#setDebug(boolean)">debug and assertion methods</a>
 * <br><a href="#print(char)">print methods</a> - convenience methods for System.out.print etc. that additionally make sure output is gets flushed immediately.
 * <br><a href="#atoi(java.lang.String)">basic converter methods</a>
 * <br><a href="#isEmpty(java.lang.String)">string methods</a>
 * <br><a href="#concat(java.util.Vector)">string/vector converter methods</a>
 * <br><a href="#toVector(java.util.Enumeration)">vector methods</a>
 * <br><a href="#quickSort(java.lang.Object[], int, int, ccl.util.Comparable)">sorting and inserting methods</a>
 * <br><a href="#system(java.lang.String)">system methods</a>
 * <br><a href="#getStandardDate(java.util.Date)">date methods</a>
 * <br><a href="#rnd()">random generator methods</a>
 * <br><a href="#getConstantObject()">miscellaneous methods</a>
 * <p>
 *
 * Some basic but none the less the most used methods by myself are:<br>
 * - {@link #println(java.lang.String) println}<br>
 * - {@link #printlnErr(java.lang.Exception) printlnErr}<br>
 * - {@link #isEmpty(java.lang.String) isEmpty}<br>
 * - {@link #stringToLines(java.lang.String) stringToLines}<br>
 * - {@link #atoi(java.lang.String) atoi}<br>
 * - {@link #itoa(int) itoa}<br>
 * - {@link #systemAndWait(java.util.Vector) systemAndWait}<br>
 * - {@link #sleep(int) sleep}<br>
 * - {@link #getDate() getDate}
 * <p>
 *
 * But there are also some gems which are less regularly used but still quite
 * usefull:<br>
 * - {@link #getStackTrace(java.lang.Throwable)}<br>
 * - {@link #getDump(java.lang.Object)}<br>
 * - {@link #formatMemoryInfo()}<br>
 * - {@link #denullify(java.lang.String)}<br>
 * - {@link #formatBlock(java.lang.String, int)}
 * <p>
 *
 * Potential future but not yet existing classes to move some code into 
 * are:<br>
 * StringUtil, VectorUtil, SystemUtil, and maybe Debug.
 *
 * @version  $Id: Util.java,v 1.1 2005/10/30 22:22:35 ksen Exp $
 * @author <a href="http://www.kclee.com/clemens/">
 *         Chr. Clemens Lee</a>
 *         &lt;<a href="mailto:clemens@kclee.com">
 *         clemens@kclee.com
 *         </a>>
 */
public class Util 
{
    // -----------------------------------------------------
    // attributes
    // -----------------------------------------------------

    private static Random       _rnd              = new Random();
    private static Object       _objSwap          = null;
    private static boolean      _bNochKeinSwap    = true;
    private static int          _swap             = -1;
    private static boolean      _bNochKeinIntSwap = true;

    private static final char[] AC_UMLAUT = { 'ä', 'Ä', 'ö', 'Ö',
                                              'ü', 'Ü', 'ß', 'é' };

    /**
     * Used to store the debug state. Can be changed during
     * runtime for more flexibility. This is more important
     * to me than speed.
     */
    private static boolean _bDebug = false;
    
    /**
     * This could be also private and instead you would have
     * to use getConstantObject(), but yet you have the 
     * choice.
     *
     * @see #getConstantObject()
     */
    public static final Object O_CONSTANT = new Object();

    // -----------------------------------------------------
    // constructor
    // -----------------------------------------------------

    /**
     * This is an utility class, there is (should be) no need
     * for an instance of this class.
     */
    private Util() 
    {
        super();
    }


    // -----------------------------------------------------
    // debug methods and assertion stuff
    // -----------------------------------------------------

    /**
     * Sets the debug mode for the running application. When
     * true, all following debug statements are equal to println
     * statements, otherwise they are ignored.
     *
     * @see #debug(Object)
     */
    public static void setDebug( boolean bDebug_ ) 
    {
        _bDebug = bDebug_;
    }

    /**
     * Returns the current debug mode.
     *
     * @return   the current debug mode.
     */
    public static boolean isDebug() 
    {
        return _bDebug;
    }

    /**
     * Sets the debug mode for the running application. When
     * true, all following debug statements are equal to
     * printlnErr statements, otherwise they are ignored.
     *
     * @see #debug(Object)
     */
    public static void setDebug( String sDebug_ ) 
    {
        setDebug( atob( sDebug_ ) );
    }
    
    /**
     * If the debug mode was set with the setDebug function
     * to true, this debug statements is equal to a printlnErr
     * statement, otherwise it will be ignored.
     *
     * @see #setDebug(boolean)
     */
    public static void debug( Object oMessage_ ) 
    {
        if ( _bDebug ) 
        {
            printlnErr( oMessage_.toString() );
        }
    }

    /**
     * If the debug mode was set with the setDebug function
     * to true, this debug statements is equal to a printlnErr
     * statement, otherwise it will be ignored.
     *
     * @see #setDebug(boolean)
     */
    public static void debug(int i) 
    {
        debug( "int: " + i );
    }

    /**
     * If the debug mode was set with the setDebug function
     * to true, this debug statements is equal to a printlnErr
     * statement, otherwise it will be ignored.
     *
     * @see #setDebug(boolean)
     * @see #printlnErr(java.lang.Object, java.lang.Object)
     */
    public static void debug( Object oOriginator_,
                              Object oMessage_ )
    {
        if ( _bDebug ) 
        {
            printlnErr( oOriginator_, oMessage_ );
        }
    }

    /**
     * panicIf <=> not assert. Throws ApplicationException if true.
     * It's not necessary to catch this exception.
     *
     * @see ApplicationException
     */
    public static void panicIf(boolean bPanic_) 
    {
        if (bPanic_) 
        {
            throw(new ApplicationException());
        }
    }

    /**
     * panicIf <=> not assert. Throws ApplicationException if true.
     * It's not necessary to catch this exception.
     *
     * @param sMessage_ The error message for the Exception.
     *
     * @see ApplicationException
     */
    public static void panicIf(boolean bPanic_, String sMessage_) 
    {
        if (bPanic_) 
        {
            throw(new ApplicationException(sMessage_));
        }
    }
    
    /**
     * Most people are used to assertions. But I once came up
     * with panicIf(boolean) and that's what I still prefer
     * to use.
     *
     * @deprecated   use 'assert' keyword from jdk 1.4 or above.
     */
//    public static void assert( boolean bAssert_ )
//    {
//        panicIf( !bAssert_ );
//    }
//
//    /**
//     * Assert pObject is not null.
//     *
//     * @deprecated   use 'assert' keyword from jdk 1.4 or above.
//     */
//    public static void assert( Object pObject_ )
//    {
//        panicIf( pObject_ == null );
//    }

    // -----------------------------------------------------
    // print methods
    // -----------------------------------------------------

    /**
     * Prints out a char to System.out.
     * Unlike using System.out directly this
     * method makes sure the content gets flushed out immediately.
     */
    public static void print(char c_) 
    {
        System.out.print(c_);
        System.out.flush();
    }
    
    /**
     * Prints out a String to System.out.
     * Unlike using System.out directly this
     * method makes sure the content gets flushed out immediately.
     */
    public static void print(String pString_) 
    {
        System.out.print(pString_);
        System.out.flush();
    }
    
    /**
     * Prints out the object to System.out.
     * Unlike using System.out directly this
     * method makes sure the content gets flushed out immediately.
     */
    public static void print(Object pObject_) 
    {
        if (pObject_ == null) 
        {
            print("null");
        }
        else 
        {
            print(pObject_.toString());
        }
    }
    
    /**
     * Prints out a String to System.out together with a
     * new line.
     * Unlike using System.out directly this
     * method makes sure the content gets flushed out immediately.
     *
     * @param   pString_   a string without a trailing newline to send
     *                     to standard output.
     */
    public static void println( String pString_ ) 
    {
        System.out.println(pString_);
        System.out.flush();
    }
    
    /**
     * Prints out the exception, its stack trace,
     * and the current thread to System.out!
     * Unlike using System.out directly this
     * method makes sure the content gets flushed out immediately.
     *
     * @see #printlnErr(java.lang.Exception) printlnErr
     */
    public static void println(Exception e) 
    {
        System.out.println("Exception: " + e.getMessage());
        /*//e.fillInStackTrace();
          //e.printStackTrace(System.out);*/
               Thread.dumpStack();
               println(Thread.currentThread().toString());
    }

    /**
     * Prints out the object to System.out together with a
     * new line.
     * Unlike using System.out directly this
     * method makes sure the content gets flushed out immediately.
     */
    public static void println(Object pObject_) 
    {
        print(pObject_);
        print('\n');
    }

    /**
     * Same as print('\n').
     *
     * @see #print(char) print
     */
    public static void println() 
    {
        print('\n');
    }
    
    /**
     * Prints out a char to System.err.
     */
    public static void printErr(char c_) 
    {
        System.err.print(c_);
        System.err.flush();
    }

    /**
     * Prints out a String to System.err.
     */
    public static void printErr(String pString_) 
    {
        System.err.print(pString_);
        System.err.flush();
    }
    
    /**
     * Prints out the object to System.err.
     */
    public static void printErr( Object pObject_ ) 
    {
        if (pObject_ == null) 
        {
            printErr( "null" );
        }
        else 
        {
            printErr( pObject_.toString() );
        }
    }

    /**
     * The same as println, except output goes to std err.
     */
    public static void printlnErr() 
    {
        printErr( '\n' );
    }
        
    /**
     * The same as println, except output goes to std err.
     * Unlike using System.err directly this
     * method makes sure the content gets flushed out immediately.
     *
     * @param   sMessage_   a string without a trailing newline to send
     *                      to standard error.
     */
    public static void printlnErr( String sMessage_ ) 
    {
        printErr( sMessage_ );
        printlnErr();
    }
        
    /**
     * Prints out the object to System.err.
     * Unlike using System.err directly this
     * method makes sure the content gets flushed out immediately.
     *
     * @param   pObject_   an object that will be converted to a string which
     *                     will be sent to standard error with a newline appended.
     */
    public static void printlnErr( Object pObject_ ) 
    {
        if ( pObject_ == null ) 
        {
            printlnErr( "null" );
        }
        else 
        {
            printlnErr( pObject_.toString() );
        }
    }

    /**
     * Prints out an error report for an exception to 
     * System.err. Prints out the exception message
     * followed by its stack trace.
     * Unlike using System.err directly this
     * method makes sure the content gets flushed out immediately.
     * 
     * @param   exception_   the exception to print to
     *                       standard error.
     */
    public static void printlnErr( Exception exception_ ) 
    {
        printlnErr( exception_.getMessage()     );
        printlnErr( getStackTrace( exception_ ) );
    }

    /**
     * Prints out a String with a prefix of the oClass_ class
     * name to System.err.
     */
    public static void printlnErr( Object oClass_, Object oMessage_ ) 
    {
        Util.panicIf( oClass_ == null );
        printErr( oClass_.getClass().getName() + "." );
        printlnErr( oMessage_ );
    }

    /**
     * For batch applications to indicate progess to the user.
     */
    public static void showLiveSignal() 
    {
        showLiveSignal( '.' );
    }

    /**
     * For batch applications to indicate progess to the user.
     *
     * @param    c_   The char that should be printed to
     *                stdout.
     */
    public static void showLiveSignal( char c_ ) 
    {
        print( c_ );
    }

    // -----------------------------------------------------
    // basic converter methods
    // -----------------------------------------------------

    /**
     * String to int converter.
     */
    public static int atoi( String pString_ ) 
    {
        if (isEmpty(pString_)) 
        {
            return 0;
        }

        int result = 0;
        boolean bNegative = false;
        int index = 0;
        int max = pString_.length();
        int limit = 0;
        int multmin = 0;
        int digit = 0;

        while( (index < max - 1) && pString_.charAt( index ) == ' ') 
        {
            index++;
        }
        if ( pString_.charAt( index ) == '-') 
        {
            bNegative = true;
            limit = Integer.MIN_VALUE;
            index++;
        }
        else 
        {
            limit = -Integer.MAX_VALUE;
        }
        multmin = limit / 10;
        if ( index < max ) 
        {
            digit = Character.digit( pString_.charAt( index++ ), 10 );
            if ( digit < 0 ) 
            {
                return 0;
            }
            else 
            {
                result = -digit;
            }
        }
        while( index < max ) 
        {
            // Accumulating negatively avoids surprises near MAX_VALUE
            digit = Character.digit( pString_.charAt( index++ ), 10 );
            if ( digit < 0 ) 
            {
                break;
            }
            if ( result < multmin ) 
            {
                break;
            }
            result *= 10;
            if ( result < limit + digit ) 
            {
                break;
            }
            result -= digit;
        }

        if ( !bNegative ) 
        {
            result = -result;
        }

        return( result );
    }

    /**
     * String to long converter.
     */
    public static long atol( String pString_ ) 
    {
        return Long.parseLong( pString_, 10 );
    }

    /**
     * Converts an int to a String.
     */
    public static String itoa( int i_ ) 
    {
        return(String.valueOf(i_));
    }

    /**
     * Converts an long to a String.
     */
    public static String ltoa( long l_ ) 
    {
        return( String.valueOf( l_ ) );
    }

    /**
     * Converts a double to a String.
     */
    public static String dtoa( double d_ ) 
    {
        return( String.valueOf( d_ ) );
    }

    /**
     * String to float converter. An empty string gets
     * converted to 0.0, while a string that is not 
     * adequately formatted results in NaN (not a number).
     */
    public static float atof( String pString_ ) 
    {
        float fRetVal = 0.0f;

        if (isEmpty(pString_)) 
        {
            return fRetVal;
        }

        try 
        {
            fRetVal = Float.valueOf( pString_ ).floatValue();
        }
        catch( NumberFormatException pNumberFormatException ) 
        {
            fRetVal = Float.NaN;
        }

        return fRetVal;
    }

    /**
     * String to double converter. An empty string gets
     * converted to 0.0, while a string that is not 
     * adequately formatted results in NaN (not a number).
     * '\'' chars are removed from the String before processing
     * takes place.
     */
    public static double atod( String pString_ ) 
    {
        double dRetVal = 0.0;

        String pString = Util.replace( pString_, "'", "" );
        if (isEmpty(pString)) 
        {
            return dRetVal;
        }

        try 
        {
            dRetVal = Double.valueOf( pString ).doubleValue();
        }
        catch( NumberFormatException pNumberFormatException ) 
        {
            dRetVal = Double.NaN;
        }

        return dRetVal;
    }

    /**
     * A long to double converter.
     *
     * @param    l_   the long value to convert to a double.
     *
     * @return        the double equivalent to the given long.
     */
    public static double ltod( long l_ )
    {
        return (double) l_;
    }

    /**
     * Converts an int into a byte array. First byte has the highest
     * value.
     *
     * @see #bytesToInt(byte[])
     */
    public static byte[] intToBytes( int i_ ) 
    {
        byte abInt[] = new byte[4];
        abInt[3] = (byte) (i_ & 255);
        abInt[2] = (byte) ((i_ >> 8) & 255);
        abInt[1] = (byte) ((i_ >> 16) & 255);
        abInt[0] = (byte) (i_ >> 24);
        
        return abInt;
    }
    
    /**
     * Converts a byte array into an int. First byte has the highest
     * value.
     *
     * @see #intToBytes(int)
     */
    public static int bytesToInt( byte[] abInt ) 
    {
        Util.panicIf(abInt.length != 4);
        Util.debug("Util.bytesToInt(): abInt: " + abInt[0] + "," + abInt[1] + "," + abInt[2] + "," + abInt[3]);
        int byteValue3 = (256 + abInt[3]) & 255;
        int byteValue2 = ((256 + abInt[2]) & 255) * 256;
        int byteValue1 = ((256 + abInt[1]) & 255) * 256 * 256;
        int byteValue0 = ((256 + abInt[0]) & 255) * 256 * 256 * 256;
        int i = byteValue0 | byteValue1 | byteValue2 | byteValue3;
        
        return i;
    }

    /**
     * Converts a byte to a char.
     */
    public static char byteToChar( byte b_ ) 
    {
        int intValue = (256 + b_) & 255;
        
        return((char) intValue);
    }

    /**
     * Converts a byte to an int.
     *
     * @deprecated   Use byteToInt(byte) instead.
     */
    public static int btoi( byte b_ ) 
    {
        return byteToInt( b_ );
    }

    /**
     * Converts a byte to an int.
     */
    public static int byteToInt( byte b_ ) 
    {
        int intValue = (256 + b_) & 255;
        
        return( intValue );
    }

    /**
     * Converts a byte to a String.
     */
    public static String byteToString( byte b_ ) 
    {
        return cToS( byteToChar( b_ ) );
    }

    /**
     * A character to String converter.
     */
    public static String cToS(char c_) 
    {
        return(new Character(c_).toString());
    }

    /**
     * Works with ints instead of chars.
     */
    public static int toUpperCase( int character_ ) 
    {
        String sTemp = "" + (char) character_;
        int retVal = sTemp.toUpperCase().charAt( 0 );
        
        return retVal;
    }
    
    /**
     * Works with ints instead of chars.
     */
    public static int toLowerCase( int character_ ) 
    {
        String sTemp = "" + (char) character_;
        int retVal = sTemp.toLowerCase().charAt( 0 );
        
        return retVal;
    }
    
    /**
     * Converts a String to a boolean.
     * It's true if the string consists of the word "true"
     * ignoring case, otherwise it returns false.
     *
     * @return ((pString_ != null) && pString_.toLowerCase().equals("true"));
     */
    public static boolean atob( String pString_ ) 
    {
        return(Boolean.valueOf(pString_).booleanValue());
    }
    
    /**
     * Boolean to boolean converter.
     *
     * @return true if input is true, false otherwise or if input
     *         is null.
     */
    public static boolean isTrue( Boolean pBoolean_ ) 
    {
        if (pBoolean_ == null) 
        {
            return false;
        }
        return pBoolean_.booleanValue();
    }

    // -----------------------------------------------------
    // string methods
    // -----------------------------------------------------
    
    /**
     * Tests, if a given String equals null or "".
     */
    public static boolean isEmpty( String sTest_ ) 
    {
        if (sTest_ == null || sTest_.equals("")) 
        {
            return true;
        }
        
        return false;
    }
    
    /**
     * Count how often a string contains a special char.
     */
    public static int getOccurances(String source, int zeichen) 
    {
        int anzahl = -1;
        
        int index = 0;
        do 
        {
            index = source.indexOf(zeichen, index) + 1;
            anzahl++;
        } while( index != 0 );
        
        return( anzahl );
    }

    /**
     * Count how often a string is contained in another string.
     */
    public static int getOccurances( String   source_
                                     , String lookFor_ )
    {
        int count = -1;
        
        int index = 0;
        do 
        {
            index = source_.indexOf( lookFor_, index ) + 1;
            count++;
        } while( index != 0 );
        
        return count;
    }

    /**
     * Create a string by concatenating one char several times.
     */
    public static String multiplyChar(char c, int anzahl) 
    {
        String s = "";

        while (anzahl > 0) 
        {
            s += c;
            anzahl--;
        }
        
        return(s);
    }

    /**
     * Create a string by concatenating one string several times.
     * The method name should maybe renamed to multiplyString or
     * something else.
     */
    public static String multiplyChar(String sFill, int anzahl) 
    {
        String sRet = "";
        
        while (anzahl > 0) 
        {
            sRet += sFill;
            anzahl--;
        }

        return(sRet);
    }
    
    /**
     * Returns a String which consists only of spaces and has the
     * lenght 'length_' or which is empty if length_ equals zero.
     */
    public static String getSpaces(int length_) 
    {
        Util.panicIf(length_ < 0);
        if (length_ == 0) 
        {
            return "";
        }
        String sSpaces = "                                             ";
        if (length_ > sSpaces.length()) 
        {
            return multiplyChar(' ', length_);
        }
        
        return(sSpaces.substring(0, length_));
    }

    /**
     * Append spaces to the end of a string. The returned string will always
     * have exactly the requested length_.
     */
    public static String appendSpaces( String pString_, int length_ ) 
    {
        if ( pString_ == null ) 
        {
            return getSpaces( length_ );
        }

        String sRetVal = pString_ + getSpaces( length_ );
        
        return sRetVal.substring( 0, length_ );
    }

    /**
     * Fill a string with a given char for alignment purposes.
     */
    public static String paddWith(int number_, int stellen_,
                                  char cPadd_)
    {
        return paddWith(itoa(number_), stellen_, cPadd_);
    }

    /**
     * Fill a string with a given char for alignment purposes.
     */
    public static String paddWith(String pString_, int stellen_,
                                  char cPadd_)
    {
        String sRetVal = new String(pString_);
        if (sRetVal.length() >= stellen_) 
        {
            return(sRetVal);
        }
        String sPadding = multiplyChar(cPadd_,
                                       stellen_ - sRetVal.length());
        sRetVal = sPadding + sRetVal;
        
        return(sRetVal);
    }

    /**
     * Fill a string with space chars for alignment purposes.
     */
    public static String paddWithSpace(int number, int stellen) 
    {
        return paddWith(number, stellen, ' ');
    }
    
    /**
     * Fill a string with space chars for alignment purposes.
     */
    public static String paddWithSpace(long number, int stellen) 
    {
        return paddWith(ltoa( number ), stellen, ' ');
    }
    
    /**
     * Fill a string with space chars for alignment purposes.
     */
    public static String paddWithSpace(double dNumber_, int stellen) 
    {
        return paddWith((new Double(dNumber_)).toString(),
                        stellen, ' ');
    }
    
    /**
     * Fill a string with space chars for alignment purposes.
     */
    public static String paddWithSpace(String pString_, int stellen) 
    {
        return paddWith(pString_, stellen, ' ');
    }
    
    /**
     * Add spaces at the right side of a string if necessary.
     */
    public static String rightPaddWithSpace( String pString_,
                                             int stellen )
    {
        StringBuffer sbReturn = new StringBuffer( 32 );
        if ( pString_ != null ) 
        {
            sbReturn.append( pString_ );
        }
        for( int i = 0; i <= stellen / 8; i++ ) 
        {
            sbReturn.append( "        " );
        }

        return sbReturn.toString().substring( 0, stellen );
    }
    
    /**
     * Fill a string with zero chars for alignment purposes.
     */
    public static String paddWithZero(int number, int stellen) 
    {
        return paddWith(number, stellen, '0');
    }
    
    /**
     * Fill a string with zero chars for alignment purposes.
     */
    public static String paddWithZero(long number, int stellen) 
    {
        return paddWith(ltoa( number ), stellen, '0');
    }
    
    /**
     * Fill a string with zero chars for alignment purposes.
     */
    public static String paddWithZero( String sNumber_, 
                                       int stellen_ )
    {
        return paddWith( sNumber_, stellen_, '0');
    }

    /**
     * What the method name says.
     */
    public static String removeMultipleSpaces(String pString_) 
    {
        panicIf(pString_ == null);
        String sRetVal = new String(pString_);
        
        int doubleSpaceIndex = sRetVal.indexOf("  ");
        while(doubleSpaceIndex != -1) 
        {
            sRetVal = sRetVal.substring(0, doubleSpaceIndex) +
                   sRetVal.substring(doubleSpaceIndex + 1,
                                     sRetVal.length());
            doubleSpaceIndex = sRetVal.indexOf("  ", doubleSpaceIndex);
        }
        
        return sRetVal;
    }

    /**
     * Removes space, carriage, linefeed, and tab chars at
     * the right side of a string.
     */
    public static String rtrim(String s) 
    {
        int index = s.length() - 1;
        
        while (index >= 0 && 
               (s.charAt(index) == ' ' ||
                s.charAt( index ) == '\n' ||
                s.charAt( index ) == '\r' ||
                s.charAt( index ) == '\t') )
        {
            index--;
        }
        
        return(s.substring(0, index + 1));
    }
    
    /**
     * Removes space, carriage, linefeed, and tab chars at
     * the right side of a string.
     */
    public static String ltrim(String s) 
    {
        int index = 0;  //s.length()-1;
        
        while (index < s.length() && s.charAt(index) == ' ') 
        {
            index++;
        }
        
        return(s.substring(index, s.length()));
    }

    /**
     * Seems to do the same as 'removeMultipleSpaces(String)'.
     * One should become deprecated.
     */
    public static String unifySpaces(String s) 
    {
        String sRetVal = new String();
        String sRest = s.trim();
        int index = 0;//s.length()-1;
        
        while (sRest != null && sRest.length() > 0) 
        {
            index = sRest.indexOf(' ');
            if (index < 0) 
            {
                sRetVal += sRest;
                sRest = null;
            }
            else 
            {
                sRetVal += sRest.substring(0, index + 1);
                sRest = sRest.substring(index + 1, sRest.length());
                sRest = ltrim(sRest);
            }
        }
        
        return(sRetVal);
    }
    
    /**
     * Compares two strings. Upper or lower case characters
     * are not considered differently.
     */
    public static boolean equalsCaseless(String sA_, String sB_) 
    {
        String sFirst = sA_.toUpperCase();
        String sSecond = sB_.toUpperCase();
        
        return sFirst.equals(sSecond);
    }

    /**
     * Returns the given string with the first char converted
     * to upper case.
     */
    public static String firstCharToUpperCase(String pString_) 
    {
        String sRetVal = new String();
        if (pString_ == null || pString_.length() == 0) 
        {
            return(sRetVal);
        }
        sRetVal = pString_.substring(0, 1).toUpperCase() +
               pString_.substring(1, pString_.length());
        
        return(sRetVal);
    }

    /**
     * Returns the given string with the first char converted
     * to lower case.
     */
    public static String firstCharToLowerCase(String pString_) 
    {
        String sRetVal = new String();
        if (pString_ == null || pString_.length() == 0) 
        {
            return(sRetVal);
        }
        sRetVal = pString_.substring(0, 1).toLowerCase() +
               pString_.substring(1, pString_.length());
        
        return(sRetVal);
    }

    /**
     * Tests if this string ends with the specified character.
     */
    public static boolean endsWith(String sThis_, char cOther_) 
    {
        if ( isEmpty( sThis_ ) ) 
        {
            return false;
        }

        return(sThis_.charAt(sThis_.length() - 1) == cOther_);
    }
    
    /**
     * Tests if this string ends with the second string.
     */
    public static boolean endsWith(String pString_, String sEnd_) 
    {
        if (isEmpty(sEnd_)) 
        {
            return true;
        }
        else if (isEmpty(pString_)) 
        {
            return false;
        }
        int stringLen = pString_.length();
        int endLen = sEnd_.length();
        if (endLen > stringLen) 
        {
            return false;
        }
        
        boolean bRetVal = sEnd_.equals(pString_.substring(stringLen - endLen,
                                                          stringLen));
        
        return bRetVal;
    }
    
    /**
     * Replaces all occurences of cOld_ in pString with cNew_.
     *
     * @see String#replace(char, char)
     */
    public static String replace(String pString_,
                                 char cOld_, char cNew_)
    {
        return replace(pString_, cOld_, cNew_, 0);
    }

    /**
     * Replaces all occurences of cOld_ in pString with cNew_.
     */
    public static String replace(String pString_,
                                 char cOld_, char cNew_,
                                 int startIndex_)
    {
        return replace(pString_, cToS(cOld_), cToS(cNew_), startIndex_);
    }

    /**
     * Replaces all occurences of sOld_ in pString with sNew_.
     * Unlike the String.replace(char, char) method this one accepts whole strings
     * for replacement and as a consequence also allows to delete sub strings.
     *
     * @param    pString_   a string that shall get some sub strings replaced.
     * @param    sOld_      a string for which all occurences in the first string shall be replaced.
     * @param    sNew_      a string which will be used for replacement of the old sub strings.
     *
     * @return              the first string provided but with the replaced sub strings.
     */
    public static String replace(String pString_,
                                 String sOld_, String sNew_)
    {
        return replace(pString_, sOld_, sNew_, 0);
    }

    /**
     * Replaces all occurences of sOld_ in pString with sNew_.
     *
     * @param startIndex_ The startindex_ gives the position in
     *                    pString_ where the replace procedure
     *                    should start.
     */
    public static String replace(String pString_,
                                 String sOld_, String sNew_,
                                 int startIndex_)
    {
        panicIf(sNew_ == null || sOld_ == null);
        // 23. 11. 1996
        // solange bis old nicht mehr gefunden wird.

        if (pString_ == null) 
        {
            return null;
        }

        StringBuffer sbRetVal = new StringBuffer
               ( pString_.length() );
        int copyIndex = 0;

        int lengthOld = sOld_.length();
        int index = startIndex_;
        while( true ) 
        {
            //Util.debug( "ccl.util.Util.replace(..).index: " + index );

            index = pString_.indexOf(sOld_, index);
            if ( index == -1 ) 
            {
                break;
            }
            sbRetVal.append( pString_.substring
                             ( copyIndex, index ) );
            sbRetVal.append( sNew_ );
            index += lengthOld;
            copyIndex = index;
        }

        sbRetVal.append( pString_.substring( copyIndex, pString_.length() ) );
        
        return sbRetVal.toString();
    }

    /**
     * Tests if a string contains only space, tab, and linefeed
     * characters.
     */
    public static boolean isSpaceLine(String sLine_) 
    {
        if (sLine_ == null || sLine_.length() == 0) 
        {
            return true;
        }
        for(int index = 0; index < sLine_.length(); index++) 
        {
            char c = sLine_.charAt(index);
            if (c != ' ' && c != '\t' && c != '\n') 
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Replaces tabs with spaces, a maximum of 8 each.
     */
    public static String untabify( String pString ) 
    {
        for( int index = 0; index < pString.length(); index++ )
        {
            if ( pString.charAt( index ) == '\t' )
            {
                pString = pString.substring( 0, index )
                       + getSpaces( 8 - (index % 8) )
                       + pString.substring( index + 1 );
            }
        }
         
        return pString;
    }

    /**
     * Returns "" if the input string is null, otherwise returns the same string back.
     */
    public static String denullify(String pString_) 
    {
        if (pString_ == null) 
        {
            return "";
        }
        
        return pString_;
    }

    /**
     * Before returning pObject_.toString() it checks if
     * pObject_ is null. If so, "null" is returned.
     */
    public static String toString( Object pObject_ ) 
    {
        if ( pObject_ == null ) 
        {
            return "null";
        }

        return pObject_.toString();
    }

    /**
     * @return -1 if sToLookIn_ does not contain sThis_.
     *         Otherwise the position is returned.
     *
     * @deprecated   Well, String.indexOf(String) should be
     *               just fine!?
     */
    public static int contains(String sToLookIn_, String sThis_) 
    {
        if (sToLookIn_ == null) 
        {
            return -1;
        }
        if (sThis_ == null) 
        {
            return -1;
        }
        for(int index = 0; index < sToLookIn_.length(); index++) 
        {
            if (sToLookIn_.regionMatches(index, sThis_, 0, sThis_.length())) 
            {
                return index;
            }
        }

        return -1;
    }

    /**
     * If you believe it or not, in a very old jdk version
     * there was a bug in String.compareTo(String) and I did
     * need this as a workaround. This method should be of no
     * use anymore [1999-07-15].
     *
     * @deprecated   Use String.compare instead.
     */
    public static int compare(String firstString,
                              String anotherString)
    {
        int len1 = firstString.length();
        int len2 = anotherString.length();
        int n = Math.min(len1, len2);
        int i = 0;
        int j = 0;
        
        while (n-- != 0) 
        {
            char c1 = firstString.charAt(i++);
            char c2 = anotherString.charAt(j++);
            if (c1 != c2) 
            {
                return(c1 - c2);
            }
        }
        
        return(len1 - len2);
    }

    /**
     * @return -1 means the string is either "" or contains just the
     *         char cNot_.
     */
    public static int indexOfNot(String pString_, char cNot_,
                                 int startIndex_)
    {
        panicIf(pString_ == null || startIndex_ < 0);
        
        while (startIndex_ < pString_.length()) 
        {
            if (pString_.charAt(startIndex_) != cNot_) 
            {
                return startIndex_;
            }
            startIndex_++;
        }

        return -1;
    }

    /**
     * @return -1 means the string is either "" or contains just the
     *            char cNot_.
     */
    public static int indexOfNot( String pString_, 
                                  char cNot_ ) 
    {
        return indexOfNot( pString_, cNot_, 0 );
    }

    /**
     * How many chars c_ contains the String pString_.
     */
    public static int count(String pString_, char c_) 
    {
        int retVal = 0;

        if (Util.isEmpty(pString_)) 
        {
            return retVal;
        }

        int index = pString_.indexOf(c_);
        while(index != -1) 
        {
            retVal++;
            index = pString_.indexOf(c_, ++index);
        }

        return retVal;
    }

    /**
     * Return true for digit characters.
     */
    public static boolean isDigit( char c_ ) 
    {
        return '0' <= c_ && c_ <= '9';
    }

    /**
     * Return true if the given character can be found
     * in the string "aeouiäöü".
     */
    public static boolean isVocal( char c_ ) 
    {
        String sChar = Util.cToS( c_ ).toLowerCase();
        String sVocals = "aouieäöü";
        return (sVocals.indexOf( sChar ) != -1);
    }

    /**
     * Reformat a string which is not longer than a given
     * size. If the input string was longer, a piece in the
     * middle of that string will be cut out and replaced
     * with '[...]'. maxSize should be not too small, btw.
     */
    public static String shrinkString( String pString
                                       , int maxSize  )
    {
        if ( Util.isEmpty( pString ) )
        {
            return "";
        }

        if ( pString.length() <= maxSize )
        {
            return pString;
        }

        String sRetVal = pString.substring( 0, maxSize / 2 - 2 )
               + "[...]";
        sRetVal += pString.substring( pString.length() - maxSize + sRetVal.length()
                                      , pString.length() );
        
        return sRetVal;
    }

    // -----------------------------------------------------
    // string/vector converter methods
    // -----------------------------------------------------
    
    /**
     * This function concatenates different Strings into one String.
     *
     * @param pVector_ Contains the Strings that will be concatenated.
     *
     * @return         There is no 'glue' between the Strings.
     *
     * @see            #stringToLines(java.lang.String)
     */
    public static String concat(Vector pVector_) 
    {
        return concat(pVector_, "");
    }

    /**
     * This function concatenates different Strings into one String.
     *
     * @param pVector_ Contains the Strings that will be concatenated.
     * @param sWith_   The 'glue' for the other Strings.
     *
     * @return         sWith_ is not appended at the end.
     *
     * @see #stringToLines(java.lang.String, char)
     */
    public static String concat(Vector pVector_, String sWith_) 
    {
        String sRetVal = new String();
        if (pVector_ == null || pVector_.size() < 1) 
        {
            return sRetVal;
        }
        if (sWith_ == null) 
        {
            sWith_ = "";
        }
        
        Enumeration e = pVector_.elements();
        sRetVal += e.nextElement().toString();
        for( ; e.hasMoreElements(); ) 
        {
            sRetVal += sWith_ + e.nextElement().toString();
        }
        
        return sRetVal;
    }
    
    /**
     * This function concatenates different Strings into one String.
     *
     * @param pVector_ Contains the Strings that will be concatenated.
     * @param cWith_   The 'glue' for the other Strings.
     *
     * @return         cWith_ is not appended at the end.
     *
     * @see #stringToLines(java.lang.String, char)
     */
    public static String concat(Vector pVector_, char cWith_) 
    {
        return concat(pVector_, cToS(cWith_));
    }

    /**
     * This function concatenates a String with a char.
     */
    public static String concat(String pString_, char cWidth_) 
    {
        return(pString_ + cToS(cWidth_));
    }

    /**
     * This function takes a String and separates it into different
     * lines. The last line does not need to have a separator character.
     *
     * @param lines_   The number of lines that should be extracted.
     *                 Zero if maximum number of lines is requested.
     * @param cCutter_ The character that separates pString_ into
     *                 different lines
     *
     * @return         The single lines do not contain the cCutter_
     *                 character at the end.
     */
    public static Vector stringToLines(int lines_,
                                       String pString_, char cCutter_)
    {
        int maxLines = Integer.MAX_VALUE;
        if (lines_ > 0) 
        {
            maxLines = lines_;
        }
        
        Vector vRetVal = new Vector();
        if (pString_ == null) 
        {
            return vRetVal;
        }
        
        int startIndex = 0;
        for( ; maxLines > 0; maxLines-- ) 
        {
            int endIndex = pString_.indexOf(cCutter_, startIndex);
            if (endIndex == -1) 
            {
                if (startIndex < pString_.length()) 
                {
                    endIndex = pString_.length();
                }
                else 
                {
                    break;
                }
            }
            String sLine = pString_.substring(startIndex, endIndex);
            vRetVal.addElement((Object) sLine);
            startIndex = endIndex + 1;
        }
        
        return vRetVal;
    }
    
    /**
     * This function takes a String and separates it into different
     * lines. The last line does not need to have a separator character.
     *
     * @param cCutter_ The character that separates pString_ into
     *                 different lines
     *
     * @return The single lines do not contain the cCutter_ character
     *         at the end.
     *
     * @see #concat(java.util.Vector, java.lang.String)
     */
    public static Vector stringToLines(String pString_, char cCutter_) 
    {
        return stringToLines(0, pString_, cCutter_);
    }
    
    /**
     * This function takes a String and separates it into different
     * lines. The last line does not need to have a '\n'. The function
     * can't handle dos carriage returns.
     *
     * @return The single lines do not contain the '\n' character
     *         at the end.
     */
    public static Vector stringToLines(String pString_) 
    {
        return stringToLines(pString_, '\n');
    }
    
    /**
     * This function takes a String and separates it into different
     * lines. The last line does not need to have a '\n'. The function
     * can't handle dos carriage returns.
     *
     * @param lines_   The number of lines that should be extracted.
     *                 Zero if maximum number of lines is requested.
     *
     * @return The single lines do not contain the line feed character
     *         at the end.
     *
     * @see #concat(java.util.Vector, java.lang.String)
     */
    public static Vector stringToLines(int lines_, String pString_) 
    {
        return stringToLines(lines_, pString_, '\n');
    }
    
    /**
     * This function takes a String and separates it into different
     * lines. The last line does not need to have a separator string.
     *
     * @return Note that the sCutter_ string will be removed from each
     *         line.
     *
     * @see #concat(java.util.Vector, java.lang.String)
     */
    public static Vector stringToLines(String sLines_, String sCutter_) 
    {
        Vector vRetVal = new Vector();
        if (sLines_ == null) 
        {
            return vRetVal;
        }

        int startIndex = 0;
        while(startIndex < sLines_.length() && startIndex != -1) 
        {
            int endIndex = sLines_.indexOf(sCutter_, startIndex);
            if (endIndex == -1) 
            {
                endIndex = sLines_.length();
            }
            String sNextLine = sLines_.substring(startIndex, endIndex);
            vRetVal.addElement(sNextLine);
            startIndex = endIndex + sCutter_.length();
        }
        
        return vRetVal;
    }
    
    /**
     * This function takes a String and separates it into different
     * lines. The last line does not need to have a separator string.
     *
     * @see #concat(java.util.Vector, java.lang.String)
     */
    public static Vector stringToLines(String sLines_, String sTokenizerString_, boolean bUseTokenizer_) 
    {
        if (bUseTokenizer_ == false) 
        {
            return stringToLines(sLines_, sTokenizerString_);
        }
        Vector vRetVal = new Vector();
        if (sLines_ == null) 
        {
            return vRetVal;
        }
        
        StringTokenizer pStringTokenizer = new StringTokenizer(sLines_, sTokenizerString_, false);
        while(pStringTokenizer.hasMoreTokens()) 
        {
            String sNextToken = pStringTokenizer.nextToken();
            vRetVal.addElement(sNextToken);
        }
        
        return vRetVal;
    }

    /**
     * This method takes a string and reformats it so that each
     * line has no more than the given length and the text will
     * be aligned as a block.
     *
     * This method uses a line length of 72.
     */
    public static String formatBlock( String pString )
    {
        return formatBlock( pString, 72 );
    }

    /**
     * Adds spaces inside the string so that the total length
     * will match exactly the requested length.
     *
     * @param   bRight   starting from the right side to add spaces.
     */
    private static String _formatBlockLine( String sLine
                                            , int lineLength
                                            , boolean bRight )
    {
        StringBuffer sbRetVal = new StringBuffer();
        sbRetVal.append( sLine );

        int spacesToAdd = lineLength - sLine.length();

        while( true )
        {
            Util.debug( "ccl.util.Util._formatBlockLine(..).enter while true" );
            int nextSpace = 0;
            int direction = 1;
            if ( bRight )
            {
                nextSpace = sbRetVal.length() - 1;
                direction = -direction;
            }
            Util.debug( "ccl.util.Util._formatBlockLine(..).direction:   " + direction );
            Util.debug( "ccl.util.Util._formatBlockLine(..).spacesToAdd: " + spacesToAdd );
            boolean bRestart = false;
            while( bRestart == false && spacesToAdd > 0 )
            {
                Util.debug( "ccl.util.Util._formatBlockLine(..).nextSpace: " + nextSpace );
                // find next space
                while( sbRetVal.charAt( nextSpace ) != ' ' ) 
                {
                    nextSpace += direction;
                    if ( nextSpace < 0
                        || sbRetVal.length() <= nextSpace )
                    {
                        Util.debug( "ccl.util.Util._formatBlockLine(..).first break finish" );
                        bRestart = true;
                        break;
                    }
                }
                if ( bRestart ) 
                {
                    break;
                }
                Util.debug( "ccl.util.Util._formatBlockLine(..).insert space at: " 
                            + nextSpace );
                sbRetVal.insert( nextSpace, ' ' );
                spacesToAdd--;
                // find next non space
                while( sbRetVal.charAt( nextSpace ) == ' ' ) 
                {
                    nextSpace += direction;
                    if ( nextSpace < 0
                         || sbRetVal.length() <= nextSpace )
                    {
                        bRestart = true;
                        break;
                    }
                }
                Util.debug( "ccl.util.Util._formatBlockLine(..).spacesToAdd: " + spacesToAdd );
            }
            if ( spacesToAdd <= 0 )
            {
                break;
            }
        }

        return sbRetVal.toString();
    }

    /**
     * This method updates the return value string buffer and
     * the line buffer assuming a new spaces has to be added.
     * Both string buffers will be changed.
     */
    private static void _processSpaceBlock( StringBuffer sbRetVal
                                            , StringBuffer sbLine
                                            , int lineLength )
    {
        if ( sbLine.length() == lineLength )
        {
            sbRetVal.append( sbLine.toString() ).append( '\n' );
            sbLine.setLength( 0 );
        }
        else if ( sbLine.length() > lineLength )
        {
            int lastSpace = sbLine.toString().lastIndexOf( ' ' );
            if ( lastSpace == -1 )
            {
                sbRetVal.append( sbLine.toString() ).append( '\n' );
                sbLine.setLength( 0 );
            }
            else
            {
                sbRetVal.append( _formatBlockLine( sbLine.substring( 0, lastSpace ), lineLength, true ) ).append( '\n' );
                sbLine.delete( 0, lastSpace + 1 ).append( ' ' );
            }
        } 
        else
        {
            sbLine.append( ' ' );
        }
    }

    /**
     * This method takes a string and reformats it so that each
     * line has no more than the given length and the text will
     * be aligned as a block.
     */
    public static String formatBlock( String pString, int lineLength )
    {
        String sWorkString = replace( pString, "\n", " " );
        sWorkString = replace( sWorkString, "\r", " " );
        sWorkString = replace( sWorkString, "\t", " " );
        sWorkString = sWorkString.trim();
        StringBuffer sbRetVal = new StringBuffer();
        StringBuffer sbLine = new StringBuffer();
        boolean bWhiteSpace = false;
        int index = 0;
        while( index <= sWorkString.length() )
        {
            char nextChar = ' ';
            if ( index < sWorkString.length() ) 
            {                
                nextChar = sWorkString.charAt( index );
            }
            Util.debug( "ccl.util.Util.formatLeft(..).nextChar: " + nextChar );
            if ( nextChar == ' '
                 || nextChar == '\t'
                 || nextChar == '\n'
                 || nextChar == '\r' )
            {
                if ( bWhiteSpace == false )
                {
                    _processSpaceBlock( sbRetVal, sbLine, lineLength );
                    bWhiteSpace = true;
                }
            }
            else
            {
                bWhiteSpace = false;
                sbLine.append( nextChar );
            }
            index++;
            Util.debug( "ccl.util.Util.formatLeft(..).sbRetVal: " + sbRetVal );
            Util.debug( "ccl.util.Util.formatLeft(..).sbLine:   " + sbLine   );            
        }
        if ( sbLine.length() > 0 )
        {
            sbRetVal.append( Util.rtrim( sbLine.toString() ) ).append( '\n' );
        }

        return sbRetVal.toString();
    }

    /**
     * This method takes a string and reformats it so that each
     * line has no more than the given length and the text will
     * be aligned as a block.
     *
     * @param   indentation   add indentation spaces to left
     *                        of each line.
     */
    public static String formatBlock( String pString
                                      , int lineLength
                                      , int indentation )
    {
        String sRetVal = formatBlock( pString, lineLength - indentation );

        Vector vLines = stringToLines( sRetVal );
        Enumeration eLines = vLines.elements();
        sRetVal = "";
        while( eLines.hasMoreElements() ) 
        {
            sRetVal += getSpaces( indentation ) 
                    +  ((String) eLines.nextElement()) 
                    +  "\n";
        }

        return sRetVal;
    }

    /**
     * This method takes a string and reformats it so that each
     * line has no more than the given length and the text will
     * be aligned to the left side.
     *
     * This method uses a line length of 72.
     */
    public static String formatLeft( String pString )
    {
        return formatLeft( pString, 72 );
    }

    /**
     * This method updates the return value string buffer and
     * the line buffer assuming a new spaces has to be added.
     * Both string buffers will be changed.
     */
    private static void _processSpaceLeft( StringBuffer sbRetVal
                                           , StringBuffer sbLine
                                           , int lineLength )
    {
        if ( sbLine.length() == lineLength )
        {
            sbRetVal.append( sbLine.toString() ).append( '\n' );
            sbLine.setLength( 0 );
        }
        else if ( sbLine.length() > lineLength )
        {
            int lastSpace = sbLine.toString().lastIndexOf( ' ' );
            if ( lastSpace == -1 )
            {
                sbRetVal.append( sbLine.toString() ).append( '\n' );
                sbLine.setLength( 0 );
            }
            else
            {
                sbRetVal.append( sbLine.substring( 0, lastSpace ) ).append( '\n' );
                sbLine.delete( 0, lastSpace + 1 ).append( ' ' );
            }
        } 
        else
        {
            sbLine.append( ' ' );
        }
    }

    /**
     * This method takes a string and reformats it so that each
     * line has no more than the given length and the text will
     * be aligned to the left side.
     */
    public static String formatLeft( String pString, int lineLength )
    {
        String sWorkString = pString.trim();
        StringBuffer sbRetVal = new StringBuffer();
        StringBuffer sbLine = new StringBuffer();
        boolean bWhiteSpace = false;
        int index = 0;
        while( index <= sWorkString.length() )
        {
            char nextChar = ' ';
            if ( index < sWorkString.length() ) 
            {                
                nextChar = sWorkString.charAt( index );
            }
            Util.debug( "ccl.util.Util.formatLeft(..).nextChar: " + nextChar );
            if ( nextChar == ' '
                 || nextChar == '\t'
                 || nextChar == '\n'
                 || nextChar == '\r' )
            {
                if ( bWhiteSpace == false )
                {
                    _processSpaceLeft( sbRetVal, sbLine, lineLength );
                    bWhiteSpace = true;
                }
            }
            else
            {
                bWhiteSpace = false;
                sbLine.append( nextChar );
            }
            index++;
            Util.debug( "ccl.util.Util.formatLeft(..).sbRetVal: " + sbRetVal );
            Util.debug( "ccl.util.Util.formatLeft(..).sbLine:   " + sbLine   );            
        }
        if ( sbLine.length() > 0 )
        {
            sbRetVal.append( Util.rtrim( sbLine.toString() ) ).append( '\n' );
        }

        return sbRetVal.toString();
    }

    /**
     * This method takes a string and reformats it so that each
     * line has no more than the given length and the text will
     * be aligned to the left side.
     *
     * @param   indentation   add indentation spaces to left
     *                        of each line.
     */
    public static String formatLeft( String pString
                                     , int lineLength
                                     , int indentation )
    {
        String sRetVal = formatLeft( pString, lineLength - indentation );

        Vector vLines = stringToLines( sRetVal );
        Enumeration eLines = vLines.elements();
        sRetVal = "";
        while( eLines.hasMoreElements() ) 
        {
            sRetVal += getSpaces( indentation ) 
                    +  ((String) eLines.nextElement()) 
                    +  "\n";
        }

        return sRetVal;
    }

    /**
     * This method takes a string and reformats it so that each
     * line has no more than the given length and the text will
     * be centered in the middle.
     */
    public static String formatCenter( String pString, int lineLength )
    {
        StringBuffer sbRetVal = new StringBuffer();

        String sLeft = formatLeft( pString, lineLength );

        // now walk through each line and center it
        Vector vLines = stringToLines( sLeft );
        Enumeration eLines = vLines.elements();
        while( eLines.hasMoreElements() )
        {
            String sLine = (String) eLines.nextElement();
            sLine = centerLine( sLine, lineLength );
            sbRetVal.append( sLine ).append( '\n' );
        }

        return sbRetVal.toString();
    }

    /**
     * Returns a string withe the provided content in the center
     * and no line feed. Also, the right side of the string
     * will not be filled up with spaces, only the left side.
     * The input should not contain line feeds, btw.
     */
    public static String centerLine( String pString, int lineLength )
    {
        if ( Util.isEmpty( pString ) )
        {
            return "";
        }

        if ( pString.length() > lineLength )
        {
            return pString;
        }

        int add = (lineLength - pString.length()) / 2;

        return getSpaces( add ) + pString;
    }

    /**
     * Finds out the first position (started with 0) at which two strings 
     * start to differ.
     *
     * @param    s1   a string to compare, is not allowed to be null.
     * @param    s2   a string to compare, is not allowed to be null.
     * @return        the first position starting with 0 at which both strings differ or
     *                the length of the smaller one or the length of both string if they are
     *                equal.
     */
    public static int getDiffPosition( String s1, String s2 )
    {
        if ( s1 == null )
        {
            throw new IllegalArgumentException( "First string is null" );
        }
        if ( s2 == null )
        {
            throw new IllegalArgumentException( "Second string is null" );
        }

        int position = -1;
        do
        {
            position++;
            // if one of the files (can also be both) end here return current position
            if ( position >= s1.length()
                 || position >= s2.length() )
            {
                break;
            }
        } while( s1.charAt( position ) == s2.charAt( position ) );

        return position;
    }

    // -----------------------------------------------------
    // vector methods
    // -----------------------------------------------------

    /**
     * Tests, if a given Vector is null or has size 0.
     */
    public static boolean isEmpty( Vector vTest_ ) 
    {
        if (vTest_ == null || vTest_.size() == 0 ) 
        {
            return true;
        }
        
        return false;
    }
    
    /**
     * Enumeration to Vector converter.
     *
     * @return   null in   empty vector out
     */
    public static Vector toVector( Enumeration pEnumeration_ ) 
    {
        Vector vRetVal = new Vector();

        if ( pEnumeration_ == null ) 
        {
            return vRetVal;
        }

        while( pEnumeration_.hasMoreElements() ) 
        {
            vRetVal.addElement( pEnumeration_.nextElement() );
        }

        return vRetVal;
    }

    /**
     * Convert an array of objects to a vector.
     */
    public static Vector objectsToVector(Object apObjects[]) 
    {
        Vector vRetVal = new Vector();
        if (apObjects != null && apObjects.length > 0) 
        {
            for(int nr = 0; nr < apObjects.length; nr++) 
            {
                vRetVal.addElement(apObjects[nr]);
            }
        }

        return vRetVal;
    }

    /**
     * Convenience class for java.util.Vector.copyInto(..).
     *
     * @param   pVector_   the vector to convert into an array.
     * @return   Never returns null, returns at least an array
     *           of length 0.
     */
    public static Object[] vectorToObjects( Vector pVector_ ) 
    {
        if ( pVector_ == null ) 
        {
            return new Object[ 0 ];
        }

        Object[] aoRetVal = new Object[ pVector_.size() ];
        pVector_.copyInto( aoRetVal );

        return aoRetVal;
    }

    /**
     * All object in this vector which equal the bad element
     * are not copied over to the resulting vector.
     */
    public static Vector filter(Vector pVector_,
                                final Object oBadElement_)
    {
        panicIf(oBadElement_ == null);
        //final String sFinalBadElement = new String(sBadElement_);
        Testable pFilter = new Testable() {
                /**
                 * Returns true when given object is none of the 
                 * bad ones.
                 */
                public boolean test(Object pObject_) 
                {
                    return(!oBadElement_.equals(pObject_));
                }
            };
        
        return filter(pVector_, pFilter);
    }

    /**
     * All object in this vector which equal an object in
     * the bad vector are not copied over to the resulting
     * vector.
     */
    public static Vector filter(Vector pVector_,
                                Vector vBadElements_)
    {
        Vector vRetVal = pVector_;
        
        panicIf(vBadElements_ == null);
        for(Enumeration e = vBadElements_.elements();
            e.hasMoreElements(); )
        {
            Object oBadElement = e.nextElement();
            vRetVal = filter(vRetVal, oBadElement);
        }

        return vRetVal;
    }

    /**
     * Create a new vector and copy all elements of
     * the pVector_ paramter over which are accepted by the
     * test filter.
     * <br>
     * Each element for which the test returns true, it gets
     * added to the returned vector.
     */
    public static Vector filter(Vector pVector_,
                                Testable pFilter_)
    {
        Vector vRetVal = new Vector();
        for(Enumeration e = pVector_.elements(); e.hasMoreElements(); ) 
        {
            Object pObject = e.nextElement();
            if (pFilter_.test(pObject)) 
            {
                vRetVal.addElement(pObject);
            }
        }
        
        return vRetVal;
    }

    /**
     * Convert each element of the vector by a transformation
     * object.
     */
    public static Vector map(Vector pVector_,
                             Transformable pTransformable_)
    {
        Vector vRetVal = new Vector();
        for(Enumeration e = pVector_.elements(); e.hasMoreElements(); ) 
        {
            Object pObject = e.nextElement();
            vRetVal.addElement(pTransformable_.transform(pObject));
        }
        
        return vRetVal;
    }

    /**
     * Test if a vector contains a given string.
     */
    public static boolean contains(Vector pVector_,
                                   final String sFind_)
    {
        panicIf(sFind_ == null);
        Testable pFilter = new Testable() {
                /**
                 * Returns true if given object equals the final string
                 * which is to be found.
                 */
                public boolean test(Object pObject_) 
                {
                    return(sFind_.equals((String) pObject_));
                }
            };

        return contains(pVector_, pFilter);
    }

    /**
     * Test if a vector contains an element which succeeds
     * a given test filter.
     */
    public static boolean contains(Vector pVector_,
                                   Testable pFilter_)
    {
        Vector vRetVal = new Vector();
        for(Enumeration e = pVector_.elements(); e.hasMoreElements(); ) 
        {
            Object pObject = e.nextElement();
            if (pFilter_.test(pObject)) 
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Convert an enumeration to a vector.
     * Sometimes you have an enumeration at hand, which you
     * want to use more than once.
     */
    public static Vector enumerationToVector(Enumeration pEnumeration_) 
    {
        Vector vRetVal = new Vector();
        while(pEnumeration_.hasMoreElements()) 
        {
            Object pObject = pEnumeration_.nextElement();
            vRetVal.addElement(pObject);
        }

        return vRetVal;
    }

    /**
     * Create a new vector which consists of both given vectors.
     */
    public static Vector concat(Vector vFirst_, Vector vSecond_) 
    {
        Vector vRetVal = (Vector) vFirst_.clone();
        for(Enumeration e = vSecond_.elements(); e.hasMoreElements(); ) 
        {
            vRetVal.addElement(e.nextElement());
        }
        
        return vRetVal;
    }

    /**
     * Create a new vector through extracting all elements of
     * the second vector from the first vector.
     */
    public static Vector subtract(Vector vSource_, Vector vToDelete_) 
    {
        Vector vRetVal = (Vector) vSource_.clone();
        for(Enumeration e = vToDelete_.elements(); e.hasMoreElements(); ) 
        {
            vRetVal.removeElement(e.nextElement());
        }

        return vRetVal;
    }

    /**
     * Insert at a special offset all elements of the second
     * vector into the first vector. The input vectors are
     * not changed but a new result vector gets created.
     */
    public static Vector insert(Vector vDestination_, Vector vOther_,
                                int destination)
    {
        panicIf(vDestination_ == null || vOther_ == null);

        if (destination > vDestination_.size()) 
        {
            destination = vDestination_.size();
        }

        Vector vRetVal = new Vector();
        for(int i = 0; i < destination; i++) 
        {
            vRetVal.addElement(vDestination_.elementAt(i));
        }
        vRetVal = concat(vRetVal, vOther_);
        for(int i = destination; i < vDestination_.size(); i++) 
        {
            vRetVal.addElement(vDestination_.elementAt(i));
        }

        return vRetVal;
    }

    /**
     * Do the elements of two vectors at the same position
     * equal each other?
     */
    public static boolean equals(Vector vFirst_, Vector vSecond_) 
    {
        if (vFirst_ == vSecond_) 
        {
            return true;
        }
        if (vFirst_ == null || vSecond_ == null) 
        {
            return false;
        }
        if (vFirst_.size() != vSecond_.size()) 
        {
            return false;
        }
        for(int index = 0; index < vFirst_.size(); index++) 
        {
            Object oFirst = vFirst_.elementAt(index);
            Object oSecond = vSecond_.elementAt(index);
            if (oFirst == null && oSecond == null) 
            {
                continue;
            }
            if (oFirst == null || oSecond == null) 
            {
                return false;
            }
            if (!oFirst.equals(oSecond)) 
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Create a new vector and invert the order of the
     * elements.
     */
    public static Vector invert(Vector vSource_) 
    {
        if (vSource_ == null || vSource_.size() == 0) 
        {
            return(new Vector());
        }

        int vectorSize = vSource_.size();
        Vector vDest = new Vector(vectorSize);
        for(int index = vectorSize - 1; index >= 0; index--) 
        {
            Object oNext = vSource_.elementAt(index);
            vDest.addElement(oNext);
        }

        return vDest;
    }

    // -----------------------------------------------------
    // sorting and inserting methods
    // -----------------------------------------------------

    /**
     * An implementation of Quicksort using medians of 3 for partitions.
     * Used internally by sort.
     * It is public and static so it can be used  to sort plain
     * arrays as well.
     *
     * Originally written by Doug Lea and released into the public domain.
     * Thanks for the assistance and support of Sun Microsystems Labs, Agorics
     * Inc, Loral, and everyone contributing, testing, and using this code.
     *
     * History:
     * Date     Who                What
     * 2Oct95   dl@cs.oswego.edu   refactored from DASeq.java
     * 13Oct95  dl                 Changed protection statuses
     *
     * @param s the array to sort
     * @param lo the least index to sort from
     * @param hi the greatest index
     * @param cmp the comparator to use for comparing elements
     */
    public static void quickSort(Object s[], int lo, int hi, Comparable cmp)
    {

        if (lo >= hi)
        {
            return;
        }

        // Use median-of-three(lo, mid, hi) to pick a partition.
        // Also swap them into relative order while we are at it.

        int mid = (lo + hi) / 2;

        if (cmp.compare(s[lo], s[mid]) > 0)
        {
            Object tmp = s[lo]; s[lo] = s[mid]; s[mid] = tmp; // swap
        }

        if (cmp.compare(s[mid], s[hi]) > 0)
        {
            Object tmp = s[mid]; s[mid] = s[hi]; s[hi] = tmp; // swap

            if (cmp.compare(s[lo], s[mid]) > 0)
            {
                Object tmp2 = s[lo]; s[lo] = s[mid]; s[mid] = tmp2; // swap
            }
        }

        int left = lo + 1;           // start one past lo since already handled lo
        int right = hi - 1;          // similarly
        if (left >= right)
        {
            return; // if three or fewer we are done
        }

        Object partition = s[mid];

        for ( ; ; )
        {

            while (cmp.compare(s[right], partition) > 0)
            {
                --right;
            }

            while (left < right && cmp.compare(s[left], partition) <= 0)
            {
                ++left;
            }

            if (left < right)
            {
                Object tmp = s[left]; s[left] = s[right]; s[right] = tmp; // swap
                --right;
            }
            else
            {
                break;
            }
        }

        quickSort(s, lo, left, cmp);
        quickSort(s, left + 1, hi, cmp);
    }

    /**
     * An implementation of Quicksort using medians of 3 for partitions.
     * Used internally by sort.
     * It is public and static so it can be used  to sort plain
     * arrays as well.
     *
     * Originally written by Doug Lea and released into the public domain.
     * Thanks for the assistance and support of Sun Microsystems Labs, Agorics
     * Inc, Loral, and everyone contributing, testing, and using this code.
     *
     * History:
     * Date     Who                    What
     * 2Oct95   dl@cs.oswego.edu       refactored from DASeq.java
     * 13Oct95  dl                     Changed protection statuses
     * 30Apr97  Clemens.Lahme@gmd.de   For use with Vector
     *
     * @param v the vector to sort
     * @param lo the least index to sort from
     * @param hi the greatest index
     * @param cmp the comparator to use for comparing elements
     *
     */
    public static void quickSort(Vector v, int lo, int hi, Comparable cmp) 
    {
        panicIf (v == null);
        
        if ( lo >= hi ) 
        {
            return;
        }
        
        // Use median-of-three(lo, mid, hi) to pick a partition.
        // Also swap them into relative order while we are at it.
        
        int mid = (lo + hi) / 2;
        
        if (cmp.compare(v.elementAt(lo), v.elementAt(mid)) > 0) 
        {
            // swap
            Object tmp = v.elementAt(lo);
            v.setElementAt(v.elementAt(mid), lo);
            v.setElementAt(tmp, mid);
        }
        
        if (cmp.compare(v.elementAt(mid), v.elementAt(hi)) > 0) 
        {
            // swap
            Object tmp = v.elementAt(mid);
            v.setElementAt(v.elementAt(hi), mid);
            v.setElementAt(tmp, hi);
            
            if (cmp.compare(v.elementAt(lo), v.elementAt(mid)) > 0) 
            {
                // swap
                Object tmp2 = v.elementAt(lo);
                v.setElementAt(v.elementAt(mid), lo);
                v.setElementAt(tmp2, mid);
            }
        }

        int left = lo + 1;           // start one past lo since already handled lo
        int right = hi - 1;          // similarly
        if (left >= right)
        {
            return; // if three or fewer we are done
        }
        
        Object partition = v.elementAt(mid);
        
        for ( ; ; ) 
        {
            
            while (cmp.compare(v.elementAt(right), partition) > 0) 
            {
                --right;
            }
            
            while (left < right && cmp.compare(v.elementAt(left), partition) <= 0) 
            {
                ++left;
            }
            
            if (left < right) 
            {
                // swap
                Object tmp = v.elementAt(left);
                v.setElementAt(v.elementAt(right), left);
                v.setElementAt(tmp, right);
                --right;
            }
            else
            {
                break;
            }
        }
        
        quickSort(v, lo, left, cmp);
        quickSort(v, left + 1, hi, cmp);
    }

    /**
     * Uses Quicksort using medians of 3 for partitions and the
     */
    public static Vector sort(final Vector vInput_,
                              Comparable pComparable_)
    {
        panicIf(vInput_ == null);
        Vector vRetVal = (Vector) vInput_.clone();
        
        if (vInput_.size() > 0) 
        {
            quickSort(vRetVal, 0, vRetVal.size() - 1, pComparable_);
        }
        
        return vRetVal;
    }

    /**
     * Like sort but works directly on the input vector.
     */
    public static void sortFast( Vector vInput_,
                                 Comparable pComparable_ )
    {
        panicIf(vInput_ == null);
        
        if (vInput_.size() > 0) 
        {
            quickSort( vInput_, 0, vInput_.size() - 1, pComparable_ );
        }
    }

    /**
     * Uses Quicksort using medians of 3 for partitions.
     */
    public static Vector sort(final Vector pVector_) 
    {
        Comparable pComparable = new Comparable() {
                /**
                 * Converts object string representation to lower
                 * case before comparing. Is that really what we want?
                 * In some cases yes, for example having 'Zorro' sorted 
                 * before 'arthur' is not what everbody expects,
                 * thought some might expect it.
                 */ 
                public int compare(Object oFirst_, Object oSecond_) 
                {
                    return( ((oFirst_.toString()).toLowerCase()).
                            compareTo((oSecond_.toString()).toLowerCase()) );
                }
            };
        
        return sort(pVector_, pComparable);
    }

    /**
     * Uses Quicksort using medians of 3 for partitions.
     */
    public static void sortFast(final Vector pVector_) 
    {
        Comparable pComparable = new Comparable() {
                /**
                 * Converts object string representation to lower
                 * case before comparing. Is that really what we want?
                 * In some cases yes, for example having 'Zorro' sorted 
                 * before 'arthur' is not what everbody expects,
                 * thought some might expect it.
                 */ 
                public int compare(Object oFirst_, Object oSecond_) 
                {
                    return( ((oFirst_.toString()).toLowerCase()).
                            compareTo((oSecond_.toString()).toLowerCase()) );
                }
            };
        
        sortFast(pVector_, pComparable);
    }

    /**
     * Quicksort for Enumeration.
     *
     * @return   null in   empty vector out
     */
    public static Vector sort( Enumeration pEnumeration_ ) 
    {
        return sort( toVector( pEnumeration_ ) );
    }

    /**
     * Quicksort for Enumeration.
     *
     * @return   null in   empty vector out
     */
    public static Vector sort( Enumeration pEnumeration_, 
                               Comparable pComparable_ ) 
    {
        return sort( toVector( pEnumeration_ ), pComparable_ );
    }

    /**
     * Case sensitive sort has 'Zorro' ordered
     * before 'arthur'. If that is not desirec,
     * use normal sort routines.
     * Uses Quicksort using medians of 3 for partitions
     */
    public static Vector sortCaseSensitive(final Vector pVector_) 
    {
        Comparable pComparable = new Comparable() {
                /**
                 * Case sensitive sort has 'Zorro' ordered
                 * before 'arthur'. If that is not desirec,
                 * use normal sort routines.
                 */ 
                public int compare(Object oFirst_, Object oSecond_) 
                {
                    return( oFirst_.toString().
                            compareTo(oSecond_.toString()) );
                }
            };

        return sort(pVector_, pComparable);
    }

    /**
     * Inert a new object into a vector and keep the
     * vector sorted.
     */
    public static int insert( Vector pVector_,
                              int lowestOffset_,
                              int highestOffset_,
                              Object pObject_,
                              Comparable pComparable_ )
    {
        if ( highestOffset_ < lowestOffset_ ) 
        {
            pVector_.insertElementAt( pObject_, lowestOffset_ );

            return lowestOffset_;
        }

        // get median element
        int midleOffset = (lowestOffset_ + highestOffset_) / 2;

        if ( pComparable_.compare( pObject_, pVector_.elementAt( midleOffset ) ) < 0 ) 
        {
            return insert( pVector_, lowestOffset_, midleOffset - 1, pObject_, pComparable_ );
        }
        else 
        {
            return insert( pVector_, midleOffset + 1, highestOffset_, pObject_, pComparable_ );
        }
    }

    /**
     * Inert a new object into a vector and keep the
     * vector sorted.
     */
    public static int insert( Vector pVector_, 
                              Object pObject_, 
                              Comparable pComparable_ ) 
    {
        panicIf( pVector_ == null );
        
        return insert( pVector_, 0, pVector_.size() - 1, pObject_, pComparable_ );
    }

    // -----------------------------------------------------
    // system methods
    // -----------------------------------------------------

    /**
     * This method does return immediately. If you want the
     * output of the process use either systemAndWait() or
     * systemAndGetError().
     *
     * @exception   IOException   Whatever can go wrong.
     *
     * @see #systemAndWait(java.util.Vector)
     * @see #systemAndGetError(java.util.Vector)
     */
    public static Process system( String sCommand_ )
        throws IOException
    {
        Process pProcess  = Runtime.getRuntime().exec( sCommand_ );
        /*DataInputStream dis = new DataInputStream(
          new BufferedInputStream(p.getInputStream()));
          
          String s = null;
          while ((s = dis.readLine()) != null) {
          System.out.println(s);
          }*/
        
        return pProcess;
    }

    /**
     * Execute an external command.
     *
     * @exception   IOException   Whatever Runtime.exec(..) throws.
     */
    public static Process system(String asCommand_[])
        throws IOException
    {
        Process pProcess  = Runtime.getRuntime().exec(asCommand_);
        
        return pProcess;
    }

    /**
     * Execute an external command. Provide arguments inside 
     * a vector.
     *
     * @exception   IOException   Whatever Runtime.exec(..) throws.
     */
    public static Process system(Vector vArgs_)
        throws IOException
    {
        panicIf(vArgs_ == null || vArgs_.size() == 0);
        String asArgs[] = new String[vArgs_.size()];
        
        int arg = 0;
        for(Enumeration eArgs = vArgs_.elements();
            eArgs.hasMoreElements(); )
        {
            asArgs[arg] = (String) eArgs.nextElement();
            arg++;
        }

        return system(asArgs);
    }

    /**
     * Does a system exec and returns the stdout.
     *
     * @exception   IOException   Whatever might go wrong.
     * @param   vArgs_   the arguments to system.exec().
     *                   Should not be null or empty.
     * @return   The standard output.
     */
    public static String systemAndWait( Vector vArgs_ ) 
        throws IOException
    {
        panicIf( vArgs_ == null || vArgs_.size() == 0 );

        String sRetVal = "";
        String sLine = null;
        Process pProcess = system( vArgs_ );
        BufferedReader reader = new BufferedReader
               ( new InputStreamReader
                 ( pProcess.getInputStream() ) );
          
        while( (sLine = reader.readLine()) != null ) 
        {
            sRetVal += sLine + "\n";
        }
        reader.close();

        return sRetVal;
    }

    /**
     * Does a system exec and returns the stdout.
     *
     * @exception   IOException   Whatever might go wrong.
     * @param   commandline   the arguments to system.exec().
     *                   Should not be null or empty.
     * @return   The standard output.
     */
    public static String systemAndWait( String commandline ) 
        throws IOException
    {
        panicIf( Util.isEmpty( commandline ) );

        String sRetVal = "";
        String sLine = null;
        Process pProcess = system( commandline );
        BufferedReader reader = new BufferedReader
               ( new InputStreamReader
                 ( pProcess.getInputStream() ) );
          
        while( (sLine = reader.readLine()) != null ) 
        {
            sRetVal += sLine + "\n";
        }
        reader.close();

        return sRetVal;
    }

    /**
     * Does a system exec and returns the stderr output.
     *
     * @exception   IOException   Whatever might go wrong.
     * @param   vArgs_   the arguments to system.exec().
     *                   Should not be null or empty.
     * @return   The standard output.
     */
    public static String systemAndGetError( Vector vArgs_ ) 
        throws IOException
    {
        panicIf( vArgs_ == null || vArgs_.size() == 0 );

        String sRetVal = "";
        String sLine = null;
        Process pProcess = system( vArgs_ );
        BufferedReader reader = new BufferedReader
               ( new InputStreamReader
                 ( pProcess.getErrorStream() ) );
          
        while( (sLine = reader.readLine()) != null ) 
        {
            sRetVal += sLine + "\n";
        }
        reader.close();

        return sRetVal;
    }

    /**
     * Does a system exec and returns the stderr output.
     *
     * @exception   IOException   Whatever might go wrong.
     * @param   commandline   the arguments to system.exec().
     *                   Should not be null or empty.
     * @return   The standard output.
     */
    public static String systemAndGetError( String commandline ) 
        throws IOException
    {
        panicIf( Util.isEmpty( commandline ) );

        String sRetVal = "";
        String sLine = null;
        Process pProcess = system( commandline );
        BufferedReader reader = new BufferedReader
               ( new InputStreamReader
                 ( pProcess.getErrorStream() ) );
          
        while( (sLine = reader.readLine()) != null ) 
        {
            sRetVal += sLine + "\n";
        }
        reader.close();

        return sRetVal;
    }

    /**
     * Returns true if the current operating system is
     * Microsoft Windows.
     */
    public static boolean isOSWindows() 
    {
        String sOSName = System.getProperty("os.name");
        Util.debug("Util.isOSWindows().sOSName: " + sOSName);

        return sOSName.startsWith("Windows");
    }

    /**
     * Returns true if the current operating system is
     * Linux.
     */
    public static boolean isOSLinux() 
    {
        String sOSName = System.getProperty("os.name");

        return sOSName.startsWith("Linux");
    }

    /**
     * Returns true if the current operating system is
     * Sun Microsystem's Solaris (or SunOS).
     */
    public static boolean isOSSolaris() 
    {
        String sOSName = System.getProperty("os.name");

        return sOSName.startsWith("Solaris") || sOSName.startsWith( "SunOS" );
    }

    /**
     * Returns true if the current operating system is
     * either Linux or Solaris.
     */
    public static boolean isOSUnix() 
    {
        return isOSLinux() || isOSSolaris();
    }

    /**
     * System.gc() does not always garanty immediate execution.
     * This method does also a yield for a bigger chance that gc
     * really does happen.
     */
    public static void gc() 
    {
        System.gc();
        Thread.currentThread().yield();
    }

    /**
     * Get the name of the localhost.
     */
    public static String getLocalHostName() 
    {
        String sHostName = "";
        try 
        {
            InetAddress iaLocalHost = InetAddress.getLocalHost();
            sHostName = iaLocalHost.getHostName();
        }
        catch(Exception eLocalHost) 
        {
            return null;
        }

        return sHostName;
    }

    /**
     * Returns true if sFullPackageName_ is a swing
     * package, either old com.sun.java.swing or new
     * javax.swing convention. Accessibilitiy is also
     * considered to be a swing package.
     */
    public static boolean isSwingPackage( String sFullPackageName_ ) 
    {
        if ( sFullPackageName_.startsWith( "javax." ) ||
             sFullPackageName_.startsWith( "com.sun.java." ) )
        {
            if (sFullPackageName_.equals("javax.swing") ||
                sFullPackageName_.equals("javax.accessibility" ) ||
                sFullPackageName_.startsWith("javax.swing.") ||
                sFullPackageName_.equals("com.sun.java.swing") ||
                sFullPackageName_.equals("com.sun.java.accessibility") ||
                sFullPackageName_.startsWith("com.sun.java.swing.")) 
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a string which contains the stack trace.
     */
    public static String getStackTrace( Throwable pThrowable_ ) 
    {
        StringWriter pStringWriter = new StringWriter();
        PrintWriter  pPrintWriter  = new PrintWriter ( pStringWriter );
        pThrowable_.printStackTrace( pPrintWriter );

        return pStringWriter.toString();
    }

    /**
     * This method returns a string with dump out of all its 
     * attribute fields, private as well as public fields.
     * Eventually you have to set the security
     * policy in order to permit access to private stuff.
     * This method also fails compiling for jdk 1.1.
     */
    public static String getDump( Object object_ ) 
    {
        String dumpString = "null";
        //if ( dumpString.equals( "null" ) ) {
        //    throw new ApplicationException( "Method ccl.Util.getDump(Object) not implemented!" );
        //}
        if ( object_ != null ) 
        {
            Class dumpClass = object_.getClass();
            dumpString = dumpClass.getName() + "@" + Integer.toHexString( object_.hashCode() ) + ":";
        
            do 
            {
                Field[] fieldArray = dumpClass.getDeclaredFields();
                for( int fieldIndex = 0; fieldIndex < fieldArray.length; fieldIndex++ ) 
                {
                    try 
                    {
                        Field nextField = fieldArray[ fieldIndex ];

                        // here comes the trick!!!
                        nextField.setAccessible( true );  

                        dumpString += "\n        " + nextField + ": " +
                               nextField.get( object_ );
                    }
                    catch( IllegalAccessException illegalAccessException ) 
                    {
                        printlnErr( illegalAccessException );
                    }
                }
                dumpClass = dumpClass.getSuperclass();
            } while( dumpClass != null );
        }

        return dumpString;
    }

    // -----------------------------------------------------
    // random generator methods
    // -----------------------------------------------------

    /**
     * @return   50% chance of either true or false.
     */
    public static boolean rnd() 
    {
        return(rnd(1) == 0);
    }

    /**
     * Random number in the range [0, end_] (both inclusive).
     */
    public static int rnd(int end_) 
    {
        return rnd(0, end_);
    }

    /**
     * Random number in the range [start_, end_] (both inclusive).
     */
    public static int rnd(int start_, int end_) 
    {
        panicIf(end_ <= start_);
        
        float fR = _rnd.nextFloat();
        int r = (int) (fR * (end_ - start_ + 1) + start_);
        
        return( r );
    }
    
    /**
     * Returns the a pseudorandom float number between
     * 0.0 and excluding the provided float value.
     */
    public static float rnd(float f) 
    {
        float fR = (float) _rnd.nextFloat();
        
        return( f * fR );
    }
    
    /**
     * Returns the a pseudorandom double number between
     * 0.0 and excluding the provided double value.
     */
    public static double rnd(double df) 
    {
        double dR = _rnd.nextDouble();
        
        return( df * dR );
    }
    
    // -----------------------------------------------------
    // date methods
    // -----------------------------------------------------

    /**
     * @return   1998-12-06 for example.
     */
    public static String getStandardDate( Date pDate_ ) 
    {
        SimpleDateFormat pSimpleDateFormat = 
               new SimpleDateFormat( "yyyy-MM-dd" );
        
        String sRetVal = pSimpleDateFormat.format( pDate_ );
        
        return sRetVal;
    }
    
    /**
     * Returns the current date as an ISO date string.
     *
     * @return   1998-12-06 for example.
     */
    public static String getDate() 
    {
        return getDate( getCalendar() );
    }
    
    /**
     * Returns the given date as an ISO date string.
     *
     * @return   1998-12-06 for example.
     */
    public static String getDate( Calendar pCalendar_ ) 
    {
        SimpleDateFormat pSimpleDateFormat = 
               new SimpleDateFormat( "yyyy-MM-dd" );

        pSimpleDateFormat.setCalendar( pCalendar_ );
        String sRetVal = pSimpleDateFormat.format( pCalendar_.getTime() );
        
        return sRetVal;
    }
    
    /**
     * Returns the time as a string of the given date object.
     *
     * @return   hh:mm:ss
     */
    public static String getTime( Date pDate_ ) 
    {
        SimpleDateFormat pSimpleDateFormat = 
               new SimpleDateFormat( "HH:mm:ss" );
        
        String sRetVal = pSimpleDateFormat.format( pDate_ );
        
        return sRetVal;
    }
    
    /**
     * @return   hh:mm:ss
     */
    public static String getTime( Calendar pCalendar_ ) 
    {
        SimpleDateFormat pSimpleDateFormat = 
               new SimpleDateFormat( "HH:mm:ss" );
        pSimpleDateFormat.setCalendar( pCalendar_ );

        String sRetVal = pSimpleDateFormat.format
               ( pCalendar_.getTime() );
        
        return sRetVal;
    }
    
    /**
     * Returns the current time with milli seconds.
     * E.g.: 20:14:59.032
     *
     * @return   current time with milli seconds.
     */
    static public String getTimeWithMillis() 
    {
        return getTimeWithMillis( Util.getCalendar() );
    }

    /**
     * Returns the current time with milli seconds. 
     * E.g.: 20:14:59.032
     *
     * @param    calendar_   the current time.
     * @return               current time with milli seconds.
     */
    static public String getTimeWithMillis( Calendar calendar_ ) 
    {
        String timeWithMillis = Util.getTime( calendar_ )
               + "."
               + Util.paddWithZero( calendar_.get( Calendar.MILLISECOND )
                                    , 3                                   );

        return timeWithMillis;
    }

    /**
     * Returns a string consiting of the iso date, time, and 
     * milli seconds, all concatenated without any space, 
     * colon, or dash. E.g. "20000811235959003" representing 
     * 2000-08-11 23:59:59.003 .
     *
     * return   a string containin only digits repesenting
     *          the date and time and milli seconds.
     */
    static public String getDateTimeAndMillis() 
    {
        Calendar now   = Util.getCalendar();
        String sRetVal = Util.getDate( now );
        sRetVal =  Util.replace          ( sRetVal, "-", "" );
        sRetVal += Util.getTimeWithMillis( now              );
        sRetVal =  Util.replace          ( sRetVal, ":", "" );
        sRetVal =  Util.replace          ( sRetVal, ".", "" );

        return sRetVal;
    }

    /**
     * Input format of the date is either CCYY-MM-DD or
     * CCYYMMDD.
     *
     * For example: 1999-11-26. Time is undefined and can
     * have every value.
     *
     * @return   null on parse error.
     */
    public static Date stringToDate( String sDate_ ) 
    {
        if ( sDate_ == null ) 
        {
            return null;
        }

        // offset used for '-' char
        int offset = 1;
        if ( sDate_.length() == 8 ) 
        {
            offset = 0;
        }

        Date dtRetVal = new Date();
        try 
        {
            String sYear = sDate_.substring( 0, 4 );
            String sMonth = sDate_.substring( 4 + offset, 
                                              6 + offset );
            String sDay = sDate_.substring( 6 + 2 * offset,
                                            8 + 2 * offset );
            int year = Util.atoi( sYear );
            int month = Util.atoi( sMonth ) - 1;
            int day = Util.atoi( sDay );
            Calendar pCalendar = new GregorianCalendar();
            pCalendar.setTime( dtRetVal );
            pCalendar.set( Calendar.YEAR, year );
            pCalendar.set( Calendar.MONTH, month );
            pCalendar.set( Calendar.DAY_OF_MONTH, day );
            dtRetVal.setTime( pCalendar.getTime().getTime() );
        }
        catch( Exception pException ) 
        {
            dtRetVal = null;
        }

        return dtRetVal;
    }

    /**
     * For example: 1999-11-26. Time is undefined and can
     * have every value.
     *
     * @return   null on parse error.
     */
    public static Calendar getDate( String sDate_ ) 
    {
        Calendar clnRetVal = getCalendar();
        try 
        {
            String sYear = sDate_.substring( 0, 4 );
            String sMonth = sDate_.substring( 5, 7 );
            String sDay = sDate_.substring( 8, 10 );
            int year = Util.atoi( sYear );
            int month = Util.atoi( sMonth ) - 1;
            int day = Util.atoi( sDay );
            clnRetVal.set( Calendar.YEAR, year );
            clnRetVal.set( Calendar.MONTH, month );
            clnRetVal.set( Calendar.DAY_OF_MONTH, day );
        }
        catch( Exception pException ) 
        {
            clnRetVal = null;
        }

        return clnRetVal;
    }

    /**
     * This is a replacement of the SimpleTimeZone.getTimeZone(String)
     * function that additionally creates a GregorianCalendar of the
     * given timezone. There is a new timezone 'CET' (Central
     * European Time. It has the official daylight saving time settings
     * (ranging from the last Sunday in March at 2:00 am to the last
     * Sunday in October at 2:00 am) and should be preferred over 'ECT'.
     *
     * @param   sTimeZoneID_   If it is null or "" then "GMT" is used.
     */
    public static Calendar getCalendar(String sTimeZoneID_) 
    {
        if ( Util.isEmpty( sTimeZoneID_ ) ) 
        {
            sTimeZoneID_ = "GMT";
        }
        TimeZone pTimeZone = null;
        if (sTimeZoneID_.equals("UTC")) 
        {
            pTimeZone = new SimpleTimeZone(0, "UTC");
        }
        else 
        {
            pTimeZone = SimpleTimeZone.getTimeZone(sTimeZoneID_);
        }
        Util.debug("Util.getCalendar(): pTimeZone: " + pTimeZone);
        // New Daylight Saving Time Convention in 35 European Countries
        if (sTimeZoneID_.equals("CET")) 
        {
            pTimeZone = new SimpleTimeZone( 1 * 1000 * 60 * 60
                                            , "CET"
                                            , Calendar.MARCH, -1
                                            , Calendar.SUNDAY
                                            , 2 * 1000 * 60 * 60
                                            , Calendar.OCTOBER
                                            , -1
                                            , Calendar.SUNDAY
                                            , 2 * 1000 * 60 * 60 );
        }
        Util.debug("Util.getCalendar(): pTimeZone: " + pTimeZone);
        Calendar pCalendar = new GregorianCalendar(pTimeZone);
        
        return pCalendar;
    }

    /**
     * @return   Calendar with local timezone
     */
    public static Calendar getCalendar() 
    {
        return getCalendar( Calendar.getInstance().getTimeZone().getID() );
    }

    /**
     * @param sTime_ e.g. 23:59:59
     */
    public static void setTime( Calendar pCalendar_,
                                String sTime_ )
    {
        panicIf( sTime_ == null );
        panicIf( sTime_.length() != 8 );

        String sDate = Util.getDate( pCalendar_ );

        pCalendar_.setTime( new Date( 0 ) );
        pCalendar_.set( atoi( sDate.substring( 0, 4 ) ),     // year
                        atoi( sDate.substring( 5, 7 ) ) - 1, // month
                        atoi( sDate.substring( 8 ) ),        // day
                        atoi( sTime_.substring( 0, 2 ) ),
                        atoi( sTime_.substring( 3, 5 ) ),
                        atoi( sTime_.substring( 6, 8 ) ) );
        Util.debug( "ccl.util.Util.setTime(..).time: " +
                    pCalendar_.getTime().getTime() );
        long lTime = pCalendar_.getTime().getTime();
        Util.debug( "ccl.util.Util.setTime(..).time%1000: " +
                    lTime % 1000L );

        Util.debug( "ccl.util.Util.setTime(..).time: " +
                    pCalendar_.getTime().getTime() );
    }

    /**
     * @param sDate_ e.g. 2000-01-26
     */
    public static void setDate( Calendar pCalendar_,
                                String sDate_ )
    {
        panicIf( sDate_ == null );
        panicIf( sDate_.length() != 10 );

        String sTime = Util.getTime( pCalendar_ );

        pCalendar_.setTime( new Date( 0 ) );
        pCalendar_.set( atoi( sDate_.substring( 0, 4 ) ),     // year
                        atoi( sDate_.substring( 5, 7 ) ) - 1, // month
                        atoi( sDate_.substring( 8 ) ),        // day
                        atoi( sTime.substring( 0, 2 ) ),
                        atoi( sTime.substring( 3, 5 ) ),
                        atoi( sTime.substring( 6, 8 ) ) );
    }

    /**
     * Input format of the date is either CCYY-MM-DD or
     * CCYYMMDD.
     */
    public static boolean isDateValid( String sDate_ ) 
    {
        if ( sDate_ == null 
             || (sDate_.length() != 8
                 && sDate_.length() != 10) )
        {
            return false;
        }

        Date pDate = Util.stringToDate( sDate_ );
        Date dtValidation = new Date( pDate.getTime() );
        String sValidation = Util.getStandardDate( dtValidation );
        sValidation = Util.replace( sValidation, "-", "" );
        //String sValidation = Util.getStandardDate( pDate );

        return sValidation.equals( Util.replace( sDate_, "-", "" ) );
    }

    /**
     * Return the number of days between to dates.
     * The first day and last day are both also counted.
     */
    public static int getNumberOfDays( String sFrom_,
                                       String sTo_ )
    {
        Calendar calFrom = getDate( sFrom_ );
        Calendar calTo   = getDate( sTo_   );

        long diff = calTo.getTime().getTime()
               - calFrom.getTime().getTime();
        diff = diff / (1000L * 60L * 60L * 24L) + 1L;

        return (int) diff;
    }

    /**
     * Provides the iso date of the next day after the given
     * date.
     */
    public static String getNextDay( String sDate_ )
    {
        Calendar calendar = getDate( sDate_ );

        calendar.add( Calendar.DATE,  1 );

        return getDate( calendar );        
    }

    /**
     * Provide an iso date string and get back an iso date
     * string with the last day in the same month.
     */
    public static String getLastDayOfMonth( String sDate_ ) 
    {
        Calendar calendar = getDate( sDate_ );

        calendar.set( Calendar.DATE ,  1 );
        calendar.add( Calendar.MONTH,  1 );
        calendar.add( Calendar.DATE , -1 );

        return getDate( calendar );
    }


    /**
     * Provide an iso date string and get back an iso date
     * string which has the day set to one.
     */
    public static String getFirstDayOfMonth( String sDate_ ) 
    {
        Calendar calendar = getDate( sDate_ );

        calendar.set( Calendar.DATE ,  1 );

        return getDate( calendar );
    }

    /**
     * Returns the English name of the month of the given 
     * iso-date.
     */
    public static String getMonth( String sDate )
    {
        int month = Util.atoi( sDate.substring( 5, 7 ) );

        if ( month == 1 )
        {
            return "Januar";
        }
        else if ( month == 2 )
        {
            return "February";
        }
        else if ( month == 3 )
        {
            return "March";
        }
        else if ( month == 4 )
        {
            return "April";
        }
        else if ( month == 5 )
        {
            return "May";
        }
        else if ( month == 6 )
        {
            return "June";
        }
        else if ( month == 7 )
        {
            return "July";
        }
        else if ( month == 8 )
        {
            return "August";
        }
        else if ( month == 9 )
        {
            return "September";
        }
        else if ( month == 10 )
        {
            return "October";
        }
        else if ( month == 11 )
        {
            return "November";
        }
        else if ( month == 12 )
        {
            return "December";
        }

        return null;
    }

    /**
     * @deprecated   use getDate() instead.
     */
    public static String getTodaySortable() 
    {
        String sDatum = null;
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        sDatum = itoa(calendar.get(Calendar.YEAR));
        if (calendar.get(Calendar.MONTH) < 9) 
        {
            sDatum += "0";
        }
        sDatum += itoa(calendar.get(Calendar.MONTH) + 1);
        if (calendar.get(Calendar.DATE) < 10) 
        {
            sDatum += "0";
        }
        sDatum += itoa(calendar.get(Calendar.DATE));
        
        return sDatum;
    }

    /**
     * @deprecated
     * @see #getTodaySortable()
     */
    public static String getHeuteSortable() 
    {
        return getTodaySortable();
    }

    /*public static long timeToSeconds(String sTime_) {
      return ((long)MultiDate.getSecondsFromTime(sTime_));
      }*/

    // -----------------------------------------------------
    // miscelaneous methods
    // -----------------------------------------------------

    /**
     * Sometimes you need a dummy object just to have any
     * value for a hashtable or so and it doesn't matter at
     * all if we always use the same object, so here is one
     * you can use without wasting extra memory.
     */
    public static Object getConstantObject() 
    {
        return O_CONSTANT;
    }

    /**
     * Current thread sleeps in seconds.
     */
    public static void sleep( int seconds_ ) 
    {
        try 
        {
            Thread.currentThread().sleep( seconds_ * 1000 );
        }
        catch( Exception pException) 
        {
        }
    }

    /**
     * Checks that a character is of type alpha. This means either the given character
     * is in range a-z or it is a German umlaut (sorry, no other countries are 
     * supported right now - please add what you need).
     *
     * @param   c_   a character to test for alpha status.
     *
     * @return       true if the input character is of type alpha
     *               (A-Za-z or German umlaut).
     */
    public static boolean isAlpha(char c_) 
    {
        if (('A' <= c_ && c_ <= 'Z') ||
            ('a' <= c_ && c_ <= 'z'))
        {
            return true;
        }
        for(int i = 0; i < AC_UMLAUT.length; i++) 
        {
            if (c_ == AC_UMLAUT[i]) 
            {
                return true;
            }
        }
        
        return false;
    }

    /**
     * @deprecated
     * @see java.lang.Math#max(long, long) java.lang.Math.max
     */
    public static long max(long a_, long b_) 
    {
        if (a_ > b_) 
        {
            return a_;
        }
        
        return(b_);
    }
    
    /**
     * @deprecated
     * @see java.lang.Math#max(int, int) java.lang.Math.max
     */
    public static int max(int a_, int b_) 
    {
        if (a_ > b_) 
        {
            return a_;
        }
        
        return(b_);
    }
    
    /**
     * @deprecated
     * @see java.lang.Math#min(int, int) java.lang.Math.min
     */
    public static int min(int a_, int b_) 
    {
        if (a_ < b_) 
        {
            return a_;
        }
        
        return(b_);
    }

    /**
     * This method is the first part of a service to swap two objects in less than
     * 3 steps.
     * Normally if you swap two objects you need 3 statements.<br>
     * c = a; a = b; b = c;<br>
     * Using this method plus method swap() you can get away with only two statements
     * in your code.<p>
     *
     * For example:<br>
     * o1 = swap(o1, o2);
     * o2 = swap();
     *
     * @return   the second input parameter object.
     *
     * @see #swap() swap
     */
    public static Object swap(Object objFirst, Object objSecond) 
    {
        panicIf(_bNochKeinSwap == false);
        _bNochKeinSwap = false;

        _objSwap = objFirst;
        
        return(objSecond);
    }

    /**
     * This method is the second step of a service to swap two objects
     * in less than 3 steps.
     * Normally if you swap two objects you need 3 statements.<br>
     * c = a; a = b; b = c;<br>
     * Using this method plus method swap() you can get away with only two
     * statements in your code.<p>
     *
     * Note that this method will keep a reference to the first object until
     * the swap method pair will be used again with a new pair of parameters!!!
     *
     * @return   the first input parameter object from a previous 
     *           swap(Object, Object) invocation.
     *
     * @see #swap(java.lang.Object, java.lang.Object) swap
     */
    public static Object swap() 
    {
        panicIf(_bNochKeinSwap == true);
        _bNochKeinSwap = true;

        return(_objSwap);

        //_objSwap = null;
    }

    /**
     * This method is the first part of a service to swap two int values in less 
     * than 3 steps.
     * Normally if you swap two objects you need 3 statements.<br>
     * c = a; a = b; b = c;<br>
     * Using this method plus method swap() you can get away with only two statements
     * in your code.
     *
     * @return   the second input parameter object.
     *
     * @see #swapInt() swapInt
     */
    public static int swapInt(int first, int second) 
    {
        panicIf(_bNochKeinIntSwap == false);
        _bNochKeinIntSwap = false;

        _swap = first;

        return(second);
    }

    /**
     * This method is the second step of a service to swap two int values
     * in less than 3 steps.
     * Normally if you swap two objects you need 3 statements.<br>
     * c = a; a = b; b = c;<br>
     * Using this method plus method swap() you can get away with only two
     * statements in your code.
     *
     * @return   the first input parameter object from a previous 
     *           swap(Object, Object) invocation.
     *
     * @see #swapInt(int, int) swapInt
     */
    public static int swapInt() 
    {
        panicIf(_bNochKeinIntSwap == true);
        _bNochKeinIntSwap = true;

        return(_swap);
    }

    /**
     * pObject_.getClass().getName() returns the name including
     * its package. This method returns just the name without
     * its package.
     */
    public static String getObjectName( Object pObject_ ) 
    {
        String sRetVal = pObject_.getClass().getName();
        sRetVal = sRetVal.substring( sRetVal.lastIndexOf( '.' ) + 1 );

        return sRetVal;
    }

    /**
     * This method returns the memory currently in use.
     */
    public static long getUsedMemory() 
    {
        long total = Runtime.getRuntime().totalMemory();
        long free  = Runtime.getRuntime().freeMemory ();
        
        return total - free;
    }

    /**
     * This method returns the percentage of used memory.
     */
    public static String getUsedMemoryPercentage() 
    {
        long total = Runtime.getRuntime().totalMemory();
        long used  = getUsedMemory();

        return getUsedMemoryPercentage( total, used );
    }

    /**
     * This method returns the percentage of used memory.
     */
    public static String getUsedMemoryPercentage( long used
                                                  , long total ) 
    {
        int percentage = (int) (used * 1000 / total);
        String sPercentage = Util.itoa( percentage / 10 ) + "." +
               Util.itoa( percentage % 10 ) + " %";
        
        return sPercentage;
    }

    /**
     * Returns a message which has nicely formatted information about the 
     * current memory usage.
     */
    public static String formatMemoryInfo() 
    {
        long total = Runtime.getRuntime().totalMemory();
        long free  = Runtime.getRuntime().freeMemory ();
        long used  = total - free;

        NumberFormat pNumberFormat = DecimalFormat.getInstance( Locale.US );

        String sTotal = pNumberFormat.format( total );
        String sUsed  = pNumberFormat.format( used  );
        sUsed = getSpaces( sTotal.length() - sUsed.length() ) + sUsed;

        return "Memory (in use/total):   " 
               + sUsed
               + " / "
               + sTotal
               + "   ("
               + getUsedMemoryPercentage( used, total )
               + ")";
    }
}
