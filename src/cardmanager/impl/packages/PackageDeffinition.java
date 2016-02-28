/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cardmanager.impl.packages;

import cardmanager.impl.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jirka
 */
public class PackageDeffinition {

    public static File ROOT = new File("data/packages");

    static {
        if (!ROOT.exists()) {
            System.out.println(ROOT.toString() + " don't exists");
        }
    }

    public static PackageDeffinition creatEmptyRandomPackageDeffinition(String s) {
        File random=new File(ROOT, "random");
        random.mkdirs();
        return creatEmptyPackageDeffinition(new File(random, s));
    }
    public static PackageDeffinition creatEmptyPackageDeffinition(String s) {
        return creatEmptyPackageDeffinition(new File(ROOT, s));
    }

    public static PackageDeffinition creatEmptyPackageDeffinition(File s) {
        return new PackageDeffinition(s);
    }

    private static String paseLineFor(String s, PackageDeffinition p, CollectionOperator c) {

        if (s.matches("^ *p *:.*")) {
            String q = s.substring(s.indexOf(":") + 1).trim();
            if (p != null) {
                p.getPiles().add(q);
            }
            return "Piles : " + q;
        } else {
            String q2 = s.substring(s.indexOf(":") + 1).trim();
            String q1 = s.substring(0, s.indexOf(":")).trim();
            Integer count = new Integer(q1);
            CardDefinition cd = c.getById(q2);
            if (cd == null) {
                throw new IllegalArgumentException("Card " + q2 + " not found in collection");
            }
            if (p != null) {
                p.getCards().add(new CountedCard(cd, count));
            }
            return "cards : " + cd.getName() + " " + count.toString() + "x";
        }
    }

    public static void main(String[] s) {
        CollectionOperator cp = new CollectionOperator(0);
        System.out.println(paseLineFor("p: ahoj jak je", null, null));
        System.out.println(paseLineFor("p:ahoj jak je", null, null));
        System.out.println(paseLineFor("p :ah oj jak je ", null, null));

        System.out.println(paseLineFor("1 : /base/BaseSet-00001.jpg", null, cp));
        System.out.println(paseLineFor("1: /base/BaseSet-00002.jpg", null, cp));
        System.out.println(paseLineFor("10:/base/BaseSet-00003.jpg", null, cp));
        System.out.println(paseLineFor("1:/base/BaseSet-00004.jpg", null, cp));
        System.out.println(paseLineFor("22 :/base/BaseSet-00005.jpg", null, cp));
        System.out.println(paseLineFor(" 0:/base/BaseSet-00006.jpg", null, cp));
        System.out.println(paseLineFor("10:/base /BaseSet-00003.jpg", null, cp));



    }

    public void save() {
        save(backenFile);
    }

    @Override
    public String toString() {
        return backenFile.getName();
    }
    private List<CountedCard> cards = new ArrayList<CountedCard>();
    private File backenFile;
    private List<String> piles = new ArrayList<String>();

    private PackageDeffinition(File file) {
        backenFile = file;

    }

    /**
     * @return the cards
     */
    public List<CountedCard> getCards() {
        return cards;
    }

    /**
     * @param cards the cards to set
     */
    public void setCards(List<CountedCard> cards) {
        this.setCards(cards);
    }

    /**
     * @return the backenFile
     */
    public File getBackenFile() {
        return backenFile;
    }

    /**
     * @param backenFile the backenFile to set
     */
    public void setBackenFile(File backenFile) {
        this.backenFile = backenFile;
    }

    /**
     * @return the piles
     */
    public List<String> getPiles() {
        return piles;
    }

    /**
     * @param piles the piles to set
     */
    public void setPiles(List<String> piles) {
        this.piles = piles;
    }
    /*1: card/id/1.jpg
     *5: card/id/2.jpg
     *p: dicarded
     *p: foreverDiscarded
     *
     */

    public static PackageDeffinitionAndErrors createFromReaderAndCollection(Reader fr, CollectionOperator collection) {

        File f = null;
        try {
            f = File.createTempFile("tempPackage", ".txt");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        PackageDeffinition p = new PackageDeffinition(f);

        return createFromReader(p, fr, collection);


    }

    public static PackageDeffinitionAndErrors createFromFileAndCollection(File f, CollectionOperator collection) {
        PackageDeffinition p = new PackageDeffinition(f);
        FileReader fr = null;
        try {
            fr = new FileReader(f);
        } catch (Exception ex) {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException ex1) {
                    ex.printStackTrace();
                }
            }
        }


        return createFromReader(p, fr, collection);
    }

    private static PackageDeffinitionAndErrors createFromReader(PackageDeffinition pd, Reader fr, CollectionOperator collection) {

        PackageDeffinitionAndErrors r = new PackageDeffinitionAndErrors();
        PackageDeffinition p = pd;
        r.packageDef = p;
        BufferedReader br = null;
        try {

            br = new BufferedReader(fr);
            while (true) {
                String s = br.readLine();
                if (s == null) {
                    break;
                }
                s = s.trim();
                if (s.length() <= 1) {
                    continue;
                }
                if (s.startsWith("#")) {
                    continue;
                }
                try {
                    paseLineFor(s, p, collection);
                } catch (Exception ex) {
                    r.ex.add(ex);
                }
            }
        } catch (Exception ex) {
            r.ex.add(ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ex) {
                r.ex.add(ex);
            }
        }
        return r;
    }

    public void save(File backenFile) {
        Writer fr = null;
        try {
            fr = new FileWriter(backenFile);
            save(fr);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    public void save(Writer fileWriter) throws IOException {
        try {
            save(new BufferedWriter(fileWriter));
        } finally {
            fileWriter.flush();
        }
    }

    public void save(BufferedWriter w) throws IOException {
        try {

            for (int i = 0; i < cards.size(); i++) {
                CountedCard countedCard = cards.get(i);
                w.write(countedCard.count.intValue() + ": " + countedCard.card.getId());
                w.newLine();
            }

            for (int i = 0; i < piles.size(); i++) {
                String s = piles.get(i);
                w.write("p: " + s);
                w.newLine();
            }
        } finally {
            w.flush();

        }
    }

    public PackageVerifikationResult verify() {
        return new PackageVerifikationResult(this);
    }

    public int getCardCount() {

        int total = 0;

        for (CountedCard cd : getCards()) {

            total += cd.getCount().intValue();

        }
        return total;


    }

    private void exportCard(CountedCard countedCard, File selectedFile) throws Exception {
        System.out.println("copying " + countedCard.getCard().getId() + " from " + countedCard.getFile().toString() + " to " + selectedFile);
        String s = countedCard.getCard().getId().substring(0, countedCard.getCard().getId().lastIndexOf("/"));
        File destDir = new File(selectedFile, s);
        if (!destDir.exists()) {
            boolean b = destDir.mkdirs();
            System.out.println(destDir.getAbsolutePath() + " creation: " + b);
        }
        File desFile = new File(destDir, countedCard.getCard().getName() + "." + countedCard.getCard().getSuffix());

        copyFile(countedCard.getFile(), desFile);


    }

    public static void copyFile(File source, File dest) throws IOException {

        FileInputStream fi = new FileInputStream(source);
        FileChannel fic = fi.getChannel();
        MappedByteBuffer mbuf = fic.map(
                FileChannel.MapMode.READ_ONLY, 0, source.length());
        fic.close();
        fi.close();
        FileOutputStream fo = new FileOutputStream(dest);
        FileChannel foc = fo.getChannel();
        foc.write(mbuf);
        foc.close();
        fo.close();

    }

    public List<Exception> exportTo(File selectedFile) {
        List<Exception> r = new ArrayList<Exception>();
        List<CountedCard> l = getCards();
        for (CountedCard countedCard : l) {
            try {
                exportCard(countedCard, selectedFile);
            } catch (Exception ex) {
                ex.printStackTrace();
                r.add(ex);
            }
        }
        return r;
    }

    public List<Exception> exportToHtmlPrintable(File selectedFile) {
        selectedFile.mkdir();
        List<Exception> r = exportTo(selectedFile);
        r.addAll(0, writeHtml(selectedFile, true));
        return r;
    }

    public List<Exception> exportToHtmlVieable(File selectedFile) {
        selectedFile.mkdir();
        List<Exception> r = exportTo(selectedFile);
        r.addAll(0, writeHtml(selectedFile, false));
        return r;
    }

    List<Exception> writeHtml(File dir, boolean expand) {
        return writeHtml(dir, "index.html", expand);
    }

    private List<Exception> writeHtml(File dir, String string, boolean expand) {
        List<Exception> l = new ArrayList<Exception>();
        int w=240;
        int h=370;
        try{
        Settings st=new Settings(0);
        h=st.getCardHeight();
        w=st.getCardWidth();
        }catch(Exception ex){
            ex.printStackTrace();
            l.add(ex);
        }
        try {
            BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dir, string)), "utf-8"));
            try {
                bf.write("<html><head>");
                bf.newLine();
                bf.write("<style TYPE=\"text/css\">");
                bf.newLine();
                bf.write(" <!--");
                bf.newLine();
                bf.write(" img.card {border:2px solid black;");
                bf.newLine();
                bf.write("           margin:5px;");
                bf.newLine();
                bf.write("           padding:5px;");
                bf.newLine();
                bf.write("           width:"+w+"px;");
                bf.newLine();
                bf.write("           height:"+h+"px;");
                bf.newLine();
                bf.write("   }");
                bf.newLine();
                bf.write(" -->");
                bf.newLine();
                bf.write("</style> ");
                bf.newLine();
                bf.write("</head><body>");
                bf.newLine();
                bf.write("<h1>" + getBackenFile().getName() + "</h1>");
                bf.write("<h4>" + getBackenFile().getAbsolutePath().substring(ROOT.getAbsolutePath().length()) + "</h4>");
                bf.write("<h6>width and height of cards depends on your settings. You can easily change tehm also in this html file (img.card width and height) </h6>");
                bf.newLine();
                List<CountedCard> c = getCards();
                for (CountedCard countedCard : c) {
                    bf.write("<h2>" + countedCard.getCard().getName() + "</h2>");
                    bf.write("<h3>" + countedCard.getCard().getId() + "(" + countedCard.count + ")</h3>");
                    String s = countedCard.getCard().getId();
                    while (s.startsWith("/")) {
                        s = s.substring(1);
                    }
                    int y = 1;
                    if (expand) {
                        y = countedCard.count;
                    }
                    for (int x = 0; x < y; x++) {
                        bf.write("<img class='card' src='" + s + "'/>");
                        bf.newLine();
                    }
                }
                bf.write("</body>");
                bf.newLine();
                bf.write("</html>");
                bf.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
                l.add(ex);
            } finally {
                bf.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            l.add(ex);
        }
        return l;
    }
}
