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
public class MainServlet extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            Connection con;
            DataSource ds;
            Statement stmt = null;
            String status1 = request.getParameter("sh");
            String status2 = request.getParameter("re");
            String genre = request.getParameter("genre");
            String sql = "";
            String sql1 = "";

            int a = Integer.parseInt(status1);
            if (a > 6) {
                a = a - 1;
            }
            status1 = Integer.toString(a);

            int b = Integer.parseInt(status2);
            if (b > 6) {
                b = b - 1;
            }
            status2 = Integer.toString(b);

            /*
            Matrice per salvare tempo totale della canzone, link e titolo
            */
            String[][] playlist = new String[30][3];

            try {

                InitialContext ic = new InitialContext();
                ds = (DataSource) ic.lookup("HumoRadioDB");
                con = ds.getConnection();

                stmt = con.createStatement();

                /*
                 Query utilizzate per la selezione delle canzoni in base all'umore
                 specificato dall'utente
                
                 Nel primo caso si effettua la query quando non c'è un genere 
                 specificato; nel secondo la query è basata su un genere specificato
                 dall'utente
                
                 I vari casi all'interno gestiscono la presenza di uno stato d'animo
                 indifferente per uno, nessuno o entrambi i parametri da specificare.
                 */
                if (!genre.equals("0")) {
                    if (a == 6 || b == 6) {
                        if (a == 6 && b != 6) {
                            sql = "select SD.time,SD.link,SD.title from song_rating2 as SR inner join song_data as SD on SD.link=SR.id where SR.genre=" + genre + " order by (SR.sh5 + SR.sh6 + SR.re" + b + ") desc";
                        }
                        if (a != 6 && b == 6) {
                            sql = "select SD.time,SD.link,SD.title from song_rating2 as SR inner join song_data as SD on SD.link=SR.id where SR.genre=" + genre + " order by (SR.re5 + SR.re6 + SR.sh" + a + ") desc";
                        }
                        if (a == 6 && b == 6) {
                            sql = "select SD.time,SD.link,SD.title from song_rating2 as SR inner join song_data as SD on SD.link=SR.id where SR.genre=" + genre + " order by (SR.re5 + SR.re6 + SR.sh5 + SR.sh6) desc";
                        }
                    } else {
                        sql = "select SD.time,SD.link,SD.title from song_rating2 as SR inner join song_data as SD on SD.link=SR.id where SR.genre=" + genre + " order by (SR.re" + b + " + SR.sh" + a + ") desc";
                    }
                } else {
                    if (a == 6 || b == 6) {
                        if (a == 6 && b != 6) {
                            sql = "select SD.time,SD.link,SD.title from song_rating2 as SR inner join song_data as SD on SD.link=SR.id order by (SR.sh5 + SR.sh6 + SR.re" + b + ") desc";
                        }
                        if (a != 6 && b == 6) {
                            sql = "select SD.time,SD.link,SD.title from song_rating2 as SR inner join song_data as SD on SD.link=SR.id order by (SR.re5 + SR.re6 + SR.sh" + a + ") desc";
                        }
                        if (a == 6 && b == 6) {
                            sql = "select SD.time,SD.link,SD.title from song_rating2 as SR inner join song_data as SD on SD.link=SR.id order by (SR.re5 + SR.re6 + SR.sh5 + SR.sh6) desc";
                        }
                    } else {
                        sql = "select SD.time,SD.link,SD.title from song_rating2 as SR inner join song_data as SD on SD.link=SR.id order by (SR.re" + b + " + SR.sh" + a + ") desc";
                    }
                }

                ResultSet rs = stmt.executeQuery(sql);

                Statement stmt2 = con.createStatement();

                sql1 = sql.replace("desc", "asc");

                ResultSet rs1 = stmt2.executeQuery(sql1);

                /*
                 Creazione delle playlists da poter fornire all'utente posizionandole
                 secondo un criterio stabilito per evitare che alcune canzoni non
                 siano mai ascoltate e quindi non possano ricevere mai un voto.
                 */
                for (int i = 0; i < 21; i++) {
                    rs.next();
                    if (i >= 0 && i < 7) {
                        playlist[i][0] = rs.getString("time");
                        playlist[i][1] = rs.getString("link");
                        playlist[i][2] = rs.getString("title");
                    }

                    if (i >= 7 && i < 14) {
                        playlist[i + 3][0] = rs.getString("time");
                        playlist[i + 3][1] = rs.getString("link");
                        playlist[i + 3][2] = rs.getString("title");
                    }

                    if (i >= 14 && i < 21) {
                        playlist[i + 6][0] = rs.getString("time");
                        playlist[i + 6][1] = rs.getString("link");
                        playlist[i + 6][2] = rs.getString("title");
                    }
                }

                for (int i = 7; i < 16; i++) {
                    rs1.next();
                    if (i >= 7 && i < 10) {
                        playlist[i][0] = rs1.getString("time");
                        playlist[i][1] = rs1.getString("link");
                        playlist[i][2] = rs1.getString("title");
                    }

                    if (i >= 10 && i < 13) {
                        playlist[i + 7][0] = rs1.getString("time");
                        playlist[i + 7][1] = rs1.getString("link");
                        playlist[i + 7][2] = rs1.getString("title");
                    }

                    if (i >= 13 && i < 16) {
                        playlist[i + 14][0] = rs1.getString("time");
                        playlist[i + 14][1] = rs1.getString("link");
                        playlist[i + 14][2] = rs1.getString("title");
                    }
                }

                con.close();

            } catch (SQLException | NamingException ex) {
                Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
            }

            /* 
            Pagina Html generata dalla Servlet
            */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>HumoRadio - Listening</title>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<meta name=\"viewport\" content=\"width=device-width\">\n");
            
            out.println("<style>");
            
            out.println("body{");
            out.println("background-image: url(\"sf33.png\");");
            out.println("background-repeat: no-repeat;");
            out.println("min-height: 570px;");
            out.println("min-width: 1350px;");
            out.println("}");
            
            out.println("p{");
            out.println("color:black;");
            out.println("}");
            
            out.println("#player {");
            out.println("margin: 110px 0px 0px 160px;");
            out.println("}");
            
            out.println("button {");
            out.println("height: 30px;");
            out.println("}");
            
            out.println("#bottoni {");
            out.println("margin: 30px 0px 0px 140px;");
            out.println("width: 670px;");
            out.println("}");
            
            out.println("#titolo {");
            out.println("border-bottom-style: solid;");
            out.println("font-size: larger;");
            out.println("font-family: Georgia;");
            out.println("font-style: italic;");
            out.println("margin-top: 15px;");
            out.println("margin-bottom: 10px;");
            out.println("height: 30px;");
            out.println("}");
            
            out.println("</style>");
            out.println("</head>");

            out.println("<body>");
            out.println("<div id='player'></div>");

            out.println("<script>");
            
            /*
            Inizializzazione di variabili necessarie per la gestione in Javascript
            delle varie canzoni da proporre all'utente e su cui applicare funzioni,
            come quella di shuffle
            */
            out.print("var songs=[");
            for (int i = 0; i < 30; i++) {
                out.print("'" + playlist[i][1] + "'");
                if (i != 29) {
                    out.print(",");
                } else {
                    out.println("];");
                }
            }

            out.print("var songs_time=[");
            for (int i = 0; i < 30; i++) {
                out.print("'" + playlist[i][0] + "'");
                if (i != 29) {
                    out.print(",");
                } else {
                    out.println("];");
                }
            }

            out.print("var songs_title=[");
            for (int i = 0; i < 30; i++) {
                out.print("\"" + playlist[i][2] + "\"");
                if (i != 29) {
                    out.print(",");
                } else {
                    out.println("];");
                }
            }
            
            out.println("var songs1, songs2, songs3;");
            /*
            Shuffle Function: funzione Javscript utilizzata per proporre le canzoni
            in maniera casuale all'interno della selezione.
            */
            out.println("function shuffle(array1, array2, array3){");
            out.println("var result = [];");
            out.println("for (var i = array1.length - 1; i > 0; i--) {");
            out.println("var j = Math.floor(Math.random() * (i + 1));");
            out.println("var temp1 = array1[i];");
            out.println("array1[i] = array1[j];");
            out.println("array1[j] = temp1;");
            out.println("var temp2 = array2[i];");
            out.println("array2[i] = array2[j];");
            out.println("array2[j] = temp2;");
            out.println("var temp3 = array3[i];");
            out.println("array3[i] = array3[j];");
            out.println("array3[j] = temp3;");
            out.println("}");
            out.println("for(var i = 0; i < 10; i++)");
            out.println("result[i] = array1[i];");
            out.println("for(var i = 10; i < 20; i++)");
            out.println("result[i] = array2[i-10];");
            out.println("for(var i = 20; i < 30; i++)");
            out.println("result[i] = array3[i-20];");
            out.println("return result;");
            out.println("}");

            /*
            Codice per creare ed impostare il player Youtube che possa fornire 
            video della canzone da proporre. 
            */
            out.println("var tag = document.createElement('script');");
            out.println("tag.src = \"https://www.youtube.com/iframe_api\";");
            out.println("var firstScriptTag = document.getElementsByTagName('script')[0];");
            out.println("firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);");
            out.println("var player;");
            
            out.println("function onYouTubeIframeAPIReady() {");
            out.println("songs1 = shuffle(songs.slice(0,10), songs_time.slice(0,10), songs_title.slice(0,10)); songs2 = shuffle(songs.slice(10,20), songs_time.slice(10, 20), songs_title.slice(10,20)); songs3 = shuffle(songs.slice(20,30), songs_time.slice(20,30), songs_title.slice(20,30));");
            out.println("for(var y = 0; y < 30; y++){");
            out.println("if(y < 10) { songs[y] = songs1[y]; songs_time[y] = songs1[y+10]; songs_title[y] = songs1[y+20];}");
            out.println("if(y >= 10 && y < 20) {songs[y] = songs2[y-10]; songs_time[y] = songs2[y]; songs_title[y] = songs2[y+10];}");
            out.println("if(y >= 20) {songs[y] = songs3[y-20]; songs_time[y] = songs3[y-10]; songs_title[y] = songs3[y];}");
            out.println("}");
            out.println("document.getElementById('titolo').innerHTML = songs_title[0];");
            
            /*
            Settaggio dei parametri del player Youtube e risposta agli eventi
            */
            out.println("player = new YT.Player('player', {");
            out.println("height: '390',");
            out.println("width: '640',");
            out.println("playerVars: {");
            out.println(" 'rel': 0,");
            out.println(" 'iv_load_policy': 3,");
            out.println(" 'controls': 0 },");
            out.println("videoId: songs[0],");
            out.println("events: {");
            out.println(" 'onReady': onPlayerReady,");
            out.println(" 'onStateChange': onPlayerStateChange");
            out.println("}");
            out.println("});");
            out.println("}");
            /*
            onPlayerReady(event) Function: funzione utilizzata per mandare in 
            esecuzione un video e mostrarlo all'utente
            */
            out.println("function onPlayerReady(event) {");
            out.println("event.target.playVideo();");
            out.println(" }");
            out.println("var done = false;");
            
            /*
            onPlayerStateChange(event) Function: funzione che gestisce il cambio
            di stato del player
            */
            out.println("function onPlayerStateChange(event) {");
            out.println(" if (event.data == YT.PlayerState.PLAYING && !done) {");
            out.println(" done = true;");
            out.println("}");
            out.println("if (event.data == YT.PlayerState.ENDED) {");
            out.println("swapVideo();");
            out.println("}");
            out.println("}");
            
            /*
            goBack() Function: funzione utilizzata per tornare alla pagina precedente
            */
            out.println("function goBack() {");
            out.println("location.replace('http://localhost:8080/HumoRadio/');");
            out.println("}");
            
            /*
            swapVideo() Function: funzione utilizzata per selezionare ed ascoltare
             la prossima canzone della selezione. La funzione gestisce anche i casi 
            in cui sono state ascoltate già tutte le canzoni di una selezione o di 
            tutte le selezioni.
            */
            out.println("var count=0;");
            out.println("function swapVideo(){");
            out.println("if( count == 9 || count == 19 ) {");
            out.println("var response = confirm('You have just listened a compilation! Now, if you want to continue click OK, otherwise Cancel to choose a new mood');");
            out.println("if( response == false)");
            out.println("location.replace('http://localhost:8080/HumoRadio/');");
            out.println("}");
            out.println("if( count == 29) {");
            out.println("alert('Your selection is over. Now, you can select a new mood!');");
            out.println("location.replace('http://localhost:8080/HumoRadio/');");
            out.println("return;}");
            out.println("player.pauseVideo();");
            out.println("update_statistics(count);");
            out.println("player.loadVideoById(songs[count+1]);");
            out.println("count = count+1;");
            out.println("document.getElementById('titolo').innerHTML = songs_title[count];");
            out.println("}");

            /*
            update_statistics(val) Function: funzione utilizzata per poter aggiornare 
            i valori nel dataset, aggiungendo un certo voto agli attributi delle
            canzoni. Crea una richiesta HTTP che viene inviata ad una servlet (servDB.java)
            che interagisce con il database per portare a termine questo task.
            */
            out.println("function update_statistics(val){");
            out.println("var xmlHttp;");
            out.println("xmlHttp= new XMLHttpRequest();");
            out.println("xmlHttp.onreadystatechange = function(){");
            out.println("if(xmlHttp.readyState == 4 && xmlHttp.status==200)");
            out.println("{");
            out.println("}");
            out.println("}");
            out.println("var list_time=player.getCurrentTime();");
            out.println("var link= songs[val];");
            out.println("var time=songs_time[val]");
            out.println("var re =" + status2 + ";");
            out.println("var sh =" + status1 + ";");
            out.println("xmlHttp.open('GET','servDB?id='+link+'&tempo='+time+'&tempo2='+list_time+'&re='+re+'&sh='+sh,true);");
            out.println("xmlHttp.send(null);");
            out.println("}");

            out.println("</script>");
            out.println("<br>");

            out.println("<div id='bottoni'>");
            out.println("<button type='button' onclick='swapVideo()'>▶▶</button>");
            out.println("<button type='button' onclick='goBack()'>CHANGE YOUR CHOICE!</button>");
            out.println("<p id = 'titolo'>titolo</p>");
            out.println("</div>");

            out.println("</body>");
            out.println("</html>");

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
