package stael.app.guesswho;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ImageView image;
    String pageSource;
    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    String answer = "";
    int indexAnswer = 0;
    int choose = 0;
    Random rand = new Random();
    Button[] btns = new Button[4];

    public void check(View view) {
        Button btn = (Button) view;
        if(btn.getText().toString().equals(celebNames.get(indexAnswer))){
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect! That was " + celebNames.get(indexAnswer), Toast.LENGTH_SHORT).show();
        }
        DownloadImage myImage = new DownloadImage();

        try {
            indexAnswer = rand.nextInt(celebURLs.size());
            Bitmap bitmap = myImage.execute(celebURLs.get(indexAnswer)).get();
            image.setImageBitmap(bitmap);
            answer = celebNames.get(indexAnswer);
            choose = rand.nextInt(4);
            btns[choose].setText(answer);
            for (int j = 0; j < btns.length; j++) {
                if (j == choose)
                    continue;
                int wrongIndex = rand.nextInt(celebNames.size());
                btns[j].setText(celebNames.get(wrongIndex));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                return bitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    public class Download extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String html = "";
            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1) {
                    char current = (char) data;
                    html += current;
                    data = reader.read();
                }
                return html;

            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.imageView);
        btns[0] = (Button) findViewById(R.id.button4);
        btns[1] = (Button) findViewById(R.id.button);
        btns[2] = (Button) findViewById(R.id.button2);
        btns[3] = (Button) findViewById(R.id.button3);


        Download dl = new Download();
        try {
            pageSource = dl.execute("http://www.posh24.se/kandisar").get();
            String [] split = pageSource.split("<div class=\"listedArticles\">");
            Pattern pattern = Pattern.compile("src=\"(.*)\" alt=\"(.*)\"/>");
            Matcher matcher = pattern.matcher(split[0]);

            int i = 0;
            while(matcher.find()) {
                celebURLs.add(matcher.group(1));
                celebNames.add(matcher.group(2));
            }
            Log.i("Number of Celebrities", celebNames.size() + "");
            DownloadImage myImage = new DownloadImage();
            indexAnswer = rand.nextInt(celebURLs.size());
            Bitmap bitmap = myImage.execute(celebURLs.get(indexAnswer)).get();
            image.setImageBitmap(bitmap);
            answer = celebNames.get(indexAnswer);
            choose = rand.nextInt(4);
            btns[choose].setText(answer);
            for(int j = 0; j < btns.length; j++) {
                if(j == choose)
                    continue;
                int wrongIndex = rand.nextInt(celebNames.size());
                btns[j].setText(celebNames.get(wrongIndex));
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
