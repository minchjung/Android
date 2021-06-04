package com.example.threadfragment03;

public abstract class Result<T> {
    private Result() {}

    public static final class Success<T> extends Result<T> {
        public T data; // << Generater 로 외부 타입 injection (의존성)

        public Success(T data) { // 상위 클래스를 상속한 내부 클래스
            this.data = data;
        }
    }
    public static final class Error<T> extends Result<T> {
        public Exception exception;

        public Error(Exception exception) {
            this.exception = exception;
        }
        // 성공시 성공 클래스 접근해, 결과물인 data를 get할 수 있고
        // 실패시 Error 클래스 접근해, 예외 처리를 할 수 있다. ==>ThredRepo 에서 구체화 (상속X)
    }
}