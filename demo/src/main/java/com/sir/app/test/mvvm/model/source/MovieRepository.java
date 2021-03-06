package com.sir.app.test.mvvm.model.source;

import com.sir.app.test.entity.MovieResult;
import com.sir.app.test.mvvm.contract.MovieContract;
import com.sir.app.test.mvvm.model.Repository;
import com.sir.library.retrofit.download.DownLoadSubscriber;
import com.sir.library.retrofit.download.ProgressCallBack;
import com.sir.library.retrofit.exception.ResponseThrowable;
import com.sir.library.retrofit.transformer.ComposeTransformer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by zhuyinan on 2019/6/24.
 */
public class MovieRepository extends Repository implements MovieContract {

    public static String EVENT_KEY_LIVE = null;
    public static String EVENT_PROGRESS = null;

    public MovieRepository() {
        if (EVENT_KEY_LIVE == null) {
            EVENT_KEY_LIVE = getEventKey();
        }

        if (EVENT_PROGRESS == null) {
            EVENT_PROGRESS = getEventKey();
        }
    }

    @Override
    public void getMovie(String city) {
        postState(ON_LOADING, "开始加工....");
        addSubscribe(apiService.getMovieB("eff63ec0285b079f8fe418a13778a10d", city)
                .compose(ComposeTransformer.<MovieResult>Observable())
                .subscribe(new Consumer<MovieResult>() {
                    @Override
                    public void accept(MovieResult movieResult) {
                        postData(EVENT_KEY_LIVE, movieResult);
                        postState(ON_SUCCESS);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        ResponseThrowable ex = (ResponseThrowable) throwable;
                        postState(ON_FAILURE, ex.message);
                    }
                }));
    }

    @Override
    public void downloadFile(String fileUrl, final ProgressCallBack callBack) {
        apiService.downloadFile(fileUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) {
                        callBack.saveFile(responseBody);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DownLoadSubscriber<ResponseBody>(callBack));
    }

    @Override
    public void uploadFile(String filePath) {

    }
}
