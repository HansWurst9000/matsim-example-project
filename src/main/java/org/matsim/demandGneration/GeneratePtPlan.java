package org.matsim.demandGneration;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.contrib.util.PopulationUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

//Imports classes for reading in CSV file
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class GeneratePtPlan {

    public static void main(String[] args) throws IOException {

        //Todo 1. Read in the trips list
        //todo 1-1 Read the trip list .csv
        //BufferReader
        //todo 1-2 find relevant columns (trip id, person id, departure time, coordinates, mode)
        //findHeaders

        Config config = ConfigUtils.createConfig();
        Scenario scenario = ScenarioUtils.createScenario(config);
        Population pop = scenario.getPopulation();

        //Path file = Paths.get(System.getProperty("user.dir")).resolve("ptTrips.csv");
        Path file = Paths.get("C:/Users/Dennis/IdeaProjects/matsim-example-project/src/main/java/org/matsim/demandGneration/ptTrips.csv");

        if(!Files.exists(file)) {
            throw new FileNotFoundException(file.toAbsolutePath().toString());
        }

        System.out.println("reading file: " + file);

        // prepare our stream
        BufferedReader reader = new BufferedReader(new FileReader(file.toFile()));

        //prime the data stream
        String line = reader.readLine();

        // data will be -null when end of stream is reached
        while((line = reader.readLine()) != null) {

            int id = Integer.parseInt(line.split(",")[0]);
            double originX = Double.parseDouble(line.split(",")[1]);
            double originY = Double.parseDouble(line.split(",")[2]);
            double destinationX = Double.parseDouble(line.split(",")[3]);
            double destinationY = Double.parseDouble(line.split(",")[4]);
            String purpose = line.split(",")[5];
            String mode = line.split(",")[6];
            double departure_time = Double.parseDouble(line.split(",")[7]);
            double departure_time_return = Double.parseDouble(line.split(",")[8]);

            //System.out.println("We finished reading id: " + id);

            Coord origin = new Coord(originX, originY);
            Coord destination = new Coord(destinationX, destinationY);

            //Todo 2. Create plan
            //todo 2-1 create population

            String odPrefix = "person";
            Id<Person> personId = Id.createPersonId(odPrefix + "_" + id);
            Person person = pop.getFactory().createPerson(personId);
            pop.addPerson(person);


            Plan plan = pop.getFactory().createPlan();

           if (purpose.equals("NHBW")){
               Activity work = pop.getFactory().createActivityFromCoord("work", origin);
               work.setEndTime(departure_time);
               plan.addActivity(work);

               Leg leg = pop.getFactory().createLeg("pt");
               plan.addLeg(leg);

               Activity other = pop.getFactory().createActivityFromCoord("other",destination);

               plan.addActivity(other);

           }
           else if(purpose.equals("NHBO")){
               Activity other1 = pop.getFactory().createActivityFromCoord("other", origin);
               other1.setEndTime(departure_time);
               plan.addActivity(other1);

               Leg leg = pop.getFactory().createLeg("pt");
               plan.addLeg(leg);

               Activity other2 = pop.getFactory().createActivityFromCoord("other",destination);
               plan.addActivity(other2);
           }else{
               Activity home = pop.getFactory().createActivityFromCoord("home", origin);
               home.setEndTime(departure_time);
               plan.addActivity(home);

               Leg leg = pop.getFactory().createLeg("pt");
               plan.addLeg(leg);

               String mainAct = "";
               switch(purpose){
                   case "HBW":
                        mainAct = "work";
                        break;
                   case "HBO":
                       mainAct = "other";
                       break;
                   case "HBE":
                       mainAct = "education";
                       break;
                   case "HBS":
                       mainAct = "shopping";
                       break;
                   case "HBR":
                       mainAct = "recreational";
                       break;
               }
               Activity mainActivity = pop.getFactory().createActivityFromCoord(mainAct ,destination);
               mainActivity.setEndTime(departure_time_return);
               plan.addActivity(mainActivity);

               Leg legReturn = pop.getFactory().createLeg("pt");
               plan.addLeg(legReturn);

               Activity home2 = pop.getFactory().createActivityFromCoord("home",origin);
               plan.addActivity(home2);
           }

            person.addPlan(plan);

        }

        // what did this do? We read the entire file into memory and stored it into a list.
        reader.close();
        //Todo 3. Write the plan
        String OUTPUT_FILE_MUNICH = "C:/Users/Dennis/IdeaProjects/matsim-example-project/scenarios/munich/planMunich.xml.gz";
        String OUTPUT_FILE_RING = "C:/Users/Dennis/IdeaProjects/matsim-example-project/scenarios/ring/planRing.xml.gz";
        PopulationWriter pw = new PopulationWriter(scenario.getPopulation());
        pw.write(OUTPUT_FILE_MUNICH);
        pw.write(OUTPUT_FILE_RING);

        /*System.out.println("How many lines: " + lines.size());
        System.out.println();
        System.out.println(lines.get(0));
        System.out.println(lines.get(1));*/
    }
}











