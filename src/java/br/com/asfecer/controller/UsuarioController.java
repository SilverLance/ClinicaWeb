package br.com.asfecer.controller;

import br.com.asfecer.dao.UsuarioDAO;
import br.com.asfecer.model.Usuario;
import java.io.IOException;
import java.util.ArrayList;
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

@WebServlet(name = "UsuarioController", urlPatterns = {"/criaUsuario.html", "/listaUsuarios.html", "/excluiUsuario.html", "/editaUsuario.html"})
public class UsuarioController extends HttpServlet {

    @PersistenceUnit
    private EntityManagerFactory emf;

    @Resource
    private UserTransaction utx;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getServletPath().contains("criaUsuario.html")) {
            criarGet(request, response);
        } else if (request.getServletPath().contains("editaUsuario.html")) {
            editarGet(request, response);
        } else if (request.getServletPath().contains("listaUsuarios.html")) {
            listarGet(request, response);
        } else if (request.getServletPath().contains("excluiUsuario.html")) {
            excluirGet(request, response);
            response.sendRedirect("listaUsuarios.html");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getServletPath().contains("editaUsuario.html")) {
            editarPost(request, response);
        }

        if (request.getServletPath().contains("criaUsuario.html")) {
            try {
                criarPost(request, response);
            } catch (Exception ex) {
                Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void criarGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("WEB-INF/views/cadastroUsuario.jsp").forward(request, response);
    }

    private void editarGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UsuarioDAO dao = new UsuarioDAO(utx, emf);
            int id = Integer.parseInt(request.getParameter("idUsuario"));
            Usuario usuario = dao.findUsuario(id);

            request.setAttribute("usuario", usuario);
            request.getRequestDispatcher("WEB-INF/views/editaUsuario.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendRedirect("listaUsuarios.html");
        }
    }

    private void listarGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Usuario> usuarios = new ArrayList<>();
        UsuarioDAO dao = new UsuarioDAO(utx, emf);
        usuarios = dao.findUsuarioEntities();

        request.setAttribute("usuarios", usuarios);
        request.getRequestDispatcher("WEB-INF/views/listaUsuario.jsp").forward(request, response);
    }

    private void excluirGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            UsuarioDAO dao = new UsuarioDAO(utx, emf);
            int id = Integer.parseInt(request.getParameter("idUsuario"));
            dao.destroy(id);
        } catch (Exception ex) {
            response.sendRedirect("listaUsuarios.html");
        }
    }

    private void criarPost(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception {
        String tipoUsuario = request.getParameter("tipoUsuario");
        char status = request.getParameter("status").charAt(0);
        String login = request.getParameter("login");
        String senha = request.getParameter("senha");
        char moduloAdministrativo = request.getParameter("moduloAdministrativo").charAt(0);
        char moduloAgendamento = request.getParameter("moduloAgendamento").charAt(0);
        char moduloAtendimento = request.getParameter("moduloAtendimento").charAt(0);
        char moduloAcesso = request.getParameter("moduloAcesso").charAt(0);
        char moduloAdmBD = request.getParameter("moduloAdmBD").charAt(0);

        Usuario usuario = new Usuario(tipoUsuario, status, login, senha, moduloAdministrativo, moduloAgendamento, moduloAtendimento, moduloAcesso, moduloAdmBD);
        UsuarioDAO dao = new UsuarioDAO(utx, emf);

        dao.create(usuario);

        response.sendRedirect("listaUsuarios.html");
    }

    private void editarPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            UsuarioDAO dao = new UsuarioDAO(utx, emf);
            int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
            Usuario usuario = dao.findUsuario(idUsuario);
            usuario.setTipousuario(request.getParameter("tipoUsuario"));
            usuario.setStatus(request.getParameter("status").charAt(0));
            usuario.setLogin(request.getParameter("login"));
            usuario.setSenha(request.getParameter("senha"));
            usuario.setModuloadministrativo(request.getParameter("moduloAdministrativo").charAt(0));
            usuario.setModuloagendamento(request.getParameter("moduloAgendamento").charAt(0));
            usuario.setModuloagendamento(request.getParameter("moduloAtendimento").charAt(0));
            usuario.setModuloacesso(request.getParameter("moduloAcesso").charAt(0));
            usuario.setModuloadmbd(request.getParameter("moduloAdmBD").charAt(0));

            dao.edit(usuario);

            response.sendRedirect("listaUsuarios.html");
        } catch (Exception e) {
            response.sendRedirect("listaUsuarios.html");

        }

    }
}

/*    
 HttpSession session = request.getSession(true);
 if (session.getAttribute("usuario") == null) {
 request.getRequestDispatcher("WEB-INF/views/cadastroUsuario.jsp").forward(request, response);
 } else {
 request.setAttribute("erro", "Login ou senha incorretos!");
 request.getRequestDispatcher("login.jsp").forward(request, response);
 }
 */
