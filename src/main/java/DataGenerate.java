import java.util.TimeZone;
import java.util.Locale;
import java.util.Date;
import java.util.Random;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import com.github.javafaker.Faker;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class DataGenerate {

    /**
     * Data Structure in Database:
     *
     * project: [
     *   {
     *      id: UUID,
     *      name: "String",
     *      date: "ISODate",
     *      topManager: "String",
     *      developers:[
     *                  {
     *                      firstName: "String",
     *                      lastName: "String",
     *                      position: "String"
     *                   }
     *                 ],
     *      tasks:[
     *                  {
     *                      name: "String",
     *                      priority: Integer ,
     *                      assignTo: "String",
     *                      state: "String"
     *                  }
     *            ],
     *      defects:[
     *                  {
     *                      name: "String",
     *                      priority: Integer ,
     *                      assignTo: "String",
     *                      state: "String"
     *                  }
     *             ]
     *    }
     * ]
     *
     */
    public static void main(String[] args) throws IOException{

        //Create the top level JSON object
        final int numOfProjects = 1000;
        JSONObject[] project = new JSONObject[numOfProjects];

        for (int pro = 0; pro < numOfProjects; pro++) {
            project[pro] = addProject();
        }

        try (FileWriter file = new FileWriter("project.json")) {
            for (int pro = 0; pro < numOfProjects; pro++) {
                file.write(project[pro].toJSONString());
                file.write("\r\n");
                System.out.println("Successfully Copied JSON Object to File...");
                System.out.println("\nJSON Object: " + project);
            }
        }
    }

    private static JSONObject addProject () {
        String[] projectName = new String[] {"MobileGame", "MovieRecommender", "Yelp", "OnlineChart", "Shopping","OnlineCourse"};
        String[] position = new String[] {"server programmer", "client programmer", "designer"};
        String[] tasksName = new String[] {"register", "search", "login", "modify info"};
        String[] defectsName = new String[] {"register error", "search result error", "login error", "modify info error"};
        String[] states = new String[] {"open", "closed"};

        Faker faker = new Faker();

        // For generate random data
        Random index = new Random();

        //For generate random date
        TimeUnit today = TimeUnit.DAYS;

        // Generate top level objects "Project"

        Faker fakeTime = new Faker();
        JSONObject project = new JSONObject();

        //insert field "id"
        String uuid = java.util.UUID.randomUUID().toString().replaceAll("-","");
        project.put("id", uuid);

        //insert field "name"
        String name = projectName[index.nextInt(projectName.length)];
        name = name + "_" + faker.name().fullName();
        project.put("name", name);

        //insert field "topManager"
        project.put("topManager", faker.name().fullName());

        //insert field "date"
        JSONObject date  = new JSONObject();
        date.put("$date",getISODate(fakeTime.date().past(100, today)));
        project.put("date",date);

        //insert fields "developers" and "tasks"
        addDeveloperAndTask(position, tasksName, states, project);

        //insert field "defects"
        addDefects(defectsName, states, project);

        return project;
    }

    private static void addDeveloperAndTask ( String[] position, String[] tasksName, String[] states, JSONObject project) {
        Faker faker = new Faker();
        // For generate random data
        Random index = new Random();
        JSONArray developers = new JSONArray();
        JSONArray tasks = new JSONArray();

        // add developers
        for (int i = 0; i < 100; i++) {
            JSONObject person = new JSONObject();
            person.put("firstName", faker.name().firstName());
            person.put("lastName", faker.name().lastName());
            person.put("position", position[index.nextInt(position.length)]);
            developers.add(person);

            JSONObject task = new JSONObject();
            task.put("name", tasksName[index.nextInt(tasksName.length)]);
            task.put("priority", index.nextInt(10));
            task.put("assignTo", person.get("firstName"));
            task.put("state", states[index.nextInt(states.length)]);
            tasks.add(task);
        }

        project.put("developers", developers);
        project.put("tasks", tasks);
    }


    private static void addDefects (String[] defectsName, String[] states, JSONObject project) {
        Faker faker = new Faker();
        // For generate random data
        Random index = new Random();
        JSONArray defects = new JSONArray();

        // add defects
        for (int i = 0; i < 100; i++) {
            JSONObject defect = new JSONObject();
            defect.put("name", defectsName[index.nextInt(defectsName.length)]);
            defect.put("priority", index.nextInt(10));
            defect.put("assignTo", faker.name().firstName());
            defect.put("state", states[index.nextInt(states.length)]);

            defects.add(defect);
        }
        project.put("defects", defects);
    }

    // Convert date to ISODate format
    private static String getISODate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    // Convert  ISODate format to Date object
    private static Date fromISOdate(String dateString) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);

        try{
            return df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

}
