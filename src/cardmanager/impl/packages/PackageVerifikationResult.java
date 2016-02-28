/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.impl.packages;

import cardmanager.impl.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jirka
 */
public class PackageVerifikationResult {

         public int duplicated=0;
         public int maxoccurence=0;
         public int underOne=0;
         public int total=0;
         public int piles=0;
         public int defs=0;
    public PackageVerifikationResult( PackageDeffinition d) {
         Map<CountedCard,Integer> m=new HashMap<CountedCard, Integer>(d.getCards().size());


                               for (CountedCard cd : d.getCards()) {
                                   Integer was=m.put(cd, new Integer(1));
                                   if (cd.getCount().intValue()<1) underOne++;
                                   if (cd.getCount().intValue()>maxoccurence) maxoccurence=cd.getCount().intValue();
                                   total+=cd.getCount().intValue();
                                   defs++;
                                   if (was!=null) duplicated++;
                               }
                               piles=d.getPiles().size();


                           }


    public String saySentence(){
        String sentence="Seams ok! ";
        if (isCheated())  sentence="Cheated! :";
                return sentence+" total:"+total+", definitions: "+defs+", max. occurence:"+maxoccurence+", piles:"+piles+", duplcate defs.:"+duplicated+", under one:"+underOne;

    }

    public boolean isCheated(){
        return (duplicated !=0 || underOne!=0);
    }



}
