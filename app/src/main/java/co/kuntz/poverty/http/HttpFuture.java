package co.kuntz.poverty.http;

public abstract class HttpFuture<T> {
    public abstract void onSuccess(T thing);

    public abstract void onFailure(String responseString, Throwable t);
}
