package testmain;

import io.reactivex.Observable;
import io.reactivex.functions.Function;



/*
 * 리엑티브의 연산자.
 * 자바의 관점에서는 메서드/함수
 * 함수형언어의 관점에서는 순수함수 이다.
 *
 * 생성            :   데이터의 흐름을 만들어내는 함수 데이터의 발행을 시작하는 함수 -> just(),create() fromXXX() 부터 interval, range, deper 등
 * 변환            :   어떤 입력을 받아 원하는 출력 결과를 내는 함수 -> map, flatMap
 * 필터            :   입력 데이터 중 원하는 데이터만 걸러내는 함수
 * 합성            :   여러 Observable을 조합하는 경우,
 * 오류처리        :   에러 상황에서의 처리
 * 유틸리티        :   비동기 프로그레밍을 지원한다
 * 조건            :   Obervable의 흐름을 제어한다.
 * 수학, 집합형    :   각종 수식을 지원한다.
 * 배압            :   배압 이슈에 대응
 * */

public class Cp3Operater {
    private  Integer[] datas = {1,2,3,4,5};

    public static void main(String args[]) {
        Cp3Operater cp3Operater = new Cp3Operater();
        //cp3Operater.mapOperater();
        //cp3Operater.flatMapOperater();
        cp3Operater.gugudanInRx();
    }


    /*
    * 특정 데이터를 입력받아 원하는 값으로 변환하는 함수
    * map은 함수를 인자로 받는 고차함수이다.
    * 해당 Observalble은
    * 인자로 받은 함수의 동작대로 작업을 수행한 결과를 발행한다.
    * */
    private void mapOperater(){
        Observable source1 = Observable.fromArray(datas);
        source1.map(data->data + "#");
        source1.subscribe(System.out::println);


        Function<Integer, String> indexToValue = data ->{
          switch (data){
              case 1 : return "Data1";
              case 2 : return "Data2";
              case 3 : return "Data3";
              case 4 : return "Data4";
              default:return "None";
          }
        };

        Observable<String> source2 = Observable.fromArray(datas).map(indexToValue);
        source2.subscribe(System.out::println);
    }

    /*
    * 일대일로 대응하는 것이 아닌 일대다, 혹은
    * Observalble 자체를 반환 할수도 있는 함수
    * 반환 타입일 Observable이라는 의미는 이해가 힘들 수 있다.
    * */
    private void flatMapOperater(){
        Function<Integer,Observable<String>> getTwoDiamond = data-> Observable.just(data + "<>", data + "<>");
        Observable<String> source1 = Observable.fromArray(datas).flatMap(getTwoDiamond);
        source1.subscribe(System.out::println);
    }

    // 반복문을 사용하지 않기
    private void gugudanInRx(){
        // 배열을 통해 1~9까지의 숫자를 차례대로 입력 받는다.
        Integer[] numbers = {1,2,3,4,5,6,7,8,9};

        // 입력받은 숫자 * 1~9 까지를 발행 할 수 있는 Function
        Function<Integer,Observable<String>> makeRow = num ->
                Observable.range(1,9).map(i ->  String.format("%d * %d = %d \t",num,i,num*i)
                );

        // 각 숫자를 fromArray를 통해 입력한다. 또한 flatMap을 사용하여 Observable을 리턴하는 Function을 인자로 받는다.
        Observable<String> source1 = Observable.fromArray(numbers).flatMap(makeRow);
        source1.subscribe(System.out::println);
        // 각각 단은 row로 출력하고, line마다 개행을 하도록 할 수는 없을까.

    }
}
