package testmain;

import io.reactivex.Observable;

public class Cp2Observable {

    /*
     * just() : 순서대로 발행 , 같은 타입, 인자의 수는 1~10
     * */

    public static void main(String args[]){

        Cp2Observable observable = new Cp2Observable();
        Observable observable1 = Observable.just(1,2,3,4,5);

        observable.subscriveTest(observable1);
    }


    public void subscriveTest(Observable observable){
        observable.subscribe();
        observable.subscribe(v->System.out.println("parm1 : "+v));

        observable.subscribe(
                v->System.out.println("parm2 : "+v),
                err->System.out.println("parm2 err :  "+err));

        observable.subscribe(
                v->System.out.println("parm3 : "+v),
                err->System.out.println("parm3 : "+err),
                ()->System.out.println("parm3 : "+"complete")
        );


    }

}
