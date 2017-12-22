package fr.umanit.testcapteur;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mbientlab.metawear.AnonymousRoute;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.data.AngularVelocity;
import com.mbientlab.metawear.data.Quaternion;
import com.mbientlab.metawear.module.Logging;
import com.mbientlab.metawear.module.SensorFusionBosch;

import bolts.Continuation;
import bolts.Task;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private BtleService.LocalBinder serviceBinder;

    private final String MW_MAC_ADDRESS= "C4:63:F9:54:7F:94";

    private MetaWearBoard board;

    public void retrieveBoard() {
        final BluetoothManager btManager=
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice remoteDevice=
                btManager.getAdapter().getRemoteDevice(MW_MAC_ADDRESS);

        // Create a MetaWear board object for the Bluetooth Device
        board= serviceBinder.getMetaWearBoard(remoteDevice);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Bind the service when the activity is created
        getApplicationContext().bindService(new Intent(this, BtleService.class),
                this, Context.BIND_AUTO_CREATE);

        Button btnConfigure = (Button) findViewById(R.id.btn_configure);
        btnConfigure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configuration();
            }
        });

        Button btnLog = (Button) findViewById(R.id.btn_log);
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLog();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unbind the service when the activity is destroyed
        getApplicationContext().unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // Typecast the binder to the service's LocalBinder class
        serviceBinder = (BtleService.LocalBinder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) { }



    public void configuration() {
        retrieveBoard();


        board.connectAsync().continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                if (task.isFaulted()) {
                    Log.i("MainActivity", "Failed to connect");
                } else {
                    Log.i("MainActivity", "connected");

                    board.tearDown();
                    final Logging logging = board.getModule(Logging.class);
                    logging.clearEntries();
                    final SensorFusionBosch sensor = board.getModule(SensorFusionBosch.class);
                    sensor.configure().mode(SensorFusionBosch.Mode.NDOF)
                            .accRange(SensorFusionBosch.AccRange.AR_16G)
                            .gyroRange(SensorFusionBosch.GyroRange.GR_2000DPS)
                            .commit();

                    sensor.quaternion().addRouteAsync(new RouteBuilder() {
                        @Override
                        public void configure(RouteComponent source) {
                            source.log(null);
                        }
                    }).continueWith(new Continuation<Route, Void>() {
                        @Override
                        public Void then(Task<Route> task) throws Exception {
                            // save the result of generateIdentifier and hardcode
                            // value in anonymous route
                            Log.i("MainActivity", "subscriber (0) = " + task.getResult().generateIdentifier(0));
                            return null;
                        }
                    }).continueWith(new Continuation<Void, Void>() {
                        @Override
                        public Void then(Task<Void> task) throws Exception {
                            if (task.isFaulted()) {
                                Log.i("MainActivity", "Failed to connect for log");
                            } else {
                                sensor.quaternion().start();
                                sensor.start();
                                logging.start(true);
                                Log.i("MainActivity", "start log");
                            }
                            return null;
                        }
                    });

                    sensor.linearAcceleration().addRouteAsync(new RouteBuilder() {
                        @Override
                        public void configure(RouteComponent source) {
                            source.log(null);
                        }
                    }).continueWith(new Continuation<Route, Void>() {
                        @Override
                        public Void then(Task<Route> task) throws Exception {
                            // save the result of generateIdentifier and hardcode
                            // value in anonymous route
                            Log.i("MainActivity", "subscriber (0) = " + task.getResult().generateIdentifier(0));
                            return null;
                        }
                    }).continueWith(new Continuation<Void, Void>() {
                        @Override
                        public Void then(Task<Void> task) throws Exception {
                            if (task.isFaulted()) {
                                Log.i("MainActivity", "Failed to connect for log");
                            } else {
                                sensor.linearAcceleration().start();
                                sensor.start();
                                logging.start(true);
                                Log.i("MainActivity", "start log");
                            }
                            return null;
                        }
                    });

                    sensor.correctedAngularVelocity().addRouteAsync(new RouteBuilder() {
                        @Override
                        public void configure(RouteComponent source) {
                            source.log(null);
                        }
                    }).continueWith(new Continuation<Route, Void>() {
                        @Override
                        public Void then(Task<Route> task) throws Exception {
                            // save the result of generateIdentifier and hardcode
                            // value in anonymous route
                            Log.i("MainActivity", "subscriber (0) = " + task.getResult().generateIdentifier(0));
                            return null;
                        }
                    }).continueWith(new Continuation<Void, Void>() {
                        @Override
                        public Void then(Task<Void> task) throws Exception {
                            if (task.isFaulted()) {
                                Log.i("MainActivity", "Failed to connect for log");
                            } else {
                                sensor.correctedAngularVelocity().start();
                                sensor.start();
                                logging.start(true);
                                Log.i("MainActivity", "start log");
                            }
                            return null;
                        }
                    });
                    sensor.correctedMagneticField().addRouteAsync(new RouteBuilder() {
                        @Override
                        public void configure(RouteComponent source) {
                            source.log(null);
                        }
                    }).continueWith(new Continuation<Route, Void>() {
                        @Override
                        public Void then(Task<Route> task) throws Exception {
                            // save the result of generateIdentifier and hardcode
                            // value in anonymous route
                            Log.i("MainActivity", "subscriber (0) = " + task.getResult().generateIdentifier(0));
                            return null;
                        }
                    }).continueWith(new Continuation<Void, Void>() {
                        @Override
                        public Void then(Task<Void> task) throws Exception {
                            if (task.isFaulted()) {
                                Log.i("MainActivity", "Failed to connect for log");
                            } else {
                                sensor.correctedMagneticField().start();
                                sensor.start();
                                logging.start(true);
                                Log.i("MainActivity", "start log");
                            }
                            return null;
                        }
                    });
                    Log.i("MainActivity", "configured");
                }
                return null;
            }
        });
    }

    public void getLog() {
        retrieveBoard();


        board.connectAsync().continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                board.createAnonymousRoutesAsync().onSuccessTask(new Continuation<AnonymousRoute[], Task<Void>>() {
                    @Override
                    public Task<Void> then(Task<AnonymousRoute[]> task) throws Exception {
                        for(final AnonymousRoute it: task.getResult()) {
                            it.subscribe(new Subscriber() {
                                @Override
                                public void apply(Data data, Object... env) {
                                    switch (it.identifier()) {
                                        // identifier earlier extracted from calling
                                        // generateIdentifier, use in switch statement to identify
                                        // which anonymous route represents gyro y-axis data
                                        case "linear-acceleration":
                                            Log.i("MainActivity", "linear-acceleration: ("+data.formattedTimestamp()+") " + data.value(Acceleration.class));
                                            break;
                                        case "corrected-angular-velocity":
                                            Log.i("MainActivity", "corrected-angular-velocity: ("+data.formattedTimestamp()+") " + data.value(SensorFusionBosch.CorrectedAngularVelocity.class));
                                            break;
                                        case "corrected-magnetic-field":
                                            Log.i("MainActivity", "corrected-magnetic-field: ("+data.formattedTimestamp()+") " + data.value(SensorFusionBosch.CorrectedMagneticField.class));
                                            break;
                                        case "quaternion":
                                            Log.i("MainActivity", "quaternion: ("+data.formattedTimestamp()+") " + data.value(Quaternion.class));
                                            break;
                                        default:
                                            Log.i("MainActivity", "logging("+it.identifier()+"): " + data.toString());

                                    }
                                }
                            });
                        }

                        return board.getModule(Logging.class).downloadAsync();
                    }
                }).continueWith(new Continuation<Void, Void>() {
                    @Override
                    public Void then(Task<Void> task) throws Exception {
                        if (task.isFaulted()) {
                            Log.i("MainActivity", "Failed to connect for log");
                        } else {
                            Log.i("MainActivity", "Download completed");
                        }
                        return null;
                    }
                });

                return null;
            }
        });

    }
}
