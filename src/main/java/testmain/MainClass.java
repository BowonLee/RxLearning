package testmain;

import io.reactivex.Observable;

public class MainClass {
   public static void main(String args[]){
      MainClass main = new MainClass();
      main.emit();
   }

   public void emit(){
      Observable.just("Hello","RxJava2").subscribe(System.out::println);
   }



}
