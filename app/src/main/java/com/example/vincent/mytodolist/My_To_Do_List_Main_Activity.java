package com.example.vincent.mytodolist;

import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class My_To_Do_List_Main_Activity extends AppCompatActivity {

    // Properties of the class...
    private EditText userInput;
    private ArrayList<String> toDoItems;
    private ArrayAdapter theAdapter;

    private TextToSpeech ttsObject;
    private int result;

    private final String keyToDoItemsFile = "To_do_items.txt";
    private final String keyUserInput = "userInput";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_to_do_list_main_activity);

        userInput = (EditText) findViewById(R.id.user_input);
        toDoItems = new ArrayList<String>();
        retrieveToDoItems();

        theAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, toDoItems);
        //theAdapter = new MyCustomAdapter(this, toDoItems);
        ListView theListView = (ListView) findViewById(R.id.the_listView);
        theListView.setAdapter(theAdapter);

        ttsObject = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // Check if the text-to-speech object is successfully created.
                if(status == TextToSpeech.SUCCESS) {
                    result = ttsObject.setLanguage(Locale.ENGLISH);
                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.text_tts_not_supported, Toast.LENGTH_SHORT).show();
                }
            }
        });

        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // If the user clicks on a ListView item (i.e. a to-do item), make the text-to-speech
                // object speak it aloud (only if the text-to-speech object is successfully created).
                if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                    Toast.makeText(getApplicationContext(), R.string.text_tts_not_supported, Toast.LENGTH_SHORT).show();
                } else {
                    String toDoItemSelected = String.valueOf(parent.getItemAtPosition(position));
                    ttsObject.speak(toDoItemSelected, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        theListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // If the user long-clicks on a ListView item (i.e. a to-do item), delete the
                // corresponding to-do item from both the ArrayList that holds all the to-do items
                // and the file that is saved to de device's storage.
                String toDoItemSelected = String.valueOf(parent.getItemAtPosition(position));
                removeToDoItemFromArrayList(toDoItemSelected);
                writeToDoItemsToFile();
                ttsObject.speak(getString(R.string.text_item_deleted), TextToSpeech.QUEUE_FLUSH, null);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_to_do_list_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //-----------------------------------------------------------------------------

    private void retrieveToDoItems() {
        // Retrieve the to-do items from the file that is saved to the device's storage and add
        // them to the ArrayList that holds all the to-do items.
        try {
            Scanner scan = new Scanner(openFileInput(keyToDoItemsFile));
            String line;
            while(scan.hasNextLine()) {
                line = scan.nextLine();
                toDoItems.add(line);
            }
        }
        catch(FileNotFoundException e) {
            Toast.makeText(this, R.string.text_no_to_dos, Toast.LENGTH_SHORT).show();
        }
    }

    //-----------------------------------------------------------------------------

    public void addToDoItem(View view) {
        // This method is called when the user clicks the 'ADD' button.
        String toDoItem = String.valueOf(userInput.getText());
        userInput.setText("");
        addToDoItemToArrayList(toDoItem);
        writeToDoItemToFile(toDoItem);
    }

    //-----------------------------------------------------------------------------

    private void addToDoItemToArrayList(String toDoItem) {
        // Add the to-do item to the ArrayList that holds all the do-do items.

        // Check for duplicates.
        if(toDoItems.contains(toDoItem)) {
            Toast.makeText(this, R.string.text_duplicate_item, Toast.LENGTH_SHORT).show();
        }
        // Check for empty to-do item.
        else if (toDoItem.equals("")) {
            Toast.makeText(this, R.string.text_empty_item, Toast.LENGTH_SHORT).show();
        }
        else {
            toDoItems.add(toDoItem);
            theAdapter.notifyDataSetChanged();
        }
    }

    //-----------------------------------------------------------------------------

    private void writeToDoItemToFile(String toDoItem) {
        // Write the to-do item to the file that is saved to the device's storage.
        // NOTE: use 'MODE_APPEND' to append the to-do item to the file.
        try {
            PrintStream out = new PrintStream(openFileOutput(keyToDoItemsFile, MODE_APPEND));
            out.println(toDoItem);
            out.close();
        }
        catch(FileNotFoundException e) {
            Toast.makeText(this, R.string.text_error_writing_to_do_item_to_storage, Toast.LENGTH_SHORT).show();
        }
    }

    //-----------------------------------------------------------------------------

    private void removeToDoItemFromArrayList(String toDoItem) {
        // Remove the to-do item from the ArrayList that holds all the do-do items.
        toDoItems.remove(toDoItem);
        theAdapter.notifyDataSetChanged();
    }

    //-----------------------------------------------------------------------------

    private void writeToDoItemsToFile() {
        // Write all the to-do items that are stored in the ArrayList that holds all the to-do items
        // to the file that is saved to the device's storage.
        // NOTE: use MODE_PRIVATE to overwrite the file's content (this prevents the use of a
        // temporary file).
        try {
            PrintStream out = new PrintStream(openFileOutput(keyToDoItemsFile, MODE_PRIVATE));
            for(int i=0; i < toDoItems.size(); i++) {
                out.println(toDoItems.get(i));
            }
            out.close();
        }
        catch(FileNotFoundException e) {
            Toast.makeText(this, R.string.text_error_writing_to_do_item_to_storage, Toast.LENGTH_SHORT).show();
        }
    }

    //-----------------------------------------------------------------------------

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(keyUserInput, String.valueOf(userInput.getText()));
    }

    //-----------------------------------------------------------------------------

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String textUserInput = savedInstanceState.getString(keyUserInput);
        userInput.setText(textUserInput);
    }

}
