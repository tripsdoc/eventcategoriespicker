package t.tripsdoc.eventcategoriespickerlibrary;

public class EventCategories {
    String id, name;

    public EventCategories(){}

    public EventCategories(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
