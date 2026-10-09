package com.panaboost.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class MainActivity extends Activity {
  final int BG=0xFF090B15, PANEL=0xFF171A2B, PANEL2=0xFF20243A, PURPLE=0xFFAA70FF, WHITE=0xFFF6F4FF, MUTED=0xFFACB0C9, GREEN=0xFF53E4B1;
  LinearLayout main, metrics, gamesList; TextView status; Handler handler=new Handler(); boolean watching=false;
  String selectedPackage=""; String selectedLabel="";
  int dp(float v){return (int)(getResources().getDisplayMetrics().density*v+0.5f);}
  GradientDrawable bg(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
  TextView label(String text,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(text);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(null,Typeface.BOLD);return t;}
  LinearLayout col(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
  void gap(LinearLayout l,int n){View v=new View(this);l.addView(v,new LinearLayout.LayoutParams(1,dp(n)));}
  void pad(View v,int a){v.setPadding(dp(a),dp(a),dp(a),dp(a));}
  void add(LinearLayout l,View v){l.addView(v,new LinearLayout.LayoutParams(-1,-2));}
  void button(LinearLayout parent,String title,Runnable click,boolean accent){TextView b=label(title,15,WHITE,true);b.setGravity(Gravity.CENTER);b.setPadding(dp(12),dp(17),dp(12),dp(17));b.setBackground(bg(accent?0xFF7738DC:PANEL2,15));add(parent,b);b.setOnClickListener(v->click.run());gap(parent,9);}
  LinearLayout card(String title,String subtitle){LinearLayout c=col();pad(c,17);c.setBackground(bg(PANEL,19));add(main,c);TextView h=label(title,18,WHITE,true);add(c,h);gap(c,6);if(!subtitle.isEmpty())add(c,label(subtitle,12,MUTED,false));gap(c,13);gap(main,16);return c;}
  @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);render();}
  void render(){ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(BG);main=col();main.setPadding(dp(18),dp(25),dp(18),dp(35));scroll.addView(main);setContentView(scroll);
    add(main,label("ϟ  PanaBoost",30,WHITE,true));add(main,label("GAMING CONTROL CENTER  ·  2.0",11,PURPLE,true));gap(main,22);
    LinearLayout overview=card("Estado del dispositivo","Datos reales obtenidos de Android · actualiza cada 3 segundos");metrics=col();add(overview,metrics);refresh();
    LinearLayout mode=card("Modo gaming","Inicia una sesión, revisa recursos y reduce interrupciones cuando Android lo permita.");
    status=label("● Sesión inactiva",14,MUTED,true);add(mode,status);gap(mode,13);
    button(mode,"⚡ INICIAR SESIÓN GAMING",()->startSession(),true);
    button(mode,"■ Finalizar sesión",()->stopSession(),false);
    button(mode,"☾ Activar / desactivar No molestar",()->toggleDnd(),false);
    LinearLayout games=card("Mis juegos","Selecciona una aplicación instalada y ábrela desde aquí.");
    button(games,"＋ Elegir juego",()->chooseGame(),true);
    gamesList=col();add(games,gamesList);restoreGame();
    LinearLayout tools=card("Herramientas reales","Operaciones que la app puede ejecutar sin root.");
    button(tools,"🧹 Limpiar caché de PanaBoost",()->cleanOwnCache(),false);
    button(tools,"↻ Actualizar diagnóstico",()->refresh(),false);
    button(tools,"🔋 Ver uso de batería del sistema",()->openSettings(android.provider.Settings.ACTION_BATTERY_SAVER_SETTINGS),false);
    LinearLayout info=card("Qué puede hacer realmente","Android impide a las apps normales controlar el reloj de CPU/GPU, cerrar todas las apps, borrar cachés ajenas o desbloquear FPS. PanaBoost no simula esas funciones.");
    add(info,label("✓ Monitoreo RAM, batería, almacenamiento y temperatura de batería\n✓ Selección y lanzamiento de juegos\n✓ Sesión gaming con seguimiento\n✓ Control de No molestar con permiso\n✓ Limpieza de su propia caché",13,GREEN,false));
  }
  String bytes(long b){return String.format(java.util.Locale.US,"%.1f GB",b/1073741824.0);}
  void refresh(){if(metrics==null)return;metrics.removeAllViews();ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE);ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();am.getMemoryInfo(mi);row("RAM libre",bytes(mi.availMem)+" / "+bytes(mi.totalMem));
    android.os.StatFs sf=new android.os.StatFs(android.os.Environment.getDataDirectory().getPath());row("Almacenamiento libre",bytes(sf.getAvailableBytes())+" / "+bytes(sf.getTotalBytes()));
    Intent battery=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));if(battery!=null){int level=battery.getIntExtra(BatteryManager.EXTRA_LEVEL,-1),scale=battery.getIntExtra(BatteryManager.EXTRA_SCALE,100),temp=battery.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,-1);row("Batería",(scale>0?level*100/scale:level)+"%");row("Temperatura batería",temp>=0?String.format(java.util.Locale.US,"%.1f °C",temp/10.0):"No disponible");}
    if(Build.VERSION.SDK_INT>=29){android.os.PowerManager pm=(android.os.PowerManager)getSystemService(POWER_SERVICE);row("Estado térmico",thermal(pm.getCurrentThermalStatus()));}
  }
  String thermal(int s){String[] a={"Normal","Ligero","Moderado","Elevado","Severo","Crítico","Emergencia"};return s>=0&&s<a.length?a[s]:"No disponible";}
  void row(String name,String value){LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.HORIZONTAL);TextView n=label(name,13,MUTED,false),v=label(value,13,WHITE,true);r.addView(n,new LinearLayout.LayoutParams(0,-2,1));r.addView(v);add(metrics,r);gap(metrics,12);}
  void startSession(){watching=true;status.setText("● Gaming activo · monitoreando");status.setTextColor(GREEN);refresh();handler.removeCallbacks(tick);handler.postDelayed(tick,3000);if(!selectedPackage.isEmpty())launchGame();else toast("Sesión iniciada. Elige tu juego.");}
  Runnable tick=new Runnable(){public void run(){if(watching){refresh();handler.postDelayed(this,3000);}}};
  void stopSession(){watching=false;handler.removeCallbacks(tick);status.setText("● Sesión inactiva");status.setTextColor(MUTED);toast("Sesión finalizada");}
  void toggleDnd(){android.app.NotificationManager nm=(android.app.NotificationManager)getSystemService(NOTIFICATION_SERVICE);if(Build.VERSION.SDK_INT>=23&&!nm.isNotificationPolicyAccessGranted()){toast("Concede acceso a No molestar y vuelve a pulsar");startActivity(new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));return;}try{int old=nm.getCurrentInterruptionFilter();nm.setInterruptionFilter(old==NotificationManager.INTERRUPTION_FILTER_NONE?NotificationManager.INTERRUPTION_FILTER_ALL:NotificationManager.INTERRUPTION_FILTER_NONE);toast("No molestar actualizado");}catch(Exception e){toast("HyperOS no permite el cambio: "+e.getClass().getSimpleName());}}
  void chooseGame(){PackageManager pm=getPackageManager();Intent q=new Intent(Intent.ACTION_MAIN);q.addCategory(Intent.CATEGORY_LAUNCHER);List<ResolveInfo> apps=pm.queryIntentActivities(q,0);Collections.sort(apps,new ResolveInfo.DisplayNameComparator(pm));ArrayList<ResolveInfo> candidates=new ArrayList<>();ArrayList<String> names=new ArrayList<>();for(ResolveInfo r:apps){if(r.activityInfo==null||r.activityInfo.packageName.equals(getPackageName()))continue;candidates.add(r);names.add(r.loadLabel(pm).toString());}if(names.isEmpty()){toast("No se encontraron apps visibles");return;}new AlertDialog.Builder(this).setTitle("Selecciona tu juego").setItems(names.toArray(new String[0]),(d,i)->{ResolveInfo r=candidates.get(i);selectedPackage=r.activityInfo.packageName;selectedLabel=names.get(i);getPreferences(0).edit().putString("pkg",selectedPackage).putString("label",selectedLabel).apply();restoreGame();}).show();}
  void restoreGame(){if(gamesList==null)return;gamesList.removeAllViews();selectedPackage=getPreferences(0).getString("pkg",selectedPackage);selectedLabel=getPreferences(0).getString("label",selectedLabel);if(!selectedPackage.isEmpty())button(gamesList,"▶ Abrir "+selectedLabel,()->launchGame(),false);}
  void launchGame(){try{Intent i=getPackageManager().getLaunchIntentForPackage(selectedPackage);if(i==null){toast("Juego no disponible; selecciónalo otra vez");return;}startActivity(i);}catch(Exception e){toast("No se pudo abrir el juego");}}
  void cleanOwnCache(){long freed=deleteContents(getCacheDir());if(getExternalCacheDir()!=null)freed+=deleteContents(getExternalCacheDir());toast("Caché propia limpiada: "+(freed/1024)+" KB");}
  long deleteContents(File dir){long sum=0;if(dir==null)return 0;File[] fs=dir.listFiles();if(fs==null)return 0;for(File f:fs){if(f.isDirectory())sum+=deleteContents(f);else sum+=f.length();f.delete();}return sum;}
  void openSettings(String action){try{startActivity(new Intent(action));}catch(Exception e){toast("Ajuste no disponible en este equipo");}}
  void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
  @Override protected void onDestroy(){handler.removeCallbacks(tick);super.onDestroy();}
}
