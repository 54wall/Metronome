package pri.weiqiang.metronome.dagger;

import javax.inject.Singleton;

import dagger.Component;
import pri.weiqiang.metronome.core.Metronome;
import pri.weiqiang.metronome.services.AudioService;
import pri.weiqiang.metronome.ui.MainActivity;


@Component(
        modules = AppModule.class
)
@Singleton
public interface AppComponent {

    void inject(MainActivity mainActivity);

    void inject(AudioService service);

    Metronome getMetronome();

}
