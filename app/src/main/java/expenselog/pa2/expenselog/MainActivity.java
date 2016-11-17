package expenselog.pa2.expenselog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
   The list adapter and data entry are implemented as form of inner class
   option menu, context menu are implemented
   supports add, delete, and contact(which shows my contact info)
   The fields are not saved with local files or SQLlite
   Thus are fields are gone after shutting down the program
   the final fields in this class are just keys for getting information from intent
   two items are added to the arraylist as default
   the items will not be added after all entries are deleted, it only adds once at the start of the program

    */
public class MainActivity extends AppCompatActivity {

    ListView lv;
    Toolbar myToolbar;
    //ArrayList<ExpenseLogEntryData> eledList;//useless
    //ExpenseTrackerAdapter eta;
    SimpleCursorAdapter sca;
    final String saveListKey = "eledListClone";
    Cursor cursor;

    DatabaseHelper dbh ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dbh = new DatabaseHelper(this);
        checkIfFirstTime();
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);//find the tool bar
        setSupportActionBar(myToolbar);//set it
        //eledList=new ArrayList<>();//initialize the list
        //RECOVER FROM DB HERE
        lv=(ListView) findViewById(R.id.expenseListView);//find the list view
        registerForContextMenu(lv);//set the list view a context menu for deleting
        //eta = new ExpenseTrackerAdapter();//initialize the adapter
        cursor=dbh.getAll();
        sca = new SimpleCursorAdapter(this,
                R.layout.expense_entry,
                cursor,
                new String[]{"TITLE","NOTES","DATE"},
                new int[]{R.id.disccc,R.id.notes,R.id.datetime},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        //lv.setAdapter(eta);//set the adapter
        lv.setAdapter(sca);//use simple adapter

    }

    private void checkIfFirstTime() {
        SharedPreferences settings = getSharedPreferences("versionFile",0);

        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Main", "First time");
            dbh.insert("Welcome","click up right corner to add more",setDate());
            dbh.insert("sup~","hold me to delete me",setDate());
            settings.edit().putBoolean("my_first_time", false).commit();
        }else{
            Log.d("Main", "Not first time");
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.longclickoptionmenu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo i = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()){
            case R.id.deleteOption:
                //ExpenseLogEntryData temp = eledList.remove(i.position);
                Integer deletedRows=dbh.remove(i.id);
                if(deletedRows > 0){
                    Log.i("Main","data base data deleted");}
                else{
                    Log.w("Main","data base data not deleted");}
                setListView();
                Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_LONG).show();
                return true;
            case R.id.edit: Toast.makeText(getApplicationContext(), "Not implemented yet...", Toast.LENGTH_SHORT).show();

                break;
        }

        return super.onContextItemSelected(item);
    }



    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addingmenu,menu);
        //Toast.makeText(getApplicationContext(), "show menu", Toast.LENGTH_LONG).show();
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedId = item.getItemId();

        switch (selectedId){
            case R.id.contactMe:
                Toast.makeText(getApplicationContext(), "Yuanze Hu\nemail:alex13@brandeis.edu", Toast.LENGTH_LONG).show();
                break;
            case R.id.addText: Toast.makeText(getApplicationContext(), "Add new expense", Toast.LENGTH_SHORT).show();
                askNewExpense();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void askNewExpense() {
        Intent i=new Intent(this, askNewExpense.class);
        //i.putExtra(saveListKey,saveList());//Convert the eledlist to an array list and send through intent
        startActivityForResult(i,1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode== RESULT_OK){
                ArrayList<String> results = data.getStringArrayListExtra("results");
                addNewExpense(results.get(0),results.get(1));
                //Log.i("Main","onActivityResult");
                setListView();
            }
        }

    }

    private void setListView(){
        cursor=dbh.getAll();
        sca = new SimpleCursorAdapter(this,
                R.layout.expense_entry,
                cursor,
                new String[]{"TITLE","NOTES","DATE"},
                new int[]{R.id.disccc,R.id.notes,R.id.datetime},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        //lv.setAdapter(eta);//set the adapter
        lv.setAdapter(sca);//use simple adapter
    }

    private void addNewExpense(String des, String notes) {
        long id = dbh.insert(des,notes,setDate());
        //eledList.add(new ExpenseLogEntryData(des,notes,id));
        //eta.notifyDataSetChanged();
    }

    private void addNewExpense(String des, String notes,String date){
        long id = dbh.insert(des,notes,date);
        //eledList.add(new ExpenseLogEntryData(des,notes,date,id));

    }

    public String setDate(){
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
    }


    /*
    This is the list adapter
     */
//    private class ExpenseTrackerAdapter extends BaseAdapter{
//
//
//        private ExpenseTrackerAdapter(){
//            if(eledList.isEmpty()&&getIntent().getStringArrayListExtra(saveListKey)==null) {
//                addNewExpense("Title of this item", "Some notes for this item");
//                addNewExpense("Books", "bought some textbooks for $145\nFreaking expensive! ");
//            }
//
//        }
//
//
//
//        @Override
//        //return the ArrayList's size.
//        public int getCount() {
//            return eledList.size();
//        }
//
//        @Override
//        //the index-th object in the ArrayList
//        //if the position is not valid
//        //return null
//        public Object getItem(int position) {
//            return (position<eledList.size()&&position>=0)?eledList.get(position):null;
//        }
//
//        @Override
//        //return its argument, index.
//        //what ???
//        public long getItemId(int position) {
//            return eledList.get(position).getID();
//        }
//
//        @Override
//        //this method does the main work of inflating the xml for an entry (expense_entry.xml).
//        // Using the View returned by inflate()
//        // it will get the references to the individual fields in the View
//        // and populate them with corresponding values from the ArrayList data.
//        // Recall that this method will be called multiple times, once for each item in the ArrayList.
//        public View getView(int position, View convertView, ViewGroup parent) {
//            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//            View temp = inflater.inflate(R.layout.expense_entry, parent, false);
//            TextView des,notes,date;
//            des=(TextView)temp.findViewById(R.id.disccc);
//            notes=(TextView)temp.findViewById(R.id.notes);
//            date=(TextView)temp.findViewById(R.id.datetime);
//
//            String s = eledList.get(position).getHeading();
//
//            des.setText(s);
//            notes.setText(eledList.get(position).getNotes());
//            date.setText(eledList.get(position).getDates());
//
//
//            return (temp);
//        }
//    }




    /*
   This is the data entry
    */
//    private class ExpenseLogEntryData{
//        private String descptHeading;
//        private String notes;
//        private String date;
//        private long ID;
//
//        private ExpenseLogEntryData(String d) {
//            descptHeading=d;
//        }
//
//
//        private ExpenseLogEntryData(String d, String n,long id) {
//            descptHeading = d;
//            notes = n;
//            ID=id;
//            setDate();
//        }
//
//        public void setDate(){
//            date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
//        }
//
//        private ExpenseLogEntryData(String d, String n,String dt,long id) {
//            ID = id;
//            descptHeading = d;
//            notes = n;
//            date=dt;
//        }
//
//        private long getID(){
//            return ID;
//        }
//
//        private String getHeading(){
//            return descptHeading;
//        }
//
//        private String getNotes(){
//            return notes;
//        }
//
//        private String getDates(){
//            return date;
//        }
//
//        private void setHeading(String inp){
//            descptHeading=inp;
//        }
//
//        private void setNotes(String inp){
//            notes=inp;
//        }
//
//
//
//        private void setDate(String d){
//            date=d;
//        }
//
//        private void setDate(int yy,int mm,int dd,int hh,int min,int ss){
//            Calendar c=Calendar.getInstance();
//            c.set(yy,mm,dd,hh,min,ss);
//            date=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(c.getTime());
//
//        }
//
//    }


}
