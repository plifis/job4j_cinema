package ru.job4j.cinema.controller;

import ru.job4j.cinema.store.MemStore;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.*;
import ru.job4j.cinema.store.PsqlStore;

public class HallServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = new PrintWriter(resp.getOutputStream());
        JSONArray array = new JSONArray(PsqlStore.instOf().findAllTickets());
        writer.println(array);
        writer.flush();
    }


}
