package com.vjit.vjittransit;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        changeStatusBarColor();
        LogoLancher logoLancher = new LogoLancher();
        logoLancher.start();
    }

    private class LogoLancher extends Thread {
        public void run() {
            try {
                sleep(3500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(splashscreen.this, LoginActivity.class));
            Animatoo.animateSlideLeft(splashscreen.this);
            splashscreen.this.finish();
        }
    }


    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
            window.setNavigationBarColor(getResources().getColor(R.color.white));
        }
    }

}
