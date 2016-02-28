/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package packagegrabber;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PackageGrabber {

    public static int anti_spam_sleep = 500;
    private final URL u;
    private final String source;
    private String prefix;
    List<CardGetter> result;
    private String content;
    private boolean cards=false;

    public PackageGrabber(String s) throws MalformedURLException {
        source = s;
        u = new URL(s);
    }

    static String getUrl(String u) throws IOException {
        return getUrl(new URL(u));
    }

    static String getUrl(URL u) throws IOException {
        try {
            Thread.sleep(anti_spam_sleep / 2 + (new Random().nextInt(anti_spam_sleep)));
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream(), "utf-8"));
        StringBuilder sb = new StringBuilder();
        try {
            while (true) {
                String s = br.readLine();
                if (s == null) {
                    return sb.toString();
                }
                sb.append(s).append("\n");
            }
        } finally {
            br.close();
        }

    }
    public static final String marker1 = "<div style=\"height: 2px; line-height: 2px\"></div>";
    public static final String marker2 = "<div style=\"width: 100%; padding: 0 0 5 150;\"><font class=default_8 color=#BFBFBF>Deck Total";
    public static final String cardDef =
            "\\d+ <a class=\"hover\" rel=\".*?\" href=\"/db/magic_single_card.asp.*?\">.*?</a><BR>";
    public static final Pattern cardDefPattern = Pattern.compile(cardDef);

    private String getContent() throws IOException {
        if (content == null) {
            content = getUrl(u);
        }
        return content;
    }

    void download() throws IOException {
        System.err.println("processing " + u.toExternalForm());
        String allBase = getContent();
        //int i1=allBase.indexOf(marker1);
        //int i2=allBase.indexOf(marker2);
        // System.err.println(marker1+" found on "+i1);
        // System.err.println(marker2+" found on "+i2);
        //String base=allBase.substring(i1+marker1.length(),i2);
        String base = allBase;
        //System.err.println(base);
        System.err.println("*************cards links**********");
        List<String> cardLinks = new ArrayList<String>(60);
        Matcher m = cardDefPattern.matcher(base);
        while (m.find()) {
            String s = m.group();
            cardLinks.add(s);
            System.err.println(s);
        }
        String parent = source.substring(0, source.indexOf("db/"));
        result = new ArrayList<CardGetter>(cardLinks.size());
        for (String string : cardLinks) {
            CardGetter cg = new CardGetter(string, parent);
            System.err.println(cg.getCount());
            System.err.println(cg.getName());
            if (cg.getCount() == null || cg.getName() == null || cg.getCount().equals("?") || cg.getName().equals("?")) {
                System.err.println("skipping '?'");
                continue;
            }
            System.err.println(cg.getId());
            CardGetter dupla = null;
            for (CardGetter c : result) {
                if (c.equals(cg)) {
                    dupla = c;
                    break;
                }
            }
            if (dupla == null) {
                result.add(cg);
                if (cards) try{
                    cg.download(prefix);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            } else {
                System.err.println("Duplicate found! " + dupla.getName() + " x " + cg.getName());
                System.err.println("Increasing! " + dupla.getCount() + " for " + cg.getCount());
                dupla.addCount(cg.getCount());
            }


        }
        System.err.flush();




    }

    public List<String> getPackage() throws IOException {
        if (result == null) {
            download();
        }
        List<String> r = new ArrayList<String>(result.size());
        for (CardGetter cardGetter : result) {
            r.add(cardGetter.toPackageEntry(prefix));

        }
        r.add("p: graveyard");
        r.add("p: exiled");
        return r;
    }

    void setPrefix(String pf) {
        prefix = pf;
    }

    public void printResult(PrintStream p) throws IOException {
        List<String> r = getPackage();
        for (String string : r) {
            p.println(string);

        }
    }

    public void save() throws IOException {
        save(new File(getName()));
    }

    public void save(File f) throws IOException {
        System.err.println("saving " + f.getAbsolutePath());
        PrintStream pf = new PrintStream(f);
        try {
            printResult(pf);
            pf.flush();
        } finally {
            pf.close();
        }

    }
    //<title>Magic TCG Deck - Solar Flare by Alex Bartel</title>
    String p1 = "<title>";
    String p2 = "</title>";
    String p3 = " by ";
    String add = "Magic TCG Deck - ";

    public String getName() throws IOException {
        String s = getContent();
        s = s.replace(add, "");
        int i1 = s.indexOf(p1);
        int i2 = s.indexOf(p2);
        if (i1 >= 0 && i2 >= 0) {
            s = s.substring(i1 + p1.length(), i2);
            if (s.contains(p3)) {
                return s.split(p3)[0];
            } else {
                return s;
            }
        } else {
            return "unknown";
        }
    }

    void setCards(boolean cards) {
        this.cards=cards;
    }
}
