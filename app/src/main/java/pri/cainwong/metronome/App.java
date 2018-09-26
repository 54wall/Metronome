package pri.cainwong.metronome;

import android.app.Application;
import android.content.Context;

import pri.cainwong.metronome.dagger.AppComponent;
import pri.cainwong.metronome.dagger.AppModule;
import pri.cainwong.metronome.dagger.DaggerAppComponent;

import pri.cainwong.metronome.dagger.DaggerAppComponent;

/**
 * Created by cwong on 10/15/15.
 */
public class App extends Application {

    private AppComponent component;

    public static AppComponent component(Context context) {
        return ((App) context.getApplicationContext()).component;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

}
