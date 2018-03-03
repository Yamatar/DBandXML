package ru.rinat.testtask.beans;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Класс, характеризующий строку в БД.
 *
 * @author Isangulov Rinat
 */
@XmlRootElement(name = "job")
@XmlAccessorType (XmlAccessType.FIELD)
public class Job {

    /** Свойства - столбцы в БД */
    private String depCode;
    private String depJob;
    private String description;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.depCode);
        hash = 53 * hash + Objects.hashCode(this.depJob);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Job other = (Job) obj;
        if (!Objects.equals(this.depCode, other.depCode)) {
            return false;
        }
        if (!Objects.equals(this.depJob, other.depJob)) {
            return false;
        }
        
        return true;
    }

    /**
     * Функция для получения значения поля {@link Job#depCode}
     * @return Возвращает номер отдела
     */
    public String getDepCode() {
        return depCode;
    }

    /**
     * Функция для получения значения поля {@link Job#depJob}
     * @return Возвращает название должности
     */
    public String getDepJob() {
        return depJob;
    }

    /**
     * Функция для получения значения поля {@link Job#description}
     * @return Возвращает описание должности
     */
    public String getDescription() {
        return description;
    }

    /**
     * Процедура определения номера отдела {@link Job#depCode}
     * @param depCode Новое значение номера отдела
     */
    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    /**
     * Процедура определения названия должности {@link Job#depJob}
     * @param depJob Новое значение названия должности
     */
    public void setDepJob(String depJob) {
        this.depJob = depJob;
    }

    /**
     * Процедура определения описания должности {@link Job#description}
     * @param description Новое значение описания должности
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Конструктор - создание нового объекта
     */
    public Job() {
    }
}
