package com.healthtracker.dao;

import com.healthtracker.app.Main;
import com.healthtracker.model.User;
import com.healthtracker.model.WeightLog;
import com.healthtracker.model.WorkoutLog;

import java.math.BigDecimal;


public class BMRDAO {
    private final UserDAO userDAO;
    private final WeightLogDAO weightLogDAO;

    public BMRDAO(UserDAO userDAO, WeightLogDAO weightLogDAO) {
        this.userDAO = userDAO;
        this.weightLogDAO = weightLogDAO;
    }
    public int calculateBMR(int userId){
        User user=userDAO.selectUser(userId);
        BigDecimal currentWeight=weightLogDAO.getLatestWeight(userId);
        if(user==null || currentWeight==null){
            return 0;
        }
        double weight=currentWeight.doubleValue();
        int heighthCm=user.getHeightCm();
        int age=user.getAge();


        if(user.getGender()== User.Gender.MALE){
            return (int) Math.round(88.362+(13.397 * weight)+(4.799 * heighthCm) -(5.677 * age));
        }else{
            return (int) Math.round(447.593+(9.247 * weight)+(3.098 * heighthCm) -(4.330 * age));
        }
    }


}



