package com.example.ltdd_thsqlite_project;

import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
/**
 * hàm hình chính cho phép chọn các thao tác
 * @author drthanh
 *
 */
public class MainActivity extends Activity {

    Button btnCreateDatabase=null;
    Button btnAddCategory=null;
    Button btnShowCategoryList=null;
    Button btnShowCategoryList2=null;
    Button btnTransaction=null;
    Button btnShowDetail=null;
    Button btnAddComputer=null;
    public static final int OPEN_AUTHOR_DIALOG=1;
    public static final int SEND_DATA_FROM_AUTHOR_ACTIVITY=2;
    SQLiteDatabase database=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAddCategory=(Button) findViewById(R.id.btnAddCategory);
        btnAddCategory.setOnClickListener(new MyEvent());
        btnShowCategoryList=(Button) findViewById(R.id.btnShowCategoryList);
        btnShowCategoryList.setOnClickListener(new MyEvent());
        btnAddComputer=(Button) findViewById(R.id.btnAddComputer);
        btnAddComputer.setOnClickListener(new MyEvent());
        getDatabase();
    }
    /**
     * hàm kiểm tra xem bảng có tồn tại trong CSDL hay chưa
     * @param database - cơ sở dữ liệu
     * @param tableName - tên bảng cần kiểm tra
     * @return trả về true nếu tồn tại
     */
    public boolean isTableExists(SQLiteDatabase database, String tableName) {
        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
    /**
     * hàm tạo CSDL và các bảng liên quan
     * @return
     */
    public SQLiteDatabase getDatabase()
    {
        try
        {
            database=openOrCreateDatabase("mydata.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
            if(database!=null)
            {
                if(isTableExists(database,"tblAuthors"))
                    return database;
                database.setLocale(Locale.getDefault());
                database.setVersion(1);
                String sqlAuthor="create table tblAuthors ("
                        +"id integer primary key autoincrement,"
                        +"firstname text, "
                        +"lastname text)";
                database.execSQL(sqlAuthor);
                String sqlBook="create table tblBooks ("
                        +"id integer primary key autoincrement,"
                        +"title text, "
                        +"dateadded date,"
                        +"authorid integer not null constraint authorid references tblAuthors(id) on delete cascade)";
                database.execSQL(sqlBook);
                //Cách tạo trigger khi nhập dữ liệu sai ràng buộc quan hệ
                String sqlTrigger="create trigger fk_insert_book before insert on tblBooks "
                        +" for each row "
                        +" begin "
                        +" 	select raise(rollback,'them du lieu tren bang tblBooks bi sai') "
                        +" 	where (select id from tblAuthors where id=new.authorid) is null ;"
                        +" end;";
                database.execSQL(sqlTrigger);
                Toast.makeText(MainActivity.this, "OK OK", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        return database;
    }
    public void createDatabaseAndTrigger()
    {
        if(database==null)
        {
            getDatabase();
            Toast.makeText(MainActivity.this, "OK OK", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * hàm mở màn hình nhập Tác giả
     */
    public void showInsertAuthorDialog()
    {
        Intent intent=new Intent(MainActivity.this, CreateCategoryActivity.class);
        startActivityForResult(intent, OPEN_AUTHOR_DIALOG);
    }
    /**
     * hàm xem danh sách tác giả dùng Activity
     * Tôi làm 2 cách để các bạn ôn tập lại ListView
     * bạn gọi hàm nào thì gọi 1 thôi showAuthorList1 hoặc showAuthorList2
     */
    public void showAuthorList1()
    {
        Intent intent=new Intent(MainActivity.this, ShowListCategoryActivity.class);
        startActivity(intent);
    }
    /**
     * hàm xem danh sách tác giả dùng ListActivity
     * Tôi làm 2 cách để các bạn ôn tập lại ListView
     * bạn gọi hàm nào thì gọi 1 thôi showAuthorList1 hoặc showAuthorList2
     */
    public void showAuthorList2()
    {
        Intent intent=new Intent(MainActivity.this, ShowListCategoryActivity2.class);
        startActivity(intent);
    }
    /**
     * Tôi cung cấp thêm hàm này để các bạn nghiên cứu thêm về transaction
     */
    public void interactDBWithTransaction()
    {
        if(database!=null)
        {
            database.beginTransaction();
            try
            {
                //làm cái gì đó tùm lum ở đây,
                //chỉ cần có lỗi sảy ra thì sẽ kết thúc transaction
                ContentValues values=new ContentValues();
                values.put("firstname", "xx");
                values.put("lastname", "yyy");
                database.insert("tblAuthors", null, values);
                database.delete("tblAuthors", "ma=?", new String[]{"x"});
                //Khi nào hàm này được gọi thì các thao tác bên trên mới thực hiện được
                //Nếu nó không được gọi thì mọi thao tác bên trên đều bị hủy
                database.setTransactionSuccessful();
            }
            catch(Exception ex)
            {
                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
            finally
            {
                database.endTransaction();
            }
        }
    }
    /**
     * hàm xử lý kết quả trả về
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==SEND_DATA_FROM_AUTHOR_ACTIVITY)
        {
            Bundle bundle= data.getBundleExtra("DATA_AUTHOR");
            String firstname=bundle.getString("firstname");
            String lastname=bundle.getString("lastname");
            ContentValues content=new ContentValues();
            content.put("firstname", firstname);
            content.put("lastname", lastname);
            if(database!=null)
            {
                long authorid=database.insert("tblAuthors", null, content);
                if(authorid==-1)
                {
                    Toast.makeText(MainActivity.this,authorid+" - "+ firstname +" - "+lastname +" ==> insert error!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, authorid+" - "+firstname +" - "+lastname +" ==>insert OK!", Toast.LENGTH_LONG).show();
                }
            }

        }
    }
    /**
     * class xử lý sự kiện
     * @author drthanh
     *
     */
    private class MyEvent implements OnClickListener
    {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(v.getId()==R.id.btnAddCategory)
            {
                showInsertAuthorDialog();
            }
            else if(v.getId()==R.id.btnShowCategoryList)
            {
                showAuthorList1();
            }

            else if(v.getId()==R.id.btnAddComputer)
            {
                Intent intent=new Intent(MainActivity.this, AddComputerActivity.class);
                startActivity(intent);
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
