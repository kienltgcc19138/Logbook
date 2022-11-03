package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button btnAdd, btnPrev, btnNext;
    EditText inputURL;
    ImageView imageView;
    TextView textView;

    ArrayList<String> savedList;

    int currentIndex = -1;

    String fileName = "file.txt";

    String regex = "(https?:\\/\\/.*\\.(?:png|jpg))";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        inputURL = findViewById(R.id.inputURL);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        savedList = readFile(fileName);

        if (savedList.size() != 0) {
            Glide.with(this).load(savedList.get(0)).into(imageView);
        }

        btnAdd.setOnClickListener(v -> {
            if (!inputURL.getText().toString().trim().isEmpty()) {
                String imageURL = inputURL.getText().toString().trim();

                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(imageURL);

                if (m.matches()) {
                    currentIndex = savedList.indexOf(imageURL);
                    Glide.with(this).load(imageURL).into(imageView);

                    savedList.add(imageURL);
                    inputURL.setText("");
                    saveFile(fileName, savedList);
                    savedList = readFile(fileName);
                } else {
                    inputURL.setError("Invalid Image URL");
                    inputURL.requestFocus();
                }
            } else {
                inputURL.setError("Please enter image URL");
                inputURL.requestFocus();
            }
        });

        btnNext.setOnClickListener(v -> {
            int count = savedList.size();
            if (count > 0 && count != 1) {
                currentIndex++;
                if (currentIndex == count) {
                    currentIndex = 0;
                }
                Glide.with(this).load(savedList.get(currentIndex)).into(imageView);
                Animation right = AnimationUtils.loadAnimation(this, R.anim.in_right);
                imageView.startAnimation(right);
            } else {
                Toast.makeText(getBaseContext(), "No image", Toast.LENGTH_LONG).show();
            }
        });

        btnPrev.setOnClickListener(v -> {
            int count = savedList.size();
            if (count > 0 && count != 1) {
                currentIndex--;
                if (currentIndex < 0) {
                    currentIndex = count - 1;
                }
                Glide.with(this).load(savedList.get(currentIndex)).into(imageView);
                Animation left = AnimationUtils.loadAnimation(this, R.anim.out_left);
                imageView.startAnimation(left);
            } else {
                Toast.makeText(getBaseContext(), "No image", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveFile(String file, ArrayList<String> text) {
        try {
            FileOutputStream fos = openFileOutput(file, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(text);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<String> readFile(String file) {
        ArrayList<String> text = new ArrayList<>();

        try {
            FileInputStream fis = openFileInput(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            text = (ArrayList<String>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show();
        }
        return text;
    }
}