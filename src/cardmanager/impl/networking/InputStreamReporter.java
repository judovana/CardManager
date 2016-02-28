/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.impl.networking;


import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


class InputStreamReporter
 extends FilterInputStream {

 //
 // Data
 //

 /** Total bytes read. */
 private long fTotal;

public InputStreamReporter(InputStream stream) {
 super(stream);
 } // <init>(InputStream)

 //
 // InputStream methods
 //

 /** Reads a single byte. */
 public int read() throws IOException {
 int b = super.in.read();
 if (b == -1) {
 System.out.println("Client: Read "+fTotal+" byte(s) total.");
 return -1;
 }
 fTotal++;
 return b;
 } // read():int

 /** Reads a block of bytes. */
 public int read(byte[] b, int offset, int length)
 throws IOException {
 int count = super.in.read(b, offset, length);
 if (count == -1) {
 System.out.println("Client: Read "+fTotal+" byte(s) total.");
 return -1;
 }
 fTotal += count;
 //if (Client.this.fVerbose) System.out.println("Client: Actually read "+count+" byte(s).");
 return count;
 } // read(byte[],int,int):int

 }