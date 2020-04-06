package com.TCPComm.SMOLcomm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;


public class prueba {

  private static final ConcurrentHashMap<String, ZonedDateTime> finalTime = new ConcurrentHashMap<>();
  private static final ConcurrentHashMap<String, Long> threadTime = new ConcurrentHashMap<>();
  public static ZonedDateTime timeInitial= ZonedDateTime.now();
  
	public prueba() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

	  Date date = new Date();
	  DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	  String dateHour=dateFormat.format(date);
		
		for (Integer i = 1; i <= 10; i++) {
		
		  try {
        Thread.sleep(1000);        
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
		  
      ZonedDateTime secondTime= ZonedDateTime.now();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
			String mac= StringUtils.leftPad(i.toString(),10,'0');	
      finalTime.put(mac, ZonedDateTime.now());
      ZonedDateTime timeFinish = finalTime.values().parallelStream().sorted((date1, date2)->date2.compareTo(date1)).findFirst().get(); 
      threadTime.put(mac, Duration.between(secondTime,timeFinish).toSeconds());
      System.out.println("1 tiempo "+Duration.between(timeInitial,timeFinish).toSeconds());
      System.out.println("2 timepo "+threadTime.values().parallelStream().sorted((time1,time2) -> time2.compareTo(time1)).findFirst().get());
		  System.out.println(mac);
		}
		System.out.println(dateHour);

		
	}	
	
}
