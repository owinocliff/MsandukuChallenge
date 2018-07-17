package com.msanduku.challenge.batch;

import com.msanduku.challenge.model.Users;
import org.springframework.batch.item.ItemProcessor;

/**
 *
 * @author Clifford Owino
 */
public class TaskItemProcessor implements ItemProcessor<Users, Users> {

    @Override
    public Users process(Users user) throws Exception {
        
        //sort the file then write a new file
        return user;
    }
}
