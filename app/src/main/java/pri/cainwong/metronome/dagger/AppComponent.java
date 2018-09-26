package pri.cainwong.metronome.dagger;

import javax.inject.Singleton;

import dagger.Component;
import pri.cainwong.metronome.core.Metronome;
import pri.cainwong.metronome.services.AudioService;
import pri.cainwong.metronome.ui.MainActivity;


@Component(
        modules = AppModule.class
)
@Singleton
public interface AppComponent {

    void inject(MainActivity mainActivity);

    void inject(AudioService service);

    Metronome getMetronome();

}
