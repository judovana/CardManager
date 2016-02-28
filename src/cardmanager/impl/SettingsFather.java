/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cardmanager.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jirka
 */
public abstract class SettingsFather {

    private Map<String, String> settings;
    protected File backendFile;

    public SettingsFather(File f) {
        if (!f.exists()) {
            throw new IllegalArgumentException(f + " does not exists");
        }
        backendFile = f;
        load(f);
    }

    protected SettingsFather() {
    }

    private void addLine(String s, Map<String, String> settings) {
        int i = s.indexOf("=");
        String key = s.substring(0, i);
        String value = s.substring(i + 1, s.length());
        settings.put(key.trim(), value.trim());
    }

    protected String getString(String string) {
        String s = getProperty(string);
        if (s == null) {
            return "";
        }
        return s;
    }

    protected void load(File f) {


        try {
            Reader is = new FileReader(f);
            load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void load(Reader is) throws IOException {
        settings = new HashMap<String, String>(6);
        BufferedReader r = new BufferedReader(is);
        while (true) {
            String s = r.readLine();
            if (s == null) {
                break;
            }
            addLine(s, settings);

        }

    }

    protected void save() throws IOException {
        if (backendFile == null) {
            throw new NullPointerException("file for save settings is not defined");
        }

        save(backendFile);
    }

    protected void save(File backendFile) throws IOException {
        Writer fw = new FileWriter(backendFile);
        try {
            save(fw);
        } finally {
            fw.close();
        }
    }

    protected void save(Writer fileWriter) throws IOException {
        BufferedWriter bw = new BufferedWriter(fileWriter);
        try {
            for (Map.Entry<String, String> e : settings.entrySet()) {
                bw.write(e.getKey() + " = " + e.getValue());
                bw.newLine();
            }
        } finally {
            bw.flush();
            bw.close();
        }
    }

    public String getProperty(String key) {
        return settings.get(key);
    }

    public String setProperty(String key, String value) throws IOException {
        String r = settings.put(key, value);
        save();
        return r;
    }

    public String setString(String key, String value) {
        if (value == null) {
            value = "";
        }
        try {
            return setProperty(key, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return "";
        }

    }

    public Integer getInteger(String key) {
        try {
            return new Integer(getString(key));
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    ;

    public Integer setInteger(String key, Integer value) {
        try {
            String s = setString(key, value.toString());
            if (s == null || s.trim().equals("")) {
                return 0;
            }
            return new Integer(s);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }




    }

    public Boolean getBoolean(String key) {
        try {
            return new Boolean(getString(key));
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    ;

    public Boolean setBoolean(String key, Boolean value) {
        try {
            String s = setString(key, value.toString());
            if (s == null || s.trim().equals("")) {
                return false;
            }
            return new Boolean(s);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
