# SwipeFinishableActivity

# What's this?

This library can help you build an Android project with an iOS Navigation Pages like UI style. You can swipe the activity to finish it just like WeChat app does.  

![](arts/swipefinishable.gif)

# How to Use?

0. Setup your dependencies in gradle:

    ```
     api "com.bennyhuo.swipefinishable:swipefinishable:1.0-rc"
    ```

1. Config application. You can use ```com.bennyhuo.swipefinishable.SwipableSupportedApplication``` directly in your AndroidManifest.xml. Customized application is also supported and easy to implement, if you want to extend other sub classes of Application. Just follow DemoApplication, and create your own:

	```java
	public class DemoApplication extends Application {
	    public static final String TAG = "DemoApplication";
	
	    @Override
	    public void onCreate() {
	        super.onCreate();
	        SwipeFinishable.INSTANCE.init(this);
	    }
	}
	```
2. Create your Activities implementing ```SwipeFinishableActivity```. All you need to do is overriding method `finish` and implementing `finishThisActivity`. You can refer to ```DetailActivity``` to work on your own or just simply extend ```BaseSwipeFinishableActivity```.

	```java
	public class DetailActivity extends Activity implements SwipeFinishableActivity {

	    @Override
	    public void finishThisActivity() {
	        super.finish();
	    }
	
	    @Override
	    public void finish() {
	        SwipeFinishable.INSTANCE.finishCurrentActivity();
	    }
	}

	```
3. Config the theme of Activities. To make the second top activity visible when swipe the top activity a little, we should make the top one translucent.

	```xml
	<activity android:name=".DetailActivity" android:configChanges="orientation" android:theme="@style/AppTranslucentTheme"/>
	```
	
	>Tips: If you apply your theme in code via the method `Activity.setTheme`, you will miss the support of ActivityRecord, i.e. you can't see the last Activity when swiping the current one.
	
4. Navigate to `SwipeFinishableActivity` with `SwipeFinishable.INSTANCE.startActivity(intent);` or call `Activity.overridePendingTransition(0, 0)` after starting the Activity directly.
	
Done!Hope you enjoy it ~

# Known Issues

1. Since we have no official access to the Activity Task Stack, any recreation of activities may affect the order of the stack we maintained in the SwipeFinishable. For example, if your app supports arbitrary orientations, you should add this configuration to any of your activities in the manifest:
      ``` xml
      android:configChanges="orientation"
      ```

2. ActionBar is not supported and I don't have any plan to work on it. You can just implement an action bar in your own layout.
3. If the theme of the LauncherActivity of your app applied is **Translucent**, the **Recent Activities**(You can see this when you press the **OverView** button besides **Home** button) may looks weird on Google Nexus 6[6.0.1]. This may relate to some bugs in Android System itself, but you can use a non-translucent Activty to avoid this problem.

	![](arts/bugs_in_recents.gif)

# Compatibility

This project has been developed and tested on Samsung S6 [5.0.1], Google Nexus 6 [6.0.1] and HUAWEI P10[8.0.0]. If you have any problems, feel free to issue.




