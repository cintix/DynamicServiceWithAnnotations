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
@SoapService(name = "Marina", uri = "http://127.0.0.1:18084")
public class MarinaService extends Service {

    @SoapAction(name = "/getContiguousListEvents")
    public ChannelSchedule getChannelSchedule(ScheduleRequest request) throws Exception {
        return soapRequest(request, ChannelSchedule.class);
    }

}
