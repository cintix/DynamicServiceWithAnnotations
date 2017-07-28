package dynamicservice.soap;

import dynamicservice.soap.annotations.SoapAction;
import dynamicservice.soap.annotations.SoapService;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author migo
 */
public abstract class Service {

    private final HashSet<String> backupEndpoints = new HashSet<>();

    /**
     *
     */
    public Service() {
    }

    /**
     *
     * @param backupEndpoints
     */
    public Service(String... backupEndpoints) {
        for (String backup : backupEndpoints) {
            this.backupEndpoints.add(backup);
        }
    }

    protected <T> T soapRequest(Object instance, Class<?> model) throws Exception {
        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

            String className = stackTraceElements[2].getClassName();
            String method = stackTraceElements[2].getMethodName();
            Class serviceClass = Class.forName(className);

            SoapService soapService = null;
            SoapAction soapAction = null;

            if (serviceClass.isAnnotationPresent(SoapService.class)) {
                soapService = (SoapService) serviceClass.getAnnotation(SoapService.class);
            }

            Method[] methods = serviceClass.getMethods();
            for (Method classMethod : methods) {
                if (classMethod.getName().equals(method)) {
                    if (classMethod.isAnnotationPresent(SoapAction.class)) {
                        soapAction = (SoapAction) classMethod.getAnnotation(SoapAction.class);
                        break;
                    }
                }
            }

            if (soapService == null) {
                throw new Exception("Sorry " + className + " is not a defined Soap service");
            }

            if (soapAction == null) {
                throw new Exception("Sorry " + method + " is not a defined Soap action");
            }

            System.out.println("Executing SOAP ACTION " + soapAction.name() + " on " + soapService.name() + " uri: " + soapService.uri());
            Object responeInstance = model.newInstance();

            @SuppressWarnings("unchecked")
            T responseClass = (T) responeInstance;

            return responseClass;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
