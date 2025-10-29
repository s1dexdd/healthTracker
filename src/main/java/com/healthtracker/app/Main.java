package com.healthtracker.app;
import com.healthtracker.init.DatabaseInitializer;
import com.healthtracker.dao.UserDAO;
import com.healthtracker.model.User;

import java.math.BigDecimal;

public class Main {
public static void main(String[] args){

    DatabaseInitializer.initialise();




    System.out.println("\n тест добавления пользователя \n");

    BigDecimal startWeight= new BigDecimal("85.5");
    BigDecimal targetWeight=new BigDecimal( 70.0);
    UserDAO userDAO=new UserDAO();
    User newUser=new User(
            "Matveev Danil",
            184,
            startWeight,
            targetWeight
    );
    int generatedUserId = userDAO.insertUser(newUser);

    System.out.println("\n тест 1 УРА \n");

    System.out.println("\n тест извлечения пользователя \n");
    User fetchedUser=userDAO.selectUser(generatedUserId);
    if (fetchedUser != null){
        System.out.println("тест 2 УРА");
        System.out.println(fetchedUser);

    }
    else{
        System.out.println("эх id не найден");
    }
    System.out.println("\n конец \n");
}
}
