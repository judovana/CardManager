/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.impl.packages;

import cardmanager.impl.packages.CountedCard;
import cardmanager.FileBearer;
import java.io.File;
import java.io.Serializable;

/**
 *
 * @author Jirka
 */
public class CardDefinition implements Serializable, FileBearer{
    
    private File file;
    private String name;
    private String suffix;
    private String id;

private static final String CL="collection";

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CountedCard) return ((CountedCard)obj).card.equals(this);
        if (!(obj instanceof CardDefinition)) return false;
        return (((CardDefinition)obj).getId().equals(this.id));
    }

    @Override
    public int hashCode() {
       return id.hashCode();
    }



    public CardDefinition(File f) {
        file=f.getAbsoluteFile();
        name=f.getName();
        suffix=name.substring(name.lastIndexOf(".")+1,name.length());
        name=name.substring(0,name.lastIndexOf("."));
        id=f.getAbsolutePath().substring(f.getAbsolutePath().indexOf(CL)+CL.length(),(f.getAbsolutePath().length()));
        id=id.replace('\\','/');
    }

    /**
     * @return the file
     */


    public File getFile() {
      return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * @param suffix the suffix to set
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public static void main(String[] a){
        CardDefinition c=new CardDefinition(new File("collection/base/BaseSet-00001.jpg"));
        System.out.println( c.getFile().getAbsolutePath());
        System.out.println( c.getId());
        System.out.println( c.name);
        System.out.println( c.suffix);





    }
}
