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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Utility class for file operations.<p>
 *
 * Simple but most commonly used methods of this class are:<br>
 * - {@link #readFile(java.lang.String) readFile}<br>
 * - {@link #writeFile(java.lang.String, java.lang.String) writeFile}<br>
 * - {@link #appendFile(java.lang.String, java.lang.String) appendFile}<br>
 * - {@link #concatPath(java.lang.String, java.lang.String) concatPath}<br>
 * - {@link #exists(java.lang.String) exists}<br>
 * - {@link #existsDir(java.lang.String) existsDir}<br>
 * - {@link #existsFile(java.lang.String) existsFile}<p>
 *
 * Other less frequently used but still handy methods are:<br>
 * - {@link #getResourceAsString(java.lang.Object, java.lang.String) getResourceAsString}<br>
 * - {@link #normalizeFileName(java.lang.String) normalizeFileName} to take the current user directory into account via the 'user.dir' system property<br>
 * - {@link #deleteRecursively(java.lang.String) deleteRecursively}<p>
 *
 * There are also the standard file operation methods available.
 * Some of these are named for convenience and easy memorization after their Unix
 * counterparts, like {@link #mv(java.lang.String, java.lang.String) mv}, {@link #rm(java.lang.String) rm}, {@link #mkdir(java.lang.String) mkdir} ({@link #md(java.lang.String) md}), and {@link #cp(java.lang.String, java.lang.String) cp}.
 *
 * @version  $Id: FileUtil.java,v 1.1 2005/10/30 22:22:35 ksen Exp $
 * @author <a href="http://www.kclee.com/clemens/">
 *         Chr. Clemens Lee</a>
 *         &lt;<a href="mailto:clemens@kclee.com">
 *         clemens@kclee.com
 *         </a>>
 */
public class FileUtil 
{
    /**
     * Utility class which should never instanciate itself.
     */
    private FileUtil() 
    {
        super();
    }
    
    /**
     * You give it a package name and it looks with the
     * help of the classpath on the file system if it can
     * find a directory that relates to this package.
     *
     * @return Includes the local path of the package too.
     *         If no path could be found, "" is returned.
     */
    public static String getPackagePath( String sPackageName_ ) 
    {
        return getPackagePath(sPackageName_,
                              System.getProperty("java.class.path"));
    }

    /**
     * You give it a package name and it looks with the
     * help of the classpath on the file system if it can
     * find a directory that relates to this package.<p>
     *
     * Todo: what happens with an empty classpath? That should be
     *       equivalent to a "." classpath.
     *
     * @return includes the local path of the package too.
     *         If no path could be found, "" is returned.
     */
    public static String getPackagePath( String sPackageName_,
                                         String sClassPath_ )
    {
        Util.debug( "ccl.util.FileUtil.getPackagePath(..).sPackageName_: " +
                    sPackageName_ );

        Util.panicIf( Util.isEmpty( sPackageName_ ) );
        Util.panicIf( Util.isEmpty( sClassPath_ ) );

        // remove leading dots
        int index = Util.indexOfNot( sPackageName_, '.' );
        String sPackagePath = "";
        if ( index > -1 ) 
        {
            sPackagePath = sPackageName_.
                   substring( index );
        }

        // '.' -> '/'
        sPackagePath = sPackagePath.replace
               ( '.', File.separatorChar );

        Vector vClassPaths = Util.stringToLines
               ( sClassPath_, File.pathSeparatorChar );
        if ( vClassPaths.size() == 0 ) 
        {
            vClassPaths.addElement( "." );
        }
        Enumeration eClassPaths = vClassPaths.elements();
        while( eClassPaths.hasMoreElements() ) 
        {
            String sNextPath = (String) eClassPaths.nextElement();
            sNextPath = concatPath( sNextPath, sPackagePath );
            Util.debug( "ccl.util.FileUtil.getPackagePath(..).sNextPath: " +
                        sNextPath );
            if ( existsDir( sNextPath ) ) 
            {
                return( sNextPath );
            }
        }

        return "";
    }
    
    /**
     * Does work only when class exists outside a zip or jar file.
     *
     * @return Includes the local path of the package too.
     */
    public static String getClassPath(Object oClass_) 
    {
        Util.debug( "ccl.util.FileUtil.getClassPath(..).oClass_: " 
                    + String.valueOf( oClass_ ) );

        if (oClass_ == null) 
        {
            return null;
        }

        String sClassName = oClass_.getClass().getName();
        int index = sClassName.lastIndexOf( '.' );
        String sPackageName = ".";
        if ( index != -1 ) 
        {
            sPackageName = sClassName.substring
                   ( 0, sClassName.lastIndexOf( '.' ) );
        }
        Util.debug( "ccl.util.FileUtil.getClassPath(..).sPackageName: " 
                    + sPackageName );
        String sPackagePath = getPackagePath(sPackageName);

        return sPackagePath;
    }

    /**
     * Does work only when class exists outside a zip or jar file.
     *
     * @return Includes the local path of the package too.
     */
    public static String getClassPath(String sFullClassName_) 
    {
        Util.debug( "FileUtil.getClassPath(..).sFullClassName_: " 
                    + sFullClassName_ );
        if (Util.isEmpty(sFullClassName_)) 
        {
            return null;
        }

        int indexLastDot = sFullClassName_.lastIndexOf('.');
        if (indexLastDot == -1) 
        {
            indexLastDot = 0;
        }
        String sPackageName = sFullClassName_.substring(0, indexLastDot);
        if (Util.isEmpty(sPackageName)) 
        {
            sPackageName = ".";
        }
        String sPackagePath = getPackagePath(sPackageName);

        return sPackagePath;
    }

    private static final String S_SWINGALL_JAR = "swingall.jar";
    private static final String S_SWING_JAR = "swing.jar";

    /**
     * It searchs in the classpath for swingall.jar, then for
     * swing.jar and last for com/sun/java/swing on the file
     * system. If user renamed the swing archive, we are out
     * of luck.
     *
     * @return null or "" if swing was not found in the
     *         classpath, otherwise returns the home directory. 
     *         It's unspecified if the home dir has a separator
     *         char at the end.
     */
    public static String getSwingHome() 
    {
        String sRetVal = null;
        Vector vClasspath = Util.stringToLines
               (System.getProperty("java.class.path"), 
                File.pathSeparatorChar);

        for(Enumeration eClasspath = vClasspath.elements();
            eClasspath.hasMoreElements(); )
        {
            String sClasspathElement = (String) eClasspath.
                   nextElement();
            Util.debug( "FileUtil.getSwingHome().sClasspathElement: " 
                        + sClasspathElement );
            if (Util.endsWith(sClasspathElement, S_SWINGALL_JAR)) 
            {
                // swingall.jar
                sRetVal = sClasspathElement.substring
                       (0, sClasspathElement.length() - 
                        S_SWINGALL_JAR.length());

                return sRetVal;
            } 
            else if (Util.endsWith(sClasspathElement, S_SWING_JAR)) 
            {
                // swing.jar
                sRetVal = sClasspathElement.substring
                       (0, sClasspathElement.length() - 
                        S_SWING_JAR.length());

                return sRetVal;
            }
        }

        sRetVal = getPackagePath("com.sun.java.swing");
        if (!Util.isEmpty(sRetVal)) 
        {
            sRetVal = sRetVal.substring(0, sRetVal.length() -
                                        18 + 1);
        }
        Util.debug("FileUtil.getSwingHome().sRetVal: " + sRetVal);

        return sRetVal;
    }

    /**
     * Concatenates a file path with the file name. If 
     * necessary it adds a File.separator between the path
     * and file name. For example "/home" or "/home/" and "clemens" both
     * become "/home/clemens".<p>
     *
     * This method is inspired from the FrIJDE project out
     * of the gCollins.File.FileTools class.<p>
     *
     * FrIJDE Homepage:
     * http://amber.wpi.edu/~thethe/Document/Besiex/Java/FrIJDE/
     *
     * @param    sPath_   a directory path. Is not allowed to be null.
     * @param    sFile_   the base name of a file.
     *
     * @return            sPath_ if sFile_ is empty.
     */
    public static String concatPath( String sPath_, String sFile_ ) 
    {
        Util.panicIf( sPath_ == null );
        Util.debug( "ccl.util.FileUtil.concatPath(..).sPath_: --->" +
                    sPath_ + "<---" );
        Util.debug( "ccl.util.FileUtil.concatPath(..).sFile_: " +
                    sFile_ );

        String sRetVal = sPath_;

        if ( !Util.isEmpty( sFile_ ) ) 
        {
            if ( sPath_.length() > 0 
                 && (!Util.endsWith( sPath_, File.separatorChar )) ) 
            {
                sRetVal += File.separator;
            }

            sRetVal += sFile_;
        }

        return sRetVal;
    }

    /**
     * Get a DataInputStream for a specified file.
     */
    public static DataInputStream openFile(String sFile) 
    {
        FileInputStream fis;

        try 
        {
            fis = new FileInputStream(sFile);
            if (fis != null) 
            {
                DataInputStream dis = new DataInputStream(fis);

                return(dis);
            }
        } 
        catch (Exception e) 
        {
        }

        return(null);
    }

    /**
     * Get a DataOutputStream for a specified file.
     */
    public static DataOutputStream openOutputFile(String sFile) 
    {
        FileOutputStream fos;

        try 
        {
            fos = new FileOutputStream(sFile);
            if (fos != null) 
            {
                DataOutputStream dos = new DataOutputStream(fos);

                return(dos);
            }
        } 
        catch (Exception e) 
        {
        }

        return(null);
    }

    /**
     * Reads a stream, gives back a string.
     *
     * @throws   FileNotFoundException   if file does not exist.
     * @throws   IOException             if any file operation fails.
     */
    public static String readStream( InputStream stream_ )
        throws IOException,
               FileNotFoundException
    {
        StringBuffer sFileContent = new StringBuffer(100000);

        InputStreamReader streamReader = new InputStreamReader( stream_ );
        if ( streamReader != null) 
        {
            BufferedReader brIni = new BufferedReader( streamReader );
            if (brIni != null) 
            {
                while(brIni.ready()) 
                {
                    String sLine = brIni.readLine();
                    if ( sLine == null )
                    {
                        break;
                    }
                    sFileContent.append( sLine ).append( '\n' );
                }
                brIni.close();
            }
            streamReader.close();
        }

        return sFileContent.toString();
    }

    /**
     * Reads a File and returns the content in a String.
     * CRLF -> LF conversion takes place. This is a convenience method so you don't
     * need to bother creating a file reader object and closing it after it has
     * been used.
     *
     * @param    sFileName_   the name of the file to read.
     *
     * @return                a string with the content of the file but without 
     *                        any CR characters.
     *
     * @throws   FileNotFoundException   if file does not exist.
     * @throws   IOException             if any file operation fails.
     */
    public static String readFile(String sFileName_)
        throws IOException
               , FileNotFoundException
    {
        StringBuffer sFileContent = new StringBuffer(100000);
        
        FileReader frIni = new FileReader(sFileName_);
        if (frIni != null) 
        {
            BufferedReader brIni = new BufferedReader(frIni);
            if (brIni != null) 
            {
                while(brIni.ready()) 
                {
                    String sLine = brIni.readLine();
                    if ( sLine == null )
                    {
                        break;
                    }
                    sFileContent.append( sLine ).append( '\n' );
                }
                brIni.close();
            }
            frIni.close();
        }

        return sFileContent.toString();
    }

    /**
     * Read binary file.
     *
     * @deprecated   Method has no implementation.
     */
    public static Byte[] readBinaryFile(String sFileName_) 
    {
        Util.panicIf( true
                      , "Method ccl.util.FileUtil.readBinaryFile(..): "
                        + "has no implementation!" );

        return null;
    }

    /**
     * Reads the content of a URL and returns a string.
     * CRLF are converted to LF.
     *
     * @throws   IOException             if any file operation fails.
     * @throws   MalformedURLException   if given location is wrong.
     */
    public static String readFile(URL location)
        throws MalformedURLException
               , IOException
    {
        InputStream is = location.openStream();
        int oneChar;
        StringBuffer sb = new StringBuffer();

        while( (oneChar = is.read()) != -1 )
        {
            if ( (char) oneChar != 13 ) 
            {
                sb.append( (char) oneChar );
            }
        }
        is.close();

        return(sb.toString());
    }

    /* * Used for synchronization lock. */
    /*private static String _sRW = "rw";*/

    /**
     * Append string to a file content.
     * On unix this is an atomic operation.
     *
     * @throws   IOException   if writing file fails.
     */
    public static void appendFile(String sFileName_, 
                                  String sAddedContent_)
        throws IOException
    {
        /*synchronized( _sRW ) {
            RandomAccessFile pRandomAccessFile = 
                   new RandomAccessFile(sFileName_, _sRW );

            pRandomAccessFile.seek(pRandomAccessFile.length());
            
            for (int i=0; i < sAddedContent_.length(); i++) {
                pRandomAccessFile.write(sAddedContent_.charAt(i));
            }
            
            pRandomAccessFile.close();
            }*/
        Util.debug( "ccl.util.FileUtil.appendFile(..).sFileName_: " 
                    + sFileName_ );
        FileWriter writer = new FileWriter( sFileName_, true );
        writer.write( sAddedContent_ );
        writer.close();
    }

    /**
     * Writes a String into a given File.
     *
     * @throws   IOException   if writing file fails.
     */
    public static void writeFile(String sFileName, String sContent)
        throws IOException
    {
        FileOutputStream fos = new FileOutputStream(sFileName);
        for ( int i = 0; i < sContent.length(); i++ ) 
        {
            fos.write(sContent.charAt(i));
        }
        fos.close();
    }

    /**
     * Writes a String into a given File and does move the old file
     * if existent to sFileName + ".bak".
     *
     * @see #writeFile(java.lang.String, java.lang.String)
     */
    public static void writeFileWithBackup(String sFileName_,
                                           String sContent_)
        throws IOException
    {
        Util.panicIf(Util.isEmpty(sFileName_));
        move(sFileName_, sFileName_ + ".bak");
        writeFile(sFileName_, sContent_);
    }

    /**
     * Copy the specified file to the same location and append
     * ".bak" to its file name.
     *
     * @exception   IOException   when creating backup file
     *                            fails.
     */
    public static void createBackupFile( String sFileName_ )
        throws IOException
    {
        Util.panicIf( Util.isEmpty( sFileName_ ) );
        if ( existsFile( sFileName_ ) ) 
        {
            copy( sFileName_, sFileName_ + ".bak");
        }
    }

    /**
     * Like writeFile but converts LFs to CRLFs.
     *
     * @see #writeFile(java.lang.String, java.lang.String)
     */
    public static void writeDosFile(String sFileName_, String sContent_)
        throws IOException
    {
        sContent_ = Util.replace(sContent_, "\n",
                                 (new Character((char) 13)).toString() + "\n");
        writeFile(sFileName_, sContent_);
    }

    /**
     * Reads two files and compares them. Be carefull, this
     * is not a binary comparison like with unix diff.
     * Instead both files are loaded and compared as strings
     * after CRLF -> LR line conversion has taken place.
     */
    public static boolean equalsFile(String sFileNameA_,
                                     String sFileNameB_)
    {
        // wenn beide nicht gelesen werden können => false
        String sFileContentA = "";
        String sFileContentB = "";
        try 
        {
            sFileContentA = readFile(sFileNameA_);
            sFileContentB = readFile(sFileNameB_);
        }
        catch(Exception e) 
        {
            return false;
        }

        return(sFileContentA.equals(sFileContentB));
    }

    /**
     * True if a specified file exists.
     */
    public static boolean existsFile(String sFileName_) 
    {
        Util.panicIf(sFileName_ == null, "FileUtil: existsFile");

        File pFile = new File(sFileName_);

        return(pFile.isFile());
    }

    /**
     * Tests, if a given directory exists.
     */
    public static boolean existsDir(String sDirName_) 
    {
        Util.panicIf(sDirName_ == null, "FileUtil: existsDir(String)");

        File pFile = new File(sDirName_);
        return(pFile.isDirectory());
    }

    /**
     * True if a specified object on the file system is either
     * a file or a directory.
     */
    public static boolean exists(String sFileOrDirName_) 
    {
        Util.panicIf(sFileOrDirName_ == null, "Util: exists");
        return(existsFile(sFileOrDirName_) ||
               existsDir(sFileOrDirName_));
    }

    /**
     * Returns a Vector with all file names that are inside the
     * specified directory. <br>
     * For example: FileUtil.getFiles("C:\", ".txt")
     *
     * @return Not the full path names are returned, just the simple
     *         file names.
     *
     * @see #getFiles(java.lang.String, java.lang.String)
     */
    public static Vector getFilteredDirContent(String sDir_,
                                               FilenameFilter pFilenameFilter_)
    {
        Util.debug( "ccl.util.FileUtil.getFilteredDirContent(..).sDir_: " +
                    sDir_ );
        Util.panicIf(sDir_ == null);
        File pFile = new File(sDir_);
        Util.panicIf(!pFile.isDirectory());
        
        String asDirContent[] = pFile.list(pFilenameFilter_);
        Util.debug( "ccl.util.FileUtil.getFilteredDirContent(..)"
                    + ".asDirContent.length: " 
                    + asDirContent.length );
        
        Vector vRetVal = new Vector();
        for( int index = 0; index < asDirContent.length; index++ )
        {
            vRetVal.addElement(asDirContent[index]);
        }
        Util.debug( "ccl.util.FileUtil.getFilteredDirContent(..).vRetVal: " +
                    vRetVal );
        
        return(vRetVal);
    }

    /**
     * @deprecated
     * @see #getFilteredDirContent(java.lang.String, java.io.FilenameFilter)
     */
    public static Vector _getFilteredDirContent(String sDir_,
                                                FilenameFilter pFilenameFilter_)
    {
        return getFilteredDirContent(sDir_, pFilenameFilter_);
    }

    /**
     * Liefert einen Vector mit allen Unter-Directories zurück.
     *
     * @return Es werden nicht die vollen Pfadangaben bei den
     *         Sub-Directories angegeben, sondern nur der einfache
     *         Name.
     */
    public static Vector getSubDirs(String sDir_) 
    {
        /** Only used internally. */
        class DirFilter implements FilenameFilter 
        {
            /**
             * Returns true, if the specified file is a directory.
             */
            public boolean accept(File flDir_, String sName_) 
            {
                File flTemp = new File(flDir_, sName_);
                return flTemp.isDirectory();
            }
        }
        
        return(getFilteredDirContent(sDir_, new DirFilter()));
    }

    /**
     * Returns a Vector with all files of the given directory.
     *
     * @return Not the full path names are returned, only the simple
     *         names.
     */
    public static Vector getFiles(String sDir_) 
    {
        FilenameFilter pFilenameFilter = new FilenameFilter() {
            /**
             * Returns true, if the specified file object is a file
             * and not a directory.
             */
            public boolean accept(File flDir_, String sName_) 
            {
                File flTemp = new File(flDir_, sName_);
                return flTemp.isFile();
            }
        };

        return(getFilteredDirContent(sDir_, pFilenameFilter));
    }

    /**
     * Returns a Vector with all file names that are inside the
     * specified directory. <br>
     * For example: Util.getFiles("C:\", ".txt")
     *
     * @param sSuffix_ A list of suffixes (separated with the
     *                 File.pathSeparatorChar) the file names must
     *                 match. Otherwise they are not selected.<br>
     *                 For example: ".gif;.jpg" or ".gif:.jpg"
     *                 depending on your operating system.
     *
     * @return Not the full path names are returned, just the simple
     *         file names.
     */
    public static Vector getFiles(String sDir_, String sSuffix_) 
    {
        Util.panicIf(sSuffix_ == null );

        final Vector vFinalSuffixes = Util.stringToLines
               (sSuffix_, File.pathSeparatorChar);

        /** Only used internally. */
        class SuffixFilter implements FilenameFilter 
        {
            /**
             * Returns true if the specified file object contains
             * one of the desired suffixes.
             */
            public boolean accept(File flDir_, String sName_) 
            {
                if ( vFinalSuffixes.size() == 0 ) 
                {
                    // empty suffix, always true
                    return true;
                }
                for( Enumeration e = vFinalSuffixes.elements()
                     ; e.hasMoreElements()
                     ; ) 
                {
                    String sSuffix = (String) e.nextElement();
                    if ( sName_.endsWith( sSuffix ) ) 
                    {
                        return true;
                    }
                }

                return false;
            }
        }

        return( getFilteredDirContent(sDir_, new SuffixFilter()) );
    }

    /**
     * Returns a Vector with all file (and dir) names that are inside the
     * specified directory. <br>
     * For example: Util.getFiles("C:\", ".txt")
     *
     * @param sPrefix_ A prefix which each file must have to be 
     *                 returned on the list.
     * @param sSuffix_ A list of suffixes (separated with the
     *                 File.pathSeparatorChar) the file names must
     *                 match. Otherwise they are not selected.<br>
     *                 For example: ".gif;.jpg" or ".gif:.jpg"
     *                 depending on your operating system.
     *
     * @return Not the full path names are returned, just the simple
     *         file names.
     */
    public static Vector getFiles( String sDir_
                                   , final String sPrefix_
                                   , String sSuffix_ )
    {
        Util.panicIf(sPrefix_ == null );
        Util.panicIf(sSuffix_ == null );

        final Vector vFinalSuffixes = Util.stringToLines
               (sSuffix_, File.pathSeparatorChar);

        /**
         * Only used internally.
         */
        class SuffixFilter implements FilenameFilter 
        {
            /**
             * Returns true if the specified file object contains
             * one of the desired suffixes.
             */
            public boolean accept(File flDir_, String sName_) 
            {
                Util.debug( this, "accept(..).sName_: " + sName_ );
                boolean bRetVal = false;

                if ( sName_.startsWith( sPrefix_ ) )
                {
                    if ( vFinalSuffixes.size() == 0 ) 
                    {
                        // empty suffix, always true
                        bRetVal = true;
                    }
                    for( Enumeration e = vFinalSuffixes.elements()
                         ; e.hasMoreElements()
                         ; )
                    {
                        String sSuffix = (String) e.nextElement();
                        if ( sName_.endsWith( sSuffix ) ) 
                        {
                            bRetVal = true;

                            break;
                        }
                    }
                }
                Util.debug( this, "accept(..).bRetVal: " + bRetVal );

                return bRetVal;
            }
        }

        return( getFilteredDirContent(sDir_, new SuffixFilter()) );
    }

    /**
     * Checks weather two paths point to the same object on
     * the file system.
     */
    public static boolean equalsPath(String sFirstPath_,
                                     String sSecondPath_)
    {
        Util.panicIf (sFirstPath_ == null || sSecondPath_ == null);
        if (sFirstPath_.equals(sSecondPath_)) 
        {
            return true;
        }
        if (sFirstPath_.equals("") || sSecondPath_.equals("")) 
        {
            return false;
        }
        sFirstPath_ = sFirstPath_.replace('\\', '/').toLowerCase();
        sSecondPath_ = sSecondPath_.replace('\\', '/').toLowerCase();
        if ( sFirstPath_.charAt( sFirstPath_.length() - 1 ) != '/' )
        {
            sFirstPath_ += "/";
        }
        if ( sSecondPath_.charAt( sSecondPath_.length() - 1 ) != '/' )
        {
            sSecondPath_ += "/";
        }

        return sFirstPath_.equals(sSecondPath_);
    }

    /**
     * Remove file on file system.
     *
     * @return true if error.
     */
    public static boolean delete(String sFileName_) 
    {
        Util.panicIf(sFileName_ == null);
        boolean bRetVal = false;
        boolean bExists = exists(sFileName_);
        if (!bExists) 
        {
            // job done
            return bRetVal;
        }
        try 
        {
            File flTemp = new File(sFileName_);
            bRetVal = !(flTemp.delete());
        }
        catch(SecurityException pSecurityException) 
        {
            return true;
        }

        return bRetVal;
    }

    /**
     * Remove file on file system. This is a shortcut for
     * the 'delete' method. Unlike the Unix 'rm' this one
     * also deletes directories, but only empty ones.
     *
     * @return true if error.
     *
     * @see   #delete
     * @see   #deleteRecursively
     */
    public static boolean rm(String sFileName_) 
    {
        return delete( sFileName_ );
    }

    /**
     * Delete file or directory. Do the same to all sub files
     * and directories.
     *
     * @return   true   on error.
     */
    public static boolean deleteRecursively( String sFileName_ ) 
    {
        Util.panicIf(sFileName_ == null);

        Util.debug( "ccl.util.FileUtil.deleteRecursively(..).sFileName_: "
                    + sFileName_ );
        boolean bRetVal = false;
        boolean bExists = exists(sFileName_);
        if (!bExists) 
        {
            // job done
            return bRetVal;
        }
        if ( existsFile( sFileName_ ) ) 
        {
            return delete( sFileName_ );
        }
        // it has to be a directory
        try
        {
            File flTemp = new File(sFileName_);
            String[] asList = flTemp.list();
            for( int file = 0; file < asList.length; file++ ) 
            {
                bRetVal = deleteRecursively( concatPath( sFileName_
                                                         , asList[ file ] ) );

                if ( bRetVal ) 
                {
                    return bRetVal;
                }
            }
            bRetVal = !(flTemp.delete());
        } 
        catch(SecurityException pSecurityException) 
        {
            return true;
        }

        return bRetVal;
    }

    /**
     * Renames or moves a file. Be aware that the old file at the
     * destination will be deleted without a warning.
     *
     * @return true if an error occurred. false if sSource_ is not
     *         existent.
     *
     * @see    #mv
     */
    public static boolean move(String sSource_, String sDest_) 
    {
        Util.panicIf(sSource_ == null || sDest_ == null);
        try
        {
            File flSource = new File(sSource_);
            File flDest = new File(sDest_);
            delete(sDest_);
            
            return !flSource.renameTo(flDest);
        }
        catch(Exception pException) 
        {
        }
        
        return true;
    }

    /**
     * Renames or moves a file. Be aware that the old file at the
     * destination will be deleted without a warning. This is a
     * shortcut for method 'move'.
     *
     * @return true if an error occurred. false if sSource_ is not
     *         existent.
     *
     * @see    #move
     */
    public static boolean mv(String sSource_, String sDest_) 
    {
        return move( sSource_, sDest_ );
    }

    /**
     * Creates the specified directory and if necessary any 
     * parent directories. It is a shortcut for 'mkdir'.
     *
     * @return true if an error occured. Note that this is 
     *         vice versa to the File.mkdirs() behavior.
     *
     * @see File#mkdirs()
     */
    public static boolean md(String sFullDirName) 
    {
        boolean bError = false;

        try 
        {
            File flDir = new File(sFullDirName);
            bError = !flDir.mkdirs();
        }
        catch(Exception e) 
        {
            bError = true;
        }

        return bError;
    }

    /**
     * Creates the specified directory and if necessary any 
     * parent directories.
     *
     * @return true if an error occured. Note that this is 
     *         vice versa to the File.mkdirs() behavior.
     *
     * @see File#mkdirs()
     */
    public static boolean mkdir(String sFullDirName) 
    {
        return md( sFullDirName );
    }

    /**
     * @return It's the canonical path of sFileName_.
     */
    public static String getAbsoluteFileName(String sFileName_) 
    {
        String sRetVal = null;

        try 
        {
            File pFile = new File(sFileName_);
            sRetVal = pFile.getCanonicalPath();
        }
        catch(Exception e) 
        {
            return null;
        }

        return sRetVal;
    }

    /**
     * The same as getAbsoluteFileName(..).
     *
     * @return It's the canonical path of sFileName_.
     */
    public static String getAbsolutePath(String sFileName_) 
    {
        return getAbsoluteFileName( sFileName_ );
    }

    /**
     * This method returns an absolute (canonical)
     * file name. The difference to getAbsoluteFileName
     * is that this method uses the system property
     * "user.dir" instead of the native system's current 
     * directory. This way you get a chance of changing
     * the current directory inside Java and let your 
     * program reflect that change.
     */
    public static String normalizeFileName( String sFile )
    {
        return normalizeFileName( sFile
                                  , (String) System.getProperties()
                                                   .get          ( "user.dir" )
                                );
    }

    /**
     * This method returns an absolute (canonical)
     * file name. The difference to getAbsoluteFileName
     * is that this method uses the system property
     * sUserDir instead of the native system's current 
     * directory. This way you get a chance of changing
     * the current directory inside Java and let your 
     * program reflect that change.
     */
    public static String normalizeFileName( String sFile, String sUserDir )
    {
        sFile = sFile.trim();
        if ( Util.isEmpty( sFile )
             || sFile.equals( "." ) )
        {
            sFile = sUserDir;
        }
        else if ( !FileUtil.isAbsolute( sFile ) )
        {
            sFile = FileUtil.concatPath( sUserDir
                                         , sFile );
        }
        sFile = FileUtil.getAbsoluteFileName( sFile );

        return sFile;
    }

    /**
     * Creates a path with a temp directory plus a 5 digit
     * random file name.
     * If 5 generated temporary file names are already in use,
     * null is returned.
     */
    public static String getTempFileName() 
    {
        /*String sRetVal = getTempDir();
          sRetVal = concatPath( sRetVal, "ccl" 
          + (new Date().getTime()) + ".tmp" );*/
        String sRetVal = null;
        int tries = 0;
        while(sRetVal == null) 
        {
            sRetVal = "~" + Util.rnd(10000);
            if (exists(sRetVal)) 
            {
                tries++;
                sRetVal = null;
            }
            if (tries >= 5) 
            {
                break;
            }
        }

        return sRetVal;
    }

    /**
     * Returns a temporary directory. This method will be upwards compatible
     * to jdk 1.2. It uses the java property "java.io.tempdir". If this is
     * not set like in jdk 1.1, "user.home" + "/tmp" will be used. If it does
     * not yet exist we take the freedom to create it. If a $HOME/tmp _file_ 
     * exists already, it will be deleted!!!
     */
    public static String getTempDir() 
    {
        // for jdk 1.2 use environment var
        String tempDir = System.getProperty( "java.io.tmpdir" );

        // for jdk 1.1 use $HOME/tmp
        // create it if it does not exist (inclusive remove it if it's a file
        if ( tempDir == null ) 
        {
            tempDir = System.getProperty( "user.home" );
            tempDir = concatPath( tempDir, "tmp" );
            if ( existsFile( tempDir ) ) 
            {
                FileUtil.delete( tempDir );
            }
            if ( !FileUtil.existsDir( tempDir ) ) 
            {
                FileUtil.md( tempDir );
            }
        }

        return tempDir;
    }

    /**
     * Unlike 'getTempDir()' which returns something like
     * "/tmp" this method creates a new temporary directory
     * which has no other files inside.
     *           
     * @return                    the name of the newly created temporary
     *                            directory.
     *
     * @exception   IOException   if the creation of the temporary directory
     *                            failed.
     */
    static public String createTempDir() 
        throws IOException
    {
        String sTempDir = File.createTempFile( "ccl"
                                               , null
                                               , null ).getPath();
        Util.debug( "ccl.util.FileUtil.createTempFile().sTempDir: " 
                    + sTempDir );
        FileUtil.delete( sTempDir );

        FileUtil.md( sTempDir );

        return sTempDir;
    }

    /**
     * Tests if the file represented by this File object is an absolute 
     * pathname. The definition of an absolute pathname is system
     * dependent. For example, on UNIX, a pathname is absolute if its first
     * character is the separator character. On Windows
     * platforms, a pathname is absolute if its first character is an 
     * ASCII '\' or '/', or if it begins with a letter followed by a colon. 
     */
    public static boolean isAbsolute( String sFileName_ ) 
    {
        Util.panicIf( Util.isEmpty( sFileName_ )
                      , "File is empty! Therefore can't test if the file is "
                        + "absolute." );

        return new File( sFileName_ ).isAbsolute();
    }

    /**
     * Tests if the file represented by this File object is an absolute 
     * pathname. The definition of an absolute pathname is system
     * dependent. For example, on UNIX, a pathname is absolute if its first
     * character is the separator character. On Windows
     * platforms, a pathname is absolute if its first character is an
     *  ASCII '\' or '/', or if it begins with a letter followed by a colon.
     */
    public static boolean areAllPathsAbsolute( String sPathList_ ) 
    {
        boolean bRetVal = false;

        if ( Util.isEmpty( sPathList_ ) ) 
        {
            return bRetVal;
        }

        Vector vPaths = Util.stringToLines
               ( sPathList_,
                 File.pathSeparatorChar );

        for( int path = 0; path < vPaths.size(); path++) 
        {
            String sSinglePath = (String) vPaths.elementAt( path );
            if ( Util.isEmpty( sSinglePath ) ) 
            {
                continue;
            }
            if ( !isAbsolute( sSinglePath ) ) 
            {
                return false;
            }

            bRetVal = true;
        }

        return bRetVal;
    }

    /**
     * Converts each path element to an aboslute path and
     * concatenates these again using the platforms path 
     * separator character.<p>
     *
     * For example: .;C:\jdk1.1.7\lib\classes.zip
     * -&gt; C:\java\projects;C:\jdk1.1.7\lib\classes.zip
     */
    public static String getAbsolutePathList( String sPathList_ ) 
    {
        String sStartPath = System.getProperty("user.dir");
        Vector vPaths = Util.stringToLines
               ( sPathList_,
                 File.pathSeparatorChar );

        for( int path = 0; path < vPaths.size(); path++) 
        {
            String sSinglePath = (String) vPaths.elementAt( path );

            if ( !isAbsolute( sSinglePath ) ) 
            {
                sSinglePath = FileUtil.concatPath( sStartPath,
                                                   sSinglePath );
                try 
                {
                    File flSinglePath = new File(sSinglePath);
                    sSinglePath = flSinglePath.getAbsolutePath();
                }
                catch(Exception e) 
                {
                    Util.printlnErr( "FileUtil.getAbsolutePath(..).e: " +
                                     e );
                }
                vPaths.setElementAt( sSinglePath, path);
            }
        }
        String sRetVal = Util.concat( vPaths,
                                      File.pathSeparatorChar );
        if ( (sRetVal.length() > 0) &&
             !Util.endsWith( sRetVal, File.pathSeparatorChar) )
        {
            // why this?
            sRetVal += Util.cToS( File.pathSeparatorChar );
        }

        return sRetVal;
    }

    /**
     * Be aware that symbolic links might lead to infinite loops. The
     * directory itself is always the first element. If
     * sFileName_ doesn't exist an empty vector is returned.
     *
     * @return   Vector with strings of file and directory names.
     */
    public static Vector getRecursiveDir( String sFileName_ ) 
    {
        Vector vsRetVal = new Vector();
        if ( !exists( sFileName_ ) ) 
        {
            return vsRetVal;
        }
        
        vsRetVal.addElement( sFileName_ );
        if ( !existsDir( sFileName_ ) ) 
        {
            return vsRetVal;
        }

        Vector vFiles = getFiles( sFileName_ );
        Enumeration eFiles = vFiles.elements();
        while( eFiles.hasMoreElements() ) 
        {
            String sNextFile = (String) eFiles.nextElement();
            vsRetVal.addElement( concatPath( sFileName_, sNextFile ) );
        }

        // sub dirs
        Vector vDirs = getSubDirs( sFileName_ );
        Enumeration eDirs = vDirs.elements();
        while( eDirs.hasMoreElements() ) 
        {
            String sNextDir = (String) eDirs.nextElement();
            Vector vsNextDir = getRecursiveDir
                   ( concatPath( sFileName_, sNextDir ) );
            vsRetVal = Util.concat( vsRetVal, vsNextDir );
        }

        return vsRetVal;
    }

    /**
     * Like: cp -r sDir_ sDestination_
     * No consideration for links are in place, so be aware
     * of possible infinite loops.
     *
     * @return   true   if any file error has happened.
     */
    public static boolean copyDir( String sDir_, String sDestination_ ) 
    {
        boolean bRetVal = false;

        // create new directory
        String sLastDirElement = getBaseName( sDir_ );
        String sNewDir = concatPath( sDestination_, sLastDirElement );
        if ( !existsDir( sNewDir ) ) 
        {
            bRetVal = md( sNewDir );
            if ( bRetVal ) 
            {
                Util.printlnErr( "ccl.util.FileUtil.copyDir(..).sNewDir: " 
                                 + sNewDir );

                return bRetVal;
            }
        }

        // list content
        //    file -> copy over
        Vector vFiles = getFiles( sDir_ );
        Enumeration eFiles = vFiles.elements();
        while( eFiles.hasMoreElements() ) 
        {
            String sNextFile = (String) eFiles.nextElement();
            bRetVal = copy( concatPath( sDir_, sNextFile ),
                            concatPath( sNewDir, sNextFile ) );
            if ( bRetVal ) 
            {
                Util.printlnErr( "ccl.util.FileUtil.copyDir(..).sNextFile: " 
                                 + sNextFile );

                return bRetVal;
            }
        }
        //    dir  -> recurse copyDir
        Vector vDirs = getSubDirs( sDir_ );
        Enumeration eDirs = vDirs.elements();
        while( eDirs.hasMoreElements() ) 
        {
            String sNextDir = (String) eDirs.nextElement();
            bRetVal = copyDir( concatPath( sDir_, sNextDir )
                               , sNewDir );
            if ( bRetVal )
            {
                Util.printlnErr( "ccl.util.FileUtil.copyDir(..).sNextDir: " 
                                 + concatPath( sDir_, sNextDir ) );

                return bRetVal;
            }
        }

        return bRetVal;
    }

    public static boolean copyDirNew( String sDir_, String sDestination_ )
    {
        boolean bRetVal = false;

        // create new directory
        String sLastDirElement = getBaseName( sDir_ );
        String sNewDir = sDestination_;//concatPath( sDestination_, sLastDirElement );
        if ( !existsDir( sNewDir ) )
        {
            bRetVal = md( sNewDir );
            if ( bRetVal )
            {
                Util.printlnErr( "ccl.util.FileUtil.copyDir(..).sNewDir: "
                                 + sNewDir );

                return bRetVal;
            }
        }

        // list content
        //    file -> copy over
        Vector vFiles = getFiles( sDir_ );
        Enumeration eFiles = vFiles.elements();
        while( eFiles.hasMoreElements() )
        {
            String sNextFile = (String) eFiles.nextElement();
            bRetVal = copy( concatPath( sDir_, sNextFile ),
                            concatPath( sNewDir, sNextFile ) );
            if ( bRetVal )
            {
                Util.printlnErr( "ccl.util.FileUtil.copyDir(..).sNextFile: "
                                 + sNextFile );

                return bRetVal;
            }
        }
        //    dir  -> recurse copyDir
        Vector vDirs = getSubDirs( sDir_ );
        Enumeration eDirs = vDirs.elements();
        while( eDirs.hasMoreElements() )
        {
            String sNextDir = (String) eDirs.nextElement();
            bRetVal = copyDir( concatPath( sDir_, sNextDir )
                               , sNewDir );
            if ( bRetVal )
            {
                Util.printlnErr( "ccl.util.FileUtil.copyDir(..).sNextDir: "
                                 + concatPath( sDir_, sNextDir ) );

                return bRetVal;
            }
        }

        return bRetVal;
    }
    /**
     * Copy file. If destination directory does not exist,
     * it will be created as well. Shortcut in the Unix
     * style for method 'copy'.
     *
     * @return   true on error.
     */
    public static boolean cp( String sSourceFile_
                              , String sDestinationFile_ ) 
    {
        return copy( sSourceFile_, sDestinationFile_ );
    }

    /**
     * Copy file. If destination directory does not exist,
     * it will be created as well.
     *
     * @return   true on error.
     */
    public static boolean copy( String sSourceFile_
                                , String sDestinationFile_ ) 
    {
        boolean bError = false;

        String sDestDir = new File( sDestinationFile_ ).getParent();
        if ( !Util.isEmpty( sDestDir ) ) 
        {
            if ( !existsDir( sDestDir ) ) 
            {
                md( sDestDir );
            }
        }

        try 
        {
            BufferedInputStream pBufferedInputStream = new BufferedInputStream
                   ( new FileInputStream( sSourceFile_ ) );
            BufferedOutputStream pBufferedOutputStream = 
                   new BufferedOutputStream
                       ( new FileOutputStream( sDestinationFile_ ) );

            bError = StreamCopier.copy( pBufferedInputStream
                                        , pBufferedOutputStream );

            pBufferedOutputStream.close();
            pBufferedInputStream.close();
        } 
        catch( Exception pException ) 
        {
            bError = true;
            /*
            Util.printlnErr( "FileUtil.copy(..).pException: " + pException );
            
            return true;
            // */
        }

        return bError;
    }

    /**
     * Copy content of an input stream into an output stream.
     *
     * @return   error.
     */
    public static boolean copy( InputStream pInputStream_, 
                                OutputStream pOutputStream_ ) 
    {
        boolean bError = false;

        try
        {
            BufferedInputStream pBufferedInputStream = new BufferedInputStream
                   ( pInputStream_ );
            BufferedOutputStream pBufferedOutputStream 
                   = new BufferedOutputStream
                       ( pOutputStream_ );

            bError = StreamCopier.copy( pBufferedInputStream
                                        , pBufferedOutputStream );

            pBufferedOutputStream.close();
            pBufferedInputStream.close();
        } 
        catch( Exception pException ) 
        {
            Util.printlnErr( "FileUtil.copy(..).pException: " + pException );
            
            return true;
        }

        return bError;
    }

    /**
     * Copy content of an buffered input stream into an buffered output stream.
     *
     * @return   error.
     */
    public static boolean copy( BufferedInputStream pBufferedInputStream_, 
                                BufferedOutputStream pBufferedOutputStream_ )
    {
        boolean bError = false;

        try 
        {
            bError = StreamCopier.copy( pBufferedInputStream_, 
                                        pBufferedOutputStream_ );

            pBufferedOutputStream_.close();
            pBufferedInputStream_.close();
        }
        catch( Exception pException ) 
        {
            Util.printlnErr( "FileUtil.copy(..).pException: " + pException );
            
            return true;
        }

        return bError;
    }

    /**
     * There is one big advantage this method has over
     * Class.getResourceAsStream(..). There are three 
     * different circumstances from where you want to load
     * a resource, only two work by the default JDK 
     * ClassLoader resource location method.
     * First case, your resource file is in the same directory
     * as your class file just on a normal file system.
     * Second case, your resource file is inside a jar file.
     * This both is handled by the normal ClassLoader.
     * But what if you have a src and a classes directory.
     * Then you want your resource file in the src directory
     * tree without the need to copy the resource file over
     * to the classes directory tree. If you stick to the
     * 'classes' and 'src' directory name convention, this
     * method still finds the resource in the src directory.
     *
     * @see java.lang.Class#getResourceAsStream(java.lang.String)
     */
    public static InputStream getResourceAsStream( Object pObject_,
                                                   String sRecourceName_ )
    {
        InputStream isResource = pObject_.getClass().
               getResourceAsStream( sRecourceName_ );
        if ( isResource == null ) 
        {
            String sPath = getClassPath( pObject_ );
            Util.debug( "ccl.util.FileUtil.getResourceAsStream(..).sPath: " +
                        sPath );

            if ( sPath.equals( "classes" ) ) 
            {
                sPath = "src";
            }
            else if ( Util.endsWith( sPath, File.separator + "classes" ) ) 
            {
                sPath = sPath.substring( 0, sPath.length() - 7 ) + "src";
            }
            else if ( sPath.startsWith( "classes" + File.separator ) ) 
            {
                sPath = "src" + sPath.substring( 7 );
            }
            else 
            {
                int index = sPath.lastIndexOf
                       ( File.separator + 
                         "classes" + File.separator );
                if ( index != -1 ) 
                {
                    sPath = sPath.substring( 0, index + 1) +
                           "src" + sPath.substring( index + 8 );
                }
            }
            try 
            {
                isResource = new FileInputStream
                       ( concatPath( sPath, 
                                     sRecourceName_ ) );
            }
            catch( Exception pException ) 
            {
                isResource = null;
            }
        }

        return isResource;
    }

    /**
     * There is one big advantage this method has over
     * Class.getResourceAsStream(..). There are three 
     * different circumstances from where you want to load
     * a resource, only two work by the default JDK 
     * ClassLoader resource location method.
     * First case, your resource file is in the same directory
     * as your class file just on a normal file system.
     * Second case, your resource file is inside a jar file.
     * This both is handled by the normal ClassLoader.
     * But what if you have a src and a classes directory.
     * Then you want your resource file in the src directory
     * tree without the need to copy the resource file over
     * to the classes directory tree. If you stick to the
     * 'classes' and 'src' directory name convention, this
     * method still finds the resource in the src directory.
     *
     * @see java.lang.Class#getResourceAsStream(java.lang.String)
     */
    public static String getResourceAsString( Object pObject_,
                                              String sRecourceName_ )
        throws IOException
    {
        InputStream isResource = getResourceAsStream( pObject_
                                                      , sRecourceName_ );

        return readStream( isResource );
    }

    /**
     * Prints a user message and reads standard input until someone types 
     * 'quit' and &lt;enter&gt;.<p>
     *
     * Shouldn't this method be in Util and not FileUtil?
     * No, because this is stdio and therefore also file
     * related.
     */
    public static void printAndWaitUntilQuit() 
    {
        Util.println( "Type 'quit' <return> to exit!" );
        while( true ) 
        {
            try 
            {
                if ( System.in.available() > 0 ) 
                {
                    String sInput = "";
                    while( System.in.available() > 0 ) 
                    {
                        sInput += (char) System.in.read();
                    }

                    if ( sInput.equals( "quit\r\n" ) ||
                         sInput.equals( "quit\n" ) )
                    {
                        break;
                    }
                } 
                else 
                {
                    Util.sleep( 1 );
                }
            } 
            catch( IOException pIOException ) 
            {
                Util.printlnErr( "ccl.util.FileUtil.printAndWaitUntilQuit()"
                                 + ".pIOException: " 
                                 + pIOException );
                
                return;
            }
        }
    }

    /**
     * Checks if the next word in the standard input stream
     * is quit followed by a linefeed.
     */
    public static boolean isQuitInStdin() 
    {
        try 
        {
            if ( System.in.available() > 0 ) 
            {
                String sInput = "";
                while( System.in.available() > 0 ) 
                {
                    sInput += (char) System.in.read();
                }

                if ( sInput.equals( "quit\r\n" ) ||
                     sInput.equals( "quit\n" ) )
                {
                    return true;
                }
            }
        }
        catch( IOException pIOException ) 
        {
            Util.printlnErr( "ccl.util.FileUtil.printAndWaitUntilQuit()"
                             + ".pIOException: "
                             + pIOException );
        }

        return false;
    }

    /**
     * Returns the directory as a string of the given file.
     */
    public static String getDir( String sFile_ ) 
    {
        File pFile = new File( sFile_ );
        
        return pFile.getPath();
    }

    /**
     * This method checks if it is save to use a file or if for example
     * someone else is currently writing into this file. Warning, this method does
     * not work for ftp. Downloading a file via ftp and checking if it is
     * readable or not results in true, thought we want false.
     *
     * Copied from usenet from Mark Rozas.
     */
    public static boolean isFileReadable( String sFileName_ ) 
    {
        try 
        {
            File file = new File( sFileName_ );
            if ( !file.isFile() || !file.canRead() ) 
            {
                Util.debug( "ccl.util.FileUtil.isFileReadable(..)"
                            + ".NOT_A_FILE_OR_NOT_READABLE" );

                return false;
            }

            FileInputStream inputStream = new FileInputStream( file );
            if ( inputStream == null ) 
            {
                Util.debug( "ccl.util.FileUtil.isFileReadable(..)"
                            + ".inputStream: " 
                            + inputStream );

                return false;
            }

            FileDescriptor descriptor = inputStream.getFD();
            if ( !descriptor.valid() ) 
            {
                Util.debug( "ccl.util.FileUtil.isFileReadable(..)"
                            + ".descriptor " 
                            + descriptor );

                return false;
            }

            SecurityManager securityManager = System.getSecurityManager();
            if ( securityManager != null ) 
            {
                securityManager.checkRead( descriptor );
            }
        } 
        catch( Exception exception ) 
        {
            Util.debug( "ccl.util.FileUtil.isFileReadable(..)"
                        + ".exception: " 
                        + exception );

            return false;
        }

        return true;
    }

    /**
     * Get the base name of a file. This is the name only,
     * without the path information.
     *                        
     * @param    sFileName_   a string with a file name.
     *                        
     * @return                the name of the file itself,
     *                        e.g. "foo/README.TXT" -> "README.TXT".
     *
     * @deprecated   use getBaseName.
     */
    public static String getBaseFileName( String sFileName_ ) 
    {
        return new File( sFileName_ ).getName();
    }

    /**
     * Get the base name of a file. This is the name only,
     * without the path information.
     *                        
     * @param    sFileName_   a string with a file name.
     *                        
     * @return                the name of the file itself,
     *                        e.g. "foo/README.TXT" -> "README.TXT".
     */
    public static String getBaseName( String sFileName_ ) 
    {
        return new File( sFileName_ ).getName();
    }

    /**
     * Equivalent to unix dirname command. Returns the parent of the given file.
     * 
     * @return   "." not null when given a base file name.
     */
    public static String getDirName( String sFileName ) 
    {
        String sRetVal = new File( sFileName ).getParent();
        if ( sRetVal == null )
        {
            sRetVal = ".";
        }

        return sRetVal;
    }

    /**
     * This method searches in the current classpath for the
     * given object. That class should be either in a jar
     * file which is located inside a lib directory or
     * as a pure class file under a classes directory. The
     * directory containing either the classes or the lib
     * directory will be returned as the applicaton home
     * directory. If this is not found, null is returned.
     *                    
     * @param    oClass   An instantiated class which belongs
     *                    to the application.
     *                    
     * @return            null if no home directory was found.
     *
     * @deprecated   use ClassPathUtil.getApplicationHome instead.
     */
    public static String getApplicationHome( Object oClass ) 
    {
        return ClassPathUtil.getApplicationHome( oClass );
    }
}
