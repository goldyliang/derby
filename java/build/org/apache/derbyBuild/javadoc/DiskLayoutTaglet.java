/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derbyBuild.javadoc
   (C) Copyright IBM Corp. 2002, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derbyBuild.javadoc;

import com.sun.tools.doclets.Taglet;
import com.sun.javadoc.*;
import java.util.Map;

public class DiskLayoutTaglet implements Taglet {
	/**
		IBM Copyright &copy notice.
	*/
	public static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_2002_2004;
    private String NAME = "disk_layout";
    private String ROWNAME = "Disk Layout";
    /**
     * Returns the name of this taglet
     * @return NAME
     */
    public String getName() {
        return NAME;
    }

    /**
     * disk_layout not expected to be used in field documentation.
     * @return false
     */
    public boolean inField() {
        return false;
    }

    /**
     * disk_layout not expected to be used in constructor documentation.
     * @return false
     */
    public boolean inConstructor() {
        return false;
    }

    /**
     * disk_layout not expected to be used in constructor documentation.
     * @return false
     */
    public boolean inMethod() {
        return false;
    }

    /**
     * disk_layout can be used in overview documentation.
     * @return true
     */
    public boolean inOverview() {
        return true;
    }

    /**
     * disk_layout can be used in package documentation.
     * @return true
     */
    public boolean inPackage() {
        return true;
    }

    /**
     * disk_layout can be used in type documentation.
     * @return true
     */
    public boolean inType() {
        return true;
    }

    /**
     * disk_layout is not an inline tag.
     * @return false
     */
    public boolean isInlineTag() {
        return false;
    }

    /**
     * Register this Taglet.
     * @param tagletMap
     */
    public static void register(Map tagletMap) {
       DiskLayoutTaglet tag = new DiskLayoutTaglet();
       Taglet t = (Taglet) tagletMap.get(tag.getName());
       if (t != null) {
           tagletMap.remove(tag.getName());
       }
       tagletMap.put(tag.getName(), tag);
    }

    /**
     * Embed the contents of the disk_layout tag as a row
     * in the disk format table. Close the table.
     * @param tag The tag to embed to the disk format the table.
     */
    public String toString(Tag tag) {
        return "<tr><td>" + ROWNAME + "</td>"
               + "<td>" + tag.text() + "</td></tr></table>\n";
    }

    /**
     * Embed multiple disk_layout tags as cells in the disk format table.
     * Close the table.
     * @param tags An array of tags to add to the disk format table.
     */
    public String toString(Tag[] tags) {
        if (tags.length == 0) {
            return null;
        }
        String result = "<tr><td>" + ROWNAME + "</td><td>" ;
        for (int i = 0; i < tags.length; i++) {
            if (i > 0) {
                result += "";
            }
            result += tags[i].text() + "</td></tr>";
        }
        return result + "</table></dt>\n";
    }
}

