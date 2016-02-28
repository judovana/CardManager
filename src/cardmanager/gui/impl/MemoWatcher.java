/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.gui.impl;

import cardmanager.impl.Settings;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Jirka
 */
public class MemoWatcher implements Runnable,DocumentListener {
    private JTextArea memo;
    private Settings settings;
    private long firstTimeModified=0;
    private long lastTimeModified=0;
    private boolean modificationInProgerss=false;
    public String last="";

    public MemoWatcher(JTextArea jTextArea1, Settings settings) {
        this.memo=jTextArea1;
        this.settings=settings;
        new Thread(this).start();
        memo.getDocument().addDocumentListener(this);
    }

    public void updateText(String s) {
        memo.getDocument().removeDocumentListener(this);
        try {
            int p = memo.getCaretPosition();
            memo.setText(s);
            if (p>=s.length()){
                memo.setCaretPosition(0);
            }else{
            memo.setCaretPosition(p);
            }
        } catch (Exception ex) {
            try {
                memo.setText(s);
                memo.setCaretPosition(0);
            } catch (Exception exx) {
                exx.printStackTrace();
            }
            ex.printStackTrace();
        }
        memo.getDocument().addDocumentListener(this);
    }

    public void onChange() {
        lastTimeModified = System.currentTimeMillis();
        if (!modificationInProgerss) {
            firstTimeModified = System.currentTimeMillis();
            settings.getTemp().getLogger().writeSharedMessage("Started editing of shared textfield");
            modificationInProgerss = true;
        }
    }

    public void run() {
        int counter=0;
      while(true){
          try{
              Thread.sleep(100);
            long currentTime = System.currentTimeMillis();
             counter++;
             if(counter>10000) counter=0;
             if (modificationInProgerss && counter%10==0){
              sendContent();
             }
             if(modificationInProgerss && (currentTime-lastTimeModified)/1000>3){
                 modificationInProgerss=false;
                 lastTimeModified=0;
                 firstTimeModified=0;
                   settings.getTemp().getLogger().writeSharedMessage("Finished editing of shared textfield");
                   //??sendContent();
             }


          }catch(Throwable t){
              t.printStackTrace();
          }
      }
    }

    public void insertUpdate(DocumentEvent e) {
        onChange();
    }

    public void removeUpdate(DocumentEvent e) {
         onChange();
    }

    public void changedUpdate(DocumentEvent e) {
        onChange();
    }

    private void sendContent() {
        if (settings.getTemp().getNetwork()!=null){
            String q=memo.getText();
            if (last.equals(q)) return;
            last=q;
            settings.getTemp().getNetwork().sendSharedContent(q);
        }

    }

}
