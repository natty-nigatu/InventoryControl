package data;

public class Category {
    int id;
    String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void save(Database db) {
        db.setCategoryData(this.id, this.name);
    }

    public void add(Database db) {
        this.id = db.addCategory(this.name);
    }

    public void delete(Database db) {
        db.deleteCategory(this.id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
