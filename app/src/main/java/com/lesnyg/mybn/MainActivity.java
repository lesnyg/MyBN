package com.lesnyg.mybn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;

import com.viewpagerindicator.CirclePageIndicator;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    AsyncTask<String, String, String> mTask;
    private Bitmap bitmap;
    private ViewPager viewPager;
    private MyPagerAdapter adapter;
    private ArrayList<ImageModel> imageModelArrayList;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;

//    private Bitmap[] myImageList = new Bitmap[3];
    private List<ImageModel> mList;
    private CirclePageIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTask = new MyTask().execute();
        imageModelArrayList = new ArrayList<>();
//        imageModelArrayList = populateList();


        init();

    }

    private void init(){
        viewPager = findViewById(R.id.pager);
        indicator = findViewById(R.id.indicator);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(3 * density);

        NUM_PAGES = imageModelArrayList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2000, 2000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

    public class MyTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            if (isCancelled())
                return (null);
            bnquery();


            return null;
        }

        protected void onPostExecute(String result) {
        }


        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private void bnquery() {
        Connection connection = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:jtds:sqlserver://222.122.213.216/mashw08", "mashw08", "msts0850op");
            Statement statement = connection.createStatement();
            ResultSet bannerResultSet = statement.executeQuery("select * from Su_배너이미지");
            byte b[];

//            mList = new ArrayList<>();

            while (bannerResultSet.next()) {
                Blob blob = bannerResultSet.getBlob(2);
                b = blob.getBytes(1, (int) blob.length());
                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//                mList.add(new ImageModel(bitmap));
                imageModelArrayList.add(new ImageModel(bitmap));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new MyPagerAdapter(MainActivity.this,imageModelArrayList);
                        viewPager.setAdapter(adapter);

                        indicator.setViewPager(viewPager);

                    }
                });
            }
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static class MyPagerAdapter extends PagerAdapter {


        private ArrayList<ImageModel> imageModelArrayList;
        private LayoutInflater inflater;
        private Context context;


        public MyPagerAdapter(Context context, ArrayList<ImageModel> imageModelArrayList) {
            this.context = context;
            this.imageModelArrayList = imageModelArrayList;
            inflater = LayoutInflater.from(context);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imageModelArrayList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.viewpager_image, view, false);

            assert imageLayout != null;
            ImageView imageView = imageLayout
                    .findViewById(R.id.image);


            imageView.setImageBitmap(imageModelArrayList.get(position).getImageBitmap());

            view.addView(imageLayout, 0);

            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
//    private ArrayList<ImageModel> populateList() {
//        ArrayList<ImageModel> list = new ArrayList<>();
//
//        for (int i = 0; i < 3; i++) {
//            myImageList[i]=mList[i];
//        }
//
//        return list;
//    }

}
