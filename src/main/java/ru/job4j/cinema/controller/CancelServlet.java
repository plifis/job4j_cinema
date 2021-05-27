package ru.job4j.cinema.controller;

import ru.job4j.cinema.store.PsqlStore;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CancelServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain");
        String email = req.getParameter("email");
        if (PsqlStore.instOf().cancelBooking(req.getParameter("email"))) {
            resp.getWriter().write("Ваша бронь отменена");
        } else {
            resp.getWriter().write("Данный почтовый адрес не найден");
        }
    }
}
