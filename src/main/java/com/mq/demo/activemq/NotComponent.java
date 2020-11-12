package com.mq.demo.activemq;

public class NotComponent {
    public static void execute(){
//        try{
//            throw new NullPointerException();
//        } catch(Throwable e){
//            //e.printStackTrace();
//            System.out.println("### caught ...");
//        }

        try{
            throw new CheckedException();
        } catch(Throwable e){
            System.out.println("### caught CheckedException ...");
        }
    }
}
