package dynamicservice.service;

import dynamicservice.service.models.ChannelSchedule;
import dynamicservice.service.models.ScheduleRequest;
import dynamicservice.soap.Service;
import dynamicservice.soap.annotations.SoapAction;
import dynamicservice.soap.annotations.SoapService;

/**
 *
 * @author migo
 */
@SoapService(name = "Netautomation", uri = "http://10.10.10.1:18084")
public class NetautomationService extends Service {

    @SoapAction(name = "/getSomeFanceyList")
    public ChannelSchedule getChannelSchedule(ScheduleRequest request) throws Exception {
        return soapRequest(request, ChannelSchedule.class);
    }

}
