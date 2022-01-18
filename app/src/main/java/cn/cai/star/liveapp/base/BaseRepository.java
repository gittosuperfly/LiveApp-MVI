package cn.cai.star.liveapp.base;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BaseRepository {

    protected <T> Observable<T> composeRequest(Observable<T> observable) {
        return observable.compose(apiIoToMain());
    }

    private static <T> ObservableTransformer<T, T> apiIoToMain() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
