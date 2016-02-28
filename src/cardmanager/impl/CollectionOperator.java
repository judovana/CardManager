/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.impl;

import cardmanager.impl.packages.CardDefinition;
import cardmanager.impl.gui.CardInstanceViewForSelectComponent;
import cardmanager.FileBearer;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 *
 * @author Jirka
 */
public class CollectionOperator implements Serializable{
public static File DEF_DIR=new File("collection");
    public CardDefinition getById(String id){
        for (CardDefinition cardDefinition : collection) {
            if (cardDefinition.getId().equals(id)) return cardDefinition;

        }
        return null;
    }

    public static  List<FileBearer> prepareColection(List<CardDefinition> collection) {
        List<FileBearer> r=new ArrayList<FileBearer>(collection.size());
        for (int i = 0; i < collection.size(); i++) {
            CardDefinition cardInstance = collection.get(i);
            r.add(new CardInstanceViewForSelectComponent(cardInstance));


        }
        return Collections.unmodifiableList(r);
    }



    List<CardDefinition>collection=new ArrayList();
    Set<String>duplicateNames=new HashSet<String>();

    public CollectionOperator(int i) {
        this(DEF_DIR);
    }

    public CollectionOperator(File sourceDir) {

        recureseThrough(sourceDir);

        }

    public List<FileBearer> getCollectionForSelectingView() {
       return  prepareColection(collection);
    }

    private boolean passed(File d) {
        if (d.getName().toLowerCase().endsWith(".db")) return false;
        return true;
    }

    private void recureseThrough(File d) {
        if (d.isDirectory()){
            System.out.println("TRAVERSING: "+d.getAbsolutePath());
File[] dd=d.listFiles();
Arrays.sort(dd,new Comparator<File>() {

                public int compare(File o1, File o2) {
                   if (o1.isDirectory() && o2.isDirectory()) return 0;
                   if (o1.isFile() && o2.isFile()) return 0;
                   if (o1.isFile() && o2.isDirectory()) return 100;
                   if (o2.isFile() && o1.isDirectory()) return -100;
                   return 0;

                }
            });

            for (int i = 0; i < dd.length; i++) {
                File file = dd[i];
                recureseThrough(file);
            }

    }else{
            if (passed(d)){
            CardDefinition toBeAdded=new CardDefinition(d);
            for (Iterator<CardDefinition> it = collection.iterator(); it.hasNext();) {
                CardDefinition cardInstance = it.next();
                if (toBeAdded.getName().equals(cardInstance.getName())){
                    duplicateNames.add(cardInstance.getName());
                    break;
                }
            }
            
collection.add(toBeAdded);
            }

    }

    }



    public List<CardDefinition> getCollection() {
        return collection;
    }

    public Set<String> getDuplicateNames() {
        return duplicateNames;
    }


    public static void main(String[] a){
       CollectionOperator co=new CollectionOperator(new File("collection"));

        for (CardDefinition c : co.getCollection()) {
            System.out.println(c.getId());

        }

        System.out.println("Total "+co.getCollection().size());
        System.out.println("possible duplicated "+co.getDuplicateNames().size());

        for (String c : co.getDuplicateNames()) {
            System.out.println(c);

        }




    }


}
