package com.TCPComm.SMOLcomm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.jmeter.protocol.tcp.sampler.TCPClientImpl;
import org.apache.jmeter.util.JsseSSLManager;
import org.apache.jmeter.util.SSLManager;

public class DemoApplication extends TCPClientImpl {

  public DemoApplication() {
    // TODO Auto-generated constructor stub
  }

  public static void main(String[] args) {
    int your_app_port = 1099;
    String your_app_host = "localhost";
    JsseSSLManager sslManager = (JsseSSLManager) SSLManager.getInstance();
    try {

      SSLSocketFactory sslsocketfactory = sslManager.getContext().getSocketFactory();
      SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(your_app_host, your_app_port);

      /*
       * send http request
       *
       * Before any application data is sent or received, the SSL socket will do SSL handshaking
       * first to set up the security attributes.
       *
       * SSL handshaking can be initiated by either flushing data down the pipe, or by starting the
       * handshaking by hand.
       *
       * Handshaking is started manually in this example because PrintWriter catches all
       * IOExceptions (including SSLExceptions), sets an internal error flag, and then returns
       * without rethrowing the exception.
       *
       * Unfortunately, this means any error messages are lost, which caused lots of confusion for
       * others using this code. The only way to tell there was an error is to call
       * PrintWriter.checkError().
       */

      sslsocket.startHandshake();

      PrintWriter out =
          new PrintWriter(new BufferedWriter(new OutputStreamWriter(sslsocket.getOutputStream())));

      out.println(
          "20191218101051&000000000018&0001&INIT&0ABEFB86C1E41D3AAB076FFE7E454BF6&HTTP/1.0");
      out.flush();
      out.println(
          "20191218101040&000000000018&0002&016F320000000005000000100001000500000010000B00050000001000020005000000100003000500000010000500050000001000F967000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&0ABEFB86C1E41D3AAB076FFE7E454BF6&HTTP/1.0");
      out.flush();

      /*
       * Make sure there were no surprises
       */

      if (out.checkError())
        System.out.println("SSLSocketClient:  java.io.PrintWriter error");

      /* read response */

      BufferedReader in = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));

      String inputLine;

      while ((inputLine = in.readLine()) != null)
        System.out.println(inputLine);

      in.close();
      out.close();
      sslsocket.close();
    } catch (Exception e) {
      // TODO: handle exception
    }

  }

}
