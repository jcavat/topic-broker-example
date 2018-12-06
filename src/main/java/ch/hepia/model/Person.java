package ch.hepia.model;

import java.io.Serializable;

/**
 * Person
 */
public class Person implements Serializable {
    private static final long serialVersionUID = 42L;

    private String name;

    public Person(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Hey ! I'm " + name;
    }

}
