package com.msanduku.challenge.lib;

import com.msanduku.challenge.model.Users;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;

/**
 *
 * @author Clifford Owino
 */
public class ExcelRowMapper implements RowMapper<Users> {

    @Override
    public Users mapRow(RowSet rowSet) throws Exception {
        Users users = new Users();
        users.setEmpID(rowSet.getColumnValue(0));
        users.setNamePrefix(rowSet.getColumnValue(1));
        users.setFirstName(rowSet.getColumnValue(2));
        users.setMiddleInitial(rowSet.getColumnValue(3));
        users.setLastName(rowSet.getColumnValue(4));
        users.setGender(rowSet.getColumnValue(5));
        users.seteMail(rowSet.getColumnValue(6));        
        users.setFatherName(rowSet.getColumnValue(7));
        users.setMotherName(rowSet.getColumnValue(8)); 
        users.setMotherMaidenName(rowSet.getColumnValue(9));
        users.setDateofBirth(rowSet.getColumnValue(10));

        return users;
    }
}
