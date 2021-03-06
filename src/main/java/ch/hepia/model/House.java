package ch.hepia.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * House
 */
public class House implements Serializable {
    private static final long serialVersionUID = 42L;

    private ArrayList<Person> persons;

    public House(ArrayList<String> names) {
        this.persons = new ArrayList<>();
        for (String name : names) {
            persons.add(new Person(name));
        }
    }

    @Override
    public String toString() {
        return "Hello, I'm a House, this persons live here : " + persons.toString();
    }
}
