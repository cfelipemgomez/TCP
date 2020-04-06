package com.TCPComm.SMOLcomm;

import java.io.BufferedWriter;
import java.io.IOException;
// import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.util.JsseSSLManager;
import org.apache.jmeter.util.SSLManager;

class Client {

  private static final String HOST = "localhost";
  private static final int PORT = 1099;
  private static final int NUM_CLIENTS = 450;
  private static final int START_CLIENTS = 401;
  private static final TipoTrama TRAMA = TipoTrama.CONTADOR3;
  private static final ConcurrentHashMap<String, ZonedDateTime> finalTime =
      new ConcurrentHashMap<>();
  private static final ConcurrentHashMap<String, Long> threadTime = new ConcurrentHashMap<>();
  protected static ZonedDateTime initialTime = ZonedDateTime.now();

  public static void main(String args[]) {

    Date date = new Date();
    long l = System.currentTimeMillis();
    System.out.println("Inicial timing: " + l);
    System.out.println(date);

    // creates the socket connections to the server
    for (Integer i = START_CLIENTS; i < (NUM_CLIENTS + START_CLIENTS); i++) {

      if (i == 26)
        i++;

      String macNumber = StringUtils.leftPad(i.toString(), 12, '0');

      new ThreadCliHandler(macNumber).start();
    }

    System.out.println("Launching timing :" + (System.currentTimeMillis() - l));
    System.out.println(date);
  }

  // Handles every socket client incoming messages in one single thread
  private static class ThreadCliHandler extends Thread {

    private String mac;

    public ThreadCliHandler(String mac) {
      this.mac = mac;
    }

    @Override
    public void run() {

      ZonedDateTime initialThreadTime = ZonedDateTime.now();

      JsseSSLManager sslManager = (JsseSSLManager) SSLManager.getInstance();

      try {

        SSLSocketFactory sslsocketfactory = sslManager.getContext().getSocketFactory();
        SSLSocket socket = (SSLSocket) sslsocketfactory.createSocket(HOST, PORT);
        socket.startHandshake();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateHour = dateFormat.format(date);

        /* Send frames to SMOLcomm */

        PrintWriter out =
            new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

        switch (TRAMA) {
        case INIT:
          out.println(
              dateHour + "&" + mac + "&0001&INIT&0ABEFB86C1E41D3AAB076FFE7E454BF6&HTTP/1.0");
          out.flush();
          break;
        case CONTADOR1:
          out.println(dateHour + "&" + mac
              + "&0002&016F320000000005000000100001000500000010000B00050000001000020005000000100003000500000010000500050000001000F967000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&0ABEFB86C1E41D3AAB076FFE7E454BF6&HTTP/1.0");
          out.flush();
          break;
        case CONTADOR2:
          out.println(dateHour + "&" + mac
              + "&0003&016F320000000005000000200001000500000020000B0005000000200002000500000020000300050000002000050005000000200048BA000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&0ABEFB86C1E41D3AAB076FFE7E454BF6&HTTP/1.0");
          out.flush();
          break;
        case CONTADOR3:
          out.println(dateHour + "&" + mac
              + "&0002&016F320000000005000000300001000500000030000B0005000000300002000500000030000300050000003000050005000000300027F1000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&0ABEFB86C1E41D3AAB076FFE7E454BF6&HTTP/1.0");
          out.flush();
          break;

        default:
          break;
        }

        /*
         * Make sure there were no surprises
         */

        if (out.checkError())
          System.out.println("SSLSocketClient:  java.io.PrintWriter error");
        out.close();
        socket.close();

      } catch (IOException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        finalTime.put(mac, ZonedDateTime.now());
        ZonedDateTime finishThreadTime = ZonedDateTime.now();

        ZonedDateTime finishTime = finalTime.values().parallelStream()
            .sorted((date1, date2) -> date2.compareTo(date1)).findFirst().get();

        threadTime.put(mac, Duration.between(initialThreadTime, finishThreadTime).toMillis());

        System.out
            .println("Tiempo de Prueba: " + Duration.between(initialTime, finishTime).toMillis());

        System.out.println("Tiempo de Hilo: " + threadTime.values().parallelStream()
            .sorted((time1, time2) -> time2.compareTo(time1)).findFirst().get());

      }
      System.out.println(mac + " ends..");
    }
  }
}