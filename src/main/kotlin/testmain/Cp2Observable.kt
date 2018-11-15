package testmain

import io.reactivex.Observable
import org.reactivestreams.Publisher
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.reflect.KCallable


fun main(args:Array<String>){

    //justExample()
    //createExample()
    //fromArrayTest()
    //fromIterableTest()
    //fromCallableTest()
    fromFutureTest()
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
