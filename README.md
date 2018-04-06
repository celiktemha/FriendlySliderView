# FriendlySliderView
User friendly slider widget for Android

Inspired by https://github.com/Ramotion/fluid-slider-android



<img src="https://github.com/celiktemha/FriendlySliderView/blob/master/FriendlySilider.gif" width="317" height="566"/>

# Usage

Gradle:
```groovy
implementation 'com.celiktemha:friendlysliderview:1.0.1'
```

Maven:
```xml
<dependency>
  <groupId>com.celiktemha</groupId>
  <artifactId>friendlysliderview</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

```xml
//XML
<com.celiktemha.friendlysliderview.FriendlySliderView
        android:id="@+id/friendlySliderView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="center"
        android:layout_marginLeft="10dp"
        android:layout_marginRight="10dp" />
```

```java
FriendlySliderView friendlySliderView = findViewById(R.id.friendlySliderView);

friendlySliderView.setCurrentValue(50);
friendlySliderView.setMinValue(0);
friendlySliderView.setMaxValue(100);

friendlySliderView.setOnSliderValueChangeListener(new OnSliderValueChangeListener() {
  @Override
  public void onSliderValueChanged(int value) {
    Toast.makeText(MainActivity.this, "" + value, Toast.LENGTH_SHORT).show();
  }
});

```
# Attributes
* `sliderBackgroundColor`.
* `sliderCircleColor`
* `sliderCircleTextColor`
* `sliderTextColor`
* `minValue`
* `maxValue`
* `currentValue`
