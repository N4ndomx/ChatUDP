package server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatPrivado {
    private String name;
    private Set<String> users = new HashSet<>();

    public ChatPrivado(String id) {
        this.name = id;
    }

    public void addUser(String user) {
        users.add(user);
    }

    public boolean hasUser(String user) {
        return users.contains(user);
    }

    public List<String> allUsers() {
        return new ArrayList<>(users);
    }

}
