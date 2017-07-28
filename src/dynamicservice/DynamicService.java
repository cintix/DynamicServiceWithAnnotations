package dynamicservice;

import dynamicservice.service.MarinaService;
import dynamicservice.service.NetautomationService;
import dynamicservice.service.models.ChannelSchedule;
import dynamicservice.service.models.ScheduleRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author migo
 */
public class DynamicService {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            
            MarinaService marinaService = new MarinaService();
            NetautomationService netautomationService = new NetautomationService();

            long start = System.currentTimeMillis();

            ChannelSchedule channelSchedule = marinaService.getChannelSchedule(new ScheduleRequest());
            System.out.println("channelSchedule - " + channelSchedule);
            
            ChannelSchedule channelSchedule1 = netautomationService.getChannelSchedule(new ScheduleRequest());
            System.out.println("channelSchedule1 - " + channelSchedule1);
            
            
            long end = System.currentTimeMillis() - start;
            
            System.out.println("Time in milleseconds : " + end);
            
            
            new A().test();
            
        } catch (Exception ex) {
            Logger.getLogger(DynamicService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
