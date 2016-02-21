/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.sql.Connection;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.lang.*;

/**
 *
 * @author tom
 */
public class servDB extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        String id = request.getParameter("id");
        String tempo = request.getParameter("tempo");
        String tempo2 = request.getParameter("tempo2");
        String re = request.getParameter("re");
        String sh = request.getParameter("sh");
        response.setContentType("text/plain");

        Connection con;
        DataSource ds;
        Statement stmt = null;
        String sql = "";

        try {

            InitialContext ic = new InitialContext();
            ds = (DataSource) ic.lookup("HumoRadioDB");
            con = ds.getConnection();

            stmt = con.createStatement();

            double tempo_d = Double.parseDouble(tempo);
            double tempo2_d = Double.parseDouble(tempo2);
            int re_int = Integer.parseInt(re);
            int sh_int = Integer.parseInt(sh);
            String eval = "";

            /*
            Update del valore relativo ad uno specifico attributo della canzone
            appena ascoltata in base alle percentuali estratte.
            */
            if ((tempo2_d / tempo_d) <= 0.25) {
                eval = "0";
            }
            if ((tempo2_d / tempo_d) > 0.25 && (tempo2_d / tempo_d) <= 0.50) {
                eval = "1";
            }
            if ((tempo2_d / tempo_d) > 0.50 && (tempo2_d / tempo_d) <= 0.75) {
                eval = "2";
            }
            if ((tempo2_d / tempo_d) > 0.75) {
                eval = "3";
            }

            /*
            Query utlizzate per l'aggiornamento del database, solo nel caso in cui
            il valore da aggiungere sia diverso da zero.
            */
            if (eval.equals("0") == false) {
                if (re_int == 6 || sh_int == 6) {
                    if (re_int == 6 && sh_int != 6) {
                        sql = "update song_rating2 set song_rating2.re5=(song_rating2.re5+" + eval + "),song_rating2.re6=(song_rating2.re6+" + eval + "),song_rating2.sh" + sh + "=(song_rating2.sh" + sh + "+" + eval + ") where song_rating2.id='" + id + "'";

                    }
                    if (re_int != 6 && sh_int == 6) {
                        sql = "update song_rating2 set song_rating2.sh5=(song_rating2.sh5+" + eval + "),song_rating2.sh6=(song_raing2.sh6+" + eval + "),song_rating2.re" + re + "=(song_rating2.re" + re + "+" + eval + ") where song_rating2.id='" + id + "'";

                    }
                    if (re_int == 6 && sh_int == 6) {
                        sql = "update song_rating2 set song_rating2.sh5=(song_rating2.sh5+" + eval + "),song_rating2.sh6=(song_rating2.sh6+" + eval + "),song_rating2.re5=(song_rating2.re5+" + eval + "),song_rating2.re6=(song_rating2.re6+" + eval + ") where song_rating2.id='" + id + "'";

                    }
                } else {
                    sql = "update song_rating2 set song_rating2.re" + re + "=(song_rating2.re" + re + "+" + eval + "),song_rating2.sh" + sh + "=(song_rating2.sh" + sh + "+" + eval + ") where song_rating2.id='" + id + "'";

                }

                int risultato = stmt.executeUpdate(sql);

            }

            con.close();

        } catch (SQLException | NamingException ex) {
            Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
