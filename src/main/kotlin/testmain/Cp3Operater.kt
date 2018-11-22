package testmain

import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.Observables


val data = listOf(1,2,3,4,5)
fun main(args:Array<String>){
//    mapOperater()
//    flatMapOperater()
//    gugudanInRx()
    filterOperater()

}


fun mapOperater(){
    val source1 = Observable.fromIterable(data)
    source1.map { "$it + <>" }
    source1.subscribe({ println(it) })


    val indexToData:(Int)->String = {
        when(it){
            1 -> "data1"
            2 -> "data2"
            3 -> "data3"
            4 -> "data4"
            else -> "None"
        }
    }

    // map의 경우는 따로 때어 놓으면 동작하지 않는 것을 확인 할 수 있다.
    val source2 = Observable.fromIterable(data).map(indexToData)
    source2.subscribe(System.out::println)

}



fun flatMapOperater(){
    val getTwoDiamond : (Int)->Observable<String> = {Observable.just("$it <>","$it <>")}
    val source = Observable.fromIterable(data).flatMap(getTwoDiamond)
    source.subscribe(System.out::println)
}

fun gugudanInRx(){
    val numbers =IntRange(1,9)

    val makeRow: (Int) -> Observable<String> = { num->Observable.range(1,9).map { "$num * $it = ${num*it}" } }


    val source = Observable.fromIterable(numbers).flatMap(makeRow)
    source.subscribe(System.out::println)
}
fun filterOperater(){
    val objs = listOf("1 CIRCLE","2 DIAMOND","3 TRIANGLE","4 DIAMOND","5 CIRCLE","6 HEXAGON")

    val source1 = Observable.fromIterable(objs).filter({it.endsWith("CIRCLE")})
    source1.subscribe(System.out::println)

    val data = listOf(1,2,3,4,5,6,7,8,9,10)
    val source2 = Observable.fromIterable(data).filter({it%2==0})
    source2.subscribe({println(it)})

    //first()       첫번쨰 값을 리턴, 값이 없을경우 기본 값 -> Single객체에서 사용된다.
    //              반드시 하나의 값만을 가지기 때문에 이와같이 사용하는 듯 하다.
    val source3 = Observable.fromIterable(data).first(0)
    source3.subscribe(Consumer { println("first() : \t: $it") })
    //last          마지막 값을 리턴, 값이 없을경우 기본 값 -> 처음, 끝의 차이는 있지만 이유는 같다.
    val source4 = Observable.fromIterable(data).last(0)
    source4.subscribe(Consumer { println("last() : \t: $it") })
    //take(N)       최초N개 만큼의 값을 리턴
    val source5 = Observable.fromIterable(data).take(3)
    source5.subscribe({ println("take() : \t: $it") })
    //              takeLast(N)마지막 N개만큼의 값을 리턴
    val source6 = Observable.fromIterable(data).takeLast(3)
    source6.subscribe({ println("takeLastt() : \t: $it") })
    //skip(N)       최초 N개 만큼의 값을 제외하고 리턴
    val source7 = Observable.fromIterable(data).skip(3)
    source7.subscribe({ println("skip() : \t: $it") })
    //skipLast(N)   마지막 N개 만큼의 값을 제외하고 리턴
    val source8 = Observable.fromIterable(data).skipLast(3)
    source8.subscribe({ println("skipLast() : \t: $it") })

}
