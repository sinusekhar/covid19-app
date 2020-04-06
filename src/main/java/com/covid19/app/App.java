package com.covid19.app;


import java.util.Calendar;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args )
    {
        Calendar now = Calendar.getInstance();

        now.set(Calendar.MONTH,Calendar.MARCH);

        System.out.println( "Hello World!" + now.get(Calendar.DAY_OF_MONTH) + " " + now.get(Calendar.MONTH) + " " + now.get(Calendar.YEAR));
    }
}
