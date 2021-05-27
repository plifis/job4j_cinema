package ru.job4j.cinema.store;

import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;

import java.util.Collection;
import java.util.List;

public interface Store {
    public List<Ticket> findAllTickets();
    public List<User> findAllUsers();

    public Ticket save(Ticket ticket);
    public User save(User user);

    public User findUserByPhone(String phone);
    public User findUserByEmail(String email);

    public boolean cancelBooking(String email);
}
