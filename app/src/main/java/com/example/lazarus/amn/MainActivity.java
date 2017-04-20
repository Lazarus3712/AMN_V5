package com.example.lazarus.amn;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mn.amn.R;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends Activity {
    Handler hand = new Handler();
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothSocket clientSocket = null;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                clientSocket.getOutputStream().write(120);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            hand.postDelayed(runnable, 1000);
        }
    };
    private PowerManager pm;
    private PowerManager.WakeLock pmw;
    private ToggleButton bluet;
    private Button run;
    private Button back;
    private Button left;
    private Button right;
    private Button run_right;
    private Button run_left;
    private Button back_left;
    private Button back_right;
    private Button five;
    private Button seven;
    private Button one;
    private Button beep;
    private TextView speed;
    private boolean conect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        run = (Button) findViewById(R.id.UP);
        back = (Button) findViewById(R.id.DOWN);
        left = (Button) findViewById(R.id.LEFT);
        right = (Button) findViewById(R.id.RIGTH);

        run_left = (Button) findViewById(R.id.UL);
        run_right = (Button) findViewById(R.id.UR);
        back_left = (Button) findViewById(R.id.DL);
        back_right = (Button) findViewById(R.id.DR);

        beep = (Button) findViewById(R.id.beep);

        speed = (TextView) findViewById(R.id.value);

        five = (Button) findViewById(R.id.btn_five);
        seven = (Button) findViewById(R.id.btn_seven);
        one = (Button) findViewById(R.id.btn_one);
        bluet = (ToggleButton) findViewById(R.id.work);

        try {
            Event();

            pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            pmw = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "AMN");
            pmw.acquire();

        } catch (Exception e) {
            Toast.makeText(this, "Is Connection: "
                    + clientSocket.isConnected()
                    + " or something else.", Toast.LENGTH_SHORT).show();
        }
    }

    private void Event() throws Exception {

        try {
            five.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        clientSocket.getOutputStream().write(128);
                        speed.setText("Speed = 50%");
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error send", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            seven.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        clientSocket.getOutputStream().write(179);
                        speed.setText("Speed = 70%");
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error send", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        clientSocket.getOutputStream().write(255);
                        speed.setText("Speed = 100%");
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error send", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            back.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    send(motionEvent, 2);

                    return true;
                }
            });

            run.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    send(motionEvent, 8);

                    return true;
                }
            });
            left.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    send(motionEvent, 4);

                    return true;
                }
            });
            right.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    send(motionEvent, 6);

                    return true;
                }
            });
            run_left.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    send(motionEvent, 7);

                    return true;
                }
            });
            run_right.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    send(motionEvent, 9);

                    return true;
                }
            });
            back_left.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    send(motionEvent, 1);

                    return true;
                }
            });

            back_right.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    send(motionEvent, 3);
                    return true;
                }
            });

            bluet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        BT();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "ХЗ", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            beep.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    send(event, 100);

                    return true;
                }
            });


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void BT() {
        try {
            if (conect) {
                clientSocket.close();
                hand.removeCallbacks(runnable);
            } else {
                hand.post(runnable);
                String enableBT = BluetoothAdapter.ACTION_REQUEST_ENABLE;
                startActivityForResult(new Intent(enableBT), 0);

                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                BluetoothDevice device = bluetoothAdapter.getRemoteDevice("20:16:01:25:23:45");

                Method m = device.getClass().getMethod(
                        "createRfcommSocket", int.class);

                clientSocket = (BluetoothSocket) m.invoke(device, 1);

                clientSocket.connect();
            }

            conect = clientSocket.isConnected();

        } catch (IOException | SecurityException | NoSuchMethodException |
                IllegalArgumentException | IllegalAccessException |
                InvocationTargetException e) {
            Log.d("BLUETOOTH", e.getMessage());
            try {
                clientSocket.close();
                conect = false;
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_LONG).show();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            bluet.setChecked(conect);
        }
    }

    private void send(MotionEvent motionEvent, int mes) {
        try {
            switch (motionEvent.getAction()) {

                case (MotionEvent.ACTION_DOWN): {

                    clientSocket.getOutputStream().write(mes);
                    break;
                }
                case (MotionEvent.ACTION_UP): {
                    clientSocket.getOutputStream().write(5);
                    break;
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error send", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pmw != null) pmw.release();
    }

}