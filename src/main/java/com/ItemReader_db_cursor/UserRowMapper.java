package com.ItemReader_db_cursor;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
public class UserRowMapper implements RowMapper<User>{

    @Override
    public User mapRow(ResultSet rs,int rowNum)throws SQLException{
        //rs为游标方式
        User user= new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setAge(rs.getInt("age"));

        return user;
        //映射，从DB中以游标的方式抓取数据，然后return回user对象中
    }
}
