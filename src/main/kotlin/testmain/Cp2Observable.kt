package testmain

import io.reactivex.Observable

fun main(args:Array<String>){

    /*
    * just() : 순서대로 발행 , 같은 타입, 인자의 수는 1~10
    * */
    val observableData = Observable.just(1,2,3,4,5)
    observableData.subscribe({println(it)})
    observableData.subscribe({println(it)},{println("err")})
    observableData.subscribe({println(it)},{println("err")}, { println("complete")})


    

}