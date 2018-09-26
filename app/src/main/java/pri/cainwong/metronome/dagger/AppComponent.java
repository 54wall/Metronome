package pri.cainwong.metronome.dagger;

import pri.cainwong.metronome.core.Metronome;
import pri.cainwong.metronome.services.AudioService;
import pri.cainwong.metronome.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;


@Component(
        modules = AppModule.class
)
@Singleton
public interface AppComponent {

    void inject(MainActivity mainActivity);

    void inject(AudioService service);

    Metronome getMetronome();

}
