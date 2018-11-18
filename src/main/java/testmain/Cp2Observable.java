package testmain;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
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
       // observable.fromPublisher();
      //  observable.singleTest();
       // observable.asyncSubjectTest();
      //  observable.behaviorSubjectTest();
        //observable.publishSubjectTest();
       // observable.replaySubjectTest();
        observable.connectableObservable();
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

        BlockingQueue<Object> orderQueue = new ArrayBlockingQueue<>(100);
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

    public void singleTest(){
        /*
        * Single 클레스는 하나의 데이터만을 발행하는 클레스이다.
        * 결과가 유일한 서버 API를 호출하는 대 쓰인다.
        * */

        // Single을 선언하여 일반 Observable 처럼 발행 할 수 있다.
        Single<String> source0 = Single.just("I'm single");
        source0.subscribe(System.out::println);

        /*
        * Single은 Observable의 특수한 형태이기에 Observable에서 변환할 수 있다.
        * */
        //1. Observable -> Single
        // 첫번쨰 값을 Single로 발행한다 인자가 둘 이상 들어 있는 경우 에러 발생
        Observable<String> source1 = Observable.just("I'm single");
        Single.fromObservable(source1).subscribe(System.out::println);

        //2. single() 메서드를 호출하여 생성
        // Obserbale 인자가 없는 경우 디폴트 벨류가 발행
        Observable.just("I'm single").single("defalut item").subscribe(System.out::println);

        //3. first함수를 호출하여 생성
        //여러 데이터를 발행 할 수 있는 Observable을 Single로 발행 한다. 여러 데이터가 있는 경우에도 첫 데이터 발행 이후 onSuccess
        String[] numbers = {"One", "Two", "Three"};
        Integer[] arr={};
        int[] arr2;

        Observable.fromArray(numbers).first("default value").subscribe(System.out::println);

        //4. empty Observable 에서 객체 생성
        //아무 데이터가 발행하지 않는 경우 디폴트 값이 출력된다.
        Observable.empty().single("default value").subscribe(System.out::println);

        //5. take()함수에서 Single 객체 생성
        Observable.just("123", "456").take(1).single("000").subscribe(System.out::println);
    }

    public void asyncSubjectTest(){

        /*
        * Subject : 차가운 Observable을 뜨거운 Observable로 변환 시켜주는 클레스
        * Observable 과 같이 여러 클레스들을 제공하여 다른 상황을 처리 할 수 있다.
        */
        // AsyncSubject : 마직막으로 발행 한 데이터를 가져오는 클래스, 언제 호출하던 마지막 데이터만 처리한다.
        // 만일 complete를 명시하지 않는다면 호출하지 못한다.
        // 또한 complete 이후의 값들은 모두 무시한다.
        AsyncSubject<String> asyncSubject = AsyncSubject.create();
        asyncSubject.subscribe(data -> System.out.println("Subscribe 1 : " + data));
        asyncSubject.onNext("1");
        asyncSubject.onNext("3");
        asyncSubject.subscribe(data -> System.out.println("Subscribe 2 : " + data));
        asyncSubject.onNext("5");
        asyncSubject.onComplete();

        // subscrobe 를 통해 동작하는 AsyncSubject
        //Subject 클레스는 Obervable 클레스를 상속받아 구현되어 있기에 이런 방식의 사용이 가능하다.
        Float[] temperature = {10.1f,13.4f,12.5f};
        Observable<Float> source = Observable.fromArray(temperature);

        AsyncSubject<Float> subject = AsyncSubject.create();
        subject.subscribe(data->System.out.println("Subscribe 3 => " + data));

        source.subscribe(subject);
    }

    public void behaviorSubjectTest(){
        /*
        * 구독자가 구독을 하면 가장 최근의 값, 혹은 기본값을 넘겨주는 클레스이다.
        * */

        BehaviorSubject<String> behaviorSubject = BehaviorSubject.createDefault("6");
        behaviorSubject.subscribe(data->System.out.println("Subscriber 1 => "+ data));
        behaviorSubject.onNext("1");
        behaviorSubject.onNext("3");
        behaviorSubject.subscribe(data->System.out.println("Subscriber 2 => "+ data));
        behaviorSubject.onNext("5");
        behaviorSubject.onComplete();

    }
    public void publishSubjectTest(){
        /*
        * 구독자가 발행을 시작하면 해당 시간부터 발행을 시작한다.
        * 기본값은 없다.
        * */
        PublishSubject<String> publishSubject= PublishSubject.create();
        publishSubject.subscribe(data->System.out.println("subscribe #1 :" +data));
        publishSubject.onNext("1");
        publishSubject.onNext("3");
        publishSubject.subscribe(data->System.out.println("subscribe #2 :" +data));
        publishSubject.onNext("5");
        publishSubject.onComplete();
    }

    public void replaySubjectTest(){

        /*
        * 차가운 Observable 처럼 동작하는 Subject
        * 구독자가 생기면 처음부터 끝까지 모두 발행한다.
        * */
        ReplaySubject replaySubject =ReplaySubject.create();
        replaySubject.subscribe(data->System.out.println("Subscribe #1 - "+ data));
        replaySubject.onNext("1");
        replaySubject.onNext("3");
        replaySubject.subscribe(data->System.out.println("Subscribe #2 - "+ data));
        replaySubject.onNext("5");
    }


    public void connectableObservable(){
        /*
        *차가운 Observable 을 뜨거운 Observable로 변환해 준다
        * 원 데이터 하나른 여러 사용자에게 전달 할 떄 사용한다.
        * connect 함수를 호출하면 그 전까지 구독한 모든 구독자에게 데이터를 발행한다.
        * connect 이후의 구독자는 구독 이후에 발생한 데이터가 발행된다.
        *
        * */

        String[] data = {"1","3","5"};
        Observable<String> balls = Observable.interval(100L,TimeUnit.MILLISECONDS).map(Long::intValue).map(i->data[i]).take(data.length);
        ConnectableObservable<String> source = balls.publish();

        source.subscribe(value-> System.out.println("Subscriber #1 : " + value));
        source.subscribe(value-> System.out.println("Subscriber #2 : " + value));
        source.connect();

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        source.subscribe(value-> System.out.println("Subscriber #3 : " + value));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
