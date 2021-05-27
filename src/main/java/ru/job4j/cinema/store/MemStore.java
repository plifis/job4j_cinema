package ru.job4j.cinema.store;

import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MemStore implements Store{
    private static final MemStore INST = new MemStore();
    private final Map<Integer, Ticket> mapHall = new ConcurrentHashMap<>();
    private final Map<Integer, User> mapUser = new ConcurrentHashMap<>();

    private MemStore() {
        mapUser.put(1, new User(1, "Bill", "bill@gmail.com", "445533"));
        mapUser.put(2, new User(2, "Alex", "alex@gmail.com", "112200"));
        mapUser.put(3, new User(3, "Ivan", "ivan@gmail.com", "667799"));
        mapHall.put(12, new Ticket(12, 111, 1, 2, mapUser.get(1).getId()));
        mapHall.put(33, new Ticket(33, 222, 3, 3, mapUser.get(3).getId()));
        mapHall.put(21, new Ticket(21, 111, 2, 1, mapUser.get(2).getId()));
    }
    public static MemStore instOf() {
        return INST;
    }

    @Override
    public List<Ticket> findAllTickets() {
        List<Ticket> list = new ArrayList();
        for (Ticket ticket : mapHall.values()) {
            list.add(ticket);
        }
        return list;
    }

    @Override
    public List<User> findAllUsers() {
        List<User> list = new ArrayList<>();
        for (User user : mapUser.values()) {
            list.add(user);
        }
        return list;
    }


    @Override
    public Ticket save(Ticket ticket) {
        return mapHall.put(ticket.getId(), ticket);
    }

    @Override
    public User save(User user) {
        return mapUser.put(user.getId(), user);
    }


    @Override
    public User findUserByPhone(String phone) {
        Set<Map.Entry<Integer, User>> entrySet = mapUser.entrySet();
        for (Map.Entry<Integer, User> userEntry : entrySet) {
            if (userEntry.getValue().getPhone().equals(phone)) {
                return userEntry.getValue();
            }
        }
            throw new IllegalArgumentException("Указаннй телефон не найден");
    }

    @Override
    public User findUserByEmail(String email) {
            Set<Map.Entry<Integer, User>> entrySet = mapUser.entrySet();
            for (Map.Entry<Integer, User> userEntry : entrySet) {
                if (userEntry.getValue().getPhone().equals(email)) {
                    return userEntry.getValue();
                }
            }
            throw new IllegalArgumentException("Указанный почтовый адрес не найден");
        }

    @Override
    public boolean cancelBooking(String email) {
        int id = findUserByEmail(email).getId();
        Set<Map.Entry<Integer, Ticket>> entrySet = mapHall.entrySet();
        for (Map.Entry<Integer, Ticket> userEntry : entrySet) {
            if (userEntry.getValue().getAccount_id() == id) {
                return true;
            }
        }
            return false;
    }
}
