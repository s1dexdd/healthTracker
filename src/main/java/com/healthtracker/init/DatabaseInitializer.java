package com.healthtracker.init;
import com.healthtracker.util.DBConfig;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
public class DatabaseInitializer {
    private static final  String CREATE_USERS_TABLE=
            "CREATE TABLE IF NOT EXISTS Users (" +
            "user_id INT PRIMARY KEY AUTO_INCREMENT," +
            "name VARCHAR(100) NOT NULL," +
            "height_cm INT NOT NULL," +
            "start_weight_kg DECIMAL(5,2) NOT  NULL," +
            "target_weight_kg DECIMAL(5,2) NOT  NULL" +
            ");";
    private static final String CREATE_FOOD_LOG_TABLE=
            "CREATE TABLE IF NOT EXISTS Food_log (" +
            "food_id INT PRIMARY KEY AUTO_INCREMENT," +
            "user_id INT NOT NULL," +
            "log_date DATE NOT NULL," +
            "description VARCHAR(255) NOT NULL," +
            "calories INT NOT NULL," +
            "protein_g DECIMAL (5,1)," +
            "fats_g DECIMAL (5,1)," +
            "carbs_g DECIMAL (5,1)," +
            "FOREIGN KEY (user_id) REFERENCES Users(user_id)"+
            ");";
    private static final String CREATE_WORKOUT_LOG_TABLE=
            "CREATE TABLE IF NOT EXISTS Workout_Log (" +
            "workout_id INT PRIMARY KEY AUTO_INCREMENT," +
            "user_id INT NOT NULL," +
            "log_date DATE NOT NULL," +
            "type VARCHAR(100) NOT NULL," +
            "duration_minutes INT NOT NULL," +
            "calories_burned INT NOT NULL," +
            "FOREIGN KEY (user_id) REFERENCES Users(user_id)"+
            ");";
    public static void initialise(){
        try (Connection connection=DBConfig.getConnection();
            Statement statement = connection.createStatement()){
            statement.executeUpdate(CREATE_USERS_TABLE);
            statement.executeUpdate(CREATE_FOOD_LOG_TABLE);
            statement.executeUpdate(CREATE_WORKOUT_LOG_TABLE);
            System.out.println("база данных готова к работе");
        }catch(SQLException e){
            System.err.println("ошибка при инициализации базы данных");
        }
    }
}
