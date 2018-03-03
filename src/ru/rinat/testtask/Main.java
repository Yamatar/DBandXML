package ru.rinat.testtask;

import ru.rinat.testtask.beans.Job;
import ru.rinat.testtask.helpers.XMLHelper;
import ru.rinat.testtask.helpers.DataBase;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.xml.sax.SAXException;

/** Главный класс программы
 *
 * @author Isangulov Rinat
 */
public class Main {
    
    /** Единый экземпляр сканера для всех методов данного класса */
    private static Scanner sc = new Scanner(System.in);
    
    /** Главный логгер программы */
    private static Logger log;
    
    /** Главный метод. Запускает меню программы
     *  При вводе числа 0 закрывает ресурсы и завершает программу
     * 
     * @param args Параметры командной строки
     */
    public static void main(String[] args) {
        
        boolean isExit = false;
        
        PropertyConfigurator.configure("src/resources/config.properties");
        log = Logger.getRootLogger();
        
        log.info("Старт программы");
        
        while(!isExit) {
            System.out.println("Меню: ");
            System.out.println("1: Выгрузить БД в XML файл.");
            System.out.println("2: Синхронизировать БД по XML файлу.");
            System.out.println("3: Вывести на экран содержимое БД");
            System.out.println("4: Вывести на экран содержимое XML-файла");
            System.out.println("0: Выход из программы.");
        
            int number = sc.nextInt();
            switch(number){
                case 1: 
                    uploadData();
                    break;
                case 2:
                    synchronizeData();
                    break;
                case 3:
                    printDB();
                    break;
                case 4:
                    printXMLFile();
                    break;
                case 0:
                    isExit = true;
                    break;
                default: 
                    System.out.println("Неправильный ввод. Попробуйте еще раз.");
            }
            
            System.out.println();
        }
        log.info("Конец программы");
        DataBase.getInstance().closeConnection();
        sc.close();
    }
    
    /** 
     * Метод выгрузки данных из БД в XML-файл
     */
    public static void uploadData() {
        log.info("Старт операции выгрузки базы данных в xml-файл");
        try {
            System.out.println("Введите имя файла: ");
            String fileName = sc.next();
            if(!fileName.endsWith(".xml"))
                fileName+=".xml";
            log.info("Введено имя файла " + fileName);
            File file = new File(fileName);
            file.createNewFile();
            Set<Job> set = DataBase.getInstance().executeQuery();
            XMLHelper.parseListToXML(set,file);
            log.info("Операция выгрузки базы данных прошла успешно");
            System.out.println("Операция выгрузки базы данных прошла успешно");
        } catch (SQLException e) {
            log.error("Не удалось выполнить запрос в базу данных",e);
            System.out.println("Не удалось выполнить запрос в базу данных");
        } catch (IOException e) {
            log.error("Не удалось подключиться к файлу",e);
            System.out.println("Не удалось подключиться к файлу");
        } catch (JAXBException e) {
            log.error("Не удалось записать данные в xml-файл",e);
            System.out.println("Не удалось записать данные в xml-файл");
        }
    }
    
    /**
     * Метод синхронизации данных БД по заданному XML-файлу
     */
    public static void synchronizeData() {
        log.info("Старт операции синхронизации базы данных по xml-файлу");
        try {
            System.out.println("Введите имя файла: ");
            String fileName = sc.next();
            if(!fileName.endsWith(".xml"))
                fileName+=".xml";
            log.info("Введено имя файла " + fileName);
            
            Set<Job> fromXML = XMLHelper.readXML(new File(fileName));
            Set<Job> fromDB = DataBase.getInstance().executeQuery();
            
            Set<Job> removeSet = new HashSet<>(fromDB);
            removeSet.removeAll(fromXML);
            
            Set<Job> addSet = new HashSet<>(fromXML);
            addSet.removeAll(fromDB);
            
            Set<Job> DBandXML = new HashSet<>(fromXML);
            Set<Job> updateSet = new HashSet<>();
            DBandXML.retainAll(fromDB);
            
            for(Job job : DBandXML) 
                for(Job job1 : fromDB) {
                    if(job.equals(job1) && !Objects.equals(job.getDescription(), job1.getDescription()))
                        updateSet.add(job);
                }
         
            DataBase.getInstance().setAutoCommit(false);
            
            updateDB(removeSet, addSet, updateSet);
            
            DataBase.getInstance().setAutoCommit(true);
            
            log.info("Операция синхронизации прошла успешно");
            System.out.println("Операция синхронизации прошла успешно");
        }
        catch (XMLException e) {
            log.error("Неправильный формат данных  в xml-файле", e);
            System.out.println("Неправильный формат данных  в xml-файле");
        } catch (SQLException e) {
            log.error("Не удалось выполнить запрос в базу данных", e);
            System.out.println("Не удалось выполнить запрос в базу данных");
        } catch (IOException e) {
            log.error("Не удалось подключиться к файлу",e);
            System.out.println("Не удалось подключиться к файлу");
        } catch (ParserConfigurationException | SAXException e ) {
            log.error("Не удалось обработать xml-файл", e);
            System.out.println("Не удалось обработать xml-файл");
        } catch(Exception e) {
            log.error("Возникла ошибка при попытке синхронизации", e);
            System.out.println("Возникла ошибка при попытке синхронизации");
        }
    }
    
    /** 
     * Метод, который обновляет данные в БД
     *
     * @param removeSet Множество объектов, которые нужно удалить из БД
     * @param addSet Множество объектов, которые нужно добавить в БД
     * @param updateSet Множество объектов, которые нужно обновить в БД
     */
    public static void updateDB(Set<Job> removeSet, Set<Job> addSet, Set<Job> updateSet) {
        try {
            DataBase.getInstance().removeFromDB(removeSet);
            DataBase.getInstance().addToDB(addSet);
            DataBase.getInstance().updateDB(updateSet);
            DataBase.getInstance().commit();
        } catch (SQLException e) {
            DataBase.getInstance().rollback();
            log.error("Не удалось обновить данные в базе данных", e);
            System.out.println("Не удалось обновить данные в базе данных");
        }
    }
    
    /**
     * Метод, который выводит в консоль содержимое БД
     */
    public static void printDB(){
        log.info("Вывод на экран содержимого БД");
        try {
            Set<Job> fromDB = DataBase.getInstance().executeQuery();
            System.out.println("DataBase: ");
            for(Job job : fromDB) {
                System.out.println("Job:");
                System.out.println("DepCode = " + job.getDepCode());
                System.out.println("DepJob = " + job.getDepJob());
                System.out.println("Description = " + job.getDescription());
            }
            System.out.println("------------------");
            log.info("Операция вывода прошла успешно");
        } catch (SQLException e) {
            log.error("Не удалось выполнить запрос в базу данных", e);
            System.out.println("Не удалось выполнить запрос в базу данных");
        }
    }
    
    /**
     * Метод, который выводит в консоль содержимое XML-файла
     */
    public static void printXMLFile(){
        log.info("Вывод на экран содержимого XML-файла");
        try {
            System.out.println("Введите имя файла: ");
            String fileName = sc.next();
            if(!fileName.endsWith(".xml"))
                fileName+=".xml";
            log.info("Введено имя файла " + fileName);
            Set<Job> fromXML = XMLHelper.readXML(new File(fileName));
            System.out.println("XML - файл : " + fileName);
            for(Job job : fromXML) {
                System.out.println("Job:");
                System.out.println("DepCode = " + job.getDepCode());
                System.out.println("DepJob = " + job.getDepJob());
                System.out.println("Description = " + job.getDescription());
            }
            System.out.println("------------------");
            log.info("Операция вывода прошла успешно");
        }
        catch(Exception e){
            log.error("Не удалось прочитать XML-файл", e);
            System.out.println("Не удалось прочитать XML-файл");
        }
    }
}
