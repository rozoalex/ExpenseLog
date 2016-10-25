package expenselog.pa2.expenselog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


//
//
public class askNewExpense extends AppCompatActivity {

    final String saveListKey = "eledListClone";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_add);

        final EditText title = (EditText)findViewById(R.id.titleEditText);
        final EditText notes = (EditText)findViewById(R.id.editText5);
        Button ok = (Button)findViewById(R.id.okButton);
        Button no = (Button)findViewById(R.id.button2);
        final Intent switchtoMain = new Intent(this, MainActivity.class);

        ok.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        if(title==null ){
                            return;
                        }
                        if(!title.getText().toString().trim().isEmpty()){
                            switchtoMain.putExtra("title",title.getText().toString());
                            if(notes==null){
                                switchtoMain.putExtra("notes","");
                            }else{
                                switchtoMain.putExtra("notes",notes.getText().toString());
                            }
                            Toast.makeText(getApplicationContext(), "New expense:\n"+title.getText(), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Empty expense", Toast.LENGTH_SHORT).show();
                        }
                        switchtoMain.putExtra(saveListKey,getIntent().getExtras().getStringArrayList(saveListKey));
                        startActivity(switchtoMain);
                    }
                }
        );

        no.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "cancelled", Toast.LENGTH_SHORT).show();
                        switchtoMain.putExtra(saveListKey,getIntent().getExtras().getStringArrayList(saveListKey));
                        startActivity(switchtoMain);
                    }
                }
        );


    }
}