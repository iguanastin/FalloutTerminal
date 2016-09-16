package gui;

/**
 * Created by Austin on 9/10/2016.
 */
public interface SliderListener {
    void sliderDragged(Slider slider, int value);

    void sliderDropped(Slider slider, int value);
}
