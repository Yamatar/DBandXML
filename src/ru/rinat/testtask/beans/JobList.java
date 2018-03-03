
package ru.rinat.testtask.beans;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Класс - список объектов, которые надо записать в XML-файл
 *
 * @author Isangulov Rinat
 */
@XmlRootElement(name = "jobs")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobList {
    
    /** Свойство - список объектов для записи */
    @XmlElement(name = "job")
    private List<Job> jobs;
    
    /**
     * Функция для получения списка объектов для записи {@link JobList#jobs}
     * @return Возвращает список объектов для записи
     */
    public List<Job> getJobs() {
        return jobs;
    }

    /**
     * Процедура определения списка объектов для записи {@link JobList#jobs}
     * @param jobs Новый список объектов для записи
     */
    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }   
}
