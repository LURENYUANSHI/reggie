package test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class testStream {
   public static void main(String[] args) {
       double sum1=0;
       /**减跟减归类,加跟加归类
        * 1/1+1/3+1/5+.....1/99
        */
       for(double i=1;i<=99;i+=2){
           sum1+=(1/i);
       }
       /**
        * 1/2+1/4+1/6+1/8+....1/100
        */
       double sum2=0;
       for(double j=2;j<=100;j+=2){
           sum2+=(1/j);
       }
       System.out.println(sum1-sum2);
   }
}
