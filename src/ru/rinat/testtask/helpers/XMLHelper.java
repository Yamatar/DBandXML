
package ru.rinat.testtask.helpers;

import ru.rinat.testtask.beans.Job;
import ru.rinat.testtask.beans.JobList;
import ru.rinat.testtask.XMLException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Класс для обработки XML-файлов
 * 
 * @author Isangulov Rinat
 */
public class XMLHelper {
    
    /**
     * Метод заполнения XML-файла данными из списка
     *
     * @param set Список объектов, которые надо записать
     * @param file Файл, в который нужно записывать
     * @throws JAXBException Ошибка при преобразовании данных в XML-файл
     */
    public static void parseListToXML(Set<Job> set,File file) throws JAXBException {
        List<Job> list = new ArrayList<>(set);
        JobList jobList = new JobList();
        jobList.setJobs(list);
        JAXBContext context = JAXBContext.newInstance(Job.class,JobList.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(jobList, file);
    }
    
    /**
     * Метод, возвращаюший список объектов из XMl-файла
     * 
     * @param inputFile Файл, из которого нужно читать данные
     * @return Возвращает спиок объектов из файла
     * @throws XMLException 
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static Set<Job> readXML(File inputFile) throws XMLException, IOException, ParserConfigurationException, SAXException {
        Set<Job> jobSet = new HashSet<>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("job");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
               Element eElement = (Element) nNode;
               Job job = new Job();
               job.setDepCode(eElement.getElementsByTagName("depCode").item(0).getTextContent());
               job.setDepJob(eElement.getElementsByTagName("depJob").item(0).getTextContent());
               job.setDescription(eElement.getElementsByTagName("description").item(0).getTextContent());
               if(jobSet.contains(job))
                   throw new XMLException();
               jobSet.add(job);
            }
        }
        
    return jobSet;
    }
}
