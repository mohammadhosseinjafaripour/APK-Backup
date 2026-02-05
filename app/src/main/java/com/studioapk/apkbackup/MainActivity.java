package com.studioapk.apkbackup;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.studioapk.apkbackup.adapter.FragmentAdapter;
import com.studioapk.apkbackup.fragment.BackupFragment;
import com.studioapk.apkbackup.fragment.RestoreFragment;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {

    //public static Toolbar toolbar;
    public static BackupFragment frag_backup;
    private RestoreFragment frag_restore;


    private ViewPager viewPager;
    private ActionBar actionBar;
    private Toolbar toolbar;

    private SearchView search;
    private FloatingActionButton fab;

    JSONObject json = null;
    String str = "";
    HttpResponse response;
    Context context;
    ProgressBar progressbar;
    Button close;
    String image_url, site_url, type;
    int i1;
    Boolean state = false;
    Boolean timer_state = false;
    String ads_state = "";
    boolean permission_status = false;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        String token = SharedPrefManager.getInstance(this).getDeviceToken();

        progressbar = (ProgressBar) findViewById(R.id.progressBar1);
        close = (Button) findViewById(R.id.close);

        actionBar = getSupportActionBar();
        checkRunTimePermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            checkRunTimePermission();
        } else {
            permission_status = true;
            if (permission_status) {
            } else {
                checkRunTimePermission();
            }
        }

        //getActionBar().setIcon(R.drawable.grid);
        /*if (isOnline())
        {
            try {
                new Thread() {
                    @Override
                    public void run() {
                        String path = "http://studioapk.ir/ads.txt";
                        URL u = null;
                        try {
                            u = new URL(path);
                            HttpURLConnection c = (HttpURLConnection) u.openConnection();
                            c.setRequestMethod("GET");
                            c.connect();
                            final InputStream in = c.getInputStream();
                            final ByteArrayOutputStream bo = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            in.read(buffer); // Read from Buffer.
                            bo.write(buffer); // Write Into Buffer.

                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   TextView textss = (TextView) findViewById(R.id.textView15);
                                    textss.setText(bo.toString());
                                    ads_state = textss.getText().toString();
                                    try {
                                        bo.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }catch (Exception e){

            }
        }*/
        Timer timers1 = new Timer();
        timers1.schedule(new TimerTask() {
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (isOnline() && ads_state.toString().trim().equals("yes")) {
                            try {
                                new GetTextViewData(MainActivity.this).execute();
                                timer_state = true;
                            } catch (Exception e) {
                            }
                        } else {
                        }
                    }
                });
            }
        }, 3000);

        Timer timers12 = new Timer();
        timers12.schedule(new TimerTask() {
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (timer_state == true) {
                            final int num = 1;
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                public void run() {
                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            int run = 1;
                                            if (isOnline() && run == num) {
                                                TabLayout tabLayout1 = (TabLayout) findViewById(R.id.tabs);
                                                toolbar = (Toolbar) findViewById(R.id.toolbar);
                                                fab = (FloatingActionButton) findViewById(R.id.fab);
                                                viewPager = (ViewPager) findViewById(R.id.viewpager);
                                                tabLayout1.setVisibility(View.GONE);
                                                toolbar.setVisibility(View.GONE);
                                                fab.setVisibility(View.GONE);
                                                viewPager.setVisibility(View.GONE);

                                                String types = getMimeType(image_url);
                                                close.setVisibility(View.VISIBLE);
                                                if (types == "image/png") {
                                                    ImageView image = (ImageView) findViewById(R.id.imageView3);
                                                    Glide.with(getApplicationContext())
                                                            .load(image_url)
                                                            .into(image);
                                                    image.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            if (type.equals("website")) {
                                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(site_url));
                                                                startActivity(browserIntent);
                                                            } else if (type.equals("instagram")) {
                                                                Uri uri = Uri.parse("https://instagram.com/_u/" + site_url);
                                                                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                                                                likeIng.setPackage("com.instagram.android");

                                                                try {
                                                                    startActivity(likeIng);
                                                                } catch (ActivityNotFoundException e) {
                                                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                                                            Uri.parse("https://instagram.com/" + site_url)));
                                                                }
                                                            } else if (type.equals("telegram")) {
                                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=" + site_url));
                                                                startActivity(intent);
                                                            }
                                                            ImageView imageView = (ImageView) findViewById(R.id.imageView3);
                                                            imageView.setVisibility(View.GONE);
                                                            close.setVisibility(View.GONE);
                                                            TabLayout tabLayout1 = (TabLayout) findViewById(R.id.tabs);
                                                            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                                                            fab = (FloatingActionButton) findViewById(R.id.fab);
                                                            viewPager = (ViewPager) findViewById(R.id.viewpager);
                                                            tabLayout1.setVisibility(View.VISIBLE);
                                                            toolbar.setVisibility(View.VISIBLE);
                                                            fab.setVisibility(View.VISIBLE);
                                                            viewPager.setVisibility(View.VISIBLE);

                                                        }
                                                    });
                                                } else if (types == "image/gif") {
                                                    gif();
                                                } else if (types == "image/jpeg") {
                                                    ImageView image = (ImageView) findViewById(R.id.imageView3);
                                                    Glide.with(getApplicationContext())
                                                            .load(image_url)
                                                            .into(image);
                                                    image.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            if (type.equals("website")) {
                                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(site_url));
                                                                startActivity(browserIntent);
                                                            } else if (type.equals("instagram")) {
                                                                Uri uri = Uri.parse("https://instagram.com/_u/" + site_url);
                                                                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                                                                likeIng.setPackage("com.instagram.android");

                                                                try {
                                                                    startActivity(likeIng);
                                                                } catch (ActivityNotFoundException e) {
                                                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                                                            Uri.parse("https://instagram.com/" + site_url)));
                                                                }
                                                            } else if (type.equals("telegram")) {
                                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=" + site_url));
                                                                startActivity(intent);
                                                            }
                                                            ImageView imageView = (ImageView) findViewById(R.id.imageView3);
                                                            imageView.setVisibility(View.GONE);
                                                            close.setVisibility(View.GONE);
                                                            TabLayout tabLayout1 = (TabLayout) findViewById(R.id.tabs);
                                                            toolbar = (Toolbar) findViewById(R.id.toolbar);
                                                            fab = (FloatingActionButton) findViewById(R.id.fab);
                                                            viewPager = (ViewPager) findViewById(R.id.viewpager);
                                                            tabLayout1.setVisibility(View.VISIBLE);
                                                            toolbar.setVisibility(View.VISIBLE);
                                                            fab.setVisibility(View.VISIBLE);
                                                            viewPager.setVisibility(View.VISIBLE);

                                                        }
                                                    });
                                                }
                                                run++;
                                            }
                                        }

                                    });
                                }
                            }, 10000);
                        }
                    }
                });
            }
        }, 5000);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        initToolbar();
//        prepareAds();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frag_backup.refresh(true);
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                // close contextual action mode
                if (frag_backup.getActionMode() != null) {
                    frag_backup.getActionMode().finish();
                }
                if (frag_restore.getActionMode() != null) {
                    frag_restore.getActionMode().finish();
                }

                if (tab.getPosition() == 0) {
                    fab.show();
                } else {
                    frag_restore.refreshList();
                    fab.hide();
                }
                search.onActionViewCollapsed();
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });




       /* if (getAPIVerison() >= 5.0) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }*/

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());

        if (frag_backup == null) {
            frag_backup = new BackupFragment();
        }
        if (frag_restore == null) {
            frag_restore = new RestoreFragment();
        }
        adapter.addFragment(frag_backup, getString(R.string.tab_title_backup));
        adapter.addFragment(frag_restore, getString(R.string.tab_title_restore));
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setIconified(false);
        if (viewPager.getCurrentItem() == 0) {
            search.setQueryHint(getString(R.string.hint_backup_search));
        } else {
            search.setQueryHint(getString(R.string.hint_restore_search));
        }
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                try {
                    if (viewPager.getCurrentItem() == 0) {
                        frag_backup.bAdapter.getFilter().filter(s);
                    } else {
                        frag_restore.rAdapter.getFilter().filter(s);
                    }
                } catch (Exception e) {

                }
                return true;
            }
        });
        search.onActionViewCollapsed();
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public static float getAPIVerison() {

        Float f = null;
        try {
            StringBuilder strBuild = new StringBuilder();
            strBuild.append(android.os.Build.VERSION.RELEASE.substring(0, 2));
            f = new Float(strBuild.toString());
        } catch (NumberFormatException e) {

        }
        return f.floatValue();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure")
                .setNegativeButton("no", null)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    private class GetTextViewData extends AsyncTask<Void, Void, Void> {
        public Context context;


        public GetTextViewData(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost("http://studioapk.ir/ads/send-data.php");

            try {
                response = myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONArray jArray = new JSONArray(str);
                int zero = 0;
                if (jArray.length() == zero) {
                    state = true;
                } else {
                    int length = jArray.length() - 1;
                    int min = 0;
                    int max = length;
                    Random r = new Random();
                    i1 = r.nextInt(max - min + 1) + min;
                    json = jArray.getJSONObject(i1);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            try {
                image_url = json.getString("image-url");
                site_url = json.getString("site-url");
                type = json.getString("type");

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //Hiding progress bar after done loading TextView.
            progressbar.setVisibility(View.GONE);

        }
    }

    public void gif() {
        final ImageView imageView = (ImageView) findViewById(R.id.imageView3);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(getApplicationContext()).load(image_url).into(imageViewTarget);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("website")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(site_url));
                    startActivity(browserIntent);
                } else if (type.equals("instagram")) {
                    Uri uri = Uri.parse("https://instagram.com/_u/" + site_url);
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                    likeIng.setPackage("com.instagram.android");

                    try {
                        startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://instagram.com/" + site_url)));
                    }
                } else if (type.equals("telegram")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=" + site_url));
                    startActivity(intent);
                }
                imageView.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
                TabLayout tabLayout1 = (TabLayout) findViewById(R.id.tabs);
                toolbar = (Toolbar) findViewById(R.id.toolbar);
                fab = (FloatingActionButton) findViewById(R.id.fab);
                viewPager = (ViewPager) findViewById(R.id.viewpager);
                tabLayout1.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);

            }
        });
    }


    private void checkRunTimePermission() {
        String[] permissionArrays = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, 1);
        } else {

        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission_status = true;

                } else {
                    //Toast.makeText(getApplicationContext(), "دسترسی رد شد ! برای بک آپ گیری نیاز به دسترسی داریم !", Toast.LENGTH_SHORT).show();
                    permission_status = false;

                }
                return;
            }

        }
    }

}
