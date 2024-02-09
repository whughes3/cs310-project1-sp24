package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap; // Import this library in order to use a LinkHashMap

public class ClassSchedule {
    
    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";
    private final String SUBJECTID_COL_HEADER = "subjectid";
    
    public String convertCsvToJsonString(List<String[]> csv) {
    JsonArray sectArray = new JsonArray(); // Creates a JsonArray for section
    LinkedHashMap cNameLinkedMap = new LinkedHashMap<>(); // LinkedHashMap for the course names
    LinkedHashMap schtypeLinkedMap = new LinkedHashMap<>(); // LinkedHashMap for schedule type
    LinkedHashMap subLinkedMap = new LinkedHashMap<>(); // LinkedHashMap for the subject

    List csvList = csv; 

    Iterator<String[]> iterator;
    iterator = csvList.iterator();

    JsonObject csvLineMap;
    String[] topline = iterator.next();

    while (iterator.hasNext()) {
    String [] valLine = iterator.next();
    csvLineMap = new JsonObject();

        for (int i = 0; i < topline.length; i++) {
            csvLineMap.put(topline[i], valLine[i]);
        }

        schtypeLinkedMap.put(csvLineMap.get(TYPE_COL_HEADER),csvLineMap.get(SCHEDULE_COL_HEADER));

        String courseName[] = csvLineMap.get(NUM_COL_HEADER).toString().split(" ");
        subLinkedMap.put(courseName[0], csvLineMap.get(SUBJECT_COL_HEADER));

       LinkedHashMap courseMap = new LinkedHashMap<>();
       courseMap.put(SUBJECTID_COL_HEADER, courseName[0]);
       courseMap.put(NUM_COL_HEADER, courseName[1]);
       courseMap.put(DESCRIPTION_COL_HEADER, csvLineMap.get(DESCRIPTION_COL_HEADER));
       int credits = Integer.parseInt(csvLineMap.get(CREDITS_COL_HEADER).toString());
       courseMap.put(CREDITS_COL_HEADER, credits);
       cNameLinkedMap.put(csvLineMap.get(NUM_COL_HEADER), courseMap);

       LinkedHashMap sectionLinkedMap = new LinkedHashMap<>();
       int crn = Integer.parseInt(csvLineMap.get(CRN_COL_HEADER).toString());
       sectionLinkedMap.put(CRN_COL_HEADER, crn);
       sectionLinkedMap.put(SUBJECTID_COL_HEADER, courseName[0]);
       sectionLinkedMap.put(NUM_COL_HEADER, courseName[1]);
       sectionLinkedMap.put(SECTION_COL_HEADER, csvLineMap.get(SECTION_COL_HEADER));
       sectionLinkedMap.put(TYPE_COL_HEADER, csvLineMap.get(TYPE_COL_HEADER));
       sectionLinkedMap.put(START_COL_HEADER, csvLineMap.get(START_COL_HEADER));
       sectionLinkedMap.put(END_COL_HEADER, csvLineMap.get(END_COL_HEADER));
       sectionLinkedMap.put(DAYS_COL_HEADER, csvLineMap.get(DAYS_COL_HEADER));
       sectionLinkedMap.put(WHERE_COL_HEADER, csvLineMap.get(WHERE_COL_HEADER));
       String names = csvLineMap.get(INSTRUCTOR_COL_HEADER).toString();
       String[] instructorArray = names.split(", ");
       sectionLinkedMap.put(INSTRUCTOR_COL_HEADER, instructorArray);

       sectArray.add(sectionLinkedMap);
}
    
       JsonObject object = new JsonObject();
       object.put("scheduletype", schtypeLinkedMap);
       object.put("subject", subLinkedMap);
       object.put("course", cNameLinkedMap);
       object.put("section", sectArray);

       return Jsoner.prettyPrint(object.toJson());
        
    }
    
    public String convertJsonToCsvString(JsonObject json) {
        
        JsonObject jsonObject = new JsonObject(json);
        StringWriter swriter = new StringWriter();
        
        JsonObject scheduleTypeObject = (JsonObject) jsonObject.get("scheduletype");
        JsonObject subjectObject = (JsonObject) jsonObject.get("subject");
        JsonObject courseObject = (JsonObject) jsonObject.get("course");
        
        JsonArray sectionArray = (JsonArray) jsonObject.get("section");
        List<String[]>sectionList = new ArrayList<>();
        
        String[] top = {CRN_COL_HEADER, SUBJECT_COL_HEADER, NUM_COL_HEADER, DESCRIPTION_COL_HEADER, SECTION_COL_HEADER, 
        TYPE_COL_HEADER, CREDITS_COL_HEADER,START_COL_HEADER, END_COL_HEADER, DAYS_COL_HEADER, WHERE_COL_HEADER, 
        SCHEDULE_COL_HEADER, INSTRUCTOR_COL_HEADER};
        sectionList.add(top);
        
        
        
        try {
            for(Object snellen: sectionArray) {
                JsonObject sectionObject = (JsonObject)snellen;
                
                String crn = sectionObject.get(CRN_COL_HEADER).toString();
                String subject = subjectObject.get(sectionObject.get(SUBJECTID_COL_HEADER)).toString();
                String courseNum = (sectionObject.get(SUBJECTID_COL_HEADER) + " " + sectionObject.get(NUM_COL_HEADER));
                
                HashMap courseNames = (HashMap)courseObject.get(courseNum);
                String description = courseNames.get(DESCRIPTION_COL_HEADER).toString();
                String section = sectionObject.get(SECTION_COL_HEADER).toString();
                String type = sectionObject.get(TYPE_COL_HEADER).toString();
                String credits = courseNames.get(CREDITS_COL_HEADER).toString();
                String start = sectionObject.get(START_COL_HEADER).toString();
                String end = sectionObject.get(END_COL_HEADER).toString();
                String days = sectionObject.get(DAYS_COL_HEADER).toString();
                String where = sectionObject.get(WHERE_COL_HEADER).toString();
                
                String schedule = scheduleTypeObject.get(type).toString();
                
                JsonArray instructArray =(JsonArray) sectionObject.get(INSTRUCTOR_COL_HEADER);
                String[] instructNames = instructArray.toArray(new String[0]);
                String instructor = String.join(", ", instructNames);
                
                String[] csvLine = {crn, subject, courseNum, description, section, type, credits, start, end, days, 
                where, schedule, instructor};
                sectionList.add(csvLine);
                
            }
        try (CSVWriter csvWriter = new CSVWriter(swriter, '\t', '"', '\\', "\n")){
            csvWriter.writeAll(sectionList);
        
            }
        }
        catch (Exception e)   {
            e.printStackTrace();
        }
        return swriter.toString();
    }
    
    public JsonObject getJson() {
        
        JsonObject json = getJson(getInputFileData(JSON_FILENAME));
        return json;
    }
    
    public JsonObject getJson(String input) {
        
        JsonObject json = null;
        
        try {
            json = (JsonObject)Jsoner.deserialize(input);
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return json;
        
    }
    
    public List<String[]> getCsv() {
        
        List<String[]> csv = getCsv(getInputFileData(CSV_FILENAME));
        return csv;
        
    }
    
    public List<String[]> getCsv(String input) {
        
        List<String[]> csv = null;
        
        try {
            
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return csv;
        
    }
    
    public String getCsvString(List<String[]> csv) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        csvWriter.writeAll(csv);
        
        return writer.toString();
        
    }
    
    private String getInputFileData(String filename) {
        
        StringBuilder buffer = new StringBuilder();
        String line;
        
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        try {
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));

            while((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return buffer.toString();
        
    }
    
}