package com.healthtracker.init;

import com.healthtracker.util.DBConfig;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {


    private static final  String CREATE_USERS_TABLE=
            "CREATE TABLE IF NOT EXISTS \"USER\" (" +
                    "user_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(100) NOT NULL," +
                    "height_cm INT NOT NULL," +
                    "start_weight_kg DECIMAL(5,2) NOT NULL," +
                    "target_weight_kg DECIMAL(5,2) NOT NULL," +
                    "age INT NOT NULL," +
                    "gender VARCHAR(10) NOT NULL," +
                    "activity_level VARCHAR(10) NOT NULL" +
                    ");";

    private static final String CREATE_FOOD_LOG_TABLE =
            "CREATE TABLE IF NOT EXISTS \"FOOD_LOG\" (" +
                    "food_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id INT NOT NULL," +
                    "log_date TIMESTAMP NOT NULL," +
                    "description VARCHAR(255) NOT NULL," +
                    "meal_type VARCHAR(50) NOT NULL," +
                    "calories_per_100g INT NOT NULL," +
                    "protein_per_100g DECIMAL(5,1)," +
                    "fats_per_100g DECIMAL(5,1)," +
                    "carbs_per_100g DECIMAL(5,1)," +
                    "portion_size_grams INT NOT NULL," +
                    "calories INT NOT NULL," +
                    "protein_g DECIMAL(5,1)," +
                    "fats_g DECIMAL(5,1)," +
                    "carbs_g DECIMAL(5,1)," +
                    "FOREIGN KEY (user_id) REFERENCES \"USER\"(user_id) ON DELETE CASCADE" +
                    ");";

    private static final String CREATE_WORKOUT_LOG_TABLE =
            "CREATE TABLE IF NOT EXISTS \"WORKOUT_LOG\" (" +
                    "workout_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id INT NOT NULL," +
                    "log_date TIMESTAMP NOT NULL," +
                    "type VARCHAR(100) NOT NULL," +
                    "duration_minutes INT NOT NULL," +
                    "calories_burned INT NOT NULL," +
                    "calories_burned_per_minute INT NOT NULL," +
                    "FOREIGN KEY (user_id) REFERENCES \"USER\"(user_id) ON DELETE CASCADE" +
                    ");";

    private static final String CREATE_WEIGHT_LOG_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS \"WEIGHT_LOG\" (" +
                    "  log_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "  user_id INT NOT NULL," +
                    "  log_date DATE NOT NULL," +
                    "  current_weight_kg DECIMAL(5, 2) NOT NULL," +
                    "  FOREIGN KEY (user_id) REFERENCES \"USER\"(user_id) ON DELETE CASCADE" +
                    ")";

    private static final String INSERT_TEST_USER_SQL =
            "MERGE INTO \"USER\" (user_id, name, height_cm, start_weight_kg, target_weight_kg, age, gender, activity_level) " +
                    "KEY (user_id) VALUES (1, 'Тестовый Пользователь', 180, 85.0, 75.0, 30, 'MALE', 'MID')";

    private static final String INSERT_INITIAL_WEIGHT_SQL =
            "INSERT INTO \"WEIGHT_LOG\" (user_id, log_date, current_weight_kg) " +
                    "SELECT 1, CURRENT_DATE(), 85.0 WHERE NOT EXISTS (SELECT 1 FROM \"WEIGHT_LOG\" WHERE user_id = 1 AND log_date = CURRENT_DATE())";


    public static void initialise(){
        try (Connection connection=DBConfig.getConnection();
             Statement statement = connection.createStatement()){


            statement.executeUpdate(CREATE_USERS_TABLE);
            statement.executeUpdate(CREATE_FOOD_LOG_TABLE);
            statement.executeUpdate(CREATE_WORKOUT_LOG_TABLE);
            statement.executeUpdate(CREATE_WEIGHT_LOG_TABLE_SQL);


            statement.executeUpdate(INSERT_TEST_USER_SQL);
            statement.executeUpdate(INSERT_INITIAL_WEIGHT_SQL);

            System.out.println("база данных готова к работе. Добавлен тестовый пользователь (ID 1).");
        }catch(SQLException e){
            System.err.println("ошибка при инициализации базы данных");
            e.printStackTrace();
        }
    }
}