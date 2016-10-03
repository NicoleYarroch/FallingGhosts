package com.nicolelivioradiocom.omg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;

class Enemy {
    int xPos;
    int yPos;
    int yVelocity;
    Bitmap image;

    Enemy(int xPos, int yVelocity, Bitmap image) {
        this.xPos = xPos;
        this.yPos = 0;
        this.yVelocity = yVelocity;
        this.image = image;
    }
}

// http://gamecodeschool.com/android/building-a-simple-game-engine/
public class GameActivity extends AppCompatActivity {

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gameView = new GameView(this);
        setContentView(gameView);
    }

    public class GameView extends SurfaceView implements Runnable {

        // Thread
        Thread gameThread = null;

        // Necessary for Canvas and Paint
        SurfaceHolder ourHolder;

        // Keeps track if game is running
        volatile boolean playing;

        // Canvas for drawing
        Canvas canvas;
        Paint paint;

        // Tracks game frame rate - different devices have varying fps
        long fps;
        private long timeThisFrame;

        // The character
        Bitmap character;

        // Is the character moving?
        boolean isMoving = true;

        // Initial x-position
        float characterXPosition = 10;
        float characterYPosition = 150;

        float mouseX = 0;
        float mouseY = 0;
        double easingAmount = 0.15;
        float rotateAngle = 0;

        private int xVelocity = 10;
        private int yVelocity = 5;

        ArrayList<Enemy> enemies = new ArrayList<Enemy>();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        public GameView(Context context) {
            super(context);

            // Init
            ourHolder = getHolder();
            paint = new Paint();

            // Load image
            character = BitmapFactory.decodeResource(this.getResources(), R.drawable.ghost);

            // Enemies
            // Init enemies
            for (int i = 0; i < 10; i += 1) {
                Bitmap turkey = BitmapFactory.decodeResource(this.getResources(), R.drawable.turkey_big);
                Enemy enemy = new Enemy(50, 2, turkey);
                this.enemies.add(enemy);
            }
        }

        @Override
        public void run() {

            while (playing) {
                long startFrameTime = System.currentTimeMillis();

                // Get character's new position
                update();

                // Draw background, character, text, etc...
                draw();

                // Calculate fps (keeps animation consistent for different devices)
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        public void update() {
            if (isMoving) {

                float xDistance = mouseX - characterXPosition;
                float yDistance = mouseY - characterYPosition;

                double distance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
                if (distance > 1) {
                    characterXPosition += xDistance * easingAmount;
                    characterYPosition += yDistance * easingAmount;
                }

                rotateAngle = (float) (Math.atan2((getHeight() / 2) - mouseY, (getWidth() / 2) - mouseX) - (Math.PI / 2));

                for (int i = 0; i < 10; i += 1) {
                    Enemy frenemy = enemies.get(i);
                    frenemy.yPos += frenemy.yVelocity;
                    enemies.set(i, frenemy);
                }

//                wm.getDefaultDisplay().getMetrics(displayMetrics);
//                int screenWidth = displayMetrics.widthPixels;
//                int screenHeight = displayMetrics.heightPixels;
//
//                if (characterXPosition > (screenWidth + character.getWidth())) {
//                    xVelocity *= -1;
//                }
//                if (characterXPosition < (0 - character.getWidth())) {
//                    xVelocity *= -1;
//                }
//                if (characterYPosition > (screenHeight + character.getHeight())) {
//                    yVelocity *= -1;
//                }
//                if (characterYPosition < (0 - character.getHeight())) {
//                    yVelocity *= -1;
//                }
//
//                characterXPosition += xVelocity;
//                characterYPosition += yVelocity;

//                if (characterXPosition > 1000) {
//                    xVelocity *= -1;
//                }
//                else if (characterXPosition < 0) {
//                    xVelocity *= -1;
//                }
//
//
//                characterXPosition = characterXPosition + (xVelocity / fps) ;
                // characterYPosition += yVelocity;



                // characterXPosition += walkSpeedPerSecond / fps;
                // characterYPosition += walkSpeedPerSecond / fps;
                // System.out.println("x-pos: " + characterXPosition);
            }
        }

        public void turkeys() {

        }


        public void draw() {
            if (ourHolder.getSurface().isValid()) {

                // Lock the canvas
                canvas = ourHolder.lockCanvas();

                // Background color
                canvas.drawColor(Color.argb(255, 26, 128, 182));

                // Brush color for drawing
                paint.setColor(Color.argb(255, 249, 129, 0));

                // Text
                paint.setTextSize(90);
                canvas.drawText("FPS: " + fps, 20, 100, paint);

                // Character
                canvas.save(Canvas.MATRIX_SAVE_FLAG); //Saving the canvas and later restoring it so only this image will be rotated.
                canvas.rotate(rotateAngle, canvas.getWidth() / 2, canvas.getHeight() / 2);
                canvas.drawBitmap(character, characterXPosition, characterYPosition, paint);
                canvas.restore();
                // canvas.drawBitmap(character, characterXPosition, characterYPosition, paint);

                // Enemy
                for (int i = 0; i < 10; i += 1) {
                    Enemy enemy = enemies.get(i);
                    canvas.drawBitmap(enemy.image, enemy.xPos, enemy.yPos, paint);
                }

                // Unlock the canvas
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void pause() {
            System.out.println("destroying thread");
            playing = false;
            try {
                // Main thread waits for the game thread to end
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }
        }

        public void resume() {
            System.out.println("created thread");
            playing = true;

            // Create a new game thread and start it
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    isMoving = true;
                    break;
                case MotionEvent.ACTION_UP:
                    isMoving = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    mouseX = motionEvent.getX();
                    mouseY = motionEvent.getY();
                    break;
            }
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
}
