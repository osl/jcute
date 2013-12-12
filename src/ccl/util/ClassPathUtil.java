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

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * An utility class to find files or directories
 * related to the Java class path or inside jar archives.
 *
 * @author  <a href="http://mats.gmd.de/clemens/">Chr. Clemens Lahme</a> (<a href="mailto:clemens.lahme@mailexcite.com"><i>Clemens.Lahme@mailexcite.com</i></a>)
 * @version $Id: ClassPathUtil.java,v 1.1 2005/10/30 22:22:35 ksen Exp $
 */
public class ClassPathUtil 
{
    /**
     * Finds the location in the file system of a java class file.
     *
     * @return   null could not be found in the given classpath or
     *           if class is hidden in a jar file. In this case
     *           use 'getJarFileName()'.
     */
    public static String getAbsoluteClassFileName(String sFullClassName_,
                                                  String sClasspath_)
    {
        String sRelativeClassName = sFullClassName_.replace('.', File.separatorChar) + ".class";

        int pos = 0;
        int lastpos = 0;
        while(lastpos != -1) 
        {
            pos = sClasspath_.indexOf(File.pathSeparatorChar, lastpos);

            String sCurrentDir;
            if (pos == -1) 
            {
                sCurrentDir = sClasspath_.substring(lastpos);
                lastpos = -1;
            }
            else 
            {
                sCurrentDir = sClasspath_.substring(lastpos, pos);
                lastpos = pos + 1;

                if        (lastpos >= sClasspath_.length()
                           ) 
                {
                    lastpos = -1;
                }
            }

            String sTestFile = FileUtil.concatPath(sCurrentDir, sRelativeClassName);
            if (FileUtil.existsFile(sTestFile)) 
            {
                return(sTestFile);
            }
        }

        return null;
    }

    /**
     * Provide a classname (e.g. ccl.jcf.JCFUtil) and a classpath,
     * and you will get the jar file location which contains
     * this class (e.g. lib/ccl.jar).
     *
     * @exception   IOException    if a file operation failed.
     * @exception   ZipException   if any zip operation failed.
     */
    public static String getJarFileName( String sFullClassName_ )
        throws IOException, ZipException
    {
        return getJarFileName( sFullClassName_, (String) System.getProperty( "java.class.path" ) );
    }

    /**
     * Provide a classname (e.g. ccl.jcf.JCFUtil) and a classpath,
     * and you will get the jar file location which contains
     * this class (e.g. lib/ccl.jar).
     *
     * @exception   IOException    if a file operation failed.
     * @exception   ZipException   if any zip operation failed.
     */
    public static String getJarFileName( String sFullClassName_, 
                                         String sClasspath_ )
        throws IOException, ZipException
    {
        String sClassFileName = sFullClassName_.replace('.', '/') + ".class";

        // first get all archives from classpath
        Vector vClasspathElements = Util.stringToLines(sClasspath_,
                                                       File.pathSeparatorChar);
        for(Enumeration eElements = vClasspathElements.elements();
            eElements.hasMoreElements(); )
        {
            String sNextArchiv = (String) eElements.nextElement();
            if ((Util.endsWith(sNextArchiv, ".zip") ||
                 Util.endsWith(sNextArchiv, ".jar")) &&
                FileUtil.existsFile(sNextArchiv))
            {
                ZipFile pZipFile = new ZipFile(sNextArchiv);
                ZipEntry pZipEntry = pZipFile.getEntry(sClassFileName);
                if (pZipEntry != null) 
                {
                    pZipFile.close();
                                                
                    return sNextArchiv;
                }
                pZipFile.close();
            }
        }

        return null;
    }

    /**
     * Either the absolut file name and path is given back or
     * the java archive file name which contains this class is
     * given back.
     *
     * @exception   ClassNotFoundException   if the given class could not be found.
     * @exception   IOException              if a file operation failed.
     * @exception   ZipException   if any zip operation failed.
     */
    public static String getClassOrJarFileName( String sFullClassName_ )
        throws IOException,
           ClassNotFoundException,
           ZipException
    {
        return getClassOrJarFileName( sFullClassName_,
                                      (String) System.getProperty( "java.class.path" ) );
    }

    /**
     * Either the absolut file name and path is given back or
     * the java archive file name which contains this class is
     * given back.
     *
     * @exception   ClassNotFoundException   if the given class could not be found.
     * @exception   IOException              if a file operation failed.
     * @exception   ZipException   if any zip operation failed.
     */
    public static String getClassOrJarFileName( String sFullClassName_,
                                                String sClasspath_ ) 
        throws IOException,
           ClassNotFoundException,
           ZipException
    {
        String sAbsoluteClassFileName = getAbsoluteClassFileName
               (sFullClassName_, sClasspath_);

        if (sAbsoluteClassFileName != null) 
        {
            return sAbsoluteClassFileName;
        }

        return getJarFileName( sFullClassName_, sClasspath_ );
    }

    /**
     * Like which for unix, finds the location
     * of a given java class,<br>
     * e.g. jwhich ccl.util.Test<br>
     * -> /home/clemens/bunin/ccl/lib/ccl.jar<br>
     * or<br>
     * -> /home/clemens/bunin/ccl/classes/ccl/util/Test.class .
     *
     * @return   null   if nothing was found.
     */
    public static String jwhich( String sClassName_,
                                 String sClasspath_ )
    {
        Vector vClasspaths = Util.stringToLines( sClasspath_, File.pathSeparatorChar );

        try 
        {
            Enumeration eClasspaths = vClasspaths.elements();
            while( eClasspaths.hasMoreElements() ) 
            {
                String sNextPath = (String) eClasspaths.nextElement();
                
                // Create test file: "path + / + class/name + .class"
                {
                    String sClassFile = FileUtil.concatPath
                           ( sNextPath, sClassName_.replace
                             ( '.', File.separatorChar ) )
                           + ".class";
                    if ( FileUtil.existsFile( sClassFile ) ) 
                    {
                        // SUCCESS
                        return sClassFile;
                    }
                }

                // is it a jar or zip file? And is it inside of it?
                // this method also checks if the file exists
                // at all
                {
                    String sZipFile = getJarFileName( sClassName_,
                                                      sNextPath );
                    if ( sZipFile != null ) 
                    {
                        // SUCCESS
                        return sZipFile;
                    }
                }
            }
        }
        catch( Exception exception ) 
        {
            // If something went wrong, it means we found nothing.
            // Therefore we print nothing.
        }

        return null;
    }

    // getApplicationHome ------------------------------------

    /**
     * Get application class path if possible from under the
     * classes directory.
     */
    private static String _getApplicationHomeFromClasses( Object oClass ) 
    {
        Util.debug( "ccl.util.ClassPathUtil._getApplicationHomeFromClasses(..).ENTER" );
        
        String sRetVal = null;

        String sClassName = oClass.getClass().getName();

        String sClassLocation = FileUtil.getClassPath( oClass );
        Util.debug( "ccl.util.ClassPathUtil._getApplicationHomeFromClasses(..).sClassLocation: " + sClassLocation );
        
        if ( !Util.isEmpty( sClassLocation ) ) 
        {
            sClassLocation = FileUtil.concatPath( sClassLocation, Util.getObjectName( oClass ) + ".class" );
            if ( FileUtil.existsFile( sClassLocation ) ) 
            {
                int dots = Util.getOccurances( sClassName, '.' );
                File flParent = new File( sClassLocation );
                Util.debug( "ccl.util.ClassPathUtil._getApplicationHomeFromClasses(..).flParent: " + flParent );
                while( dots >= 0 ) 
                {
                    flParent = new File( flParent.getParent() );
                    dots--;
                }
                String sName = flParent.getName();
                if ( sName.equals( "classes" ) ) 
                {
                    sRetVal = flParent.getParent();
                }
                else 
                {
                    sRetVal = flParent.getPath();
                }
            }
        }

        return sRetVal;
    }

    /**
     * Get application class path if possible from under the
     * lib directory.
     */
    private static String _getApplicationHomeFromLib( Object oClass ) 
    {
        String sRetVal = null;

        String sClassName = oClass.getClass().getName();

        try 
        {
            String sJarFile = getJarFileName( sClassName );
            if ( !Util.isEmpty( sJarFile )
                 && FileUtil.existsFile( sJarFile ) )
            {
                File flParent = new File( sJarFile );
                flParent = new File( flParent.getParent() );
                if ( flParent.getName().equals( "lib" ) ) 
                {
                    sRetVal = flParent.getParent();
                }
                else 
                {
                    sRetVal = flParent.getPath();
                }
            }
        }
        catch( IOException ioException ) 
        {
            Util.printlnErr( ioException );
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
     */
    public static String getApplicationHome( Object oClass ) 
    {
        String sRetVal = null;

        String sClassName = oClass.getClass().getName();
        Util.debug( "ccl.util.ClassPathUtil.getApplicationHome(..).sClassName: " + sClassName );

        sRetVal = _getApplicationHomeFromClasses( oClass );

        Util.debug( "ccl.util.ClassPathUtil.getApplicationHome(..).appHome: " + String.valueOf( sRetVal ) );

        if ( Util.isEmpty( sRetVal ) ) 
        {
            sRetVal = _getApplicationHomeFromLib( oClass );
        }

        Util.debug( "ccl.util.ClassPathUtil.getApplicationHome(..).sRetVal: " + sRetVal );

        return sRetVal;
    }
}
