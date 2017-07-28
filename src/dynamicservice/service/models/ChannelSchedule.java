package dynamicservice.service.models;

/**
 *
 * @author migo
 */
public class ChannelSchedule {

    private String name = "NOT WORKING YET";

    public ChannelSchedule() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ChannelSchedule{" + "name=" + name + '}';
    }

}
