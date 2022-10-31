package com.example.ltdd_thsqlite_project;

/**
 * class dùng chung để đọc dữ liệu hiển thị lên ListView Sách và tác giả
 * bạn có thể bỏ class này viết class khác
 * Ở đây Tôi hơi làm biếng 1 chút là Tôi muốn viết 1 Class có các kiểu Object
 * để nó tự hiểu mọi kiểu dữ liệu đỡ phải viết lại nên bạn đọc có vẻ khó hiểu
 * Nhưng thôi ---> ráng lên
 * @author drthanh
 *
 */
public class InforData {
    private Object field1;
    private Object field2;
    private Object field3;
    public Object getField1() {
        return field1;
    }
    public void setField1(Object field1) {
        this.field1 = field1;
    }
    public Object getField2() {
        return field2;
    }
    public void setField2(Object field2) {
        this.field2 = field2;
    }
    public Object getField3() {
        return field3;
    }
    public void setField3(Object field3) {
        this.field3 = field3;
    }
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.field1 +" - " +this.field2 +" - "+this.field3;
    }
}

