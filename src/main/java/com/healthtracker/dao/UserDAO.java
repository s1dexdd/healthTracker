package com.healthtracker.dao;

import com.healthtracker.model.User;
import com.healthtracker.util.DBConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.sql.Statement;

public class UserDAO {


    private static final String INSERT_USERS_SQL =
            "INSERT INTO \"USER\" (name, height_cm, start_weight_kg, target_weight_kg, age, gender, activity_level) VALUES (?, ?, ?, ?,?,?,?)";

    private static final String SELECT_USER_BY_ID =
            "SELECT user_id, name, height_cm, start_weight_kg, target_weight_kg, age, gender, activity_level FROM \"USER\" WHERE user_id=?";

    public int insertUser(User user) {

        try (Connection connection = DBConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setInt(2, user.getHeightCm());
            preparedStatement.setBigDecimal(3, user.getStartWeightKg());
            preparedStatement.setBigDecimal(4, user.getTargetWeightKg());
            preparedStatement.setInt(5,user.getAge());
            preparedStatement.setString(6,user.getGender().name());
            preparedStatement.setString(7, user.getActivityLevel().name());

            preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    user.setUserId(generatedId);
                    System.out.println(" Пользователь " + user.getName() + " успешно добавлен c ID:"+ generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            System.err.println(" Ошибка при добавлении пользователя");
            e.printStackTrace();
        }
        return -1;
    }

    public User selectUser(int userId){
        User user= null;


        try (Connection connection=DBConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)){


            preparedStatement.setInt(1, userId);

            try (ResultSet rs = preparedStatement.executeQuery()) {

                if(rs.next()){
                    int id = rs.getInt("user_id");
                    String name = rs.getString("name");
                    int height = rs.getInt("height_cm");
                    BigDecimal startWeight = rs.getBigDecimal("start_weight_kg");
                    BigDecimal targetWeight = rs.getBigDecimal("target_weight_kg");
                    int age =rs.getInt("age");
                    User.Gender gender=User.Gender.valueOf(rs.getString("gender"));
                    User.ActivityLevel activityLevel=User.ActivityLevel.valueOf(rs.getString("activity_level"));

                    user = new User(id, name, height, startWeight, targetWeight, age, gender, activityLevel);
                }
            }
        }catch (SQLException e){
            System.err.println(" Ошибка при выборе пользователя");
            e.printStackTrace();
        }
        return user;
    }
}
