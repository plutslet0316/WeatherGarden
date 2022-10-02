package com.unity3d.player;

import android.content.Context;
import android.os.Process;

public class MyUnityPlayer extends UnityPlayer {


    public MyUnityPlayer(Context context) {
        super(context);
    }

    public MyUnityPlayer(Context context, IUnityPlayerLifecycleEvents iUnityPlayerLifecycleEvents) {
        super(context, iUnityPlayerLifecycleEvents);
    }

    @Override
    protected void kill() {
        super.kill();
        Process.killProcess(Process.myPid());
    }
}
