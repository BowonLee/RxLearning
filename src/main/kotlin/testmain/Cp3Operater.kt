package testmain

import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.Observables


val data = listOf(1,2,3,4,5)
fun main(args:Array<String>){
//    mapOperater()
//    flatMapOperater()
    gugudanInRx()
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
