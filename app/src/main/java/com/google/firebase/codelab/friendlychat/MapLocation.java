package com.google.firebase.codelab.friendlychat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class MapLocation extends AppCompatActivity implements OnMapReadyCallback {
    public static GoogleMap map;
    LocationManager manager;
    MyLocationListener listener;
    CircleOptions co;
    Circle circle;
    ImageView enableRangeButton, disableRangeButton, enableMarkerButton, disableMarkerButton;
    TextView textView, textView2, textView3,test;
    Double editRan; //custon alertdialog에서 edittext에 입력한 range//
    Double rangeLat, rangeLong;
    Double onlineLat, onlineLong;
    LayoutInflater inflater;
    AlertDialog.Builder CreateRangeCircle;
    AlertDialog.Builder Notification;
    Marker marker;//목적지 표시를 위한 마커//
    Intent intent;
    ArrayList<Marker> trackingmarker;
    //Notification.Builder notfbuilder = new Notification.Builder(this);
    public static ArrayList<String> userName;
    public static ArrayList<Double> latitudeList;
    public static ArrayList<Double> longitudeList;
    public static Double MyLatitude,MyLongitude;
    public static String name,dest,time,etc;

    boolean circleVisible = false; //지도상에서 원이 보이지 안보이는지 판단//
    boolean databaseon = false;
    boolean showcurrentloc = false;

    int n; //선생님,학생 구별 땜빵용

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map); //getSupportFragmentManager():activity 안에서 fragment 관리, findFragmentById:xml파일과 java파일 연동//
        fragment.getMapAsync(this);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //getSystemService(String name):Context 클래스의 getSystemService 메소드 , 시스템 레벨의 서비스 제어 , name값에 따라 서비스 제어//
        listener = new MyLocationListener();
        enableRangeButton = (ImageView) findViewById(R.id.imageView10);
        disableRangeButton = (ImageView) findViewById(R.id.imageView11);
        enableMarkerButton = (ImageView) findViewById(R.id.imageView7);
        disableMarkerButton = (ImageView) findViewById(R.id.imageView9);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE); //xml파일에 정의된 resource들을 view 형태로 반환
        CreateRangeCircle = new AlertDialog.Builder(this); //CreateRangeCircle이라는 이름의 alertdialog 생성
        Notification = new AlertDialog.Builder(this);


        intent = getIntent();

        name = intent.getExtras().getString("Name");

        if (intent.getExtras().getString("WhoIs").equals("student")) //intent의 getExtras에서 WhoIs에 있는 값이 문자열 student와 같으면//
        {
            textView2.setText("학생입니다(이름 : " + name + ")");
            enableRangeButton.setVisibility(View.INVISIBLE);
            disableRangeButton.setVisibility(View.INVISIBLE);
            enableMarkerButton.setVisibility(View.INVISIBLE);
            disableMarkerButton.setVisibility(View.INVISIBLE);
            n=0;
        }

        if (intent.getExtras().getString("WhoIs").equals("teacher")) //위와 동일//
        {
            textView2.setText("선생님입니다");
            n=1;
        }
    }

    @Override
    protected void onPause() //activity 위에 다른 activity가 오거나 focus를 잃었을때(예:전화 올때 등)//
    {
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        super.onPause();

        //map.setMyLocationEnabled(false);

        if (manager != null)
        {
            //manager.removeUpdates(listener); //위치정보 업데이트 중단//
        }
    }

    @Override
    protected void onResume() //focus를 다시 얻었을 때
    {
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);//ACCESS_FINE_LOCATION 권한 확인//
        super.onResume();

        requestMyLocation();
    }

    public void requestMyLocation()
    {
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);//ACCESS_FINE_LOCATION 권한 확인//

        long minTime = 3000; //3초 간격으로 업데이트//
        float minDistance = 0; //거리 상관없이 항상 업데이트//

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, listener); //requestLocationUpdates(String provider,long minTime,float minDistance,LocationListener listener):위치 정보 요청, minTime과 minDistance-위치 업데이트 시의 최소 시간과 거리, listener-위치정보를 받을수 있게 해줌, gps기반//
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, listener); //requestLocationUpdates(String provider,long minTime,float minDistance,LocationListener listener):위치 정보 요청, minTime과 minDistance-위치 업데이트 시의 최소 시간과 거리, listener-위치정보를 받을수 있게 해줌, 기지국 기반//

        Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //gps기반 가장 최근의 좌표를 알려줌//
        if (lastLocation != null)
        {
            double latitude = lastLocation.getLatitude(); //위도 값//
            double longitude = lastLocation.getLongitude(); //경도 값//
        }
    }


    private void showCurrentMap(Double latitude, Double longitude) //자기 위치를 지도에 표시함//
    {
        float[] results = new float[5];

        if(!showcurrentloc)
        {
            LatLng curPoint = new LatLng(latitude, longitude); //LatLng:latitude와 longitude 를 가지고 있는 포인터
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 17)); //animateCamera:지도에서 현재 위치에서 정해진 위치로 움직임, newLatLngZoom(LatLng latLng,float zoom):latLng확대 및 확대 수치 설정//
            showcurrentloc = true;
        }

        MyLatitude = latitude;
        MyLongitude = longitude;

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL); //지도 지형 설정//


        System.out.println(intent.getExtras().getString("WhoIs"));
        if(intent.getExtras().getString("WhoIs").equals("teacher"))
        {
            writeNewUser("+Teacher", "선생님", MyLatitude, MyLongitude);
        }
        else
        {
            writeNewUser(name, name, MyLatitude, MyLongitude);
        }
        if(!databaseon)
        {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            databaseon = true;
        }

        mDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                collectUserInfo((Map<String,Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {}
        });

        mDatabase.child("+RangeCircle").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getValue() != null)
                {
                    Map value = (Map) dataSnapshot.getValue();
                    onlineLat = (Double) value.get("latitude");
                    onlineLong = (Double) value.get("longitude");
                    Double onlineRad = (Double) value.get("radius");
                    editRan = onlineRad;

                    System.out.println(onlineLat+","+onlineLong+","+onlineRad);

                    if (!circleVisible)
                    {
                        co = new CircleOptions(); //co:원의 속성//
                        co.center(new LatLng(onlineLat, onlineLong)); //생성할 원의 중심 좌표//
                        co.radius(onlineRad); //생성할 원의 지름//
                        co.fillColor(Color.argb(70, 128, 255, 255)); //생성할 원의 색(알파값,r,g,b)//
                        co.strokeColor(Color.BLUE); //생성할 원의 테두리 색//
                        co.strokeWidth(2.0f); //생성할 원의 테두리 두께//

                        circle = map.addCircle(co); //addCircle(CircleOptions):해당 map에 CircleOptions를 갖고 있는 원 생성//

                        circleVisible = true;
                    }

                    System.out.println(value.get("latitude"));

                }
                else if(circleVisible && dataSnapshot.getValue() == null)
                {
                    circle.remove();
                    circleVisible = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {}
        });


        mDatabase.child("+Notification").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getValue() != null)
                {
                    Map value = (Map) dataSnapshot.getValue();
                    if(dest ==null && time ==null && etc==null)
                    {
                        Intent asdf = new Intent(getApplicationContext(),notification_activity.class);
                        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), asdf, 0);
                        Notification noti = new Notification.Builder(getApplicationContext())
                                .setContentTitle("새로운 공지사항")
                                .setContentIntent(pIntent)
                                .setContentText("알림을 눌러서 확인해주세요").setSmallIcon(R.mipmap.ic_launcher).build();
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(0, noti);
                    }

                    else if (!dest.equals(value.get("dest")) || !time.equals(value.get("time")) || !etc.equals(value.get("etc")))
                    {
                        Intent asdf = new Intent(getApplicationContext(),notification_activity.class);
                        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), asdf, 0);
                        Notification noti = new Notification.Builder(getApplicationContext())
                                .setContentTitle("새로운 공지사항")
                                .setContentIntent(pIntent)
                                .setContentText("알림을 눌러서 확인해주세요").setSmallIcon(R.mipmap.ic_launcher).build();
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(0, noti);
                    }
                    dest = (String) value.get("dest");
                    time = (String) value.get("time");
                    etc = (String) value.get("etc");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {}
        });
    }

    private void collectUserInfo(Map<String,Object> users) //with marker on
    {
        float[] results = new float[5];
        userName = new ArrayList<>();
        latitudeList = new ArrayList<>();
        longitudeList = new ArrayList<>();

        for(Map.Entry<String,Object> entry : users.entrySet())
        {
            Map singleUser = (Map) entry.getValue();
            if(!entry.getKey().equals("+RangeCircle") && !entry.getKey().equals("messages") && !entry.getKey().equals("+Notification"))
            {
                userName.add((String) singleUser.get("username"));
                latitudeList.add((Double) singleUser.get("latitude"));
                longitudeList.add((Double) singleUser.get("longitude"));
            }
        }
        if(trackingmarker != null)
        {
            for(int i=0;i<trackingmarker.size();i++)
            {
                trackingmarker.get(i).remove();
            }
        }
        trackingmarker = new ArrayList<>();
        for(int i=0;i<userName.size();i++)
        {
            if(!userName.get(i).equals(name) && intent.getExtras().getString("WhoIs").equals("student"))
            {
                MarkerOptions User = new MarkerOptions(); //마커의 옵션 설정//

                User.position(new LatLng(MapLocation.latitudeList.get(i), MapLocation.longitudeList.get(i))); //마커 위치 지정//
                User.title(MapLocation.userName.get(i)); //마커 이름,마커를 터치했을때 표시//
                User.draggable(false);
                if (userName.get(i).equals("목적지"))
                {
                    User.icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_icon));
                }
                else if (userName.get(i).equals("선생님"))
                {
                    User.icon(BitmapDescriptorFactory.fromResource(R.drawable.newteacher));
                }
                else
                {
                    User.icon(BitmapDescriptorFactory.fromResource(R.drawable.newclient));
                }
                trackingmarker.add(map.addMarker(User));
            }
            if(!userName.get(i).equals("선생님") && intent.getExtras().getString("WhoIs").equals("teacher"))
            {
                MarkerOptions User = new MarkerOptions(); //마커의 옵션 설정//

                User.position(new LatLng(MapLocation.latitudeList.get(i), MapLocation.longitudeList.get(i))); //마커 위치 지정//
                User.title(MapLocation.userName.get(i)); //마커 이름,마커를 터치했을때 표시//
                User.draggable(false);
                if(userName.get(i).equals("목적지"))
                {
                    User.icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_icon));
                }
                else if(userName.get(i).equals("선생님"))
                {
                    User.icon(BitmapDescriptorFactory.fromResource(R.drawable.newteacher));
                }
                else
                {
                    User.icon(BitmapDescriptorFactory.fromResource(R.drawable.newclient));
                }
                trackingmarker.add(map.addMarker(User));
            }
            if(circleVisible && !userName.get(i).equals("선생님") && !userName.get(i).equals("목적지"))
            {
                Location.distanceBetween(onlineLat, onlineLong, latitudeList.get(i), longitudeList.get(i), results);
                if (results[0] > editRan) //원이 만들어졌고 두점 사이 거리가 100(원의 radius 값)이상이면//
                {
                    Notification.Builder builder = new Notification.Builder(this);
                    System.out.println(this);
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    builder.setContentTitle("사용자가 활동반경을 벗어났습니다.");
                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(123456, builder.build());
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) //getMapAsync 사용 시 필요//
    {
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);//ACCESS_FINE_LOCATION 권한 확인//
        map = googleMap;
        map.setMyLocationEnabled(true); // 지도에 내 위치를 표시해줌//
    }

    class MyLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location) //위치가 바뀌었을때//
        {
            Double latitude = location.getLatitude(); //위도 값//
            Double longitude = location.getLongitude(); //경도 값//
            showCurrentMap(latitude, longitude);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    }

    public void onButton1Clicked(View v)
    {
        textView3.setText("범위의 중심이 될 곳을 터치하세요.");
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng)
            {
                rangeLat = latLng.latitude;
                rangeLong = latLng.longitude;

                final View layout = inflater.inflate(R.layout.rangesetting_layout, null);
                //뭔소린지 모르겠음, rangesetting_layout.xml을 dialog로 만듬 아마//

                CreateRangeCircle.setTitle("범위 설정"); //alertdialog의 제목
                CreateRangeCircle.setView(layout); //alertdialog의 내용이 될 custom view를 설정//
                CreateRangeCircle.setPositiveButton("생성", new DialogInterface.OnClickListener() { //확인 버튼 눌렀을때("생성"은 확인버튼의 이름//
                    public void onClick(DialogInterface dialog, int which)
                    {
                        EditText rangeEdit = (EditText) layout.findViewById(R.id.RangeEdit); //rangesetting_layout에 있는 RangeEdit라는 id를 가진 EditText를 찾음//

                        String sran = rangeEdit.getText().toString(); //RangeEdit의 내용을 sran에 저장//

                        editRan = Double.parseDouble(sran); //sran의 값을 double로 변환해서 editRan에 저장//

                        enableRangeButton.setVisibility(View.INVISIBLE); //생성 버튼을 없애고//
                        disableRangeButton.setVisibility(View.VISIBLE); //제거 버튼 남김//

                        com.google.firebase.codelab.friendlychat.Circle circle = new com.google.firebase.codelab.friendlychat.Circle(rangeLat,rangeLong,editRan);
                        mDatabase.child("+RangeCircle").setValue(circle);
                    }
                });
                CreateRangeCircle.setNegativeButton("취소", new DialogInterface.OnClickListener() //취소 버튼 눌렀을때//
                {
                    public void onClick(DialogInterface dialog, int which)
                    {}
                });
                CreateRangeCircle.create().show(); //create():dialog의 즉시제작을 강제함(뭔소린지 모르겟음) , show():dialog를 시작해서 화면에 표시//
                map.setOnMapClickListener(null);
                textView3.setText("");
            }
        });
    }

    public void onButton2Clicked(View v)
    {
        circle.remove();//지도에서 원을 없앰//
        circleVisible = false;
        enableRangeButton.setVisibility(View.VISIBLE); //제거 버튼 없애고//
        disableRangeButton.setVisibility(View.INVISIBLE); //생성 버튼 남김//

        mDatabase.child("+RangeCircle").removeValue();
    }

    public void onButton3Clicked(View v)
    {
        textView3.setText("지도를 터치해 주세요");
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() { //onmapclicklistener 설정(지도를 터치했을때의 listener)//
            @Override
            public void onMapClick(LatLng latLng) //지도를 터치했을때(latLng에 터치한 좌표값 입력)//
            {
                final View nlayout = inflater.inflate(R.layout.notification_layout, null);

                Notification.setTitle("공지");
                Notification.setView(nlayout);
                Notification.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        EditText destinationAddress = (EditText) nlayout.findViewById(R.id.editText2);
                        EditText dueTime = (EditText) nlayout.findViewById(R.id.editText3);
                        EditText etcMessage = (EditText) nlayout.findViewById(R.id.editText4);

                        NotificationMessage notification = new NotificationMessage(destinationAddress.getText().toString(),dueTime.getText().toString(),etcMessage.getText().toString());
                        mDatabase.child("+Notification").setValue(notification);
                    }
                });
                Notification.setNegativeButton("취소", new DialogInterface.OnClickListener() //취소 버튼 눌렀을때//
                {
                    public void onClick(DialogInterface dialog, int which)
                    {}
                });
                Notification.create().show();

                MarkerOptions destination = new MarkerOptions(); //마커의 옵션 설정//
                destination.position(new LatLng(latLng.latitude, latLng.longitude)); //마커 위치 지정//
                destination.title("목적지"); //마커 이름,마커를 터치했을때 표시//
                destination.draggable(false);
                destination.icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_icon)); //마커 이미지 설정//

                marker = map.addMarker(destination); //지도에 마커 표시//
                map.setOnMapClickListener(null); //onmapclicklistener해제(목적지를 버튼 눌렀을때 한번만 설정해야 되니까)//

                textView3.setText("");

                enableMarkerButton.setVisibility(View.INVISIBLE);
                disableMarkerButton.setVisibility(View.VISIBLE);

                writeNewUser("+Destination_Marker","목적지",latLng.latitude,latLng.longitude);
            }
        });
    }

    public void onButton4Clicked(View v)
    {
        marker.remove();
        enableMarkerButton.setVisibility(View.VISIBLE);
        disableMarkerButton.setVisibility(View.INVISIBLE);
        mDatabase.child("+Destination_Marker").removeValue();
    }

    public void onButton5Clicked(View v)
    {
        Intent chatIntent = new Intent(this,MainActivity.class); //MapLocation으로 이동
        //intent:내가 행동하고 싶은 것에 대한 정의, Intent intent = new Intent(activity,activity):두 액티비티 간의 통신이 가능
        startActivity(chatIntent);
    }

    private void writeNewUser(String userId, String username,Double latitude, Double longitude)
    {
        User user = new User(username,latitude,longitude);

        mDatabase.child(userId).setValue(user);
    }
}