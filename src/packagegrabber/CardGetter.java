/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package packagegrabber;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CardGetter {

    static int anti_spam_sleep;
    private String count;
    private String name;
    private String id;
    private final String srcS;
    private final String parent;

    public String getCount() {
        if (count == null) {
            count = srcS.split(" ")[0];
        }
        return count;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if (!(obj instanceof CardGetter)) {
                return false;
            }
            CardGetter cg = (CardGetter) obj;
            return getId().equals(cg.getId());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int hashCode() {
        try {
            return getId().hashCode();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    public static final String nameGrabberP1 = "\">";
    public static final String nameGrabberP2 = "</a><BR>";
    public static final String nameGrabber = nameGrabberP1 + ".*?" + nameGrabberP2;
    public static final Pattern nameGrabberPattern = Pattern.compile(nameGrabber);

    public String getName() {
        if (name == null) {
            Matcher mm = nameGrabberPattern.matcher(srcS);
            mm.find();
            name = mm.group();
            name = name.substring(nameGrabberP1.length(), name.length() - nameGrabberP2.length());
        }
        return name;
    }

    public String getId() throws IOException {
        if (id == null) {
            id = proceedCard(srcS.substring(getCount().length() + 1/*space*/));
        }
        return id;
    }

    CardGetter(String string, String parent) {
        this.srcS = string;
        this.parent = parent;
    }
    public static final String hrefGrabberP1 = " rel=\"";
    public static final String hrefGrabberP2 = "\" href";
    public static final String hrefGrabber = hrefGrabberP1 + ".*?" + hrefGrabberP2;
    public static final Pattern hrefGrabberPattern = Pattern.compile(hrefGrabber);
    //<a class="hover" rel="/db/WP-CH.asp?CN=Island" href="/db/magic_single_card.asp?cn=Island&ref=hover">Island</a><BR>

    private String getCardHtmlUrl(String substring) {
        Matcher m = hrefGrabberPattern.matcher(substring);
        m.find();
        String urlEnd = m.group();
        urlEnd = urlEnd.substring(hrefGrabberP1.length(), urlEnd.length() - hrefGrabberP2.length());
        return urlEnd;
    }
    //<img style="margin: 7px 7px 8px 8px; width: 200px; height: 285px" src="http://magic.tcgplayer.com/db/cards/22682.jpg" width="200" height="285" border="0" OnError="this.src='http://magic.tcgplayer.com/db/cards/0.jpg'" alt="Island">
    private static final String imgPaternEx1 =
            "<img style=.*? src=\"";
    private static final String imgPaternEx2 =
            "\" width=.*? OnError=.*?\">";
    public static final Pattern imgPattern1 = Pattern.compile(imgPaternEx1);
    public static final Pattern imgPattern2 = Pattern.compile(imgPaternEx2);

    private String getCardImageUrl(String substring) throws IOException {
        String urlHtml = parent + getCardHtmlUrl(substring);
        System.err.println("getting card id from " + urlHtml);
        urlHtml = urlHtml.replace(" ", "%20");//this is nasty, but in magic names we should not face worse tehn space
        String s = PackageGrabber.getUrl(urlHtml);
        Matcher m1 = imgPattern1.matcher(s);
        Matcher m2 = imgPattern2.matcher(s);
        m1.find();
        m2.find();
        String idUrl = s.substring(m1.end(), m2.start());
        return idUrl;
    }

    private String proceedCard(String substring) throws IOException {
        String url = getCardImageUrl(substring);
        System.err.println("found " + url);
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public String toPackageEntry(String prefix) throws IOException {
        String l = getCount() + ": " + prefix + "/" + getId();
        while (l.indexOf("//") > -1) {
            l = l.replaceAll("//", "/");
        }
        return l;
    }

    public void addCount(String by) {
        int a = Integer.valueOf(by);
        int b = Integer.valueOf(getCount());
        count = (a + b) + "";
    }

    public static void urlToFile(String url, File f) throws IOException {

        URL u = new URL(url);
        URLConnection uc = u.openConnection();
        uc.connect();
        InputStream in = uc.getInputStream();
        FileOutputStream out = new FileOutputStream(f);
        final int BUF_SIZE = 1 << 8;
        byte[] buffer = new byte[BUF_SIZE];
        int bytesRead = -1;
        while ((bytesRead = in.read(buffer)) > -1) {
            out.write(buffer, 0, bytesRead);
        }
        in.close();
        out.flush();
        out.close();

    }

    void download(String prefix) throws IOException {
        try {
            Thread.sleep(anti_spam_sleep / 2 + (new Random().nextInt(anti_spam_sleep)));
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        String url = getCardImageUrl(srcS.substring(getCount().length() + 1/*space*/));
        while (prefix.startsWith("/")) {
            prefix = prefix.substring(1);
        }
        File dir = new File(prefix);
        dir.mkdirs();
        File dest = new File(dir, getId());
        System.err.println("Saving " + url + " to " + dest.getAbsolutePath());
        urlToFile(url, dest);
    }
}
