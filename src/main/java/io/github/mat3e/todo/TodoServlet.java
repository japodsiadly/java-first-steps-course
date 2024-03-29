package io.github.mat3e.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "Todo", urlPatterns = {"/api/todos/*"})
public class TodoServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(io.github.mat3e.todo.TodoServlet.class);

    private TodoRepository repository;
    private ObjectMapper mapper;

    /**
     * Servlet container needs it.
     */
    @SuppressWarnings("unused")
    public TodoServlet() {
        this(new TodoRepository(), new ObjectMapper());
    }

    public TodoServlet(TodoRepository repository, ObjectMapper mapper){
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Got request with parameters " + req.getParameterMap());
        resp.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(resp.getOutputStream(), repository.findAll());
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Got Put request with parameters " + req.getParameterMap());
        String pathInfo = req.getPathInfo();

        try {
            var todoId = Integer.valueOf(pathInfo.substring(1));

            var todo = repository.toggleTodo(todoId);

            resp.setContentType("application/json;charset=UTF-8");
            mapper.writeValue(resp.getOutputStream(), todo);
        } catch (NumberFormatException e) {
            logger.warn("Wrong path" + pathInfo);
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        Todo input = mapper.readValue(req.getInputStream(), Todo.class);
        resp.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(resp.getOutputStream(), repository.addTodo(input));
    }
}