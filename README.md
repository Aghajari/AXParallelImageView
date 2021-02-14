# AXParallelImageView
 Parallel Line Collage ImageView

<img src="./images/AXParallelImageView_TL.gif" width=300 title="AXParallelImageView">  <img src="./images/AXParallelImageView_L.gif" width=300 title="AXParallelImageView">

## Installation

AXParallelImageView is available in the JCenter, so you just need to add it as a dependency (Module gradle)

Gradle
```gradle
implementation 'com.aghajari.parallelimageview:AXParallelImageView:1.0.0'
```

Maven
```xml
<dependency>
  <groupId>com.aghajari.parallelimageview</groupId>
  <artifactId>AXParallelImageView</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

# Usage
Add the AXParallelImageView to your layout:

```xml
<com.aghajari.parallelimageview.AXParallelImageView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:background="@drawable/image1"
    app:image="@drawable/image2"
    app:direction="TOP_LEFT"
    app:maxStroke="40dp"
    app:minStroke="2dp"
    app:maxLinesCount="8"
    app:strokePeriod="4"
    app:duration="800"/>
```

That's all you need! If you don't want the AXPowerView to automatically start animating, omit the app:autoStart option and start it manually yourself:

```java
AXParallelImageView view = findViewById(R.id.AXParallelImageViewID);
view.start();
//view.stop();
```

## Directions
- TOP_LEFT
- BOTTOM_RIGHT
- TOP_RIGHT
- BOTTOM_LEFT
- LEFT
- RIGHT
- TOP
- BOTTOM

## XML attributes

| Name | Type | Default | Description |
|:----:|:----:|:-------:|:-----------:|
| image | DrawableRes | NULL | the photo behind the lines |
| grayImage | DrawableRes | NULL | the photo behind the lines with grayscale effect |
| minStroke | dimension | 18dp | min stroke |
| maxStroke | dimension | 36dp | max stroke |
| strokePeriod | integer | 6 | period of division stroke between lines |
| maxLinesCount | integer | 6 | maximum number of lines displayed on the view |
| startPosition | dimension | 0 | start position |
| duration | integer | 500 | the duration of moving a line |
| autoStart | boolean | true | Whether the view should automatically start animating once it is initialized. |
| direction | Direction | TOP_LEFT | line's direction |

## Public Methods

| Name | Description |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| start() | Start Animation |
| stop() | Stop Animation |
| isRunning() | Check whether animation is running |
| setDirection(Direction) | Sets the direction |
| getDirection() | Returns the direction |
| setStartPosition(float) | Sets start position |
| setStrokePeriod(int) | Sets period of division stroke between lines |
| getStrokePeriod() | Gets period of division stroke between lines |
| setMaxLinesCount(int) | Sets maximum number of lines displayed on the view |
| getMaxLinesCount() | Gets maximum number of lines displayed on the view |
| setMinStroke(float) | Sets min stroke |
| getMinStroke() | Gets min stroke |
| setMaxStroke(float) | Sets max stroke |
| getMaxStroke() | Gets max stroke |
| setParallelLineImage(Bitmap) | Sets the photo behind the lines |
| setParallelLineImage(int) | Sets the photo behind the lines |
| setGrayscaleParallelLineImage(Bitmap) | Sets the photo behind the lines with grayscale effect |
| setGrayscaleParallelLineImage(int) | Sets the photo behind the lines with grayscale effect |
| getParallelLineImage() | Returns the photo behind the lines |

## Author 
- **Amir Hossein Aghajari**

License
=======

    Copyright 2021 Amir Hossein Aghajari
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


<br><br>
<div align="center">
  <img width="64" alt="LCoders | AmirHosseinAghajari" src="https://user-images.githubusercontent.com/30867537/90538314-a0a79200-e193-11ea-8d90-0a3576e28a18.png">
  <br><a>Amir Hossein Aghajari</a> • <a href="mailto:amirhossein.aghajari.82@gmail.com">Email</a> • <a href="https://github.com/Aghajari">GitHub</a>
</div>
