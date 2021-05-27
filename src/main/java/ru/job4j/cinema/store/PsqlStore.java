package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;

import javax.xml.stream.events.Comment;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new FileReader("db.properties"))) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST =new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }


    /**
     * Возвращает все билеты
     * @return коллекцию билетов, которые есть в хранилище
     */
    @Override
    public List<Ticket> findAllTickets() {
        List<Ticket> list = new ArrayList<>();
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM ticket");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                list.add(new Ticket(result.getInt("id"),
                        result.getInt("session_id"),
                        result.getInt("row"),
                        result.getInt("cell"),
                        result.getInt("account_id")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Возращает все билеты
     * @return возвращает коллекцию всех билетов в хранилище
     */
    @Override
    public List<User> findAllUsers() {
        List<User> list = new ArrayList<>();
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM account");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                list.add(new User(result.getInt("id"),
                        result.getString("username"),
                        result.getString("email"),
                        result.getString("phone")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * Метод сохраняет билет (ряд, место) в хранилище, если данное место не было ранее заброинровано
     * @param ticket Обект класса Ticket, описывающий ряд, место и идентификатор сеанса
     * @return возвращает объект класса Ticket,
     * в случае невозможности сохранения идентификатор объекта имеет значение по умолчанию "0"
     */
    @Override
    public Ticket save(Ticket ticket)    {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO ticket(session_id, row, cell, account_id) VALUES (?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ticket.getSession_id());
            ps.setInt(2, ticket.getRow());
            ps.setInt(3, ticket.getCell());
            ps.setInt(4, ticket.getAccount_id());
             ps.execute();
                ResultSet id = ps.getGeneratedKeys();
                id.next();
                ticket.setId(id.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ticket;
    }

    /**
     * Метод сохраняет пользователя (имя, почта и телефон) в хранилище,
     * если данные почта или телефон не содержатся в хранилище
     * @param user Обект класса User, описывающий имя, почта и телефон пользователя
     * @return возвращает объект класса User,
     * в случае невозможности сохранения идентификатор объекта имеет значение по умолчанию "0"
     */
    @Override
    public User save(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO account (username, email, phone) VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            try { ps.execute();
                ResultSet id = ps.getGeneratedKeys();
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            } catch (SQLException sqlExc) {
                sqlExc.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Возвращает объект User найденный по нмоеру телефона
     * @param phone номер телефона по которому требуется найти пользователя
     * @return если пользователь найден, то возвращается объект, если нет, то null
     */
    @Override
    public User findUserByPhone(String phone) {
        User user = null;
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM account WHERE phone = (?)");
            ps.setString(1, phone);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                user = new User(result.getInt("id"),
                        result.getString("username"),
                        result.getString("email"),
                        result.getString("phone"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Возвращает объект User найденный по адресу эл. почты
     * @param email  адрес электронной почты по которому требуется найти пользователя
     * @return если пользователь найден, то возвращается объект, если нет, то null
     */
    @Override
    public User findUserByEmail(String email) {
        User user = null;
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM account WHERE email = (?)");
            ps.setString(1, email);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                user = new User(result.getInt("id"),
                        result.getString("username"),
                        result.getString("email"),
                        result.getString("phone"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Метод удаляет все бронирования по адресу электронной почты
     * @param email адрес электронной почты для которой необходимо удалить брони
     * @return возвращет истину,
     * если в результатах SQL запроса на удаление количество затронутых записей больше 0
     */

    @Override
    public boolean cancelBooking(String email) {
        User user = findUserByEmail(email);
        try (Connection cn = pool.getConnection()) {
            PreparedStatement ps = cn.prepareStatement("DELETE FROM ticket WHERE account_id = (?)");
            ps.setInt(1, user.getId());
            int result = ps.executeUpdate();
            if (result > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
