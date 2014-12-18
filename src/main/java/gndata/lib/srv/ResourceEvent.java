package gndata.lib.srv;


import java.time.LocalDateTime;
import java.util.Random;

import com.hp.hpl.jena.rdf.model.Resource;


public class ResourceEvent implements EventAdapter {

    private Resource res;
    private LocalDateTime start;
    private LocalDateTime end;

    public ResourceEvent(Resource res) {
        this.res = res;

        // TODO get actual values from resource

        Random generator = new Random();
        Integer day_of_month = LocalDateTime.now().getDayOfMonth() - 2 + generator.nextInt(6);
        Integer start_hour = generator.nextInt(5) + 8;
        Integer end_hour = generator.nextInt(7) + start_hour;

        start = LocalDateTime.now();
        start = start.withDayOfMonth(day_of_month);
        start = start.withHour(start_hour);

        end = LocalDateTime.now();
        end = end.withDayOfMonth(day_of_month);
        end = end.withHour(end_hour);
    }

    public LocalDateTime getEventStart() {
        return start;
    }

    public LocalDateTime getEventEnd() {
        return end;
    }

    public String getSummary() {
        //return Resources.toNameString(res);
        return res.getLocalName();
    }

    public String getDescription() {
        //return Resources.toInfoString(res);
        return res.getURI();
    }


}