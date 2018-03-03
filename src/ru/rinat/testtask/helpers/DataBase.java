package ru.rinat.testtask.helpers;

import ru.rinat.testtask.beans.Job;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * Класс(Singleton) для взаимодействующия с БД
 * 
 * @author Isangulov Rinat
 */
public class DataBase {    
    
    /** Свойство - соединение с БД */
    private Connection con; 

    /** Свойство - единственный экземпляр данного класса */
    public static DataBase db;
    
    /** Свойство - главный логгер программы */
    private static Logger log = Logger.getRootLogger();
    
    /**
     * Метод, возвращающий единственный экземпляр данного класса
     *
     * @return Возвращает единственный экземпляр данного класса
     */
    public static DataBase getInstance() {
        if(db==null)
            db = new DataBase();
        return db;
    } 
    
    /**
     * Конструктор - создание нового объекта
     */
    private DataBase() {
        log.info("Попытка создания соединения с базой данных");
        
        try {
            Class.forName("org.postgresql.Driver");
            Properties property = new Properties();
            property.load(new FileInputStream("src/resources/config.properties"));
            String url = property.getProperty("db.url");
            String login = property.getProperty("db.login");
            String password = property.getProperty("db.password");
            con = DriverManager.getConnection(url,login,password);
            log.info("Соединение успешно установлено");
        }
        catch(ClassNotFoundException | SQLException  e) {
            log.error("Не удалось подключиться к базе данных", e);
            System.out.println("Не удалось подключиться к базе данных");
        } catch (IOException e) {
            log.error("Не удалось подключиться к файлу настроек приложения", e);
            System.out.println("Не удалось подключиться к файлу настроек приложения");
        }
    }

    /**
     * Метод удаления данных из БД
     *
     * @param removeSet Множество объектов, которые нужно удалить из БД
     * @throws SQLException Ошибка выполнения запроса
     */
    public void removeFromDB(Set<Job> removeSet) throws SQLException {
        PreparedStatement ps = con.prepareStatement("DELETE FROM job_tb WHERE DepCode = ? AND DepJob = ?");
        for(Job job : removeSet) {
            ps.setString(1, job.getDepCode());
            ps.setString(2, job.getDepJob());
            ps.executeUpdate();
        }   
        ps.close();
    }
    
    /**
     * Метод добавления данных в БД
     * 
     * @param addSet Множество объектов, которые нужно добавить в БД
     * @throws SQLException Ошибка выполнения запроса
     */
    public void addToDB(Set<Job> addSet) throws SQLException {
        PreparedStatement ps = con.prepareStatement("INSERT into job_tb(DepCode, DepJob, Description) values(?, ?, ?)");
        for(Job job : addSet) {
            ps.setString(1, job.getDepCode());
            ps.setString(2, job.getDepJob());
            ps.setString(3, job.getDescription());
            ps.executeUpdate();
        }   
        ps.close();
    }
    
    /**
     * Метод обновления данных в БД
     *
     * @param updateSet Множество объектов, которые нужно обновить в БД
     * @throws SQLException Ошибка выполнения запроса
     */
    public void updateDB(Set<Job> updateSet) throws SQLException {
        PreparedStatement ps = con.prepareStatement("UPDATE job_tb SET Description = ? WHERE DepCode = ? AND DepJob = ?");
        for(Job job : updateSet){
            ps.setString(1, job.getDescription());
            ps.setString(2, job.getDepCode());
            ps.setString(3, job.getDepJob());
            ps.executeUpdate();
        }   
        ps.close();
    }
    
    /**
     * Метод управления транзакциями
     *
     * @param b Переменная, характеризующая коммит соединения
     */
    public void setAutoCommit(boolean b) {
        try {
            con.setAutoCommit(b);
        } catch (SQLException e) {
            log.error("Не удалось перекомитить соединение", e);
            System.out.println("Не удалось перекомитить соединение");
        }
    }
    
    /**
     * Метод выполнения запросов
     * @throws SQLException Ошибка выполнения запросов
     */
    public void commit() throws SQLException {
        con.commit();
    }
    
    /**
     * Метод отмены ранее выполненных запросов
     */
    public void rollback() {
        try {
            con.rollback();
        } catch (SQLException e) {
            log.error("Не удалось отменить запросы в базу данных", e);
            System.out.println("Не удалось отменить запросы в базу данных");
        }
    }
    
    /**
     * Метод, возвращающий содержимое БД
     * 
     * @return Возвращает список объектов, содержащихся в БД
     * @throws SQLException Ошибка выполнения запроса
     */
    public Set<Job> executeQuery() throws SQLException {
        Set<Job> set = new HashSet<>();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("Select * from job_tb");
        while (rs.next()) {
            Job job = new Job();
            job.setDepCode(rs.getString("DepCode"));
            job.setDepJob(rs.getString("DepJob"));
            job.setDescription(rs.getString("Description"));
            set.add(job);
        }
        rs.close();
        stmt.close();
        
        return set;
    }
    
    /**
     * Метод, закрывающий соединение с БД
     */
    public void closeConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            log.error("Не удалось закрыть соединение", e);
            System.out.println("Не удалось закрыть соединение");
        }
    }   
}
