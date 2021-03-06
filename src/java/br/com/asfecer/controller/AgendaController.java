package br.com.asfecer.controller;

import br.com.asfecer.dao.AgendaDAO;
import br.com.asfecer.dao.HorarioDAO;
import br.com.asfecer.dao.PacienteDAO;
import br.com.asfecer.dao.UsuarioDAO;
import br.com.asfecer.dao.exceptions.RollbackFailureException;
import br.com.asfecer.model.Agenda;
import br.com.asfecer.model.Horario;
import br.com.asfecer.model.Paciente;
import br.com.asfecer.model.Usuario;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

@WebServlet(name = "AgendaController", urlPatterns = {"/criaAgenda.html", "/listaAgendas.html", "/excluiAgenda.html", "/editaAgenda.html"})
public class AgendaController extends HttpServlet {

    @PersistenceUnit
    private EntityManagerFactory emf;

    @Resource
    private UserTransaction utx;

    SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat formatHour = new SimpleDateFormat("hh:mm:ss");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getServletPath().contains("criaAgenda.html")) {
            criarGet(request, response);
        } else if (request.getServletPath().contains("editaAgenda.html")) {
            editarGet(request, response);
        } else if (request.getServletPath().contains("listaAgendas.html")) {
            listarGet(request, response);
        } else if (request.getServletPath().contains("excluiAgenda.html")) {
            try {
                excluirGet(request, response);
            } catch (Exception ex) {
                Logger.getLogger(AgendaController.class.getName()).log(Level.SEVERE, null, ex);
            }
            response.sendRedirect("listaAgendas.html");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getServletPath().contains("/editaAgenda.html")) {
            try {
                editarPost(request, response);
            } catch (ParseException ex) {
                Logger.getLogger(AgendaController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(AgendaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (request.getServletPath().contains("/criaAgenda.html")) {
            try {
                criarPost(request, response);
            } catch (ParseException ex) {
                Logger.getLogger(AgendaController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(AgendaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void criarGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("WEB-INF/views/cadastroAgenda.jsp").forward(request, response);
    }

    private void editarGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AgendaDAO dao = new AgendaDAO(utx, emf);
        int id = Integer.parseInt(request.getParameter("idAgenda"));
        Agenda agenda = dao.findAgenda(id);

        request.setAttribute("agenda", agenda);
        request.getRequestDispatcher("WEB-INF/views/editaAgenda.jsp").forward(request, response);
    }

    private void listarGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Agenda> agendas = new ArrayList<>();
        AgendaDAO dao = new AgendaDAO(utx, emf);
        agendas = dao.findAgendaEntities();

        request.setAttribute("agendas", agendas);
        request.getRequestDispatcher("WEB-INF/views/listaAgenda.jsp").forward(request, response);
    }

    private void excluirGet(HttpServletRequest request, HttpServletResponse response) throws IOException, RollbackFailureException, Exception {
        AgendaDAO dao = new AgendaDAO(utx, emf);
        int id = Integer.parseInt(request.getParameter("idAgenda"));
        dao.destroy(id);

        response.sendRedirect("listaAgendas.html");
    }

    private void criarPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, Exception {

        HorarioDAO hor = new HorarioDAO(utx, emf);
        PacienteDAO pac = new PacienteDAO(utx, emf);
        UsuarioDAO usu = new UsuarioDAO(utx, emf);
        Agenda agenda = new Agenda();

        agenda.setData(formatDate.parse(request.getParameter("data")));
        agenda.setHora(formatHour.parse(request.getParameter("hora")));
        agenda.setRetorno("on".equalsIgnoreCase(request.getParameter("retorno")));
        agenda.setCancelado("on".equalsIgnoreCase(request.getParameter("cancelado")));
        agenda.setMotivocancelamento(request.getParameter("motivoCancelamento"));
        agenda.setStatus("on".equalsIgnoreCase(request.getParameter("status")));
        Horario medico = hor.findHorario(Integer.parseInt(request.getParameter("medico")));
        agenda.setMedico(medico);
        Paciente paciente = pac.findPaciente(Integer.parseInt(request.getParameter("paciente")));
        agenda.setPaciente(paciente);
        Usuario usuario = usu.findUsuario(Integer.parseInt(request.getParameter("usuario")));
        agenda.setUsuario(usuario);

        AgendaDAO dao = new AgendaDAO(utx, emf);

        dao.create(agenda);

        response.sendRedirect("listaAgendas.html");
    }

    private void editarPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, RollbackFailureException, Exception {

        HorarioDAO hor = new HorarioDAO(utx, emf);
        PacienteDAO pac = new PacienteDAO(utx, emf);
        UsuarioDAO usu = new UsuarioDAO(utx, emf);
        AgendaDAO dao = new AgendaDAO(utx, emf);
        Agenda agenda = new Agenda();

        int registroAgenda = Integer.parseInt(request.getParameter("registroAgenda"));
        dao.findAgenda(registroAgenda);
        agenda.setData(formatDate.parse(request.getParameter("data")));
        agenda.setHora(formatHour.parse(request.getParameter("hora")));
        agenda.setRetorno("on".equalsIgnoreCase(request.getParameter("retorno")));
        agenda.setCancelado("on".equalsIgnoreCase(request.getParameter("cancelado")));
        agenda.setMotivocancelamento(request.getParameter("motivoCancelamento"));
        agenda.setStatus("on".equalsIgnoreCase(request.getParameter("status")));
        Horario medico = hor.findHorario(Integer.parseInt(request.getParameter("medico")));
        agenda.setMedico(medico);
        Paciente paciente = pac.findPaciente(Integer.parseInt(request.getParameter("paciente")));
        agenda.setPaciente(paciente);
        Usuario usuario = usu.findUsuario(Integer.parseInt(request.getParameter("usuario")));
        agenda.setUsuario(usuario);


        dao.edit(agenda);

        response.sendRedirect("listaAgendas.html");

    }

}
