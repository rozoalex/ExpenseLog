package expenselog.pa2.expenselog;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


//
//
public class askNewExpense extends AppCompatActivity {

    final String saveListKey = "eledListClone";
    Intent switchtoMain;
    ArrayList<String> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_add);

        final EditText title = (EditText)findViewById(R.id.titleEditText);
        final EditText notes = (EditText)findViewById(R.id.editText5);
        Button ok = (Button)findViewById(R.id.okButton);
        Button no = (Button)findViewById(R.id.button2);
        results=new ArrayList<>();

        ok.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        if(title==null ){
                            Log.wtf("askNewExpense","wtf?! title is null?");
                            return;
                        }
                        if(!title.getText().toString().trim().isEmpty()){
                            results.add(title.getText().toString());
                            //switchtoMain.putExtra("title",title.getText().toString());
                            if(notes==null){
                                results.add("");
                                //switchtoMain.putExtra("notes","");
                            }else{
                                results.add(notes.getText().toString());
                                //switchtoMain.putExtra("notes",notes.getText().toString());
                            }
                            Toast.makeText(getApplicationContext(), "New expense:\n"+title.getText(), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Sorry, there has to be a title.", Toast.LENGTH_SHORT).show();
                            cancelResult();
                        }
                        //switchtoMain.putExtra(saveListKey,getIntent().getExtras().getStringArrayList(saveListKey));
                        //startActivity(switchtoMain);
                        switchtoMain=new Intent();
                        switchtoMain.putExtra("results",results);
                        setResult(Activity.RESULT_OK,switchtoMain);
                        finish();
                    }
                }
        );

        no.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "cancelled", Toast.LENGTH_SHORT).show();
                        cancelResult();
                        //switchtoMain.putExtra(saveListKey,getIntent().getExtras().getStringArrayList(saveListKey));
                        //startActivity(switchtoMain);
                    }
                }
        );


    }

    private void cancelResult(){
        switchtoMain=new Intent();
        setResult(Activity.RESULT_CANCELED,switchtoMain);
        finish();
    }
}