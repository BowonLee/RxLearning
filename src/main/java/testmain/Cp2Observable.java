package testmain;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class Cp2Observable {


    /*
     * 인자가 없을때는 Error일 경우만 Exception을 던진다
     * 인자가 하니일 경우 onNext를 처리하며 에러가 생일 경우 위와 동일
     * 인자가 둘일 경우 onNext를 처리하며 에러가 생길 경우 해당 동작 수행
     * 인자가 셋일경우 onNext,onError,onComplete 모두 처리
     * */
    public static void main(String args[]){

        Cp2Observable observable = new Cp2Observable();

        //observable.justTest();
        //observable.createTest();
        //observable.fromArrayTest();
        //observable.fromIterableTest();
//        observable.fromCallableTest();
//        observable.fromFutureTest();
        observable.fromPublisher();
    }

    public void justTest(){
        /*
         * just() : 순서대로 발행 , 같은 타입, 인자의 수는 1~10
         * 데이터를 넣으면 자동으로 OnNext가 호츌
         * */

        Observable observable = Observable.just(1,2,3,4,5);

        observable.subscribe();
        observable.subscribe(v->System.out.println("parm1 : "+v));
        observable.subscribe(v->System.out.println("parm1 : "+v));


        observable.subscribe(
                v->System.out.println("parm2 : "+v),
                err->System.out.println("parm2 err :  "+err));

        Disposable d = observable.subscribe(
                v->System.out.println("parm3 : "+v),
                err->System.out.println("parm3 : "+err),
                ()->System.out.println("parm3 : "+"complete")
        );

        System.out.println("Disposed : " + d.isDisposed());

    }

    public void createTest(){

        /*
        * onNexr와 같은 메서드를 직접 호출하여 사용하는 create
        * 1. 메서드 레퍼런스로 축약할 수 있는가
        * 2. 람다 표현식을 사용할 수 있는가
        * 3. 1,2를 활용할 수 없으면 익명 객체나 멤버변수로 표현
        * */
        /*
        * 사용법이 까다로운 상급자용 활용법에 속한다.
        * 주의사항
        * 1. DIsposed되었을 때 등록 된 콜백을 모두 제거해야 한다.
        * 2. 구독자가 구독하는 동안에만 onNext와 onComplete 함스를 호출해야 한다
        * 3. 에러가 발생한 경우 오직 onError 이벤트로만 에러를 전달해야 한다.
        * 4. 배압을 직접 처리해야 한다.
       * */
        /*
         * onComplete를 직접 호출해 주지 않는다면 Disposed 되지 않는다.
         * */
        Observable<Integer> source = Observable.create(
                (emitter -> {
                    emitter.onNext(100);
                    emitter.onNext(200);
                    emitter.onNext(300);
                    emitter.onComplete();
                })
        );
        source.subscribe(System.out::println);
        source.subscribe(data->System.out.println("OnNext = " + data));
    }
    public void fromArrayTest(){

        /*
        * 배열을 발행하는 경우
        * */
        //Integer[] arr = {100,200,300};
        int[] arr = {100,200,300};
        Observable<Integer> source = Observable.fromArray(toInteagerArray(arr));
        source.subscribe(System.out::println);
    }
    public void fromIterableTest(){
        /*
        * Iterable 객체를 발행하려는 경우 다양한 Iterable을 받을 수 있다.
        * */
        //List
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        list.add("three");

        Observable source1 = Observable.fromIterable(list);
        source1.subscribe(System.out::println);

        //HashSet
        Set<String> set = new HashSet<>();
        set.add("1");
        set.add("2");
        set.add("3");

        Observable source2 = Observable.fromIterable(set);
        source2.subscribe(System.out::println);

        BlockingQueue orderQueue = new ArrayBlockingQueue<>(100);
        orderQueue.add("one");
        orderQueue.add("two");
        orderQueue.add("three");

        Observable source3 = Observable.fromIterable(orderQueue);
        source3.subscribe(System.out::println);

    }


    /*
    * 자바에서 Inteager 와 int는 엄연히 다른 형식이다.
    * 하지만 보통 배열을 선언하는대 있어서는int 를 이용하는 경우가 많다.
    * 해당 경우 아래와 같은 방식으로 자료형을 수정하도록 한다.
    * */
    private static Integer[] toInteagerArray(int[] intArray){
        return IntStream.of(intArray).boxed().toArray(Integer[]::new);
    }

    public void fromCallableTest(){
        /*
        * 비동기 작업을 할 수 있는 Callable을 받아 처리하는 클레스
        * callable 객체를 그대로 대입한 뒤 해당 callable을 실행한 뒤
        * 해당 반환값을 그대로 받아 사용할 수 있도록 되어있다.
        * */
        Callable<String> callable = () -> {
            Thread.sleep(1000);
            return "Hello Callable";
        };

        Observable<String> source = Observable.fromCallable(callable);
        source.subscribe(System.out::println);
    }

    public void fromFutureTest(){
        /*
        * 동시성 객체
        * 주로 비동기적 계산의 결과를 구할 때 사요한다.
        * Excutor 인터페이스를 구현한 뒤 Callalble 객체를 인자로 넣는다.
        * get메서드 호출시 Callalbe의 계산 결과가 나오기 전까지 기다린다(블로킹)
        * */
        Future<String> future = Executors.newSingleThreadExecutor().submit(()->{
            Thread.sleep(1000);
            return "Hello Future";
        });
        Observable<String> source = Observable.fromFuture(future);
        source.subscribe(System.out::println);

    }

    public void fromPublisher(){
        /*
        * 자바 9 에서 새로 발표한 Flow API의 일부분
        * */
        Publisher<String> publisher = s ->{
            s.onNext("Hello Observaler.FromPublisher");
            s.onComplete();
        };
        Observable<String> source = Observable.fromPublisher(publisher);
        source.subscribe(System.out::println);

    }

}
