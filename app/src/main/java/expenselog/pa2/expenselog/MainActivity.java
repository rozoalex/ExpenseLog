package expenselog.pa2.expenselog;

import android.content.Intent;
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
import android.widget.ListView;
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
    ArrayList<ExpenseLogEntryData> eledList;
    ExpenseTrackerAdapter eta;
    final String saveListKey = "eledListClone";
    final String titleIntentKey="title";
    final String notesIntentKey="notes";
    DatabaseHelper dbh ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dbh = new DatabaseHelper(this);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);//find the tool bar
        setSupportActionBar(myToolbar);//set it
        if(eledList==null){
            eledList=new ArrayList<>();//initialize the list
        }

        lv=(ListView) findViewById(R.id.expenseListView);//find the list view
        registerForContextMenu(lv);//set the list view a context menu for deleting
//        Bundle resultData = getIntent().getExtras();//get the extras
//        if(resultData!=null){//if there is something in the extras ,get them
//            String t = resultData.getString(titleIntentKey);
//            String n = resultData.getString(notesIntentKey);
//            recoverArrayList(resultData.getStringArrayList(saveListKey));//recover the list from intent
//            Log.i("mylog","add new Expense");
//            if(t!=null&&n!=null){
//                addNewExpense(t,n);//add the new item
//            }
//        }
        eta = new ExpenseTrackerAdapter();//initialize the adapter
        lv.setAdapter(eta);//set the adapter


    }

    //recover all info from an arraylist of strings
    private void recoverArrayList(ArrayList<String> sal) {
        if(sal==null){return;}
        Log.i("mylog","recovering...");
        ExpenseLogEntryData temp = null;
        int counter = 0;
        for(String s:sal){
            switch (counter%3){
                case 0:
                    temp=new ExpenseLogEntryData(s);
                    break;
                case 1:
                    temp.setNotes(s);
                    break;
                case 2:
                    temp.setDate(s);
                    eledList.add(temp);
                    break;
            }
            counter++;
            Log.i("mylog",String.valueOf(eledList.size()));
            Log.i("mylog",s);
        }
    }

    public void onActivityResult(){

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
                eledList.remove(i.position);
                eta.notifyDataSetChanged();
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

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode== RESULT_OK){
                ArrayList<String> results = data.getStringArrayListExtra("results");
                addNewExpense(results.get(0),results.get(1));
                eta.notifyDataSetChanged();
            }
        }

    }

    //Convert the eledlist to an array list and send through intent
    private ArrayList<String> saveList() {
        ArrayList<String> saving=new ArrayList<>();
        for(ExpenseLogEntryData eled : eledList){
            saving.add(eled.getHeading());
            Log.i("mylog","saving....."+eled.getHeading());
            saving.add(eled.getNotes());
            saving.add(eled.getDates());
        }
        return saving;

    }




    private void addNewExpense(String des, String notes) {
        eledList.add(new ExpenseLogEntryData(des,notes));
        //eta.notifyDataSetChanged();
    }

    private void addNewExpense(String des, String notes,String date){
        eledList.add(new ExpenseLogEntryData(des,notes,date));
    }





    /*
    This is the list adapter
     */
    private class ExpenseTrackerAdapter extends BaseAdapter{


        private ExpenseTrackerAdapter(){
            if(eledList.isEmpty()&&getIntent().getStringArrayListExtra(saveListKey)==null) {
                addNewExpense("Title of this item", "Some notes for this item");
                addNewExpense("Books", "bought some textbooks for $145\nFreaking expensive! ");
            }

        }



        @Override
        //return the ArrayList's size.
        public int getCount() {
            return eledList.size();
        }

        @Override
        //the index-th object in the ArrayList
        //if the position is not valid
        //return null
        public Object getItem(int position) {
            return (position<eledList.size()&&position>=0)?eledList.get(position):null;
        }

        @Override
        //return its argument, index.
        //what ???
        public long getItemId(int position) {
            return position;
        }

        @Override
        //this method does the main work of inflating the xml for an entry (expense_entry.xml).
        // Using the View returned by inflate()
        // it will get the references to the individual fields in the View
        // and populate them with corresponding values from the ArrayList data.
        // Recall that this method will be called multiple times, once for each item in the ArrayList.
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View temp = inflater.inflate(R.layout.expense_entry, parent, false);
            TextView des,notes,date;
            des=(TextView)temp.findViewById(R.id.disccc);
            notes=(TextView)temp.findViewById(R.id.notes);
            date=(TextView)temp.findViewById(R.id.datetime);

            String s = eledList.get(position).getHeading();

            des.setText(s);
            notes.setText(eledList.get(position).getNotes());
            date.setText(eledList.get(position).getDates());


            return (temp);
        }
    }




    /*
   This is the data entry
    */
    private class ExpenseLogEntryData{
        private String descptHeading;
        private String notes;
        private String date;

        private ExpenseLogEntryData(String d) {
            descptHeading=d;
        }


        private ExpenseLogEntryData(String d, String n) {
            descptHeading = d;
            notes = n;
            setDate();
        }

        private ExpenseLogEntryData(String d, String n,String dt) {
            descptHeading = d;
            notes = n;
            date=dt;
        }


        private String getHeading(){
            return descptHeading;
        }

        private String getNotes(){
            return notes;
        }

        private String getDates(){
            return date;
        }

        private void setHeading(String inp){
            descptHeading=inp;
        }

        private void setNotes(String inp){
            notes=inp;
        }

        private void setDate(){
            date=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        }

        private void setDate(String d){
            date=d;
        }

        private void setDate(int yy,int mm,int dd,int hh,int min,int ss){
            Calendar c=Calendar.getInstance();
            c.set(yy,mm,dd,hh,min,ss);
            date=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(c.getTime());

        }

    }


}
