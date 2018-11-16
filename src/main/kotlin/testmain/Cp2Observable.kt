package testmain

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.observables.ConnectableObservable
import io.reactivex.subjects.AsyncSubject
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import org.reactivestreams.Publisher
import java.util.concurrent.*
import kotlin.reflect.KCallable


fun main(args:Array<String>){

    //justExample()
    //createExample()
    //fromArrayTest()
    //fromIterableTest()
    //fromCallableTest()
   // fromFutureTest()
    //fromPublisher()
//    singleTest()
//    asyncSubjectTest()
//    behaviorSubject()
//    publishSubject()
//    replaysubject()
    connectableObservable()
}
fun justExample(){
    /*
    * just() : 순서대로 발행 , 같은 타입, 인자의 수는 1~10
    * 코틀린 에서는 각각 람다식을 중괄호로 묶어 주어야 한다.
    * */
    val observableData = Observable.just(1,2,3,4,5)
    observableData.subscribe({println(it)})
    observableData.subscribe({println(it)},{println("err")})
    val isDisposed = observableData.subscribe({println(it)},{println("err")}, { println("complete")})
    /*
    * 인자가 없을때는 Error일 경우만 Exception을 던진다
    * 인자가 하니일 경우 onNext를 처리하며 에러가 생일 경우 위와 동일
    * 인자가 둘일 경우 onNext를 처리하며 에러가 생길 경우 해당 동작 수행
    * 인자가 셋일경우 onNext,onError,onComplete 모두 처리
    * */
    /*
    * Disposed 함수의 활용, 더이상 데이터를 발행하지 않도록 하는 함수이다.
    * complete 가 호출되는 경우 자동으로 disposed를 호출하여 구독자와의 관계를 끊는다.
    * */
    println("Disposed : ${isDisposed.isDisposed}")

}
fun createExample(){
    /**/
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
    val observableData = Observable.create<Int> { emitter ->
        run {
            emitter.onNext(100)
            emitter.onNext(200)
            emitter.onNext(300)
            emitter.onComplete()
        }
    }

    println(observableData.subscribe(System.out::println).isDisposed)
}

fun fromArrayTest(){
    /*
    * 단일 데이터가 아닌 경우
    *
    * 코틀린의 경우는 fromArray를 사용하려는 경우 문제점이 발생한다.
    * RxKotlin 이라는 Rx확장 소스를 이용하는 것을 권장하고 있는 듯 하다.
    *
    * Java에서는 int[] 배열과 같은 경우는 Intrager[] 형식으로 변환 하여야 했다.
    * Array타입은 int[] 로 변환되어 사용을 할 수 없다.
    *
    * Rx 환경에서 fromArray를 원활히 사용하려면 조금 더 연구가 필요할 듯 하다.
    * */
    val array = List(3){i->i+1}

    val observableData = Observable.fromArray(array)


    //observableData.subscribe({println(it)})
    observableData.subscribe(System.out::println)
}
fun fromIterableTest(){
    /*
    * Iterable 데이터를 발행하려는 경우
    * */

    var list = arrayListOf<String>()
    list.add("one")
    list.add("two")
    list.add("three")

    val observableData = Observable.fromIterable(list)
    observableData.subscribe(System.out::println)
}

fun fromCallableTest(){

    /*
    * kotlin에서 Callable을 미리 선언해 놓는 방법은 어떻게 해야 할 지 모르겠다.
    * 아래의 선언은 Java의 Callable을 받은 것이다.
    * */
    /*
     * 비동기 작업을 할 수 있는 Callable을 받아 처리하는 클레스
     * callable 객체를 그대로 대입한 뒤 해당 callable을 실행한 뒤
     * 해당 반환값을 그대로 받아 사용할 수 있도록 되어있다.
     * */

    val callable = Callable<String> {
        Thread.sleep(1000)
        "Hello Callable"
    }

    val observableData = Observable.fromCallable(callable)


    observableData.subscribe(System.out::println)

}

fun fromFutureTest(){
    /*
 * 동시성 객체
 * 주로 비동기적 계산의 결과를 구할 때 사요한다.
 * Excutor 인터페이스를 구현한 뒤 Callalble 객체를 인자로 넣는다.
 * get메서드 호출시 Callalbe의 계산 결과가 나오기 전까지 기다린다(블로킹)
 * */
    val callable = Callable<String> {
        Thread.sleep(1000)
        "Hello Future"
    }

    val future:Future<String> = Executors.newSingleThreadExecutor().submit(callable)
    val observableData = Observable.fromFuture(future)
    observableData.subscribe(System.out::println)
}
fun fromPublisher(){
    /*
    * 자바 9 에서 새로 발표한 Flow API의 일부분
    * */

    val publisher:Publisher<String> = Publisher { s ->
        run {
            s.onNext("Hello Observaler.FromPublisher")
            s.onComplete()
        }
    }
    val source = Observable.fromPublisher(publisher)
    source.subscribe(System.out::println)
}

fun singleTest(){
    // 왜 인지는 잘 모르겠지만 single에서는 System.out::print 이 작동하지 않아 아해와 같이 했다. 또한 인자가 하나가 아니기에 it도 사용 할 수 없다.
    // 다양한 방식으로 single 클레스를 구현하는 것이 가능하다.

    /*
    * Single 클레스는 하나의 데이터만을 발행하는 클레스이다.
    * 결과가 유일한 서버 API를 호출하는 대 쓰인다.
    * */

    // Single을 선언하여 일반 Observable 처럼 발행 할 수 있다.
    val source0 = Single.just("I'm single")
    source0.subscribe { t: String? -> println(t) }
    source0.subscribe({value-> println(value)})

    /*
        * Single은 Observable의 특수한 형태이기에 Observable에서 변환할 수 있다.
        * */
    //1. Observable -> Single
    // 첫번쨰 값을 Single로 발행한다 인자가 둘 이상 들어 있는 경우 에러 발생
    val source1 = Observable.just("Hello Single");
    Single.fromObservable(source1).subscribe({t -> println("source1 : $t" ) })

    //2. single() 메서드를 호출하여 생성
    // Obserbale 인자가 없는 경우 디폴트 벨류가 발행
    Observable.just("I'm singgle").single("default").subscribe(Consumer { println(it) })

    //3. first함수를 호출하여 생성
    //여러 데이터를 발행 할 수 있는 Observable을 Single로 발행 한다. 여러 데이터가 있는 경우에도 첫 데이터 발행 이후 onSuccess
    val numbers = listOf("one","two","three")
    Observable.fromIterable(numbers).first("default").subscribe({ value -> println("first is $value") })

    //4. empty Observable 에서 객체 생성
    //아무 데이터가 발행하지 않는 경우 디폴트 값이 출력된다.
    Observable.empty<String>().single("defalut").subscribe(Consumer { println(it) })

    //5. take()함수에서 Single 객체 생성
    //take(2) 이런식으로 사용한다고 456 이 나오지는 않는다. 에러가 발생한다.
    Observable.just("123","456","789").take(1).single("000").subscribe(Consumer { println("take $it") })
}

fun asyncSubjectTest(){
    /*
       * Subject : 차가운 Observable을 뜨거운 Observable로 변환 시켜주는 클레스
       * Observable 과 같이 여러 클레스들을 제공하여 다른 상황을 처리 할 수 있다.
       */
    // AsyncSubject : 마직막으로 발행 한 데이터를 가져오는 클래스, 언제 호출하던 마지막 데이터만 처리한다.
    // 만일 complete를 명시하지 않는다면 호출하지 못한다.
    // 또한 complete 이후의 값들은 모두 무시한다.
    //정확히는 onComplete() 가 호출되기 전 마지막 값을 발행한다.
    val asyncSubject = AsyncSubject.create<String>()
    asyncSubject.subscribe({ println("Subscriber #1 : $it") })
    asyncSubject.onNext("1")
    asyncSubject.onNext("3")
    asyncSubject.subscribe({ println("Subscriber #2 : $it") })
    asyncSubject.onNext("5")
    asyncSubject.onComplete()

    // subscrobe 를 통해 동작하는 AsyncSubject
    //Subject 클레스는 Obervable 클레스를 상속받아 구현되어 있기에 이런 방식의 사용이 가능하다.
    val temperature = listOf(10.1f,13.4f , 12.5f)
    val source = Observable.fromIterable(temperature)
    val subject = AsyncSubject.create<Float>()
    subject.subscribe({println("Subscriber #1 : $it")})

    source.subscribe(subject)
}

fun behaviorSubject(){
    /*
* 구독자가 구독을 하면 가장 최근의 값, 혹은 기본값을 넘겨주는 클레스이다.
* */
   val behaviorSubjecr = BehaviorSubject.createDefault("6")
    behaviorSubjecr.subscribe({println("Subscriber #1 : $it")})
    behaviorSubjecr.onNext("1")
    behaviorSubjecr.onNext("3")
    behaviorSubjecr.subscribe({println("Subscriber #2 : $it")})
    behaviorSubjecr.onNext("5")
    behaviorSubjecr.onComplete()
}

fun publishSubject(){
    /*
   * 구독자가 발행을 시작하면 해당 시간부터 발행을 시작한다.
   * 기본값은 없다.
   * */
    val subject = PublishSubject.create<String>()
    subject.subscribe({println("Subscribe #1 : $it")})
    subject.onNext("1")
    subject.onNext("3")
    subject.subscribe({println("Subscribe #2 : $it")})
    subject.onNext("5")
    subject.onComplete()
}
fun replaysubject(){
    /*
     * 차가운 Observable 처럼 동작하는 Subject
     * 구독자가 생기면 처음부터 끝까지 모두 발행한다.
     * */
    val subject = ReplaySubject.create<String>()
    subject.subscribe({println("Subscribe #1 : $it")})
    subject.onNext("1")
    subject.onNext("3")
    subject.subscribe({println("Subscribe #2 : $it")})
    subject.onNext("5")
    subject.onComplete()
}

fun connectableObservable(){
    /*
    *차가운 Observable 을 뜨거운 Observable로 변환해 준다
     * 원 데이터 하나른 여러 사용자에게 전달 할 떄 사용한다.
     * connect 함수를 호출하면 그 전까지 구독한 모든 구독자에게 데이터를 발행한다.
    * connect 이후의 구독자는 구독 이후에 발생한 데이터가 발행된다.
    *
    * */
    val data = listOf("1","2","3")
    val balls = Observable.interval(100L,TimeUnit.MILLISECONDS).map(Long::toInt).map({data[it]}).take(data.size.toLong())

    val source = balls.publish()

    source.subscribe({println("Subscriber #1 : $it")})
    source.subscribe({println("Subscriber #2 : $it")})
    source.connect()

    Thread.sleep(250)
    source.subscribe({println("Subscriber #3 : $it")})
    Thread.sleep(100)

}