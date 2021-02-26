package com.hoc_android.test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button btnIncrease1, btnIncrease2, btnIncrease3, btnDecrease1, btnDecrease2, btnDecrease3, btnStart1, btnStart2, btnStart3, btnStop1, btnStop2, btnStop3;
    TextView progressNum1, progressNum2, progressNum3;
    Spinner setMode1, setMode2, setMode3;
    TextInputEditText volInput1, volInput2, volInput3;
    WebView webView;
    ProgressDialog pd;
    String url = "http://192.168.100.6:8000/index.html";
    int setRate1, setRate2, setRate3 = 0;
    private Handler repeatUpdateHandler1 = new Handler();
    private Handler repeatUpdateHandler2 = new Handler();
    private Handler repeatUpdateHandler3 = new Handler();
    private boolean mAutoIncrement1, mAutoIncrement2, mAutoIncrement3 = false;
    private boolean mAutoDecrement1, mAutoDecrement2, mAutoDecrement3 = false;
    public static int REP_DELAY = 20;



    class RptUpdater1 implements Runnable            //This little guy handles the auto part of the auto incrementing feature.
    {                                               //In doing so it instantiates itself.
        public void run() {
            if (mAutoIncrement1) {
                increaseRate1();
                repeatUpdateHandler1.postDelayed(new RptUpdater1(), REP_DELAY);
            } else if (mAutoDecrement1) {
                decreaseRate1();
                repeatUpdateHandler1.postDelayed(new RptUpdater1(), REP_DELAY);
            }
        }
    }

    class RptUpdater2 implements Runnable            //This little guy handles the auto part of the auto incrementing feature.
    {                                               //In doing so it instantiates itself.
        public void run() {
            if (mAutoIncrement2) {
                increaseRate2();
                repeatUpdateHandler2.postDelayed(new RptUpdater2(), REP_DELAY);
            } else if (mAutoDecrement2) {
                decreaseRate();
                repeatUpdateHandler2.postDelayed(new RptUpdater2(), REP_DELAY);
            }
        }
    }

    class RptUpdater3 implements Runnable            //This little guy handles the auto part of the auto incrementing feature.
    {                                               //In doing so it instantiates itself.
        public void run() {
            if (mAutoIncrement3) {
                increaseRate3();
                repeatUpdateHandler3.postDelayed(new RptUpdater3(), REP_DELAY);
            } else if (mAutoDecrement3) {
                decreaseRate3();
                repeatUpdateHandler3.postDelayed(new RptUpdater3(), REP_DELAY);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner Mode1 = findViewById(R.id.Mode1), Mode2 = findViewById(R.id.Mode2), Mode3 = findViewById(R.id.Mode3);
        btnIncrease1 = (Button) findViewById(R.id.Increase1);
        btnIncrease2 = (Button) findViewById(R.id.Increase2);
        btnIncrease3 = (Button) findViewById(R.id.Increase3);
        btnDecrease1 = (Button) findViewById(R.id.Decrease1);
        btnDecrease2 = (Button) findViewById(R.id.Decrease2);
        btnDecrease3 = (Button) findViewById(R.id.Decrease3);
        progressNum1 = findViewById(R.id.progressNum1);
        progressNum2 = findViewById(R.id.progressNum2);
        progressNum3 = findViewById(R.id.progressNum3);
        setMode1 = (Spinner) findViewById(R.id.Mode1);
        setMode2 = (Spinner) findViewById(R.id.Mode2);
        setMode3 = (Spinner) findViewById(R.id.Mode3);
        btnStart1 = (Button) findViewById(R.id.Start1);
        btnStart2 = (Button) findViewById(R.id.Start2);
        btnStart3 = (Button) findViewById(R.id.Start3);
        volInput1 = (TextInputEditText) findViewById(R.id.Input_Volume1);
        volInput2 = (TextInputEditText) findViewById(R.id.Input_Volume2);
        volInput3 = (TextInputEditText) findViewById(R.id.Input_Volume3);
        webView = (WebView) findViewById(R.id.WebView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);


        //DROP-DOWN MENU FOR MODE
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.Mode_Items, R.layout.custom_spinner_font);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        Mode1.setAdapter(adapter);
        Mode1.setOnItemSelectedListener(this);
        Mode2.setAdapter(adapter);
        Mode2.setOnItemSelectedListener(this);
        Mode3.setAdapter(adapter);
        Mode3.setOnItemSelectedListener(this);


        //INCREMENT / DECREMENT SECTION
        // Increment once for a click
        btnIncrease1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseRate1();
            }
        });
        btnDecrease1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseRate1();
            }
        });

        btnIncrease2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseRate2();
            }
        });
        btnDecrease2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseRate();
            }
        });

        btnIncrease3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseRate3();
            }
        });
        btnDecrease3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseRate3();
            }
        });


        // Auto increment for a long press
        btnIncrease1.setOnLongClickListener(new View.OnLongClickListener() {
                                                public boolean onLongClick(View arg0) {
                                                    mAutoIncrement1 = true;
                                                    repeatUpdateHandler1.post(new RptUpdater1());
                                                    return false;
                                                }
                                            }
        );
        btnDecrease1.setOnLongClickListener(new View.OnLongClickListener() {
                                                public boolean onLongClick(View arg0) {
                                                    mAutoDecrement1 = true;
                                                    repeatUpdateHandler1.post(new RptUpdater1());
                                                    return false;
                                                }
                                            }
        );

        btnIncrease2.setOnLongClickListener(new View.OnLongClickListener() {
                                                public boolean onLongClick(View arg1) {
                                                    mAutoIncrement2 = true;
                                                    repeatUpdateHandler2.post(new RptUpdater2());
                                                    return false;
                                                }
                                            }
        );
        btnDecrease2.setOnLongClickListener(new View.OnLongClickListener() {
                                                public boolean onLongClick(View arg1) {
                                                    mAutoDecrement2 = true;
                                                    repeatUpdateHandler2.post(new RptUpdater2());
                                                    return false;
                                                }
                                            }
        );

        btnIncrease3.setOnLongClickListener(new View.OnLongClickListener() {
                                                public boolean onLongClick(View arg2) {
                                                    mAutoIncrement3 = true;
                                                    repeatUpdateHandler3.post(new RptUpdater3());
                                                    return false;
                                                }
                                            }
        );
        btnDecrease3.setOnLongClickListener(new View.OnLongClickListener() {
                                                public boolean onLongClick(View arg2) {
                                                    mAutoDecrement3 = true;
                                                    repeatUpdateHandler3.post(new RptUpdater3());
                                                    return false;
                                                }
                                            }
        );


        // When the button is released, while auto increment is happening, stop
        btnIncrease1.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoIncrement1) {
                    mAutoIncrement1 = false;
                }
                return false;
            }
        });
        btnDecrease1.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoDecrement1) {
                    mAutoDecrement1 = false;
                }
                return false;
            }
        });

        btnIncrease2.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoIncrement2) {
                    mAutoIncrement2 = false;
                }
                return false;
            }
        });
        btnDecrease2.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoDecrement2) {
                    mAutoDecrement2 = false;
                }
                return false;
            }
        });

        btnIncrease3.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoIncrement3) {
                    mAutoIncrement3 = false;
                }
                return false;
            }
        });
        btnDecrease3.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoDecrement3) {
                    mAutoDecrement3 = false;
                }
                return false;
            }
        });

        btnStart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePump1();
            }
        });

        btnStart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePump2();
            }
        });

        btnStart3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePump3();
            }
        });
    }

    public void increaseRate1() {
        setRate1++;
        progressNum1.setText(Integer.toString(setRate1) + " μL/min");
    }

    public void increaseRate2() {
        setRate2++;
        progressNum2.setText(Integer.toString(setRate2) + " μL/min");
    }

    public void increaseRate3() {
        setRate3++;
        progressNum3.setText(Integer.toString(setRate3) + " μL/min");
    }


    public void decreaseRate1() {
        if (setRate1 >= 1) {
            setRate1--;
            progressNum1.setText(Integer.toString(setRate1) + " μL/min");
        }
    }

    public void decreaseRate() {
        if (setRate2 >= 1) {
            setRate2--;
            progressNum2.setText(Integer.toString(setRate2) + " μL/min");
        }
    }

    public void decreaseRate3() {
        if (setRate3 >= 1) {
            setRate3--;
            progressNum3.setText(Integer.toString(setRate3) + " μL/min");
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        String text = adapterView.getItemAtPosition(position).toString();
        Toast.makeText(this, adapterView.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void updatePump1() {
        // [START write_message]
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Map<String, Object> newMap = new HashMap<>();
        newMap.put("rate", setRate1);
        newMap.put("volume", Integer.parseInt(volInput1.getText().toString()));
        newMap.put("mode", setMode1.getSelectedItem().toString());
        DatabaseReference myRef1 = database.getReference("pump1");
        myRef1.setValue(newMap);
    }

    public void updatePump2() {
        // [START write_message]
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Map<String, Object> newMap = new HashMap<>();
        newMap.put("rate", setRate2);
        newMap.put("volume", Integer.parseInt(volInput2.getText().toString()));
        newMap.put("mode", setMode2.getSelectedItem().toString());
        DatabaseReference myRef2 = database.getReference("pump2");
        myRef2.setValue(newMap);
    }

    public void updatePump3() {
        // [START write_message]
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Map<String, Object> newMap = new HashMap<>();
        newMap.put("rate", setRate3);
        newMap.put("volume", Integer.parseInt(volInput3.getText().toString()));
        newMap.put("mode", setMode3.getSelectedItem().toString());
        DatabaseReference myRef1 = database.getReference("pump3");
        myRef1.setValue(newMap);
    }
}