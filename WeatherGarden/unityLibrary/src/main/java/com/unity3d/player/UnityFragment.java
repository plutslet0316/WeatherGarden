package com.unity3d.player;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UnityFragment extends Fragment{
    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    FrameLayout frameLayout;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_unity, container, false);
        mUnityPlayer = new UnityPlayer(view.getContext());
        frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout_unity);
        frameLayout.addView(mUnityPlayer.getView());
        mUnityPlayer.requestFocus();
        mUnityPlayer.windowFocusChanged(true);

        return frameLayout;
    }

    @Override
    public void onDestroy() {

        mUnityPlayer.quit();
//        Process.killProcess(Process.myPid());

        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        mUnityPlayer.pause();
    }

    @Override
    public void onStart() {
        super.onStart();
        mUnityPlayer.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mUnityPlayer.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mUnityPlayer.resume();
    }
}
