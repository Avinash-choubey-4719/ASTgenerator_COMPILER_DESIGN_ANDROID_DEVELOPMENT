package com.example.astgenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button buttonSave;
    Button buttonSubmit;
    TextView textView;
    private static String sourceCode;
    private static String output;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSave = findViewById(R.id.save);
        buttonSubmit = findViewById(R.id.submitCode);
        textView = findViewById(R.id.textView);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceCode = textView.getText().toString();

                //        sourceCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";

                if(sourceCode == ""){
                    Toast.makeText(MainActivity.this, "Please enter the valid source code", Toast.LENGTH_SHORT).show();
                }

                else{
                    // Create a CharStream from the source code
                    CharStream input = CharStreams.fromString(sourceCode);

                    // Create a lexer that reads from the CharStream
                    Java8Lexer lexer = new Java8Lexer(input);

                    // Create a token stream from the lexer
                    CommonTokenStream tokens = new CommonTokenStream(lexer);

                    // Create a parser that reads from the token stream
                    Java8Parser parser = new Java8Parser(tokens);

                    // Parse the input and generate an AST
                    Java8Parser.CompilationUnitContext tree = parser.compilationUnit();

                    // Print the AST to the console
//        System.out.println(tree.toStringTree(parser));

                    output = tree.toStringTree(parser);
                }
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndDownloadFile(output);
            }

            private void saveAndDownloadFile(String output) {
                // Save the string to a file
                String fileName = "output.txt";
                FileOutputStream outputStream;

                try {
                    File file = new File(getFilesDir(), fileName);
                    outputStream = new FileOutputStream(file);
                    outputStream.write(output.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    // Grant read permission to the file
                    file.setReadable(true, false);

                    // Create an intent to download the file
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(MainActivity.this, "com.example.astgenerator.fileprovider", file);
                    intent.setDataAndType(uri, "text/plain");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } catch (IOException e) {
                    Log.d("tag1", e.toString());
                    e.printStackTrace();
                }catch (Exception e){
                    Log.d("tag1", e.toString());
                    e.printStackTrace();
                }
            }
        });
    }
}