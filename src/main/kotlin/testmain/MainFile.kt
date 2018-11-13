package testmain

import io.reactivex.Observable

fun main(args:Array<String>){
    val emit = {Observable.just("Hello","RxJKotlin").subscribe(System.out::println)}
    emit()
}
